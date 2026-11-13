# 🚨🚨🚨 TOP PRIORITY — FINAL FULL P/INVOKE SCANNER: FIX, VERIFY, ACCEPT 🚨🚨🚨

**This is a mandatory handoff record. Read this first in the next conversation.**
**Do not restart the investigation, re-ask for already recorded evidence, or claim completion without the acceptance evidence below.**

## Project Lead's top-priority instructions

1. Keep a prominent record of everything still incomplete and what the final scanner must prove.
2. Repair the Stage 3 scanner carefully; do not merely patch symptoms.
3. Explain status with the traffic lights **GREEN / YELLOW / RED** in short, direct language.
4. Spend more effort on actual inspection, repair, execution and verification than on lengthy explanations.
5. Follow the Project Lead's lead. Do not select a canonical source, overwrite user files, or commit scanner-source changes until explicitly authorized.
6. Update the GitHub ChatGPT transcript with conversation decisions and evidence at each handoff.

## Current Stage 3 source variants — keep distinct

Two user-uploaded files are available in the 2026-11-14 conversation:

| Variant | Path | Bytes | SHA-256 |
|---|---|---:|---|
| A | `/mnt/data/pinvoke_stage3_scan(1)(1).py` | 10,931 | `f97cda39548cf76700b178edb54d23c6008de489d34d1bc6fb56a999dd944601` |
| B | `/mnt/data/pinvoke_stage3_scan(2).py` | 10,825 | `347b2989687d5220fc5101e50bc21512b134497d7e6efc5db61ed967e0a130bf` |

The variants differ in the ImplMap-to-MethodDef RID resolution block. Variant A builds an object-identity map and has a `rid_of(mr)` fallback; Variant B uses `rid_of(mr)` directly. A previously mentioned patched hash `72b182648654f21b5ea9a9c19c33978ab544ed498b33dd93dec9f7310874c3cc` does not match either upload. **No canonical/final scanner has been selected.**

Both uploaded files passed `python -m py_compile`. Runtime `--help` / scan has not been validated in this review because `dnfile` was missing in the assistant's runtime. No fresh scan output has been generated here.

## Confirmed scanner defects to repair

### 🔴 RED — must fix before acceptance

1. **CSV write ordering:** CSVs are written before MemberRef/MethodSpec matching mutates `calls` and `pinvokes`. Output CSVs can therefore omit rows later included in the summary. Move final serialization after all analysis/matching is complete and reconcile CSV counts with summary counts.
2. **P/Invoke declaration reconciliation:** The scanner does not prove that every MethodDef P/InvokeImpl flag maps to exactly one ImplMap row, or that every ImplMap row resolves exactly once. Add explicit totals, duplicate detection, unresolved-row reporting and hard assertions.
3. **Exit status:** Nonzero ImplMap resolution errors currently do not cause failure. Treat unresolved metadata mapping / required scan errors as a failing exit status; make the reason explicit.

### 🟡 YELLOW — resolution quality needs strengthening

4. **Name-only matching:** Matching by lowercased managed type + method name can misassociate overloads and same-named types across assemblies. Resolve with assembly identity, metadata token/RID, method signature and generic context wherever available. If exact resolution is impossible, label it ambiguous/unresolved; do not silently claim a match.
5. **MemberRef / MethodSpec coverage:** Validate direct calls and generic instantiations separately; document unsupported metadata patterns and preserve raw tokens/operands for auditability.
6. **Cross-assembly resolution:** Establish explicit resolution boundaries and report unresolved external references instead of treating a name match as proof.

## Final scanner acceptance criteria — all required

The final scanner is accepted only when all of these have evidence:

- [ ] Reproducible execution command, dependency versions and input manifest.
- [ ] All intended managed assemblies enumerated; unreadable/missing files explicitly counted.
- [ ] MethodDef, method-body, ImplMap and P/InvokeImpl counts reconciled.
- [ ] Every ImplMap row resolved to the correct MethodDef exactly once, or explicitly reported as unresolved with a failing/incomplete status.
- [ ] Every P/InvokeImpl MethodDef reconciled against ImplMap; no unexplained missing or duplicate mappings.
- [ ] MethodDef / MemberRef / MethodSpec references classified and resolved using token/signature/assembly identity, not name-only inference.
- [ ] Direct IL call-site evidence recorded separately from inferred reachability; include call, callvirt, ldftn, ldvirtftn and relevant function-pointer/calli patterns.
- [ ] Dynamic native loading reviewed: LoadLibrary/GetProcAddress, dlopen/dlsym, NativeLibrary.Load/GetExport, reflection/computed names and other loader patterns.
- [ ] Reverse P/Invoke / native callbacks and function-pointer paths reviewed or explicitly marked unsupported/unresolved.
- [ ] CSVs are written only after all mutations; every CSV row count matches the final summary and reconciliation tables.
- [ ] Deterministic output, stable IDs/tokens, duplicate/ambiguity reports, and clear error accounting.
- [ ] Negative tests prove that unresolved mappings, missing assemblies and parse failures cannot silently return success.
- [ ] Clean runtime test on the user's Termux environment with `dnfile` / `dncil` installed, followed by a fresh scan and independently checked output counts.

**Important:** Static declaration inventory completeness is not the same as runtime reachability, and neither proves that a native export exists or that the ABI works at runtime.

## Full P/Invoke audit — remaining work beyond the scanner

1. **Declaration / ABI interpretation:** calling convention, charset, EntryPoint, ExactSpelling, mapping flags, return/parameter types, marshalling, StructLayout, MarshalAs, pointer width, generic TypeSpec and signature edge cases.
2. **Call-site / dynamic-loading coverage:** determine direct call references and possible runtime paths; separately identify dynamic loads, function pointers, `calli`, reflection, computed library names and reverse callbacks.
3. **Native correlation:** map managed module names and entry points to native binary imports/exports, including ordinal-only exports; inspect implementation bodies only where evidence is available.
4. **Runtime validation:** architecture/dependency compatibility, symbol availability, load success, callable ABI and actual runtime errors. Static reports alone cannot establish these.

## Existing audit baseline (historical evidence; not newly rerun here)

- Stage 1 historical result: 119/119 managed DLLs parsed; 3,791 P/Invoke declarations and 3,791 ImplMap rows; 17 assemblies contain declarations; 102 do not; 66 ModuleRef names; 0 reported metadata/signature parse errors.
- Stage 2: 3,791-row metadata profile; 66 native module groups; 33 ABI combinations; 1,976 exact-signature groups; 196 duplicate DLL+EntryPoint keys. Deeper semantic ABI/marshalling review remains.
- Assembly-CSharp-specific RID check: 360/360 ImplMap MemberForwarded RIDs validated against MethodDef rows, with no duplicate RIDs or errors. This validates that assembly's RID mapping only; it does not validate all scanner logic or all 3,791 declarations.
- Historical Stage 3 whole-assembly figures (119 files, 208,570 MethodDefs, 182,514 bodies, 695,709 IL rows) and prior P/Invoke/call-site figures are not accepted as fresh proof until reproduced by the corrected scanner.
- Stage 4 native PE import report previously had 692 rows including 37 parser header/section rows (655 candidate import rows); export report had 1,148 rows. Native import/export-to-managed-declaration correlation remains incomplete.

## Traffic-light status at this handoff

- **🟢 GREEN:** Uploaded A/B Python syntax compilation; Assembly-CSharp 360/360 RID mapping historical check; Stage 1 historical inventory baseline.
- **🟡 YELLOW:** Stage 2 semantic ABI interpretation; scanner source comparison; some metadata/call-site resolution design.
- **🔴 RED:** Final Stage 3 scanner not selected, repaired, executed and accepted; full dynamic-loading coverage not proven; Stage 4 native correlation and runtime validation incomplete.

**Next conversation: read this file and the latest ChatGPT transcript first. Continue with scanner repair and verification. Keep updates short: show GREEN/YELLOW/RED, what evidence passed, what remains, and the next concrete action.**
