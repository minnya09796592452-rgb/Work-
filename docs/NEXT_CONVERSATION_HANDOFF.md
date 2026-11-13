# Next Conversation Handoff — CASTAWAY DEPTHS

**Read this file first at the start of the next conversation.** It is the current concise handoff; then consult `docs/PROJECT_STATE.md` and the detailed P/Invoke report.

## Project authority and boundaries
- User is Project Lead / Chief Engineer. Follow the user's lead; implement only requested work and do not redesign unsolicited.
- Assistant role: implementation engineer; preserve the user's decisions and evidence.
- Project: CASTAWAY DEPTHS, original native Android survival game.
- Repository: `devmyatmin/Read-CASTAWAY-DEPTHS`, branch `main`.
- This is original development, not a Stranded Deep port. P/Invoke investigation is reference-only. Do not import or reuse Stranded Deep code, IL, DLLs, binaries, assets, branding, or protected implementation.
- Do not introduce Godot unless the user explicitly reintroduces it.

## ဝါစိမ်းနီ status key
- 🟢 GREEN = confirmed by evidence / PASS.
- 🟡 YELLOW = caution; further inspection required.
- 🔴 RED = required fix; blocking issue.

## Current milestone state — 2026-11-14
| Stage | Status | Meaning |
|---|---|---|
| Stage 1 | 🟢 PASS | Completed |
| Stage 2 | 🟢 PASS | 3,791 P/Invoke rows grouped and reported |
| Stage 3 | 🟡 IN PROGRESS | Assembly-CSharp EOS IL call-site scanner work |
| Stage 4 | 🔴 INCOMPLETE | Not completed; do not mark PASS |

### Stage 3 Scanner — current evidence
- 🟢 Python Variant A and Variant B pass syntax compilation; source differences have been identified and the GitHub review recorded.
- 🟡 Method association based only on method name is not sufficiently robust.
- 🟡 ImplMap errors currently can result in exit code 0; failures must be surfaced as failures.
- 🟡 A declaration-to-ImplMap completeness check is still required.
- 🔴 CSV output is written before MemberRef processing; fix the processing/output order so the required MemberRef data is available before CSV emission.
- No successful end-to-end runtime scan result has been recorded yet. Syntax compilation is not runtime validation.
- Two Stage 3 scanner script variants were supplied in the conversation. The user said the next Termux output would follow; wait for that output and inspect it before claiming a scan result.

## Next action — continue, do not restart
1. Wait for and inspect the user's next Termux output.
2. Review the two supplied scanner variants against the RED/YELLOW findings above.
3. Fix only the identified scanner defects, following the user's direction.
4. Run the scanner against the verified original `Assembly-CSharp.dll` read-only.
5. Verify declaration/ImplMap coverage, MemberRef association, IL operand results, exit status, and generated CSV/report files.
6. Mark Stage 3 GREEN only after successful runtime evidence and all required checks pass. Keep Stage 4 INCOMPLETE until separately completed.

## Prior verified baseline
- Original `Assembly-CSharp.dll` SHA-256: `d5501ae7a2c09499cdf569c5b52578401f039c2f4aa5e7e9240c22faeef214bc`.
- Metadata validation: 26,143 MethodDef rows, 6,078 MemberRef rows, 1 ModuleRef row, 360 ImplMap/P/Invoke rows.
- Stage 2 grouping: 3,791 rows, 66 native-module groups, 17 assemblies, 33 ABI combinations, 1,976 exact signatures, 196 repeated DLL + entry-point keys, 0 missing-field rows.
- Detailed technical evidence: `docs/research/PINVOKE_GROUP_AUDIT.md`.

**Resume point:** continue from Stage 3 Scanner validation and wait for the user's promised output. Do not restart earlier stages or infer runtime success from syntax checks.


## New starting evidence — Stage 2 rerun and uploaded scanner review (2026-11-14)

User supplied a fresh Termux run of `$HOME/pinvoke_stage2.sh` against `/storage/emulated/0/Download/CASTAWAY_PInvoke_Full_Audit.zip`.

Observed output:
- CSV: `$HOME/PINVOKE_STAGE2_20261113_235114/extracted/CASTAWAY_PInvoke_Full_Audit/managed_pinvoke_all_3791.csv`
- 3,791 rows; 21 columns.
- Calling conventions: Winapi 2,033; Cdecl 1,159; StdCall 599.
- Charset: unspecified 3,384; Unicode 177; Ansi 153; Auto 77.
- The output says `native_module: NOT FOUND`, but the actual CSV schema column is `dll`; this is a reporting-field-name mismatch, not evidence that module names are absent. The entry-point column is `entrypoint`.
- Signature-shape counts (pointer, valuetype, byref, etc.) overlap and must not be summed as mutually exclusive categories.
- The prior Stage 2 group audit remains the authoritative grouped baseline: 66 modules, 17 assemblies, 33 ABI combinations, 1,976 exact signatures, 196 repeated DLL/entry-point keys, zero missing-field rows.

Two newly uploaded scanner files were inspected and both pass `python -m py_compile`:
- `pinvoke_stage3_scan(2).py` SHA-256: `347b2989687d5220fc5101e50bc21512b134497d7e6efc5db61ed967e0a130bf`.
- `pinvoke_stage3_scan(1)(1).py` SHA-256: `f97cda39548cf76700b178edb54d23c6008de489d34d1bc6fb56a999dd944601`.
- The second variant only adds an object-identity lookup for MethodDef rows before falling back to `rid_of(mr)`; it does not yet establish a reliable coded-index RID mapping.
- Both variants still write all three CSV files before the MemberRef/MethodSpec matching loop (script lines 196–211 or 197–212). The later in-memory changes are therefore not persisted in the CSV files. This remains 🔴 RED.
- Both variants associate MemberRef/MethodSpec references by managed type + method name only; this remains 🟡 YELLOW.
- ImplMap resolution exceptions are counted but do not cause a non-zero exit status; declaration-to-ImplMap completeness is not checked. Both remain 🟡 YELLOW.
- No Stage 3 runtime scan has been established by this Stage 2 output or by syntax compilation.

**Immediate next action:** perform a read-only audit of the extracted CSV's actual `dll` and `entrypoint` columns (distinct module count, row totals, empty values) to confirm the “NOT FOUND” display mismatch. Do not run either uploaded Stage 3 scanner yet. Fix the scanner RED/YELLOW defects first, then run against the original verified Managed directory and validate its outputs.


## Verified Termux CSV column check (2026-11-14)

User ran a read-only Python check against the extracted Stage 2 CSV. Exact observed results:

- Columns include `dll` and `entrypoint` (21 columns total).
- `dll`: 3,791 rows; 0 missing; **66 unique values**.
- `entrypoint`: 3,791 rows; 0 missing; **3,324 unique values**.
- Top DLL counts: `steam_api64` 1,025; `gdiplus` 556; `EOSSDK-Win64-Shipping` 360; `Microsoft.Xbox.Services.141.GDK.C.Thunks` 350; `XGamingRuntimeThunks` 190; `libmono-btls-shared` 188; `kernel32.dll` 125; `steam_api` 115; `user32.dll` 79; `SDL2` 55; `libSDL2-2.0.so.0` 55; `advapi32.dll` 53; `Rewired_DirectInput` 52; `odbc32.dll` 45; CoreFoundation framework path 38.
- Top entrypoint counts begin: `XInputGetState` 10; `ReadFile` 7; `CreateFileW`, `CloseHandle`, `WriteFile`, `XInputGetCapabilities`, `XInputSetState`, `RegQueryValueExW`, and `RegSetValueExW` each 5.

**Conclusion:** Stage 2's 66-module count is directly re-confirmed from the raw CSV. The earlier `native_module: NOT FOUND` is strictly a reporting-field alias mismatch: the real column is `dll`; it does not indicate missing module data. The raw data has zero missing DLL and entrypoint cells. Mark this field-profile check GREEN.

**Next action:** Stage 2 profile is now closed. Do not re-run it. Proceed to Stage 3 scanner repair: move all CSV writes after MemberRef/MethodSpec enrichment; improve reference resolution beyond name-only matching; make ImplMap resolution errors and declaration/ImplMap coverage failures affect the exit status. Then validate with a read-only scan of the original Managed directory.


## Verified original Managed inventory and Assembly-CSharp identity (2026-11-14)

User supplied fresh Termux output from the source game directory. Observed facts:

- Managed directory: `./Stranded_Deep_Data/Managed`.
- Directory exists; observed permissions/metadata: `drwxrws---. 2 u0_a237 media_rw 20480 Jan 24 2024`.
- DLL inventory: `find "$MANAGED" -type f -iname '*.dll' | wc -l` returned **119**.
- Original assembly path: `./Stranded_Deep_Data/Managed/Assembly-CSharp.dll`.
- SHA-256: `d5501ae7a2c09499cdf569c5b52578401f039c2f4aa5e7e9240c22faeef214bc`.
- This SHA-256 exactly matches the previously recorded authoritative baseline. The identity check is GREEN.

**Scope/interpretation:** This verifies the directory, DLL file count, and identity of the original Assembly-CSharp.dll only. It does not establish that a Stage 3 scan has run or passed. No source DLL was modified by these read-only commands. Preserve the original Managed directory as read-only for the audit.

**Next action:** Repair the Stage 3 scanner before executing it. Do not restart Stage 1/2 and do not run either uploaded scanner unchanged. Once the scanner is repaired, validate it on the 119-DLL Managed directory with read-only input and separate output files; report actual runtime counts and errors before assigning Stage 3 status.


## Stage 3 scanner repair candidate prepared (2026-11-14)

A repaired candidate scanner was authored locally as `pinvoke_stage3_scan_fixed.py` (SHA-256: `754c331afde0fc55f781f138128271451d3002381731a0ee09e6804eeb25e402`). It is a candidate for user-side Termux validation, NOT yet a runtime-validated Stage 3 scanner.

Implemented changes:
- All CSV output writes occur only after cross-assembly enrichment is complete.
- P/Invoke MemberRef/MethodSpec linking no longer uses managed type + method name alone. It requires normalized assembly identity (name, version, culture, public-key token), declaring type, method name, and exact raw signature bytes.
- Unmatched MemberRefs are not treated as errors or guessed as P/Invokes; ambiguous matches are explicitly marked and left unlinked.
- ImplMap MemberForwarded resolution errors, duplicate mappings, MethodDef mdPinvokeImpl flag read errors, and declaration-to-ImplMap coverage mismatches now fail validation (exit code 2).
- Adds separate declaration, callsite, dynamic-loader, and error CSVs plus a JSON summary.
- Refuses to place output inside the original Managed input directory.

Local checks completed:
- `python -m py_compile` PASS.
- Pure helper self-tests PASS for Assembly/AssemblyRef public-key-token normalization, TypeRef AssemblyRef scope, nested TypeRef naming, exact-signature matching, unmatched references remaining unlinked, ambiguous references remaining unlinked, and CSV-write ordering.
- These tests used dependency stubs for import/helper isolation; they do NOT validate dnfile/dncil behavior on the real 119-DLL Managed directory.

**Next action:** user should place the candidate script in Termux, run syntax/dependency checks, then execute a read-only scan against `./Stranded_Deep_Data/Managed` with output under `$HOME`. Review the actual summary/errors before assigning Stage 3 PASS/FAIL. Original DLLs remain read-only. Do not claim runtime success until Termux output is received and checked.
