#!/data/data/com.termux/files/usr/bin/bash
set -e

BASE="$HOME/PINVOKE_STAGE2_20261113_235016/extracted"
CSV="$(find "$BASE" -name managed_pinvoke_all_3791.csv -type f | head -n 1)"

if [ -z "$CSV" ]; then
  echo "CSV not found. Give the audit ZIP path:"
  read -r ZIP
  TMP="$HOME/PINVOKE_GROUP_INPUT"
  mkdir -p "$TMP"
  unzip -oq "$ZIP" -d "$TMP"
  CSV="$(find "$TMP" -name managed_pinvoke_all_3791.csv -type f | head -n 1)"
fi

[ -n "$CSV" ] || { echo "ERROR: CSV missing"; exit 1; }

OUT="$HOME/PINVOKE_GROUP_AUDIT_$(date +%Y%m%d_%H%M%S)"
mkdir -p "$OUT"

cat > "$OUT/group_audit.py" <<'PY'
import csv, os, sys, re, collections

src, out = sys.argv[1], sys.argv[2]

with open(src, newline="", encoding="utf-8-sig") as f:
    reader = csv.DictReader(f)
    rows = list(reader)
    headers = reader.fieldnames

def val(r, k):
    return (r.get(k) or "").strip()

def counter_file(name, title, counter):
    with open(os.path.join(out, name), "w", encoding="utf-8") as f:
        f.write(title + "\n")
        f.write("COUNT\tGROUP\n")
        for k, n in counter.most_common():
            f.write(f"{n}\t{k}\n")

def group_count(keyfn):
    c = collections.Counter()
    for r in rows:
        c[keyfn(r)] += 1
    return c

# 1. Native DLL groups
dll = group_count(lambda r: val(r, "dll") or "<EMPTY>")
counter_file("01_native_dll_groups.tsv", "P/Invoke by native DLL", dll)

# 2. Assembly groups
asm = group_count(lambda r: val(r, "assembly") or "<EMPTY>")
counter_file("02_assembly_groups.tsv", "P/Invoke by assembly", asm)

# 3. ABI groups
cc = group_count(lambda r: val(r, "calling_convention") or "<EMPTY>")
charset = group_count(lambda r: val(r, "charset") or "<EMPTY>")
flags = group_count(lambda r: val(r, "mapping_flags") or "<EMPTY>")

counter_file("03_calling_conventions.tsv", "Calling conventions", cc)
counter_file("04_charsets.tsv", "Charsets", charset)
counter_file("05_mapping_flags.tsv", "Mapping flags", flags)

matrix = collections.Counter()
for r in rows:
    matrix[(val(r,"calling_convention") or "<EMPTY>",
            val(r,"charset") or "<EMPTY>",
            val(r,"mapping_flags") or "<EMPTY>")] += 1

with open(os.path.join(out,"06_abi_matrix.tsv"),"w") as f:
    f.write("count\tcalling_convention\tcharset\tmapping_flags\n")
    for (a,b,c),n in matrix.most_common():
        f.write(f"{n}\t{a}\t{b}\t{c}\n")

# 4. Assembly -> DLL -> calling convention groups
triple = collections.Counter()
for r in rows:
    triple[(val(r,"assembly"),val(r,"dll"),
            val(r,"calling_convention"))] += 1

with open(os.path.join(out,"07_assembly_dll_abi.tsv"),"w") as f:
    f.write("count\tassembly\tdll\tcalling_convention\n")
    for (a,b,c),n in triple.most_common():
        f.write(f"{n}\t{a}\t{b}\t{c}\n")

# 5. EntryPoint groups and duplicate mappings
entry_groups = collections.defaultdict(list)
for i,r in enumerate(rows,1):
    key=(val(r,"dll"),val(r,"entrypoint"))
    entry_groups[key].append((i,r))

with open(os.path.join(out,"08_duplicate_entrypoints.tsv"),"w") as f:
    f.write("dll\tentrypoint\tcount\tassemblies\tmanaged_methods\n")
    for (d,e),items in sorted(entry_groups.items()):
        if len(items)>1:
            assemblies=";".join(sorted(set(val(x[1],"assembly") for x in items)))
            methods=";".join(val(x[1],"type")+"::"+val(x[1],"method")
                             for x in items)
            f.write(f"{d}\t{e}\t{len(items)}\t{assemblies}\t{methods}\n")

# 6. Signature groups
sig = group_count(lambda r: val(r,"signature") or "<EMPTY>")
counter_file("09_exact_signature_groups.tsv", "Exact signature groups", sig)

# Broad signature shape classifier (heuristic grouping only)
shape = collections.Counter()
for r in rows:
    s=val(r,"signature").lower()
    tags=[]
    if "*" in s or "native int" in s: tags.append("POINTER")
    if "&" in s: tags.append("BYREF")
    if "[]" in s or "szarray" in s or "array" in s: tags.append("ARRAY")
    if "valuetype" in s: tags.append("VALUETYPE")
    if "class " in s: tags.append("CLASS")
    if "string" in s: tags.append("STRING")
    if "fnptr" in s or "function pointer" in s: tags.append("FUNCTION_POINTER")
    if "!!" in s or re.search(r'(^|[\s,(])!\d+',s): tags.append("GENERIC")
    shape["+".join(tags) if tags else "OTHER"] += 1

counter_file("10_signature_shapes.tsv","Signature shape groups",shape)

# 7. Metadata boolean/value distributions
metadata_fields = [
    "exact_spelling","supports_last_error","best_fit",
    "throw_on_unmappable_char","pinvoke_impl_flag",
    "method_flags","impl_flags","mapping_flags_hex",
    "method_flags_hex","impl_flags_hex"
]
for field in metadata_fields:
    if field in headers:
        c=group_count(lambda r, field=field: val(r,field) or "<EMPTY>")
        counter_file("field_"+field+".tsv",field,c)

# 8. Missing required fields / suspicious combinations
required=["type","method","dll","entrypoint","assembly","signature",
          "calling_convention","mapping_flags"]
with open(os.path.join(out,"11_missing_fields.tsv"),"w") as f:
    f.write("row\tmissing_fields\ttype\tmethod\tdll\tentrypoint\n")
    for i,r in enumerate(rows,1):
        missing=[k for k in required if not val(r,k)]
        if missing:
            f.write(f"{i}\t{','.join(missing)}\t{val(r,'type')}\t"
                    f"{val(r,'method')}\t{val(r,'dll')}\t{val(r,'entrypoint')}\n")

# 9. Row-level full normalized export, retaining original columns
with open(os.path.join(out,"12_all_rows_sorted.csv"),"w",
          newline="",encoding="utf-8") as f:
    w=csv.DictWriter(f,fieldnames=headers)
    w.writeheader()
    for r in sorted(rows,key=lambda x:(val(x,"dll"),val(x,"assembly"),
                                       val(x,"entrypoint"),val(x,"method"))):
        w.writerow(r)

# Summary
summary=[
    "P/INVOKE GROUP AUDIT",
    f"Input: {src}",
    f"Rows: {len(rows)}",
    f"Columns: {len(headers)}",
    f"Native DLL groups: {len(dll)}",
    f"Assembly groups: {len(asm)}",
    f"Unique ABI combinations: {len(matrix)}",
    f"Unique exact signatures: {len(sig)}",
    f"Duplicate DLL+EntryPoint keys: {sum(1 for x in entry_groups.values() if len(x)>1)}",
]
with open(os.path.join(out,"SUMMARY.txt"),"w") as f:
    f.write("\n".join(summary)+"\n")

print("\n".join(summary))
print("Reports:",out)
PY

echo "[GROUP AUDIT] Processing all declarations..."
python "$OUT/group_audit.py" "$CSV" "$OUT"

echo
echo "DONE"
echo "Output: $OUT"
echo "Summary: $OUT/SUMMARY.txt"
