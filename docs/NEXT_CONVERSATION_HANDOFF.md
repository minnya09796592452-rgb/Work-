# LATEST HANDOFF UPDATE — 2026-11-14 — READ THIS FIRST

## Project authority and fixed boundaries
- Project: CASTAWAY DEPTHS; repository devmyatmin/Read-CASTAWAY-DEPTHS; branch main.
- GitHub is the sole project source of truth. Read this section, docs/PROJECT_STATE.md, and the relevant Stage 4 report before acting.
- User is Project Lead / Architect. Follow the user's lead; keep narration concise and evidence-based.
- CASTAWAY-DEPTHS is an original native Android survival game, not a Stranded Deep port.
- P/Invoke/EOS investigation is reference-only. Never reuse Stranded Deep code, IL, DLLs, binaries, assets, branding, or protected implementation.
- Preserve original Managed DLLs and Stage 3 CSVs read-only. Do not restart Stage 1/2.
- Do not introduce Godot unless the user explicitly reintroduces it. Do not use Winlator.
- Static references do not prove runtime reachability, startup dependency, offline necessity, or Android compatibility.

## Current verified status
- Stage 1 🟢 PASS.
- Stage 2 🟢 PASS / CLOSED.
- Stage 3 scanner/declaration/ImplMap validation 🟢 PASS for 119 DLLs.
- Stage 3 MemberRef/MethodSpec identity completeness 🟡 300,068 incomplete identities; two unattributed compressed-integer warnings remain.
- Stage 3 linked static P/Invoke subset: 4,318 rows; declarations: 3,791; dynamic-loader references: 27.
- Stage 4 🟡 IN PROGRESS; do not mark complete.

## EOS evidence
- Assembly-CSharp.dll has 382 linked static IL callsites to EOSSDK-Win64-Shipping across 33 EOS wrapper caller types.
- Of 1,668 Epic-target rows classified, all 1,668 were EOS-wrapper callers; 0 non-EOS callers, 0 blank callers; 317 unique wrapper caller types; 0 direct game-to-EOS edges in that classification.
- Broad indirect screen: reflection 1,586 rows / 398 caller types; delegate/event 2,980 / 494; callback-prefix 17 / 5; 4,579 unique matched rows after per-row de-duplication (category totals sum to 4,583 due to four overlaps). These are broad candidates with false positives, not confirmed EOS use.
- Current targeted TXT created by user at $HOME/PINVOKE_STAGE3_20261114/STAGE4_EOS_TARGETED_SCREEN_SUMMARY.txt:
  - CANDIDATE_ROWS = 4562
  - UNIQUE_CALLER_TYPES = 656
  - EOS_RELATED_ROWS = 255
  - STATUS = COMPLETE
- The 255 are text-matched candidate rows only. The full TXT has not yet been uploaded or reviewed. The screen does not inspect ldstr operands.
- Epic's official docs confirm an EOS SDK Android integration path. That does not make EOSSDK-Win64-Shipping usable on Android or prove the audited wrappers are Android-compatible.
- Epic's release notes say EOS SDK 1.19.0.3+ requires 64-bit Android above OS 7; Android DynamicSTDC binaries are no longer provided, and StaticSTDC flavor is required. The audited game's exact EOS SDK version is unknown.
- Detailed official-source research: docs/research/PINVOKE_STAGE4_EOS_ANDROID_PLATFORM_RESEARCH_20261114.md.

## Exact resume point — do this next
1. Ask the user to upload STAGE4_EOS_TARGETED_SCREEN_SUMMARY.txt itself. Inspect all 255 EOS-related rows and distinguish true EOS references from false positives. Do not ask for a large terminal paste.
2. Continue the planned read-only original Assembly-CSharp.dll IL scan for ldstr operands containing Epic.OnlineServices, EOS_, and known EOS wrapper type/method names. Record each enclosing caller type/method and nearby reflection/delegate/callback instructions.
3. If actual EOS use is found, identify exact EOS interfaces/services and audited SDK/API version before evaluating Android replacement feasibility.
4. Keep original DLLs/CSVs read-only. Do not claim runtime reachability, startup dependency, offline necessity, or Android compatibility without independent evidence.

Research report: docs/research/PINVOKE_STAGE4_EOS_ANDROID_PLATFORM_RESEARCH_20261114.md
Transcript archive: conversation/ChatGPT_Conversation_2026-11-14.txt

--- PREVIOUS HANDOFF CONTENT (HISTORICAL; LATER SECTIONS BELOW MAY BE SUPERSEDED) ---

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


## Stage 3 Termux runtime validation (2026-11-14) — user-provided output

The repaired candidate `pinvoke_stage3_scan_fixed.py` was executed by the user in Termux against the original `./Stranded_Deep_Data/Managed` directory, with output isolated at `$HOME/PINVOKE_STAGE3_20261114`.

Observed run results:
- Dependency import check: `DEPENDENCIES=PASS` (`dnfile`, `dncil`).
- Scan completed all **119/119 DLLs**.
- `implmap_rows_seen=3,791`; `pinvoke_flagged_methoddefs=3,791`; `pinvoke_declarations_resolved=3,791`.
- `pinvoke_missing_implmap=0`; `implmap_orphan_rows=0`.
- `methoddefs_seen=208,570`; `method_bodies_attempted=182,514`; `method_bodies_parsed=182,514`.
- `call_instruction_rows=695,709`; `pinvoke_declaration_rows=3,791`; `dynamic_loader_rows=27`.
- Fatal validation counters all zero: file errors, method-body parse errors, ImplMap resolution errors, P/Invoke flag read errors, missing ImplMap, orphan ImplMap rows, duplicate MethodDef mappings.
- Scanner reported `validation=PASS` and `STAGE3_VALIDATION=PASS`.
- Two warnings appeared: `invalid compressed int: leading byte: 0xfb`.
- `memberref_identity_incomplete=300,068`; therefore a large set of MemberRef identities could not be fully resolved. These are not counted as fatal by this scanner and must remain an explicit limitation, not be interpreted as successfully linked callsites.

**Status:** Stage 3's implemented scanner validation gate is **GREEN / PASS** for the 119-DLL inventory and the reported declaration/ImplMap and method-body parsing checks. MemberRef/MethodSpec association completeness remains **YELLOW / unresolved** because 300,068 identities were incomplete, and the two compressed-integer warnings need attribution. This PASS does not prove runtime reachability, Android compatibility, or native-library availability.

**Next action:** Inspect the generated CSVs and summary in `$HOME/PINVOKE_STAGE3_20261114`, especially the declaration-to-callsite linkage and unresolved MemberRef records. Do not rerun Stage 1/2 or modify original DLLs. Stage 4 remains incomplete until its evidence-based analysis is performed.


## Stage 3 CSV evidence review — 2026-11-14 (latest user output)

The user ran the requested read-only summary against `$HOME/PINVOKE_STAGE3_20261114`. This section supersedes the earlier “inspect generated CSVs” resume instruction above.

**Observed CSV results**
- `il_call_sites.csv`: **695,709** rows; target kinds: MethodDef **395,641**, MemberRef **279,460**, MethodSpec **20,608**.
- Exact linked P/Invoke callsite rows in `il_call_sites.csv`: **4,318** (`pinvoke_dll` populated).
- `pinvoke_declarations.csv`: **3,791** declarations.
- `pinvoke_callsite_and_declarations.csv`: **8,109** rows. Treat this as joined output rows, not automatically 8,109 unique callsites; validate join multiplicity before using as a call count.
- `dynamic_loader_call_sites.csv`: **27** rows: `dlopen` 6, `dlclose` 6, `dlsym` 5, `GetProcAddress` 4, `FreeLibrary` 3, `LoadLibraryEx` 2, `LoadLibraryExW` 1.
- `stage3_errors.csv`: **0** rows.
- Top declared native modules: `steam_api64` 1,025; `gdiplus` 556; `EOSSDK-Win64-Shipping` 360; `Microsoft.Xbox.Services.141.GDK.C.Thunks` 350; `XGamingRuntimeThunks` 190; `libmono-btls-shared` 188; `kernel32.dll` 125; `steam_api` 115; `user32.dll` 79; `SDL2` 55; `libSDL2-2.0.so.0` 55.
- Sample linked rows are MethodDef calls in `Assembly-CSharp.dll` to EOS P/Invoke wrappers, including `EOS_EResult_IsOperationComplete`, `EOS_ContinuanceToken_ToString`, `EOS_EpicAccountId_IsValid`, `EOS_EpicAccountId_ToString`, and `EOS_EpicAccountId_FromString`.

**Interpretation / limits**
- The 4,318 populated-link rows are direct static IL callsite links produced by the scanner. They are not proof of runtime execution or reachability from startup/gameplay.
- Do not interpret the 8,109 joined rows as unique calls until checking whether each IL callsite expands to multiple declaration rows.
- `memberref_identity_incomplete=300,068` remains YELLOW; exact MemberRef/MethodSpec association is not complete. The 4,318 count must be presented as the scanner's successfully linked subset, not complete coverage.
- The two `invalid compressed int: leading byte: 0xfb` warnings remain unattributed.
- Zero error CSV rows and scanner `validation=PASS` confirm the scanner's own reported validation checks only; they do not remove the MemberRef limitation.

**Current status**
- Stage 1 🟢 PASS; Stage 2 🟢 PASS / CLOSED.
- Stage 3 scanner execution and declaration/ImplMap validation 🟢 PASS.
- Stage 3 reference-association completeness 🟡 YELLOW / unresolved.
- Stage 4 🟡 IN PROGRESS (evidence analysis begun), not PASS.

**Immediate next action**
Run the next read-only Stage 3 CSV aggregation to quantify linked calls by native module, caller assembly, caller type, and target kind; inspect join multiplicity and print all 27 dynamic-loader callsites with caller context. Then use those results to build Stage 4's evidence-based native dependency map. Do not rerun Stage 1/2 or modify original DLLs.


## Standing `@GitHub` protocol — user instruction (2026-11-14)

When the user writes `@GitHub` in this project, treat it as an instruction to synchronize project memory and resume execution, not as a request for an explanation:
1. Read this handoff first, then `docs/PROJECT_STATE.md` and the relevant research/audit document from the repository's current `main` branch.
2. Use the latest recorded resume point and project boundaries. Do not ask the user to repeat information already recorded here.
3. Record the user's newly supplied verified outputs/findings in the relevant GitHub memory documents. Preserve raw counts, caveats, status colors, and exact next action; do not turn inference into fact.
4. Fetch the updated files again and verify the new evidence is present before saying the memory update is complete.
5. Continue with the next concrete task already specified by the handoff, giving the user only the necessary next command or decision. Do not restart closed stages, modify read-only source files, introduce excluded technologies, or perform unrequested redesign.

The user is Project Lead/Architect. Follow the user's lead; minimize narration and maximize verified work.


## 2026-11-14 — Stage 3 aggregation review and Stage 4 dependency evidence

User uploaded `PINVOKE_STAGE3_AGGREGATION_20261114.txt`. The exact 168-line report was archived at `docs/research/PINVOKE_STAGE3_AGGREGATION_20261114.txt`; its content was re-fetched and byte-for-byte text-equal to the uploaded root file before the root copy was deleted.

### Linked static P/Invoke callsites

- Successfully linked IL callsite rows: **4,318**; all target-kind rows are MethodDef.
- By native module, highest counts: `steam_api64` 1,321; `gdiplus` 639; `EOSSDK-Win64-Shipping` 382; `Microsoft.Xbox.Services.141.GDK.C.Thunks` 333; `XGamingRuntimeThunks` 203; `libmono-btls-shared` 189; `kernel32.dll` 167; `steam_api` 116; `user32.dll` 80; `advapi32.dll` 67; `SDL2` 55; `libSDL2-2.0.so.0` 55.
- By caller assembly, highest counts: `com.rlabrecque.steamworks.net.dll` 1,214; `System.Drawing.dll` 784; `XGamingRuntime.dll` 536; `Rewired_Windows.dll` 494; `System.dll` 458; `Assembly-CSharp.dll` 382; `mscorlib.dll` 157; `Rewired_Core.dll` 110; `System.Data.dll` 86.
- The game assembly contributes **382 linked callsite rows** to EOS wrappers. This is direct static IL reference evidence only; it does not establish startup reachability, actual invocation, or EOS being mandatory for offline play.
- Caller-type counts include Steamworks, XBL/XGamingRuntime, System.Drawing, and obfuscated game types. The full breakdown is preserved in the archived report.

### Join-multiplicity audit — interpretation corrected

The script's proposed key fields `caller_method_rid`, `il_offset`, `opcode`, and `target_token` were empty in the 3,791 declaration-only records. Consequently, the reported 17 "duplicate keys" are not verified duplicate callsites: they are 17 assembly-level buckets of declaration rows with blank callsite identity. Their multiplicities sum to **3,791**, exactly the declaration count.

The other **4,318** rows have populated callsite keys and each appears once in the report's multiplicity counter. Thus the available output is consistent with **4,318 unique linked callsite rows plus 3,791 declaration-only rows**, totaling 8,109. Do not report 8,109 as call count, and do not treat the 17 blank-key buckets as duplicate calls. The raw CSV header/field audit is still needed to confirm the exact row schema and why declaration-only rows are present.

### Dynamic-loader callsites — all 27 inspected

The 27 static loader API references divide into clearly platform-specific caller contexts:

- **Apple/macOS context:** CoreFoundation and CFNetwork-related Mono classes: `Mono.CFObject`, `Mono.CFArray`, `Mono.CFDictionary`, `Mono.CFBoolean`, `Mono.Net.CFProxy`, `Mono.Net.CFProxySettings`, and `System.Net.NetworkInformation.MacNetworkChange`. These use `dlopen`, `dlsym`, and `dlclose`.
- **Windows context:** `System.Data.LocalDBAPI`, `System.Data.SqlClient.SNI.LocalDB`, `Microsoft.Win32.UnsafeNativeMethods`, `System.Diagnostics.Win32EventLog`, `SafeLibraryHandle.ReleaseHandle`, and `System.TimeZoneInfo.TryGetLocalizedNameByNativeResource`. These use `GetProcAddress`, `LoadLibraryEx[W]`, and `FreeLibrary`.
- No listed dynamic-loader caller is an Android-specific loader path. These are static references in cross-platform/framework assemblies, not proof that any library was loaded on the user's device or that these methods execute on Android.

### Status

- Stage 1: 🟢 PASS.
- Stage 2: 🟢 PASS / CLOSED.
- Stage 3 scanner execution and declaration/ImplMap validation: 🟢 PASS.
- Stage 3 MemberRef/MethodSpec association completeness: 🟡 YELLOW (300,068 incomplete identities remain); two compressed-integer warnings remain unattributed.
- Stage 3 linked subset: 🟢 4,318 linked MethodDef callsite rows.
- Stage 3 join schema/multiplicity explanation: 🟡 partially resolved; inspect raw headers/blank fields before final closure.
- Stage 4 native dependency map: 🟡 IN PROGRESS, not complete.

### Immediate next action

Run a read-only schema audit on `il_call_sites.csv`, `pinvoke_declarations.csv`, and `pinvoke_callsite_and_declarations.csv`: print headers, non-empty counts for every column, and the first 3 rows of each file. Specifically identify the actual callsite identity columns and determine whether the joined file contains callsite rows plus declaration-only rows. Do not rerun Stage 1/2, change the scanner, or modify original Managed DLLs. After schema confirmation, build the Stage 4 platform/native-module dependency map from the 4,318 linked calls and the 27 loader contexts.


## 2026-11-14 — VERIFIED CURRENT RESUME POINT (READ THIS FIRST)

The user supplied the Stage 3 schema audit report at commit `de877d735fa575f7876d9afef30d01eb728a89dd`. It was archived unchanged at `docs/research/PINVOKE_STAGE3_SCHEMA_AUDIT_20261114.txt` (244 lines; exact content re-fetch verified; blob SHA `4bb0953c807faff032b8b6b0bf77b2fb499619ed`).

### Schema findings — join ambiguity resolved for supplied output
- `il_call_sites.csv`: 695,709 rows. All callsite identity columns are populated in all rows. `pinvoke_dll` and `pinvoke_entrypoint` are populated in 4,318 rows.
- `pinvoke_declarations.csv`: 3,791 rows; all 8 columns populated in every row.
- `pinvoke_callsite_and_declarations.csv`: 8,109 rows. Callsite fields are populated in exactly 4,318 rows; declaration fields are populated in exactly 3,791 rows.
- The combined file therefore contains two distinct row groups: 4,318 linked callsite records plus 3,791 declaration records. It is NOT 8,109 unique calls.
- The earlier 17 repeated blank-key buckets were declaration-only records with blank callsite identity fields, not verified duplicate callsites. The previous join-multiplicity ambiguity is resolved for this supplied schema.

### Current status
- Stage 1 🟢 PASS.
- Stage 2 🟢 PASS / CLOSED.
- Stage 3 scanner execution and declaration/ImplMap validation 🟢 PASS.
- Stage 3 linked static callsite subset 🟢 4,318 rows.
- Stage 3 join schema interpretation 🟢 RESOLVED for the supplied CSVs.
- Stage 3 MemberRef/MethodSpec association completeness 🟡 unresolved: 300,068 incomplete identities.
- Two warnings, `invalid compressed int: leading byte: 0xfb`, remain unattributed.
- Stage 4 native dependency map 🟡 IN PROGRESS; not complete.

### NEXT ACTION — BEGIN STAGE 4 NATIVE DEPENDENCY MAP
Use the archived reports:
- `docs/research/PINVOKE_STAGE3_AGGREGATION_20261114.txt`
- `docs/research/PINVOKE_STAGE3_SCHEMA_AUDIT_20261114.txt`
- `docs/research/PINVOKE_GROUP_AUDIT.md`

Build an evidence-based dependency matrix from the 4,318 linked callsites and 3,791 declarations:
1. Classify native modules by platform family (Windows, Apple/macOS, Linux/POSIX, cross-platform, unknown); mark uncertain items unknown rather than guessing.
2. Correlate modules with caller assembly/type/method, managed declaration, entrypoint, and counts wherever source data supports it.
3. Separate game-assembly direct references from third-party/framework references (Steamworks, EOS, Xbox/XGamingRuntime, Rewired, System.Drawing/GDI+, Mono BTLS, SDL, etc.).
4. Distinguish direct static references from runtime reachability and actual dynamic loading. Do not claim a module is required for startup/gameplay or available on Android without independent evidence.
5. Keep 300,068 incomplete MemberRef identities and the two unattributed compressed-integer warnings explicitly YELLOW.
6. Produce the dependency matrix and highest-priority unresolved questions. Do not change the scanner or original Managed DLLs.

Project boundaries remain unchanged: CASTAWAY DEPTHS is an original native Android survival game; this P/Invoke analysis is reference-only. Do not restart Stage 1/2, reuse third-party code/assets, or introduce excluded technologies. User is Project Lead; follow their lead and keep narration concise.

The conversation continuation was appended to `conversation/ChatGPT_Conversation_2026-11-14.txt`; the transcript explicitly notes that hidden/collapsed messages are not fabricated as verbatim content.


## 2026-11-14 — Stage 4 EOS wrapper linkage verified; current resume point

User supplied `hhh.txt` containing the Termux output of `STAGE4_EOS_GAME_DEPENDENCY_AUDIT`.

Verified findings:
- `Assembly-CSharp.dll` has **382** linked static callsites to `EOSSDK-Win64-Shipping`.
- The report contains **382 unique** managed caller-type / caller-method / native-entrypoint tuples across **33** `Epic.OnlineServices.*` managed wrapper types; grouped counts sum to 382.
- Important interpretation: the P/Invoke callers shown are EOS managed wrapper types inside `Assembly-CSharp.dll`, not yet identified game-specific gameplay/UI callers. This establishes the wrapper-to-native boundary, not the game-feature call graph.
- Declaration modules = **66**; linked-callsite modules = **65**. `comdlg32.dll` has 2 declarations and no linked callsites. Case-only module-name spellings remain separate raw strings.
- Full verified totals and all 33 caller-type counts are archived in `docs/research/PINVOKE_STAGE4_EOS_GAME_DEPENDENCY_AUDIT_20261114.md`.
- Static linkage does not prove runtime invocation, startup requirement, offline-mode requirement, or Android compatibility.

### Current status
- Stage 1 🟢 PASS.
- Stage 2 🟢 PASS / CLOSED.
- Stage 3 scanner and declaration/ImplMap validation 🟢 PASS.
- Stage 3 MemberRef/MethodSpec identity completeness 🟡 unresolved (300,068 incomplete identities); two compressed-integer warnings remain unattributed.
- Stage 4 🟡 IN PROGRESS; EOS wrapper/native linkage verified, game-level wrapper callers not yet mapped.

### NEXT ACTION — TRACE GAME TYPES INTO EOS WRAPPERS
Run a read-only analysis of `il_call_sites.csv` for ordinary managed call instructions in `Assembly-CSharp.dll` whose target type begins `Epic.OnlineServices.`. Group by calling game type/method and target wrapper type/method. This identifies which game-specific systems actually reference EOS wrappers. Do not modify original Managed DLLs, rerun closed stages, or claim runtime reachability/Android compatibility.


## 2026-11-14 — Stage 4 EOS zero-result diagnostic; current resume point

User supplied Termux output from a read-only diagnostic over $HOME/PINVOKE_STAGE3_20261114/il_call_sites.csv. Full summary archived at docs/research/PINVOKE_STAGE4_EOS_ZERO_DIAGNOSTIC_20261114.md.

Verified output:
- Total CSV rows: **695,709**.
- Assembly-CSharp.dll rows: **82,804**.
- Rows with target_type containing Epic: **1,668**.
- Rows with target_assembly containing Epic: **0**.
- Epic-target rows by either field: **1,668**.
- In game-assembly rows, target assembly is empty in 48,640 rows and Assembly-CSharp|0.0.0.0|neutral|null in 34,164 rows; target kinds: MemberRef 41,144, MethodDef 34,164, MethodSpec 7,496.
- The displayed Epic-target sample shows EOS wrapper callers (Epic.OnlineServices.*) targeting other EOS wrapper types. One sample shows Epic.OnlineServices.Helper calling native EOS_EResult_IsOperationComplete via EOSSDK-Win64-Shipping.

Interpretation:
- The previous Game-to-EOS filter returned 0 after excluding callers whose type begins Epic.OnlineServices. This is consistent with the displayed wrapper-internal sample; it is not evidence that EOS is absent.
- The 1,668 count is not yet split across EOS-wrapper and non-wrapper callers. The first-30 sample cannot establish the complete caller population.
- EOS wrapper types are defined in the same Assembly-CSharp.dll; therefore target_assembly does not contain an Epic string. This is not a missing dependency signal.
- Static IL references do not prove runtime invocation, startup dependency, offline necessity, or Android compatibility.

Status: CSV diagnostic 🟢 complete; game-type-to-EOS-wrapper call graph 🟡 unresolved; no confirmed failure 🔴.

**Next action:** Run a read-only full classification of all 1,668 Epic-target rows: count EOS-wrapper callers vs non-EOS callers; list every non-EOS caller type with target wrapper type/method; separately count blank/malformed caller types. Do not modify original DLLs/CSVs, restart closed stages, or infer runtime reachability.


## 2026-11-14 — Latest resume point: EOS caller classification

User's latest read-only classification of all 1,668 Assembly-CSharp IL rows whose target type contains `Epic` returned:
- Total: 1,668
- EOS-wrapper callers: 1,668
- Non-EOS callers: 0
- Blank callers: 0
- Unique EOS wrapper caller types: 317
- Unique non-EOS caller types: 0
- Unique game-to-EOS direct edges: 0

The row partition reconciles exactly. All filtered calls are within `Epic.OnlineServices.*` wrapper types. Therefore no direct game-specific caller → EOS-wrapper edge was found by this static callsite classification. Do not conclude EOS is unused: reflection, string-based lookup, delegates, callback registration, or other indirect paths remain possible. The separate 382 Assembly-CSharp → EOS native P/Invoke callsites represent the wrapper/native boundary and do not independently establish gameplay invocation.

Detailed evidence: `docs/research/PINVOKE_STAGE4_EOS_CALLER_CLASSIFICATION_20261114.md`.

**Next action:** perform a read-only indirect EOS usage audit across managed metadata/IL. Search reflection APIs and string constants for EOS wrapper type/method names; inspect delegate construction/invocation and callback registration; map any hits to concrete caller methods. Keep original Managed DLLs and Stage 3 CSVs read-only. Do not restart Stage 1/2 or infer runtime reachability, offline necessity, or Android compatibility.


## Next-chat exact first-pass command — indirect EOS callsite screen

Run this read-only CSV screen first. It uses the verified `il_call_sites.csv` schema and restricts to `Assembly-CSharp.dll`; it counts reflection, delegate, and callback-like call targets and prints up to 120 candidate rows. This is only a screening pass: it cannot find `ldstr` string constants or prove callback execution. After reviewing its output, inspect the original IL for string-based EOS type/method lookup and any indirect wiring candidates. Original DLLs and Stage 3 CSVs remain read-only.

```bash
cd "$HOME/PINVOKE_STAGE3_20261114" && python - <<'PY'
import csv
from collections import Counter

path = "il_call_sites.csv"
reflection = {
    "GetType", "GetMethod", "GetMethods", "GetMember", "GetMembers",
    "GetProperty", "GetProperties", "GetField", "GetFields",
    "Invoke", "InvokeMember", "CreateInstance", "CreateDelegate",
    "GetConstructor", "GetConstructors", "GetEvent", "GetEvents",
    "AddEventHandler", "RemoveEventHandler", "GetCustomAttributes",
    "GetCustomAttribute", "GetTypeFromHandle", "GetMethodFromHandle",
    "GetInterface", "GetInterfaces", "IsAssignableFrom"
}
delegates = {
    "DynamicInvoke", "BeginInvoke", "EndInvoke", "Combine", "Remove",
    "CreateDelegate", "GetDelegateForFunctionPointer",
    "GetFunctionPointerForDelegate"
}
callback_prefixes = ("AddNotify", "RemoveNotify", "Register", "Unregister")
rows = []
counts = Counter()
caller_types = { "reflection": set(), "delegate": set(), "callback": set() }

with open(path, newline="", encoding="utf-8") as f:
    for r in csv.DictReader(f):
        if r.get("assembly") != "Assembly-CSharp.dll":
            continue
        method = (r.get("target_method") or "").split("::")[-1]
        opcode = (r.get("opcode") or "").lower()
        categories = []
        if method in reflection:
            categories.append("reflection")
        if method in delegates or opcode in {"ldftn", "ldvirtftn"}:
            categories.append("delegate")
        if method.startswith(callback_prefixes):
            categories.append("callback")
        if not categories:
            continue
        for cat in categories:
            counts[cat] += 1
            caller_types[cat].add(r.get("caller_type", ""))
        rows.append((categories, r))

print("STAGE4_EOS_INDIRECT_CALLSITE_SCREEN")
for cat in ("reflection", "delegate", "callback"):
    print(f"{cat.upper()}_CANDIDATE_ROWS = {counts[cat]}")
    print(f"{cat.upper()}_UNIQUE_CALLER_TYPES = {len(caller_types[cat])}")
print("TOTAL_MATCHED_ROWS =", len(rows))
print("=== CANDIDATE CALLSITES (first 120) ===")
for cats, r in rows[:120]:
    print(
        ",".join(cats),
        "|", r.get("caller_type", ""),
        "|", r.get("caller_method", ""),
        "|", r.get("il_offset", ""),
        "|", r.get("opcode", ""),
        "|", r.get("target_kind", ""),
        "|", r.get("target_type", "") or "<unresolved>",
        "|", r.get("target_method", "")
    )
print("STAGE4_EOS_INDIRECT_CALLSITE_SCREEN_END")
PY
```


## 2026-11-14 — Stage 4 broad indirect callsite screen; latest resume point

User supplied first-pass read-only screen results from `$HOME/PINVOKE_STAGE3_20261114/il_call_sites.csv`, restricted to `Assembly-CSharp.dll`:
- Reflection-name candidates: **1,586 rows**, **398** unique caller types.
- Delegate/event candidates: **2,980 rows**, **494** unique caller types.
- Callback-prefix candidates: **17 rows**, **5** unique caller types.
- Total matched rows after per-row category de-duplication: **4,579**. Category totals add to 4,583, so four rows overlap categories; do not sum category totals as unique rows.
- Only the first 120 candidate rows were printed.

Interpretation:
- These are broad candidates, not confirmed EOS indirect calls.
- Sample `Beam.Serialization.Json.JObject.GetField` calls are JSON field access, not evidence of reflection.
- `GetTypeFromHandle` alone is metadata/type-handle access, not EOS lookup.
- Sample `ldftn` targets are ordinary game delegates/event handlers; `Combine`, `Remove`, and `Invoke` are common C# event/delegate patterns.
- Callback-like name prefixes may be unrelated registration APIs.
- The sample demonstrates substantial false-positive potential; no EOS-specific reflection, string lookup, delegate wiring, or callback registration is confirmed by this output.
- This call-instruction CSV screen does not inspect `ldstr` operands, so string-based EOS lookup remains untested.

Detailed report: `docs/research/PINVOKE_STAGE4_EOS_INDIRECT_CALLSITE_SCREEN_20261114.md` (commit `630733bf626a694520822168910763a93bd444c3`).

Status: Stage 4 remains 🟡 IN PROGRESS. Broad candidate screen executed; semantic/EOS attribution unresolved. Do not mark Stage 4 complete or infer runtime reachability, startup dependency, offline necessity, or Android compatibility.

**NEXT ACTION:** inspect original `Assembly-CSharp.dll` IL read-only for `ldstr` operands containing EOS identifiers (e.g. `Epic.OnlineServices`, `EOS_`, wrapper type/method names), recording enclosing caller type/method and nearby reflection/delegate/callback instructions. Separately narrow callsite candidates by resolving actual target declaring type/assembly and excluding ordinary JSON accessors and game-event delegates. Preserve original DLLs and Stage 3 CSVs read-only.
