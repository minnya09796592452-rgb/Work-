# P/Invoke DLL Inventory — Reference Audit

Date: 2026-11-13
Scope: User-provided Stranded Deep installation; forensic reference only.

## Evidence package

Termux created:
- Archive: `/storage/emulated/0/Download/CASTAWAY_PInvoke_Audit.zip`
- Reported size: 25 MB
- Included all files under `Stranded_Deep_Data/Managed/`, `Stranded_Deep_Data/Plugins/x86_64/`, `UnityPlayer.dll`, and `PInvoke_SHA256_MANIFEST.txt`.
- The archive has not yet been uploaded to this conversation. Its contents have therefore not been independently inspected here.

## Located clue assemblies and native binaries

| File | Location | Reported size | SHA-256 |
|---|---|---:|---|
| Assembly-CSharp-firstpass.dll | Managed | 627K | `05dc598d55fcd2440946fceb7a47a829fa23de26f7c05283080013e4c1c588ae` |
| Assembly-CSharp.dll | Managed | 3.3M | `d5501ae7a2c09499cdf569c5b52578401f039c2f4aa5e7e9240c22faeef214bc` |
| Rewired_Core.dll | Managed | 2.0M | `018f083660e95467146c23ef07246ee4150401c5e6f3437e7ead33c052c40ab4` |
| Rewired_Windows.dll | Managed | 908K | `956b674bb57869657aa27f1bea133ea3737cef497fba2898eb6d9e7d52c13cbd` |
| Rewired_Windows_Functions.dll | Managed | 4.0K | `b5863c2d83d6c12a32188a2f91257dd35a7ce71aaf322112120638592e13b516` |
| com.rlabrecque.steamworks.net.dll | Managed | 358K | `465c8f4e30120e3060d57efd4d4ffb5061fbcfa85d1320f286bedd1e4152f49c` |
| Rewired_DirectInput.dll | Plugins/x86_64 | 13K | `9989b282d43621259160954f3c2191afc3e3a13adb0091c3f60be228831858ad` |
| steam_api64.dll | Plugins/x86_64 | 1.7M | `53ca528e1cf660e8b53c5bbc78decb994cb650709155407fa9978200f9a05ae2` |
| UnityPlayer.dll | Game root | 28M | `e75ab32370fcd78d454dbac6d1648ee19d9ff6d9b9e481538216409fd860af2e` |

These are search clues, not confirmed P/Invoke-containing assemblies.

## Current conclusion

No assembly has yet been proven to contain the complete P/Invoke inventory. GitHub research previously identified Assembly-CSharp.dll as an input to gameplay IL/metadata analysis, but that does not prove it contains all P/Invoke declarations. Managed P/Invoke metadata is assembly-local and can be distributed across multiple assemblies.

## Required inspection after archive upload

For every managed assembly, enumerate ECMA-335:
- MethodDef P/InvokeImpl flags
- ImplMap table entries
- ModuleRef rows
- Imported module name and entry point
- Mapping flags / calling convention
- Managed method name and signature

For native PE files, separately inspect import and export tables. Do not conflate native imports with managed P/Invoke declarations or Unity's internal native binding mechanism.

## Project boundary

REFERENCE ONLY. This audit concerns third-party Stranded Deep binaries. Do not place the DLLs, IL, proprietary code, or archive in the CASTAWAY DEPTHS source repository. Only code-free, high-level findings may inform independently authored systems.
