# P/Invoke DLL Inventory — Reference Binary Audit

Date: 2026-11-13
Scope: User-provided Stranded Deep Windows installation; forensic reference only.
Audit package: `CASTAWAY_PInvoke_Audit.zip`
Original local path: `/storage/emulated/0/Download/CASTAWAY_PInvoke_Audit.zip`

> **Project boundary:** CASTAWAY DEPTHS will not use any code, DLL, IL, asset, shader, model, audio, name, branding, or other game material from Stranded Deep. This document records a forensic investigation only. It is not an import or reuse specification.

## 1. Archive integrity and manifest

The archive was inspected in the conversation before this report was committed.

| Check | Result |
|---|---|
| ZIP archive integrity test | PASS |
| Files reported in archive | 123 |
| SHA-256 manifest entries checked | 119 |
| SHA-256 matches | 119 / 119 PASS |
| Archive purpose | Windows binary / managed-assembly audit |
| Android-ready native library set | Not present / not established |

The ZIP was approximately 25 MB as reported during creation. The 119 manifest checksums matched the corresponding extracted files. The count of 123 archive entries and 119 manifest records are different measures; do not interpret them as 123 independently hashed files.

## 2. Archive contents and binary classification

The archive included the game's Managed assembly directory, the x86_64 native plugin directory, `UnityPlayer.dll`, and the SHA-256 manifest.

### Confirmed Windows PE binaries

| File | Classification | Audit finding |
|---|---|---|
| `UnityPlayer.dll` | PE32+ / x86-64 Windows native binary | Unity runtime binary; not an Android ARM64 library |
| `steam_api64.dll` | PE32+ / x86-64 Windows native plugin | Windows Steam API plugin; not Android ABI-compatible |
| `Rewired_DirectInput.dll` | PE32+ / x86-64 Windows native plugin | DirectInput/Windows input dependency; not Android ABI-compatible |

The native plugin directory is specifically `Plugins/x86_64`. These binaries cannot be treated as Android `arm64-v8a` shared libraries.

### Managed assemblies

The archive also contains managed .NET assemblies under `Stranded_Deep_Data/Managed/`, including the game assemblies and Unity / third-party managed libraries. These are CLR-managed assemblies, not Android native `.so` files.

Previously identified principal managed files include:
- `Assembly-CSharp.dll`
- `Assembly-CSharp-firstpass.dll`
- `Rewired_Core.dll`
- `Rewired_Windows.dll`
- `Rewired_Windows_Functions.dll`
- `com.rlabrecque.steamworks.net.dll`
- UnityEngine module assemblies

The existence of a managed assembly does not by itself prove that it declares P/Invoke methods.

## 3. Unity runtime and operating-system dependency findings

The inspected `UnityPlayer.dll` identifies a Windows x86-64 Unity runtime (version strings observed: Unity 2021.2.7f1). Its PE metadata shows PE32+, image base `0x180000000`, eight sections, and no CLR header.

Windows import dependencies observed include:

- `KERNEL32.dll`
- `USER32.dll`
- `OPENGL32.dll`
- `WINMM.dll`
- `WINHTTP.dll`
- `HID.dll`
- `WS2_32.dll`
- `ADVAPI32.dll`
- `GDI32.dll`
- `SHELL32.dll`

These imports establish Windows platform coupling in the inspected runtime. They do not establish that every listed API is required by a future CASTAWAY DEPTHS engine.

Graphics backend strings observed in the Unity runtime included Vulkan, Direct3D 11, Direct3D 12, OpenGL, and OpenGL ES. These strings indicate backend support/code paths in that Windows Unity runtime; they do not make the Windows binary portable to Android and do not provide Android shader source or an Android Vulkan implementation.

## 4. Native plugin dependency findings

### `Rewired_DirectInput.dll`

The inspected PE imports include Windows DirectInput / system dependencies, including `DINPUT8.dll` and `VCRUNTIME140.dll`, as well as Windows runtime components. This is a Windows controller-input plugin, not an Android input library.

### `steam_api64.dll`

The inspected PE imports include Windows modules such as `IPHLPAPI.dll`, `WS2_32.dll`, `ADVAPI32.dll`, `SHELL32.dll`, and `KERNEL32.dll`. It is a Windows x86-64 Steam plugin, not an Android library.

## 5. P/Invoke status — important distinction

**The ZIP audit did not yet produce a complete, exact P/Invoke inventory.**

The following concepts must remain separate:

1. **Native PE imports:** Windows DLL dependencies recorded in a PE import table.
2. **Managed P/Invoke declarations:** .NET methods whose metadata maps managed methods to native modules and entry points.
3. **Unity internal native bindings:** Unity-managed/native engine interfaces, which are not automatically equivalent to ordinary user-assembly P/Invoke declarations.

A Windows PE import list is not a P/Invoke report. Likewise, finding a managed DLL does not prove that it contains P/Invoke declarations.

### Exact metadata work still required

For every managed assembly in the archive, a complete inventory must inspect ECMA-335 metadata:
- MethodDef `PInvokeImpl` flags
- `ImplMap` table rows
- `ModuleRef` table rows
- Native module name
- Entry-point name (including name-mangling / ordinal cases where applicable)
- Mapping flags and calling convention
- Managed declaring type and method name
- Complete managed method signature, including parameter and return types
- Character-set / string-marshalling and related mapping flags where encoded

The report must include zero-result assemblies too, so that the assembly coverage is demonstrably complete. It must distinguish an absent P/Invoke map from a tool/parser failure.

Native PE import and export tables should be recorded separately, with architecture and imported module names.

## 6. CASTAWAY DEPTHS relevance

This audit provides only platform-compatibility and binary-classification evidence:

- Windows x86-64 PE binaries are not Android ARM64 native libraries.
- Windows-specific input and Steam plugins are not Android-compatible drop-ins.
- Windows API dependencies must not be assumed available through Android NDK.
- A future Android engine must be independently implemented against Android APIs and the Android Vulkan loader/driver.
- The Unity runtime's backend strings are not a transferable Android renderer or shader package.

No binary, managed assembly, source, IL, asset, shader, or proprietary implementation from this archive is approved for inclusion in CASTAWAY DEPTHS.

## 7. Evidence limits and next audit gate

This report records the archive-level results observed in the conversation. The ZIP itself is not being committed to the CASTAWAY DEPTHS repository; only this code-free audit report is stored.

**Next gate:** perform a complete metadata pass across every managed assembly and publish a per-assembly P/Invoke table with exact counts, module names, entry points, calling conventions, and signatures. Do not label the P/Invoke investigation complete until that table and the native PE import/export inventory are reproducible.
