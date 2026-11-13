# P/Invoke Group Audit — Stage 2

**Date:** 2026-11-14 (user-provided Termux output; audit folder timestamp: 2026-11-13 23:56:58)  
**Input:** `managed_pinvoke_all_3791.csv` from the full metadata audit  
**Local output folder:** `$HOME/PINVOKE_GROUP_AUDIT_20261113_235658`  
**Purpose:** group and classify the already-extracted P/Invoke metadata. Reference-only forensic work; no Stranded Deep payload is imported into CASTAWAY DEPTHS.

## 1. Group audit totals

| Metric | Result |
|---|---:|
| CSV rows | 3,791 |
| CSV columns | 21 |
| Native DLL/module groups | 66 |
| Managed assembly groups | 17 |
| Unique ABI combinations | 33 |
| Unique exact signatures | 1,976 |
| Duplicate native DLL + entry-point keys | 196 |
| Missing-field data rows | 0 |

The missing-fields TSV contains only its header (one line total), so none of the 3,791 records was reported with missing fields by this audit script.

## 2. Native-module concentration

Largest module groups reported:

| Native module | Declarations |
|---|---:|
| steam_api64 | 1,025 |
| gdiplus | 556 |
| EOSSDK-Win64-Shipping | 360 |
| Microsoft.Xbox.Services.141.GDK.C.Thunks | 350 |
| XGamingRuntimeThunks | 190 |
| libmono-btls-shared | 188 |
| kernel32.dll | 125 |
| steam_api | 115 |
| user32.dll | 79 |
| SDL2 | 55 |
| libSDL2-2.0.so.0 | 55 |
| advapi32.dll | 53 |
| Rewired_DirectInput | 52 |
| odbc32.dll | 45 |

These are metadata declaration counts, not runtime call counts or proof that every named library is present in the installation.

## 3. ABI and mapping dimensions

The original inventory reports:

- Calling convention: Winapi 2,033; Cdecl 1,159; StdCall 599.
- Character set: unspecified 3,384; Unicode 177; Ansi 153; Auto 77.
- Group audit: 33 unique combinations across the ABI fields included by the script.

The group audit's ABI matrix includes the raw mapping-flag dimension. Example leading rows:

| Calling convention | Charset | Mapping flags | Rows |
|---|---|---:|---:|
| Winapi | Not specified | 256 | 1,621 |
| Cdecl | Not specified | 512 | 958 |
| StdCall | Not specified | 768 | 554 |
| Winapi | Not specified | 320 | 122 |
| Cdecl | Ansi | 514 | 110 |
| Cdecl | Not specified | 513 | 91 |
| Winapi | Unicode | 260 | 59 |

The mapping-flag values are raw metadata flags as rendered by the audit script. They should be decoded against the ECMA-335 / .NET interop flag definitions before using them to generate any ABI shim. Calling-convention and charset totals are separate marginal totals; do not add them together.

## 4. Duplicate DLL + entry-point analysis

The script found 196 repeated `(dll, entrypoint)` keys. This is **not automatically an error**: several managed declarations can legitimately bind to the same native function.

Observed examples:
- Apple Carbon/CoreFoundation methods in System.Drawing.dll and System.dll, including 32/64-bit managed wrappers for the same native entry point.
- EOS release functions declared by multiple managed wrapper types in Assembly-CSharp.dll.
- Windows APIs in Rewired_Windows.dll, including repeated obfuscated methods and repeated User32 imports.
- SDL2 `SDL_GameControllerMappingForGUID` repeated within Rewired_Core.dll.

One notable item is `Kernel32.dll!SetNamedPipeHandleState`, shown twice under the same obfuscated declaring type/method in Rewired_Windows.dll. A direct check of the source CSV rows found two distinct decoded signatures: `int32 (native int, int32&, int32&, int32&)` and `int32 (native int, int32&, native int, native int)`. Their raw signature blobs also differ (`00040818100810081008` vs `0004081810081818`), so these are distinct metadata signatures sharing the same native entry point—not duplicate identical records. A separate `System.Core.dll` declaration targets the lowercase `kernel32.dll` module name with its own SafePipeHandle signature and different mapping flags.

**Interpretation:** use the full duplicate report `08_duplicate_entrypoints.tsv` to triage by exact managed signature, declaring type, assembly, and mapping flags. Do not deduplicate or delete declarations solely because the native DLL and entry-point strings match.

## 5. Signature-shape report

The script groups signature tags such as POINTER, BYREF, VALUETYPE, CLASS, ARRAY, STRING, and OTHER. These tags are heuristic and may overlap at the parameter/signature level; they are not a count of mutually exclusive ABI types.

The largest displayed shape counts include:
- POINTER: 1,004
- POINTER+VALUETYPE: 511
- POINTER+BYREF+VALUETYPE: 183
- VALUETYPE: 116
- POINTER+ARRAY+VALUETYPE: 105
- OTHER: 103
- POINTER+STRING: 86
- ARRAY+VALUETYPE: 44
- POINTER+BYREF+ARRAY+VALUETYPE: 39

Do not infer blittability, pinning safety, ownership, lifetime, or Android compatibility from these coarse tags. Exact decoded signatures and call-site semantics are required.

## 6. Android relevance classification — provisional

| Area | Evidence in metadata | Current classification |
|---|---|---|
| EOS / Epic Online Services | 360 Assembly-CSharp declarations target EOSSDK-Win64-Shipping | Game assembly interop; runtime reachability and offline necessity not established |
| Steamworks | 921 declarations in com.rlabrecque.steamworks.net.dll; Windows steam_api64 PE exists in archive | Windows Steam integration; not an Android drop-in |
| Xbox GDK | XGamingRuntime.dll and GDK thunk module references | Platform-specific Windows/Xbox integration; not Android drop-in |
| Rewired Windows / DirectInput | 513 Rewired_Windows declarations; Rewired_DirectInput PE imports Windows input dependencies | Windows input path; not Android touch/input implementation |
| Rewired Core / SDL | 55 SDL2 and 55 libSDL2-2.0.so.0 declarations | Multiple platform bindings exist in metadata; Android availability and selected runtime path remain unproven |
| System.Drawing / GDI+ | 696 declarations, including 556 gdiplus | Desktop drawing interop; not assumed available on Android |
| Windows OS APIs | Kernel32, User32, advapi32, HID, setupapi and related modules | Windows-specific declarations; require platform guards or independent platform implementation in any compatible runtime |
| Mono / TLS | libmono-btls-shared and MonoPosixHelper references | Runtime-specific native dependencies; inspect the actual Android runtime separately |

This table classifies the audited Stranded Deep metadata only. It is not an implementation plan to port or reuse those components in CASTAWAY DEPTHS. The current CASTAWAY DEPTHS repository policy is original native Android game development; the audit is reference-only and must not introduce third-party game code, binaries, IL, assets, or protected implementation.

## 7. Evidence limits and next checks

This grouping pass does not establish:
- which P/Invoke declarations are reached during startup or offline gameplay;
- whether declarations are behind platform-specific compilation/runtime branches;
- whether native libraries are dynamically loaded;
- whether SDL declarations resolve to an Android-compatible library;
- whether any signature can be safely marshalled by an Android runtime.

### Next focused checks

1. Inspect all 196 duplicate-key groups by exact signature and raw metadata flags; begin with the repeated Kernel32 SetNamedPipeHandleState row.
2. Build a module-by-assembly cross-reference for the 66 ModuleRef targets and mark each target as Windows-only, Apple-only, Linux/Unix, cross-platform, or unknown based on evidence—not name guessing.
3. Separate game-assembly P/Invokes (Assembly-CSharp.dll) from framework/third-party declarations.
4. For the 360 EOS declarations, inspect managed call sites and control flow to determine initialization gates and whether offline gameplay can proceed without EOS. Do not infer optionality from declaration presence alone.
5. Keep all results in the reference-only audit records; do not copy the audited game's code, IL, binaries, or assets into CASTAWAY DEPTHS.

## 8. Generated local reports

The user's Termux script produced:
`01_native_dll_groups.tsv`, `02_assembly_groups.tsv`, `03_calling_conventions.tsv`, `04_charsets.tsv`, `05_mapping_flags.tsv`, `06_abi_matrix.tsv`, `07_assembly_dll_abi.tsv`, `08_duplicate_entrypoints.tsv`, `09_exact_signature_groups.tsv`, `10_signature_shapes.tsv`, `11_missing_fields.tsv`, `12_all_rows_sorted.csv`, and `SUMMARY.txt`.

These TSV/CSV files remain in the user's Termux output directory unless separately uploaded. This document records the pasted summary/output and does not claim those files themselves are committed.


## 9. Stage 3 — Assembly-CSharp / EOS declaration isolation (2026-11-14)

User supplied the output of `13_game_assembly_pinvoke.tsv`.

| Metric | Result |
|---|---:|
| Assembly-CSharp P/Invoke declarations | 360 |
| Unique declaring methods | 360 |
| Native module groups | 1 |
| Sole native module | `EOSSDK-Win64-Shipping` |

All 360 P/Invoke declarations in `Assembly-CSharp.dll` target `EOSSDK-Win64-Shipping`. The first 40 sorted declarations show EOS interface families including Achievements, ActiveSession, Auth, and Connect. Examples include achievement query/copy/unlock/notification APIs; active-session inspection; login/logout/token/login-status APIs; and Connect authentication notifications.

**What this establishes:** the game assembly's audited static P/Invoke surface is exclusively EOS-targeted. It does not establish that every API is called, that EOS initializes during startup, or that offline gameplay is blocked without EOS. Declaration rows are not call-site evidence.

**Next forensic gate:** inspect managed IL method bodies and references to identify which EOS wrappers are called from startup, scene loading, save/load, and offline gameplay paths. Distinguish declaration presence, managed call-site references, reachable call paths, and actual native invocation at runtime. Do not label EOS optional or mandatory until call-graph and runtime evidence support that conclusion.

Keep this research reference-only; no EOS SDK, Stranded Deep IL/code, binaries, or assets are to be reused in CASTAWAY DEPTHS. Local report: `$HOME/PINVOKE_GROUP_AUDIT_20261113_235658/13_game_assembly_pinvoke.tsv`.


## 10. IL inspection environment check (2026-11-14)

User ran a search for `Assembly-CSharp.dll` under `$HOME/PINVOKE_STAGE2_20261113_235016/extracted`; it returned no file. The same Termux check found none of `monodis`, `ilspycmd`, `ildasm`, or `dotnet` on PATH. This is expected if that extracted directory contains only the audit ZIP's CSV/report outputs rather than the original managed game assemblies. No conclusion about the original game installation's DLL availability can be drawn yet.

Next action: locate the original `Assembly-CSharp.dll` under the user's actual game directory. Only after confirming its exact path and file metadata should an IL inspection approach/tool be selected. Do not install tooling or alter the source DLL before locating it.


## 11. Original Assembly-CSharp.dll located (2026-11-14)

User located the original managed game assembly at:
`/storage/emulated/0/Download/PC GAMES/StrandedDeep/Stranded_Deep_Data/Managed/Assembly-CSharp.dll`

Reported file metadata: 3.3 MiB; timestamp shown by Termux `Jan 24 2024`; SHA-256 `d5501ae7a2c09499cdf569c5b52578401f039c2f4aa5e7e9240c22faeef214bc`.

This resolves the earlier false start: the audit ZIP extraction did not contain the original DLL, but the actual game installation does. Next inspect the DLL read-only. First verify the hash again and check available Python/runtime capabilities; do not modify, patch, or replace the source DLL. The next analysis should establish actual managed IL call-site references to the EOS P/Invoke declarations, not infer reachability from declaration counts alone.


## 12. IL analysis environment verified (2026-11-14)

Original assembly: `/storage/emulated/0/Download/PC GAMES/StrandedDeep/Stranded_Deep_Data/Managed/Assembly-CSharp.dll`.

Verified metadata: 3,432,448 bytes; modified 2024-01-24 05:51:28 (+0630); SHA-256 `d5501ae7a2c09499cdf569c5b52578401f039c2f4aa5e7e9240c22faeef214bc`.

Termux: Python 3.14.10; pip 26.3; `dnfile`, `dncil`, and `pefile` are importable; `lief` is unavailable. The Managed directory contains `Assembly-CSharp-firstpass.dll`, the target `Assembly-CSharp.dll`, and supporting Rewired, Photon, GDK utility, and Unity managed assemblies.

Status: GREEN to begin read-only managed metadata and IL call-site analysis with the available Python packages. Keep the original DLL untouched. First validate dnfile/dncil compatibility against this exact file, then enumerate MethodDef bodies and call operands referencing the 360 EOS P/Invoke declarations. Do not classify EOS as optional or mandatory until call-site, startup/control-flow, and runtime evidence supports the conclusion.


## 13. dnfile/dncil API and target metadata validation (2026-11-14)

User ran the compatibility check in Termux against the original Assembly-CSharp.dll.

Verified Python package locations:
- dnfile: /data/data/com.termux/files/usr/lib/python3.14/site-packages/dnfile/__init__.py
- dncil: /data/data/com.termux/files/usr/lib/python3.14/site-packages/dncil/cil/body/__init__.py

Verified parser API:
- CilMethodBody(reader) constructor signature: (reader: 'CilMethodBodyReaderBase')
- CilMethodBodyReaderBytes(bs: 'bytes') constructor signature.
- Reader implementation: dncil/cil/body/reader.py.

Target DLL metadata parsed successfully with dnfile.dnPE:
| Metadata item | Count / result |
|---|---:|
| CLI metadata | Present |
| MethodDef rows | 26,143 |
| MemberRef rows | 6,078 |
| ModuleRef rows | 1 |
| ImplMap / P/Invoke rows | 360 |

**Result: PASS.** The original DLL is readable by the installed metadata/IL tooling, and its 360 P/Invoke rows match the previously isolated Assembly-CSharp EOS declaration count. This validates the parser environment and target metadata only; no IL call-site scan has been run yet, so no wrapper call references or runtime reachability conclusions are claimed.

**Next action:** build and run a read-only IL scanner using dnfile plus dncil.cil.body.reader.CilMethodBodyReaderBytes, map the 360 ImplMap methods to MethodDef tokens, and enumerate IL operands (call, callvirt, ldftn, ldvirtftn, and relevant function-pointer forms) that reference those methods. Record direct references separately from transitive call-chain inference. Preserve the source DLL unchanged and keep all outputs in a separate $HOME audit directory.

## 14. ဝါစိမ်းနီ Stage 3 Scanner status (2026-11-14)

**Status key:** 🟢 GREEN = confirmed; 🟡 YELLOW = caution / further inspection; 🔴 RED = required fix.

| Marker | Scanner finding | Current state |
|---|---|---|
| 🟢 GREEN | Python Variant A and Variant B syntax compilation | PASS |
| 🟢 GREEN | Differences between the two supplied variants identified; GitHub review recorded | PASS |
| 🟡 YELLOW | Method-name-only association | Strengthen association using metadata identity/token information |
| 🟡 YELLOW | ImplMap processing errors can return exit code 0 | Must return a non-zero failure status |
| 🟡 YELLOW | Declaration-to-ImplMap completeness check | Required; not yet validated |
| 🔴 RED | CSV is emitted before MemberRef processing | Fix processing/output order before runtime validation |

No successful end-to-end runtime scan result is recorded at this checkpoint. Syntax compilation does not prove runtime correctness or call-site coverage. The user indicated that the next Termux output would be supplied; inspect that output before claiming runtime results.

**Resume gate:** review the supplied scanner variants and next user output; fix the RED/YELLOW items; run read-only against the verified original Assembly-CSharp.dll; validate ImplMap/declaration coverage, MemberRef mapping, IL operand references, exit code, and generated reports. Only then may Stage 3 be marked GREEN. Stage 4 remains INCOMPLETE.


## 15. Stage 2 rerun — raw schema profile and scanner source review (2026-11-14)

User supplied a fresh Termux run of `$HOME/pinvoke_stage2.sh` against `/storage/emulated/0/Download/CASTAWAY_PInvoke_Full_Audit.zip`.

### Observed metadata profile

| Metric | Result |
|---|---:|
| CSV rows | 3,791 |
| CSV columns | 21 |
| Winapi | 2,033 |
| Cdecl | 1,159 |
| StdCall | 599 |
| Charset unspecified | 3,384 |
| Unicode | 177 |
| Ansi | 153 |
| Auto | 77 |

The output's `native_module: NOT FOUND` is a field-name mismatch: the printed CSV schema contains `dll`, not `native_module`. The actual native module values should be read from `dll`; the entry-point column is `entrypoint`. This is not evidence that the CSV lacks native module data. The previous grouped audit remains the verified baseline for 66 distinct native modules, 17 assemblies, 33 ABI combinations, 1,976 exact signatures, 196 repeated DLL/entry-point keys, and zero missing-field rows.

The output's signature-shape counts are overlapping tags (for example, a signature may contain both pointer and valuetype forms); they are not mutually exclusive buckets and must not be summed.

### Uploaded Stage 3 scanner files reviewed

Both uploaded files pass Python syntax compilation:
- `pinvoke_stage3_scan(2).py` — SHA-256 `347b2989687d5220fc5101e50bc21512b134497d7e6efc5db61ed967e0a130bf`.
- `pinvoke_stage3_scan(1)(1).py` — SHA-256 `f97cda39548cf76700b178edb54d23c6008de489d34d1bc6fb56a999dd944601`.

The second variant adds an object-identity lookup for the MethodDef row, then falls back to `rid_of(mr)`; it does not yet prove stable resolution of the MemberForwarded coded index.

| Marker | Finding |
|---|---|
| 🟢 GREEN | Both uploaded scripts pass `python -m py_compile`. |
| 🟡 YELLOW | Variant B's object-identity fallback is an incremental improvement only; robust MemberForwarded table/RID resolution is still required. |
| 🟡 YELLOW | MemberRef/MethodSpec association relies on declaring type + method name only. |
| 🟡 YELLOW | ImplMap resolution exceptions are counted but are not included in the non-zero exit condition. |
| 🟡 YELLOW | No declaration-to-ImplMap completeness check is performed. |
| 🔴 RED | CSV files are written before MemberRef/MethodSpec matching mutates the in-memory rows; the later matching results are not persisted to the emitted CSVs. |

No Stage 3 runtime success is established by the Stage 2 output or by syntax compilation.

**Next action:** inspect the extracted CSV's real `dll` and `entrypoint` columns read-only, verify distinct-module count and missing values, then repair the scanner defects before any Stage 3 execution against the original Managed directory.


## Verified Termux CSV column check (2026-11-14)

User ran a read-only Python check against the extracted Stage 2 CSV. Exact observed results:

- Columns include `dll` and `entrypoint` (21 columns total).
- `dll`: 3,791 rows; 0 missing; **66 unique values**.
- `entrypoint`: 3,791 rows; 0 missing; **3,324 unique values**.
- Top DLL counts: `steam_api64` 1,025; `gdiplus` 556; `EOSSDK-Win64-Shipping` 360; `Microsoft.Xbox.Services.141.GDK.C.Thunks` 350; `XGamingRuntimeThunks` 190; `libmono-btls-shared` 188; `kernel32.dll` 125; `steam_api` 115; `user32.dll` 79; `SDL2` 55; `libSDL2-2.0.so.0` 55; `advapi32.dll` 53; `Rewired_DirectInput` 52; `odbc32.dll` 45; CoreFoundation framework path 38.
- Top entrypoint counts begin: `XInputGetState` 10; `ReadFile` 7; `CreateFileW`, `CloseHandle`, `WriteFile`, `XInputGetCapabilities`, `XInputSetState`, `RegQueryValueExW`, and `RegSetValueExW` each 5.

**Conclusion:** Stage 2's 66-module count is directly re-confirmed from the raw CSV. The earlier `native_module: NOT FOUND` is strictly a reporting-field alias mismatch: the real column is `dll`; it does not indicate missing module data. The raw data has zero missing DLL and entrypoint cells. Mark this field-profile check GREEN.

**Next action:** Stage 2 profile is now closed. Do not re-run it. Proceed to Stage 3 scanner repair: move all CSV writes after MemberRef/MethodSpec enrichment; improve reference resolution beyond name-only matching; make ImplMap resolution errors and declaration/ImplMap coverage failures affect the exit status. Then validate with a read-only scan of the original Managed directory.
