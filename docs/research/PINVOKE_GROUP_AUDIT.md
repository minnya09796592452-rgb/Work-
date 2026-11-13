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
