#!/usr/bin/env python3
"""Stage 3 read-only ECMA-335 IL / PInvoke inventory scanner.

Requires: dnfile, dncil
Input DLLs are opened read-only. All generated files go to --out.
MemberRef/MethodSpec P/Invoke links require exact assembly + type + method +
raw signature matches; ambiguous or unresolved references are reported, never
silently linked by method name alone.
"""
import argparse
import csv
import hashlib
import json
import os
import re
import sys
from collections import Counter, defaultdict

import dnfile
from dncil.cil.body import CilMethodBody
from dncil.cil.body.reader import CilMethodBodyReaderBytes

SCANNER_VERSION = "3.1.0-audit"
CALL_OPS = {"call", "callvirt", "newobj", "calli", "ldftn", "ldvirtftn"}
DYNAMIC_NAMES = {
    "loadlibrary", "loadlibrarya", "loadlibraryw", "loadlibraryex",
    "loadlibraryexa", "loadlibraryexw", "getprocaddress", "freelibrary",
    "ldrloaddll", "ldrgetprocedureaddress", "dlopen", "dlsym", "dlclose",
    "android_dlopen_ext", "mono_dl_open",
}
TOKEN_RE = re.compile(r"0x([0-9a-fA-F]{1,8})")


def text(value):
    if value is None:
        return ""
    try:
        return str(value)
    except Exception:
        return repr(value)


def row_name(row, attr="Name"):
    try:
        return text(getattr(row, attr))
    except Exception:
        return ""


def row_ref(value):
    try:
        return value.row
    except Exception:
        return None


def token_value(operand):
    if operand is None:
        return None
    for attr in ("value", "token", "raw"):
        try:
            value = getattr(operand, attr)
            if isinstance(value, int):
                return value
        except Exception:
            pass
    match = TOKEN_RE.search(text(operand))
    return int(match.group(1), 16) if match else None


def table_name(token):
    return (token >> 24) & 0xFF


def token_rid(token):
    return token & 0x00FFFFFF


def blob_bytes(heap_item):
    """Extract the exact signature blob bytes from dnfile heap wrappers."""
    if heap_item is None:
        return b""
    for attr in ("value", "raw_data", "data"):
        try:
            value = getattr(heap_item, attr)
            if isinstance(value, bytes):
                return value
            if isinstance(value, bytearray):
                return bytes(value)
        except Exception:
            pass
    try:
        value = bytes(heap_item)
        if value:
            return value
    except Exception:
        pass
    return b""


def signature_hex(row):
    try:
        return blob_bytes(row.Signature).hex()
    except Exception:
        return ""


def public_key_token(row, is_reference):
    """Normalize Assembly/AssemblyRef public-key data to a public-key token."""
    key_blob = b""
    for key_field in (("PublicKeyOrToken", "PublicKey") if is_reference else ("PublicKey",)):
        try:
            key_blob = blob_bytes(getattr(row, key_field))
            if key_blob:
                break
        except Exception:
            pass
    if not key_blob:
        return "null"
    is_full_key = False
    try:
        is_full_key = bool(row.Flags.afPublicKey)
    except Exception:
        # Assembly definitions carry a full public key; AssemblyRef may carry a token.
        is_full_key = not is_reference
    if is_full_key:
        return hashlib.sha1(key_blob).digest()[-8:][::-1].hex()
    return key_blob.hex()


def assembly_identity_from_row(row, is_reference):
    name = row_name(row, "Name")
    if not name:
        return ""
    version = ".".join(str(getattr(row, field, 0)) for field in
                        ("MajorVersion", "MinorVersion", "BuildNumber", "RevisionNumber"))
    culture = row_name(row, "Culture") or "neutral"
    token = public_key_token(row, is_reference)
    return f"{name}|{version}|{culture}|{token}"


def assembly_identity(pe, fallback_path):
    try:
        rows = pe.net.mdtables.Assembly.rows
        if rows:
            identity = assembly_identity_from_row(rows[0], False)
            if identity:
                return identity
    except Exception:
        pass
    fallback = os.path.splitext(os.path.basename(fallback_path))[0]
    return f"{fallback}|unknown|neutral|null"


def type_identity(type_row, current_assembly, seen=None):
    """Return (full type name, defining assembly simple name); empty if unknown."""
    if type_row is None:
        return "", ""
    if seen is None:
        seen = set()
    marker = id(type_row)
    if marker in seen:
        return "", ""
    seen.add(marker)

    cls = type(type_row).__name__
    name = row_name(type_row, "TypeName") or row_name(type_row, "Name")
    namespace = row_name(type_row, "TypeNamespace")
    if not name:
        return "", ""
    full_name = (namespace + "." if namespace else "") + name

    if cls == "TypeDef":
        return full_name, current_assembly
    if cls != "TypeRef":
        return "", ""

    scope = row_ref(getattr(type_row, "ResolutionScope", None))
    if scope is None:
        return full_name, ""
    scope_cls = type(scope).__name__
    if scope_cls == "AssemblyRef":
        return full_name, assembly_identity_from_row(scope, True)
    if scope_cls == "TypeRef":
        parent_name, parent_assembly = type_identity(scope, current_assembly, seen)
        if parent_name:
            return parent_name + "+" + name, parent_assembly
        return "", ""
    if scope_cls in ("Module", "ModuleRef"):
        return full_name, current_assembly
    return "", ""


def method_identity(method_row, current_assembly, owners, method_rid_by_obj):
    """Return exact lookup identity for MethodDef or MemberRef row."""
    cls = type(method_row).__name__
    method = row_name(method_row, "Name")
    sig = signature_hex(method_row)
    if cls == "MethodDef":
        rid = method_rid_by_obj.get(id(method_row), 0)
        owner = owners.get(rid, "")
        return current_assembly, owner, method, sig
    if cls == "MemberRef":
        parent = row_ref(getattr(method_row, "Class", None))
        owner, assembly = type_identity(parent, current_assembly)
        return assembly, owner, method, sig
    return "", "", method, sig


def build_owner_map(md):
    owners = {}
    methods = getattr(getattr(md, "MethodDef", None), "rows", []) or []
    typedefs = getattr(getattr(md, "TypeDef", None), "rows", []) or []
    starts = []
    for type_row in typedefs:
        method_list = getattr(type_row, "MethodList", None)
        try:
            first = method_list[0] if isinstance(method_list, (list, tuple)) and method_list else method_list
            start = int(first.row_index) if first is not None else 0
        except Exception:
            start = 0
        if start:
            starts.append((start, type_row))
    starts.sort(key=lambda item: item[0])
    for index, (start, type_row) in enumerate(starts):
        end = starts[index + 1][0] - 1 if index + 1 < len(starts) else len(methods)
        namespace = row_name(type_row, "TypeNamespace")
        name = row_name(type_row, "TypeName") or row_name(type_row, "Name")
        full_name = (namespace + "." if namespace else "") + name
        for rid in range(start, end + 1):
            owners[rid] = full_name
    return owners


def method_has_pinvoke_flag(method_row):
    flags = getattr(method_row, "Flags", None)
    try:
        return bool(flags.mdPinvokeImpl)
    except Exception:
        try:
            return bool(int(flags) & 0x2000)
        except Exception:
            raise ValueError("cannot read MethodDef mdPinvokeImpl flag")


def get_implmap_scope(row):
    scope = row_ref(getattr(row, "ImportScope", None))
    return row_name(scope, "Name") if scope is not None else ""


def write_csv(path, rows):
    if not rows:
        with open(path, "w", encoding="utf-8", newline="") as handle:
            handle.write("")
        return
    columns = []
    for row in rows:
        for key in row:
            if key not in columns:
                columns.append(key)
    with open(path, "w", encoding="utf-8", newline="") as handle:
        writer = csv.DictWriter(handle, fieldnames=columns, extrasaction="ignore")
        writer.writeheader()
        writer.writerows(rows)


def scan_one(path, all_calls, all_declarations, all_dynamic, errors, counters):
    basename = os.path.basename(path)
    pe = None
    try:
        pe = dnfile.dnPE(path)
        if not getattr(pe, "net", None) or not getattr(pe.net, "mdtables", None):
            counters["non_managed_or_no_metadata"] += 1
            return
        md = pe.net.mdtables
        methods = getattr(getattr(md, "MethodDef", None), "rows", []) or []
        implmaps = getattr(getattr(md, "ImplMap", None), "rows", []) or []
        memberrefs = getattr(getattr(md, "MemberRef", None), "rows", []) or []
        methodspecs = getattr(getattr(md, "MethodSpec", None), "rows", []) or []
        current_assembly = assembly_identity(pe, path)
        method_rid_by_obj = {id(row): rid for rid, row in enumerate(methods, 1)}
        owners = build_owner_map(md)

        implmap_by_rid = {}
        counters["implmap_rows_seen"] += len(implmaps)
        for implmap in implmaps:
            try:
                forwarded = row_ref(getattr(implmap, "MemberForwarded", None))
                rid = method_rid_by_obj.get(id(forwarded), 0)
                if not rid:
                    raise ValueError("MemberForwarded did not resolve to a MethodDef row in this module")
                if rid in implmap_by_rid:
                    counters["implmap_duplicate_method_rows"] += 1
                    raise ValueError(f"multiple ImplMap rows for MethodDef RID {rid}")
                implmap_by_rid[rid] = {
                    "dll": get_implmap_scope(implmap),
                    "entrypoint": row_name(implmap, "ImportName") or row_name(forwarded),
                    "managed_method": row_name(forwarded),
                    "managed_type": owners.get(rid, ""),
                    "signature_hex": signature_hex(forwarded),
                    "assembly_identity": current_assembly,
                    "method_rid": rid,
                }
            except Exception as exc:
                counters["implmap_resolution_errors"] += 1
                errors.append({"assembly": basename, "stage": "ImplMap", "detail": text(exc)})

        flagged_rids = set()
        for rid, method_row in enumerate(methods, 1):
            try:
                if method_has_pinvoke_flag(method_row):
                    flagged_rids.add(rid)
            except Exception as exc:
                counters["pinvoke_flag_read_errors"] += 1
                errors.append({"assembly": basename, "stage": "MethodDefFlags", "detail": f"RID {rid}: {exc}"})
        missing_implmap = flagged_rids - set(implmap_by_rid)
        orphan_implmap = set(implmap_by_rid) - flagged_rids
        counters["pinvoke_flagged_methoddefs"] += len(flagged_rids)
        counters["pinvoke_declarations_resolved"] += len(implmap_by_rid)
        counters["pinvoke_missing_implmap"] += len(missing_implmap)
        counters["implmap_orphan_rows"] += len(orphan_implmap)
        for rid in sorted(missing_implmap):
            errors.append({"assembly": basename, "stage": "ImplMapCoverage", "detail": f"MethodDef RID {rid} has mdPinvokeImpl but no resolved ImplMap"})
        for rid in sorted(orphan_implmap):
            errors.append({"assembly": basename, "stage": "ImplMapCoverage", "detail": f"ImplMap resolved to MethodDef RID {rid} without mdPinvokeImpl flag"})

        declarations = []
        for rid, declaration in implmap_by_rid.items():
            declarations.append({
                "assembly": basename,
                "assembly_identity": current_assembly,
                "managed_type": declaration["managed_type"],
                "managed_method": declaration["managed_method"],
                "method_rid": rid,
                "dll": declaration["dll"],
                "entrypoint": declaration["entrypoint"],
                "signature_hex": declaration["signature_hex"],
            })
        all_declarations.extend(declarations)

        counters["managed_files_scanned"] += 1
        counters["methoddefs_seen"] += len(methods)
        counters["method_bodies_attempted"] += sum(1 for row in methods if getattr(row, "Rva", 0))

        for caller_rid, caller in enumerate(methods, 1):
            rva = getattr(caller, "Rva", 0) or 0
            if not rva:
                continue
            try:
                body = CilMethodBody(CilMethodBodyReaderBytes(pe.get_data(rva)))
                instructions = getattr(body, "instructions", []) or []
                counters["method_bodies_parsed"] += 1
            except Exception as exc:
                counters["method_body_parse_errors"] += 1
                errors.append({"assembly": basename, "stage": "ILParse", "detail": f"MethodDef RID {caller_rid}: {type(exc).__name__}: {exc}"})
                continue

            caller_name = row_name(caller)
            caller_type = owners.get(caller_rid, "")
            for instruction in instructions:
                try:
                    opcode = text(instruction.opcode.name).lower()
                except Exception:
                    opcode = text(getattr(instruction, "opcode", "")).lower()
                if opcode not in CALL_OPS:
                    continue
                token = token_value(getattr(instruction, "operand", None))
                table = table_name(token) if token is not None else None
                rid = token_rid(token) if token is not None else None
                target_name = target_type = target_assembly = target_dll = target_entry = target_kind = ""
                target_sig = ""
                if table == 0x06 and rid and rid <= len(methods):
                    target = methods[rid - 1]
                    target_name = row_name(target)
                    target_type = owners.get(rid, "")
                    target_assembly = current_assembly
                    target_sig = signature_hex(target)
                    target_kind = "MethodDef"
                    declaration = implmap_by_rid.get(rid)
                    if declaration:
                        target_dll = declaration["dll"]
                        target_entry = declaration["entrypoint"]
                elif table == 0x0A and rid and rid <= len(memberrefs):
                    target = memberrefs[rid - 1]
                    target_name = row_name(target)
                    target_assembly, target_type, _, target_sig = method_identity(target, current_assembly, owners, method_rid_by_obj)
                    target_kind = "MemberRef"
                elif table == 0x2B and rid and rid <= len(methodspecs):
                    spec = methodspecs[rid - 1]
                    base = row_ref(getattr(spec, "Method", None))
                    if base is not None:
                        target_name = row_name(base)
                        target_assembly, target_type, _, target_sig = method_identity(base, current_assembly, owners, method_rid_by_obj)
                    target_kind = "MethodSpec"
                row = {
                    "assembly": basename,
                    "assembly_identity": current_assembly,
                    "caller_type": caller_type,
                    "caller_method": caller_name,
                    "caller_method_rid": caller_rid,
                    "il_offset": getattr(instruction, "offset", ""),
                    "opcode": opcode,
                    "target_token": f"0x{token:08X}" if token is not None else "",
                    "target_table": f"0x{table:02X}" if table is not None else "",
                    "target_kind": target_kind,
                    "target_assembly": target_assembly,
                    "target_type": target_type,
                    "target_method": target_name,
                    "target_signature_hex": target_sig,
                    "pinvoke_dll": target_dll,
                    "pinvoke_entrypoint": target_entry,
                }
                all_calls.append(row)
                if target_dll or target_name.lower() in DYNAMIC_NAMES:
                    all_declarations.append({
                        "assembly": basename, "assembly_identity": current_assembly,
                        "managed_type": caller_type, "managed_method": caller_name,
                        "method_rid": caller_rid, "dll": target_dll,
                        "entrypoint": target_entry, "signature_hex": "",
                        "record_kind": "DIRECT_CALLSITE", "target_method": target_name,
                        "target_type": target_type,
                    })
                if target_name.lower() in DYNAMIC_NAMES:
                    all_dynamic.append(row)
    except Exception as exc:
        counters["file_errors"] += 1
        errors.append({"assembly": basename, "stage": "File", "detail": f"{type(exc).__name__}: {exc}"})
        print(f"[ERROR] {path}: {type(exc).__name__}: {exc}", file=sys.stderr)
    finally:
        if pe is not None:
            try:
                pe.close()
            except Exception:
                pass


def enrich_cross_assembly_calls(calls, declarations, counters):
    index = defaultdict(list)
    for declaration in declarations:
        if declaration.get("record_kind") == "DIRECT_CALLSITE":
            continue
        key = (
            declaration.get("assembly_identity", "").casefold(),
            declaration.get("managed_type", "").casefold(),
            declaration.get("managed_method", "").casefold(),
            declaration.get("signature_hex", "").lower(),
        )
        if all(key):
            index[key].append(declaration)

    for row in calls:
        if row.get("target_kind") not in ("MemberRef", "MethodSpec"):
            continue
        key = (
            row.get("target_assembly", "").casefold(),
            row.get("target_type", "").casefold(),
            row.get("target_method", "").casefold(),
            row.get("target_signature_hex", "").lower(),
        )
        if not all(key):
            counters["memberref_identity_incomplete"] += 1
            continue
        matches = index.get(key, [])
        if len(matches) == 1:
            match = matches[0]
            row["pinvoke_dll"] = match.get("dll", "")
            row["pinvoke_entrypoint"] = match.get("entrypoint", "")
            row["target_kind"] += "_PInvokeExactMatch"
            counters["memberref_pinvoke_exact_matches"] += 1
        elif len(matches) > 1:
            row["target_kind"] += "_PInvokeAmbiguous"
            counters["memberref_pinvoke_ambiguous"] += 1
        else:
            # Most MemberRefs are ordinary managed/native API references, not P/Invokes.
            counters["memberref_no_pinvoke_match"] += 1


def main():
    parser = argparse.ArgumentParser(description="Read-only Stage 3 IL/PInvoke inventory")
    parser.add_argument("managed_dir", help="Directory containing original managed DLL files")
    parser.add_argument("-o", "--out", default="PINVOKE_STAGE3_OUTPUT")
    args = parser.parse_args()

    managed_dir = os.path.realpath(args.managed_dir)
    out_dir = os.path.realpath(args.out)
    if not os.path.isdir(managed_dir):
        parser.error(f"managed_dir does not exist or is not a directory: {managed_dir}")
    if out_dir == managed_dir or out_dir.startswith(managed_dir + os.sep):
        parser.error("output directory must be outside the original Managed input directory")

    dll_files = []
    for root, _, names in os.walk(managed_dir):
        for name in names:
            if name.lower().endswith(".dll"):
                dll_files.append(os.path.join(root, name))
    dll_files.sort()
    os.makedirs(out_dir, exist_ok=True)

    calls, declarations, dynamics, errors = [], [], [], []
    counters = Counter()
    counters["dll_files_found"] = len(dll_files)
    for index, path in enumerate(dll_files, 1):
        scan_one(path, calls, declarations, dynamics, errors, counters)
        if index % 25 == 0 or index == len(dll_files):
            print(f"Progress: {index}/{len(dll_files)} DLLs", flush=True)

    # Enrichment MUST happen before any CSV is written.
    enrich_cross_assembly_calls(calls, declarations, counters)

    opcode_counts = Counter(row["opcode"] for row in calls)
    fatal_checks = {
        "file_errors": counters["file_errors"],
        "method_body_parse_errors": counters["method_body_parse_errors"],
        "implmap_resolution_errors": counters["implmap_resolution_errors"],
        "pinvoke_flag_read_errors": counters["pinvoke_flag_read_errors"],
        "pinvoke_missing_implmap": counters["pinvoke_missing_implmap"],
        "implmap_orphan_rows": counters["implmap_orphan_rows"],
        "implmap_duplicate_method_rows": counters["implmap_duplicate_method_rows"],
    }
    validation_pass = not any(fatal_checks.values())
    summary = {
        "scanner_version": SCANNER_VERSION,
        "input_directory": managed_dir,
        "output_directory": out_dir,
        "dll_files_found": len(dll_files),
        "counters": dict(counters),
        "call_instruction_rows": len(calls),
        "pinvoke_declaration_rows": sum(1 for row in declarations if row.get("record_kind") != "DIRECT_CALLSITE"),
        "dynamic_loader_rows": len(dynamics),
        "opcode_counts": dict(opcode_counts),
        "fatal_validation_checks": fatal_checks,
        "validation": "PASS" if validation_pass else "FAIL",
        "limitations": [
            "MemberRef/MethodSpec P/Invoke association requires exact assembly identity (name, version, culture, public-key token), declaring type, method name, and raw signature bytes; unmatched and ambiguous references remain unlinked.",
            "This is static IL evidence, not runtime reachability proof.",
            "Indirect calls, reflection, native callbacks, and computed loader names may require deeper analysis.",
            "Declaration-only metadata records do not prove a P/Invoke is called.",
            "A successful scan does not establish Android ABI compatibility or native-library availability.",
        ],
    }

    # Persist all outputs only after cross-assembly enrichment is complete.
    write_csv(os.path.join(out_dir, "il_call_sites.csv"), calls)
    write_csv(os.path.join(out_dir, "pinvoke_declarations.csv"), [r for r in declarations if r.get("record_kind") != "DIRECT_CALLSITE"])
    write_csv(os.path.join(out_dir, "pinvoke_callsite_and_declarations.csv"), [r for r in calls if r.get("pinvoke_dll") or r.get("target_kind", "").endswith("PInvokeAmbiguous")] + [r for r in declarations if r.get("record_kind") != "DIRECT_CALLSITE"])
    write_csv(os.path.join(out_dir, "dynamic_loader_call_sites.csv"), dynamics)
    write_csv(os.path.join(out_dir, "stage3_errors.csv"), errors)
    with open(os.path.join(out_dir, "stage3_summary.json"), "w", encoding="utf-8") as handle:
        json.dump(summary, handle, indent=2)
        handle.write("\n")

    print(json.dumps(summary, indent=2))
    print("OUTPUT_DIR=" + out_dir)
    if not validation_pass:
        print("STAGE3_VALIDATION=FAIL", file=sys.stderr)
        sys.exit(2)
    print("STAGE3_VALIDATION=PASS")


if __name__ == "__main__":
    main()
