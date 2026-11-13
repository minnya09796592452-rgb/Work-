# P/Invoke Reference Catalog — Use When Needed

**Purpose:** searchable engineering reference for the Windows-era managed/native interop declarations discovered in the supplied audit archive.

> **Important:** These are metadata-derived declarations, not recovered C/C++ source code and not drop-in Android implementations. A declaration records a managed method, native module name, entry point, signature and marshaling flags. It does not include the native function body. Do not copy proprietary game code or binaries into CASTAWAY DEPTHS.

## Verified inventory

- Managed assemblies parsed: **119/119**
- P/Invoke declarations: **3,791**
- MethodDef.PInvokeImpl ↔ ImplMap cross-check: **3,791 = 3,791**
- Distinct native module names: **66**
- Assemblies containing declarations: **17**
- Metadata/signature parse errors: **0**

## Assembly index

| Managed assembly | Declarations | Potential reference area |
|---|---:|---|
| com.rlabrecque.steamworks.net.dll | 921 | Steamworks interop declarations |
| System.Drawing.dll | 696 | GDI+ / drawing interop |
| XGamingRuntime.dll | 540 | Xbox Gaming Runtime interop |
| Rewired_Windows.dll | 513 | Windows input integration |
| System.dll | 386 | .NET platform interop |
| Assembly-CSharp.dll | 360 | Game-managed native interop declarations |
| Rewired_Core.dll | 110 | Rewired core interop |
| mscorlib.dll | 100 | Runtime/platform interop |
| System.Data.dll | 77 | Data/provider interop |
| System.Core.dll | 27 | Framework interop |
| System.ServiceModel.Internals.dll | 19 | Service-model internals |
| System.IO.Compression.dll | 11 | Compression interop |
| UnityEngine.NVIDIAModule.dll | 11 | NVIDIA-related Unity module |
| PhotonBolt.dll | 9 | Photon Bolt interop |
| GdkUtilities.dll | 4 | GDK utilities |
| System.Security.dll | 4 | Security interop |
| System.Xml.dll | 3 | XML interop |

## Example declaration (metadata only)

- Managed type: AchievementsInterface.Epic.OnlineServices.Achievements
- Managed method: EOS_Achievements_AddNotifyAchievementsUnlocked
- Native module: EOSSDK-Win64-Shipping
- Native entry point: EOS_Achievements_AddNotifyAchievementsUnlocked
- Calling convention: Winapi
- Managed signature: uint64 (native int, valuetype AddNotifyAchievementsUnlockedOptionsInternal.Epic.OnlineServices.Achievements&, native int, class OnAchievementsUnlockedCallbackInternal.Epic.OnlineServices.Achievements)

This is an interop contract description only. It is not the implementation of the EOS function.

## Safe reuse workflow

1. Search this catalog for the relevant managed assembly, native module, entry point and signature.
2. Confirm the target platform has a compatible native library and ABI.
3. Confirm the native symbol is exported by that exact library.
4. Verify pointer widths, structure layout, string encoding, ownership/lifetime, callbacks and calling convention.
5. Implement a clean platform-specific adapter where required. Never assume a Windows DLL or its ABI can be loaded by Android.
6. For CASTAWAY DEPTHS, use only independently implemented code and original assets. Treat this catalog as technical research, not source material to copy.

## Full audit record

See [Full Managed P/Invoke and Native PE Audit](PINVOKE_FULL_METADATA_INVENTORY.md) for archive validation, all assembly totals, module totals, mapping flags, and native PE import/export results.

The complete row-level declaration dataset was exported separately as managed_pinvoke_all_3791.csv in the audit package. It contains declaring type, managed method, native module, entry point, decoded signature, raw signature bytes, MethodDef flags, ImplFlags and ImplMap fields.
