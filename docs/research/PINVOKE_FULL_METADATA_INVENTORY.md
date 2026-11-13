# Full Managed P/Invoke and Native PE Audit

**Input:** `CASTAWAY_PInvoke_Audit_SAFE.zip`  
**Scope:** all Managed DLLs; ECMA-335 MethodDef P/InvokeImpl flags, ImplMap and ModuleRef tables; managed signatures and mapping flags; native PE imports/exports.

## Archive validation

- ZIP integrity: **PASS**
- Archive entries: **124** (including the Managed directory entry and SHA-256 manifest)
- Managed DLLs parsed: **119 / 119**
- Manifest records: **119**; SHA-256 matches: **119 / 119**
- Native PE files outside Managed: **3**
- Total uncompressed archive content: **63,252,228 bytes**

## Managed P/Invoke inventory

| Metric | Count / result |
|---|---:|
| MethodDef rows scanned | 208,570 |
| MethodDef P/InvokeImpl methods (flag 0x2000) | 3,791 |
| ImplMap rows | 3,791 |
| MethodDef ↔ ImplMap cross-check | **PASS — exact match** |
| Assemblies containing P/Invoke | 17 |
| Assemblies with zero P/Invoke | 102 |
| Distinct native ModuleRef target names | 66 |
| Distinct native-module / entry-point pairs | 3,563 |
| Managed signature decode errors | 0 |
| Managed metadata parse errors | 0 |

The parser inspected MethodDef (table 0x06), ModuleRef (0x1A), ImplMap (0x1C), TypeDef (0x02) and TypeRef (0x01). Every P/InvokeImpl MethodDef found had a corresponding ImplMap row.

### P/Invoke declarations by managed assembly

| Assembly | Count |
|---|---:|
| com.rlabrecque.steamworks.net.dll | 921 |
| System.Drawing.dll | 696 |
| XGamingRuntime.dll | 540 |
| Rewired_Windows.dll | 513 |
| System.dll | 386 |
| Assembly-CSharp.dll | 360 |
| Rewired_Core.dll | 110 |
| mscorlib.dll | 100 |
| System.Data.dll | 77 |
| System.Core.dll | 27 |
| System.ServiceModel.Internals.dll | 19 |
| System.IO.Compression.dll | 11 |
| UnityEngine.NVIDIAModule.dll | 11 |
| PhotonBolt.dll | 9 |
| GdkUtilities.dll | 4 |
| System.Security.dll | 4 |
| System.Xml.dll | 3 |

The other 102 assemblies were parsed and contained zero P/InvokeImpl methods and zero ImplMap rows.

### Most frequently referenced native modules

| Native module name | P/Invoke declarations |
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
| /System/Library/Frameworks/CoreFoundation.framework/CoreFoundation | 38 |
| hid.dll | 28 |
| /System/Library/Frameworks/Carbon.framework/Versions/Current/Carbon | 28 |
| sni.dll | 27 |
| gdi32 | 27 |
| libasound | 27 |
| kernel32 | 23 |
| User32.dll | 22 |
| MonoPosixHelper | 20 |
| sspicli.dll | 19 |
| Kernel32.dll | 18 |

### Mapping flags

| Calling convention | Count |
|---|---:|
| Winapi | 2,033 |
| Cdecl | 1,159 |
| StdCall | 599 |

| Character set | Count |
|---|---:|
| Not specified | 3,384 |
| Unicode | 177 |
| Ansi | 153 |
| Auto | 77 |

The complete declaration CSV includes declaring type, method, native module, entry point, decoded managed signature, raw signature bytes, MethodDef flags, ImplFlags, raw MappingFlags, calling convention, character set, ExactSpelling, SetLastError, BestFitMapping and ThrowOnUnmappableChar.

## Native PE import/export audit

All three native files are Windows **PE32+ x86-64** binaries. Their import/export directories were parsed successfully with `objdump -p`.

| Binary | Size (bytes) | Imported DLLs | Named import symbols | Named exports |
|---|---:|---:|---:|---:|
| Rewired_DirectInput.dll | 12,800 | 4 | 43 | 51 |
| steam_api64.dll | 1,692,672 | 5 | 158 | 1,096 |
| UnityPlayer.dll | 28,773,464 | 19 | 491 | 1 |

Totals: 28 imported-DLL dependency entries, 692 named imported symbols, and 1,148 named exports.

## Engineering interpretation

1. The three native binaries are Windows x64 PE files, not Android ELF shared objects; Android's native linker cannot load them directly.
2. Managed ModuleRef values include Windows modules (for example kernel32, user32, gdiplus, Rewired_DirectInput, EOSSDK-Win64-Shipping and GDK thunks), Apple framework paths, and Linux-style names (libc, libasound and libSDL2-2.0.so.0). Their presence in metadata does not prove the corresponding native libraries are bundled or usable on Android.
3. Assembly-CSharp.dll has 360 P/Invoke declarations targeting EOSSDK-Win64-Shipping. This establishes an interop dependency declaration, not that EOS is mandatory for offline gameplay; call-site/control-flow analysis is needed.
4. com.rlabrecque.steamworks.net.dll has 921 P/Invoke declarations. The archive's steam_api64.dll is a Windows x64 binary, not an Android Steamworks implementation.
5. Rewired_Windows.dll has 513 P/Invoke declarations; Rewired_DirectInput.dll is a Windows x64 plugin importing DINPUT8.dll and Windows runtime DLLs. This is Windows input plumbing, not Android touch input.
6. System.Drawing.dll contributes 696 declarations; its gdiplus bindings are desktop-oriented and cannot be assumed available on Android.
7. No obvious Android system targets such as libandroid.so or liblog.so appeared in this Managed P/Invoke ModuleRef inventory. This is not proof that no Android-related code exists elsewhere.

## Limits

- Complete for managed metadata and native PE import/export directories contained in this ZIP.
- Does not establish runtime reachability or whether declarations are behind platform conditionals.
- Does not disassemble all native functions or resolve dynamic LoadLibrary/GetProcAddress calls.
- Named-export counts do not include ordinal-only exports.
- Local TypeDef/TypeRef signature tokens are resolved; generic TypeSpec references remain TypeSpec tokens.
- No ZIP payload was copied into CASTAWAY DEPTHS source. This is forensic research only; it is not permission to reuse Stranded Deep code, assets, binaries, names or implementation.

## Companion machine-readable audit files

The full audit bundle contains:
- `managed_assembly_summary.csv` — all 119 assemblies, including zero-P/Invoke DLLs.
- `managed_pinvoke_all_3791.csv` — every P/Invoke declaration and decoded signature/flags.
- `managed_module_refs_all.csv` — every ModuleRef row.
- `native_module_target_summary.csv` — native-module counts and assembly cross-reference.
- `native_pe_summary.csv` — native PE summary.
- `native_pe_import_symbols.csv` — named native imports.
- `native_pe_export_symbols.csv` — named native exports.
- `audit_summary.json` — compact totals.
