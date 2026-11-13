# Project State — CASTAWAY DEPTHS

Last updated: 2026-11-13

## Identity
- Game: CASTAWAY DEPTHS
- Type: original native Android survival game
- Repository: https://github.com/devmyatmin/Read-CASTAWAY-DEPTHS
- Temporary development app: Editor/Control APK
- Release product: standalone final Android game APK
- Historical codename: NYAMIST only

## Non-negotiable direction
- Build the game from the ground up; this is not porting.
- No Stranded Deep Windows executable/runtime, source code, DLLs, or protected assets.
- Treat Stranded Deep only as a high-level reference.
- Create original gameplay logic, names, art, audio, models, world, UI, and story, or document valid licenses for third-party content.

## APK lifecycle
The temporary Editor/Control APK is a development tool only. It controls and tests the engine and game systems. Once the game is complete and validated, create the final standalone APK and retire the temporary editor APK.

## Target hardware
- Initial performance target: user's Mali-G925-class Android hardware.
- Previous probe enumerated Mali-G925-Immortalis MC12, Vulkan 1.3.278, vendor 0x13B5, device 0xD8300010.
- This proves loader/device enumeration only, not surface presentation or rendering.
- Support other Android devices with capability detection and adaptive quality profiles.

## Baseline at repository initialization
- Private repository, default branch main; admin/push access confirmed.
- Existing README was a placeholder.
- No engine, APK, renderer, gameplay implementation, or final OBB archive is claimed to exist yet.

## Validation
Mark each gate PASS only with captured evidence: BUILD, INSTALL, LAUNCH, VULKAN DEVICE, SURFACE, SWAPCHAIN, SUBMIT/PRESENT, RENDER OUTPUT, GAMEPLAY, SAVE/LOAD, DEVICE COMPATIBILITY.


## Current research activity — 2026-11-13
- Active investigation: forensic inventory of P/Invoke declarations across the user's local Stranded Deep managed assemblies.
- Audit package prepared locally as `CASTAWAY_PInvoke_Audit.zip`; awaiting upload into the analysis conversation before metadata inspection.
- The DLL list and hashes are documented in `docs/research/PINVOKE_DLL_AUDIT.md`.
- This is reference-only research. No third-party DLLs or code are to be added to the original CASTAWAY DEPTHS implementation.
- Historical NYAMIST WSI work remains diagnostic/reference material; no production renderer or visible game rendering is claimed.


## 2026-11-14 — P/Invoke Stage 2 grouping update

- User supplied the Termux grouping-audit output from `PINVOKE_GROUP_AUDIT_20261113_235658`.
- Input CSV: 3,791 rows / 21 columns; 66 native-module groups; 17 assembly groups; 33 unique ABI combinations; 1,976 unique exact signatures; 196 repeated DLL + entry-point keys.
- Missing-fields report: header only, zero data rows flagged.
- Repeated DLL + entry-point keys are not automatically errors. The examples include repeated platform API wrappers and legitimate shared entry points; one Rewired `Kernel32.dll!SetNamedPipeHandleState` binding is marked for focused raw-row inspection.
- Signature-shape labels are heuristic groupings, not proof of blittability or ABI safety.
- Detailed evidence and next checks: [P/Invoke Group Audit — Stage 2](research/PINVOKE_GROUP_AUDIT.md).
- Audit remains reference-only. It does not change CASTAWAY DEPTHS implementation status, and no third-party Stranded Deep code, IL, binaries, or assets are to be imported.


## 2026-11-14 — P/Invoke Stage 3: Assembly-CSharp EOS isolation

- User supplied focused report output for `Assembly-CSharp.dll`: 360 P/Invoke declarations, 360 unique declaring methods, and exactly one native module target, `EOSSDK-Win64-Shipping`.
- The first 40 sorted declarations include EOS Achievements, ActiveSession, Auth, and Connect API families.
- This confirms the audited game-assembly P/Invoke declarations are all EOS-targeted. It does **not** prove those APIs are reached during startup or that EOS is mandatory for offline play.
- Next evidence gate: inspect managed IL method bodies and call-site/call-graph references, then distinguish declaration presence from reachable calls and actual native invocation.
- Detailed report: [P/Invoke Group Audit — Stage 2 and Stage 3](research/PINVOKE_GROUP_AUDIT.md).
- Research remains reference-only; no Stranded Deep IL/code, EOS SDK, binaries, or assets are to be reused in CASTAWAY DEPTHS.


## 2026-11-14 — ဝါစိမ်းနီ milestone and next-conversation handoff

- Read `docs/NEXT_CONVERSATION_HANDOFF.md` first when resuming in a new conversation.
- Status key: 🟢 GREEN = evidence-confirmed; 🟡 YELLOW = caution / further inspection; 🔴 RED = required fix.
- Overall forensic stages: Stage 1 🟢 PASS; Stage 2 🟢 PASS; Stage 3 🟡 IN PROGRESS; Stage 4 🔴 INCOMPLETE.
- Stage 3 Scanner GREEN: Python Variant A/B syntax compilation passed; source differences identified and GitHub review recorded.
- Stage 3 Scanner YELLOW: method-name-only association needs stronger metadata identity; ImplMap errors must not exit with code 0; declaration/ImplMap completeness validation is required.
- Stage 3 Scanner RED: CSV is emitted before MemberRef processing; correct the processing/output order.
- No successful end-to-end runtime scan result is recorded yet. Wait for the user's next Termux output; do not infer runtime success from syntax compilation.
- Next: inspect the user's output and the two supplied scanner variants, fix only the identified issues, run read-only against the verified original Assembly-CSharp.dll, validate coverage and outputs, then update status with evidence.
- This P/Invoke work remains reference-only and does not change the original native Android CASTAWAY DEPTHS implementation boundary.


## 2026-11-14 — Stage 2 rerun and Stage 3 scanner source review

- Fresh Termux Stage 2 run processed the audit ZIP and reported 3,791 rows / 21 columns.
- The profile reported Winapi 2,033, Cdecl 1,159, StdCall 599; charset unspecified 3,384, Unicode 177, Ansi 153, Auto 77.
- Its `native_module: NOT FOUND` line is a display/schema mismatch: the source CSV column is named `dll`; the entry-point column is `entrypoint`. Do not treat it as missing native-module data. Confirm actual values with a read-only CSV check.
- Re-reviewed the two newly uploaded Stage 3 scanner variants. Both pass syntax compilation, but neither is runtime-validated.
- Scanner RED: CSV files are written before MemberRef/MethodSpec matching; fix output order.
- Scanner YELLOW: method-name-only association, incomplete MemberForwarded RID resolution, ImplMap errors not fatal, and no declaration-to-ImplMap completeness gate.
- Immediate resume point: inspect `dll` / `entrypoint` values in the extracted Stage 2 CSV; then repair the scanner before running it against the original Managed directory.
- Full evidence: [P/Invoke Group Audit](research/PINVOKE_GROUP_AUDIT.md) and [Next Conversation Handoff](NEXT_CONVERSATION_HANDOFF.md).
- Forensic research remains reference-only; original source DLLs remain read-only, and no Stranded Deep code, IL, binaries, or assets may be reused in CASTAWAY DEPTHS.


## 2026-11-14 — Raw Stage 2 CSV verification closed

- User's read-only Termux check confirms `dll`: 3,791 rows, 0 missing, 66 unique modules.
- `entrypoint`: 3,791 rows, 0 missing, 3,324 unique entrypoints.
- Therefore the previous `native_module: NOT FOUND` is only a report alias mismatch; source CSV uses `dll`. No missing module data.
- Stage 2 field-profile validation is GREEN / closed; do not rerun Stage 2.
- Resume at Stage 3 scanner repair and validation. Exact evidence is in [P/Invoke Group Audit](research/PINVOKE_GROUP_AUDIT.md) and [Next Conversation Handoff](NEXT_CONVERSATION_HANDOFF.md).


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


## 2026-11-14 — Stage 3 CSV callsite review

User supplied the generated CSV summary from `$HOME/PINVOKE_STAGE3_20261114`.

### Observed results
- `il_call_sites.csv`: 695,709 rows; MethodDef 395,641; MemberRef 279,460; MethodSpec 20,608.
- Rows with a populated P/Invoke link (`pinvoke_dll`): **4,318**.
- P/Invoke declaration rows: **3,791**.
- Joined callsite/declaration output: **8,109 rows**. This is a join-row count, not yet a verified unique-callsite count; join multiplicity must be audited.
- Dynamic-loader callsites: **27** (`dlopen` 6, `dlclose` 6, `dlsym` 5, `GetProcAddress` 4, `FreeLibrary` 3, `LoadLibraryEx` 2, `LoadLibraryExW` 1).
- Error CSV: **0 rows**.
- Top declaration modules include `steam_api64` 1,025; `gdiplus` 556; `EOSSDK-Win64-Shipping` 360; `Microsoft.Xbox.Services.141.GDK.C.Thunks` 350; `XGamingRuntimeThunks` 190; `libmono-btls-shared` 188; `kernel32.dll` 125; `steam_api` 115; `user32.dll` 79.
- Sample linked game-assembly calls target EOS wrappers such as `EOS_EResult_IsOperationComplete`, `EOS_ContinuanceToken_ToString`, and `EOS_EpicAccountId_*`.

### Status and limits
- Stage 3 scanner validation: 🟢 PASS (119 DLLs; 3,791 declarations resolved; zero missing/orphan ImplMap; 182,514/182,514 attempted method bodies parsed; zero error CSV rows).
- Linked static P/Invoke callsite subset: 4,318 rows. This is not proof of runtime execution or reachability.
- `memberref_identity_incomplete=300,068` remains 🟡 YELLOW; association coverage is incomplete.
- Two compressed-integer warnings remain unattributed.
- The 8,109 joined rows must not be described as unique calls until join multiplicity is checked.
- Stage 4 remains in progress; no Android compatibility or native dependency availability conclusion is established.

Next: aggregate the linked callsites by native module, caller assembly/type, and target kind; audit join multiplicity; inspect all 27 dynamic-loader callsites in context. Use that evidence to construct the Stage 4 native dependency map. Do not rerun Stage 1/2 or modify original Managed DLLs.


## 2026-11-14 — Stage 3 aggregation and Stage 4 evidence update

- Aggregation report archived at `docs/research/PINVOKE_STAGE3_AGGREGATION_20261114.txt`.
- 4,318 linked static IL callsite rows were grouped by native module, caller assembly/type, and target kind. Largest groups are Steamworks (`steam_api64` 1,321), GDI+ (`gdiplus` 639), EOS (382), Xbox GDK thunks (333), XGamingRuntime thunks (203), and Mono BTLS (189).
- `Assembly-CSharp.dll` has 382 linked callsite rows targeting EOS wrappers. This does not establish runtime reachability or EOS necessity for offline play.
- The attempted join-multiplicity key used empty fields for 3,791 declaration-only rows. The 17 reported duplicate buckets are assembly-level blank-key buckets, not verified duplicate callsites. The 4,318 populated callsite keys each appear once in the supplied multiplicity output. Raw CSV schema audit remains required.
- All 27 dynamic-loader callsites were inspected: Apple/macOS CoreFoundation/CFNetwork paths use `dlopen/dlsym/dlclose`; Windows framework paths use `GetProcAddress/LoadLibraryEx/FreeLibrary`. No Android-specific loader caller appears in this set. Static references do not prove runtime loading.
- Stage 3 scanner/declaration validation remains 🟢 PASS; MemberRef/MethodSpec association remains 🟡 (300,068 incomplete identities); Stage 4 native dependency mapping remains 🟡 IN PROGRESS.
- Next: audit raw CSV headers, per-column populated counts, and sample rows to establish the correct joined-row schema; then complete the Stage 4 platform/native dependency map. Original Managed DLLs remain read-only; research remains reference-only.


## 2026-11-14 — Stage 3 schema audit verified; Stage 4 resume point

- Schema report archived at `docs/research/PINVOKE_STAGE3_SCHEMA_AUDIT_20261114.txt` (244 lines; exact copy verified).
- `il_call_sites.csv`: 695,709 rows; all callsite identity fields populated; P/Invoke link fields populated in 4,318 rows.
- `pinvoke_declarations.csv`: 3,791 rows; all columns populated.
- `pinvoke_callsite_and_declarations.csv`: 8,109 rows consisting of 4,318 linked callsite rows plus 3,791 declaration rows. This is not 8,109 unique calls.
- The 17 repeated blank-key groups from the earlier multiplicity check were declaration-only rows with empty callsite identity fields, not duplicate callsites. Join-schema interpretation is now GREEN / RESOLVED for the supplied files.
- Stage 3 scanner and declaration/ImplMap validation remain GREEN. MemberRef/MethodSpec completeness remains YELLOW (300,068 incomplete identities); two compressed-integer warnings remain unattributed.
- Stage 4 remains YELLOW / IN PROGRESS. Next: build a platform/native-module dependency matrix from 4,318 linked callsites and 3,791 declarations, correlate module-to-caller and entrypoint evidence, distinguish Assembly-CSharp direct references from framework/third-party references, and keep static references separate from runtime reachability and Android availability.
- Handoff is recorded in `docs/NEXT_CONVERSATION_HANDOFF.md`; conversation continuation is appended to `conversation/ChatGPT_Conversation_2026-11-14.txt`. Hidden/collapsed conversation messages are not represented as verbatim text.
- Preserve original Managed DLLs read-only. Do not restart Stage 1/2 or reuse third-party game code/assets.


## 2026-11-14 — Stage 4 EOS managed-wrapper linkage verified

- User supplied the Termux output from `STAGE4_EOS_GAME_DEPENDENCY_AUDIT`, generated from the Stage 3 `il_call_sites.csv`.
- `Assembly-CSharp.dll` → `EOSSDK-Win64-Shipping`: **382 linked static IL callsites**.
- The output reports **382 unique caller-type / caller-method / EOS-entrypoint tuples** across **33** managed caller types; the per-type counts sum to 382.
- The caller types are `Epic.OnlineServices.*` wrapper types. This proves the EOS managed-wrapper/native boundary is embedded in `Assembly-CSharp.dll`; it does **not** yet prove which game-specific gameplay/UI classes call those wrappers.
- This is static linkage only. Runtime execution, startup dependency, offline necessity, and Android binary/API compatibility remain unproven.
- Declaration/callsite module reconciliation: **66** exact declaration module strings vs **65** exact linked-callsite module strings. `comdlg32.dll` is declaration-only (2 declarations, 0 linked calls). Case-only name variants remain distinct; no normalization has been applied.
- Evidence summary: [Stage 4 EOS Game Dependency Audit](research/PINVOKE_STAGE4_EOS_GAME_DEPENDENCY_AUDIT_20261114.md).
- Stage 4 remains 🟡 IN PROGRESS; do not mark complete.
- Next: trace non-wrapper managed IL calls into `Epic.OnlineServices.*` wrapper methods to identify game-level feature use. Do not infer runtime reachability or Android compatibility from static P/Invoke links.


## 2026-11-14 — Stage 4 EOS zero-result diagnostic

User-provided read-only Termux diagnostic against il_call_sites.csv reported 695,709 total rows, 82,804 rows from Assembly-CSharp.dll, and 1,668 rows whose target_type contains Epic; no target assembly string contains Epic. In the game-assembly subset, target assembly is blank in 48,640 rows and the same-assembly identity Assembly-CSharp|0.0.0.0|neutral|null in 34,164 rows. Target kinds: MemberRef 41,144, MethodDef 34,164, MethodSpec 7,496. The sample shows EOS wrapper caller types targeting other Epic.OnlineServices.* wrapper types; one sample is Epic.OnlineServices.Helper → EOS_EResult_IsOperationComplete via EOSSDK-Win64-Shipping.

The prior Game-to-EOS filter's 0 result is consistent with excluding EOS wrapper callers. The current output does not yet establish whether any non-EOS/game-specific caller types target those wrappers, because only 30 sample rows were printed and no full caller classification was produced. EOS wrapper types are contained in Assembly-CSharp.dll, so target_assembly not containing Epic is expected in this output. Static references do not establish runtime invocation, startup/offline necessity, or Android compatibility.

Status: CSV diagnostic 🟢 complete; game-type-to-EOS-wrapper caller map 🟡 unresolved; no confirmed failure. Full report: docs/research/PINVOKE_STAGE4_EOS_ZERO_DIAGNOSTIC_20261114.md.

Next: classify all 1,668 Epic-target rows by EOS-wrapper vs non-EOS caller type; enumerate non-EOS caller type → EOS wrapper type/method edges; count blank/malformed caller types. Read-only only; do not modify original DLLs/CSVs or restart closed stages.


## 2026-11-14 — Stage 4 EOS caller classification completed

User supplied the full read-only classification output for the 1,668 Assembly-CSharp IL rows whose target type contains `Epic`.

Verified partition:
- Total Epic-target rows: **1,668**.
- EOS-wrapper caller rows (`caller_type` begins `Epic.OnlineServices.`): **1,668**.
- Non-EOS caller rows: **0**.
- Blank caller rows: **0**.
- Unique EOS wrapper caller types: **317**.
- Unique non-EOS caller types: **0**.
- Unique game-to-EOS direct edges: **0**.
- Partition reconciles exactly: 1,668 = 1,668 + 0 + 0.

Interpretation:
- Every row in this filtered direct IL callsite set is an EOS-wrapper-internal call. No direct non-EOS/game-specific caller → EOS wrapper edge was found by this method.
- This does not prove the game never uses EOS. Reflection, string-based lookup, delegate/callback wiring, generated bindings, or other indirect paths may not appear as direct call edges in this classification.
- Earlier evidence remains: Assembly-CSharp.dll has 382 linked static callsites into EOSSDK-Win64-Shipping at the managed wrapper/native boundary. That does not prove game-specific invocation, runtime execution, startup necessity, offline-play necessity, or Android compatibility.
- Research report: [Stage 4 EOS Caller Classification](research/PINVOKE_STAGE4_EOS_CALLER_CLASSIFICATION_20261114.md).

Status:
- Classification partition 🟢 PASS.
- Direct game-specific EOS caller linkage 🟡 unresolved / no edge found by this method.
- No confirmed failure 🔴.

Next: read-only audit of indirect EOS paths in managed metadata/IL: reflection APIs, string-based EOS type/method lookup, delegate construction/invocation, and callback registration. Correlate hits to concrete caller methods. Preserve original Managed DLLs and Stage 3 CSVs read-only; do not restart closed stages.


## 2026-11-14 — Stage 4 broad indirect callsite screen

User supplied first-pass read-only screen results from `$HOME/PINVOKE_STAGE3_20261114/il_call_sites.csv`, restricted to `Assembly-CSharp.dll`:
- Reflection-name candidates: **1,586 rows**, **398** unique caller types.
- Delegate/event candidates: **2,980 rows**, **494** unique caller types.
- Callback-prefix candidates: **17 rows**, **5** unique caller types.
- Total matched rows after per-row category de-duplication: **4,579**. Category totals sum to 4,583, so four rows overlap categories.
- Only the first 120 matched rows were displayed.

These are screening candidates, not confirmed EOS indirect calls. The sample includes `Beam.Serialization.Json.JObject.GetField` JSON accessors, `GetTypeFromHandle` metadata access, ordinary game `ldftn` delegates, and standard `Combine`/`Remove`/`Invoke` event patterns. Callback-like prefixes may be unrelated APIs. The method-name/opcode heuristic has false positives and does not robustly resolve target declaring type/assembly. No EOS-specific reflection, string lookup, delegate wiring, or callback registration is confirmed by this output. `il_call_sites.csv` does not contain the `ldstr` operand scan required to test string-based lookup.

Report: `docs/research/PINVOKE_STAGE4_EOS_INDIRECT_CALLSITE_SCREEN_20261114.md`.

Status: Stage 4 remains 🟡 IN PROGRESS. The broad candidate screen ran successfully, but semantic/EOS attribution is unresolved. Do not infer runtime reachability, startup dependency, offline necessity, or Android compatibility.

Next: inspect the original `Assembly-CSharp.dll` IL read-only for `ldstr` operands containing EOS identifiers (`Epic.OnlineServices`, `EOS_`, and known wrapper type/method names), and record enclosing caller type/method plus nearby reflection/delegate/callback instructions. Narrow callsite candidates using resolved target declaring type/assembly; exclude ordinary JSON accessors and game-event delegates. Preserve original DLLs and Stage 3 CSVs read-only.


## 2026-11-14 — Stage 4 EOS targeted screen and Android platform research

### User's latest targeted screen
User successfully generated $HOME/PINVOKE_STAGE3_20261114/STAGE4_EOS_TARGETED_SCREEN_SUMMARY.txt using a read-only scan of il_call_sites.csv, restricted to Assembly-CSharp.dll.

Console output:
- CANDIDATE_ROWS = 4562
- UNIQUE_CALLER_TYPES = 656
- EOS_RELATED_ROWS = 255
- STATUS = COMPLETE

The candidate set is based on broad reflection/delegate method-name matches or ldftn/ldvirtftn opcodes. EOS-related rows are selected by text matching Epic.OnlineServices or EOS_ in caller type/method and target type/method. The 255 rows are candidates only; no semantic EOS attribution has been verified. This screen does not inspect ldstr operands. The complete TXT has not yet been uploaded for row-level review. The user's first attempt had a typo (d instead of cd) and failed before Python ran; the corrected command succeeded. No original DLL or CSV was modified.

### Official EOS Android research
Epic's official documentation confirms that an EOS SDK Android integration path exists:
- Android SDK guide: Android Studio/NDK setup, native library loading, Android-specific initialization, same-thread requirement because EOS is not thread-safe, and foreground/background notifications using EOS_Platform_SetApplicationStatus.
- Unity Android guide documents Android SDK installation, dependencies, initialization, Platform Interface creation, ticking, and shutdown. This is vendor documentation only; it does not change CASTAWAY-DEPTHS engine direction.
- EOS Platform Interface documentation says services require initialization and a Platform Interface handle before accessing other EOS interfaces.
- EOS_Initialize must precede other SDK calls, be called once, and have a matching EOS_Shutdown.
- EOS SDK 1.19.0.3 and later no longer support 32-bit Android or Android OS 6/7; 1.19.0.3+ requires 64-bit Android above OS 7. DynamicSTDC Android binaries are no longer provided; StaticSTDC flavor is required.
- The exact EOS SDK version embedded/used by the audited Stranded Deep build is not known.

Official sources:
- https://dev.epicgames.com/docs/epic-online-services/platforms/android
- https://dev.epicgames.com/docs/epic-online-services/game-engines/eos-unity-integration/eos-unity-integration-android
- https://dev.epicgames.com/docs/epic-online-services/release-notes
- https://dev.epicgames.com/docs/epic-online-services/eos-fundamentals/eos-platform-interface
- https://dev.epicgames.com/docs/api-ref/functions/eos-initialize

### Interpretation
Android EOS SDK availability is confirmed. This does not make EOSSDK-Win64-Shipping usable on Android and does not prove that the audited managed wrappers are Android-compatible. It does not prove runtime use, startup dependency, or offline-play necessity. Android integration feasibility would require the correct Android native SDK package, ABI and SDK version, compatible bindings, lifecycle/thread integration, and product/service configuration.

### Current Stage 4 status
- Android EOS SDK availability: 🟢 confirmed by official Epic docs.
- Targeted-screen TXT creation: 🟢 confirmed by user's console output.
- 255 EOS-related rows: 🟡 candidate-only; row-level review pending.
- Audited game's exact EOS SDK version: 🟡 unknown.
- Runtime reachability, startup dependency, offline necessity, Android compatibility: 🟡 unproven.
- Stage 4 remains IN PROGRESS.

### Next action
1. Review the uploaded STAGE4_EOS_TARGETED_SCREEN_SUMMARY.txt row by row.
2. Perform a read-only original-IL scan for ldstr operands containing EOS identifiers and correlate them to enclosing caller type/method and nearby reflection/delegate/callback instructions.
3. If genuine EOS usage is identified, determine the actual services/interfaces and exact audited SDK version before evaluating Android replacement.
4. Preserve original Managed DLLs and Stage 3 CSVs read-only; do not restart Stage 1/2 or infer runtime reachability from static callsites.
