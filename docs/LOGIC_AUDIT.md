# Logic and Pipeline Audit — CASTAWAY DEPTHS

Last updated: 2026-11-13

## Purpose
This document records the current verified project state and technical findings that affect planning and validation. It is not a source-code import from another game.

## Project identity and boundary
- Project: CASTAWAY DEPTHS.
- Product direction: original native Android survival game.
- Rendering target: Android Vulkan.
- The Stranded Deep research repository is a study/reference source only.
- Do not copy or port Stranded Deep code, decompiled IL, DLLs, executable files, assets, models, textures, audio, maps, names, UI, story, or proprietary gameplay implementation into CASTAWAY DEPTHS.
- CASTAWAY DEPTHS gameplay logic and content must be independently designed and implemented.

## Current CASTAWAY DEPTHS implementation status
The main repository currently contains project documentation, Termux setup, and an empty content-staging structure. The following are not yet evidenced as implemented in this repository:
- Android Activity/JNI application shell.
- Native engine runtime.
- Vulkan surface creation, swapchain, command submission, and presentation.
- Production renderer and game scene.
- World simulation, player controller, survival systems, inventory, crafting, interaction, physics, ocean, weather, AI, and audio.
- Save/load implementation.
- Editor/Control APK, final APK, or packaged OBB archive.

A successful Vulkan loader/device enumeration on the target Android device is a device-access milestone only. It does not establish that surface presentation, swapchain, rendering, or gameplay works.

## External technical research findings — reference only
The separate research repository `devmyatmin/Read` was inspected at commit `d82ad9bb0ae4a850e9c79dbbecfca144f42506af`. The main logic files inspected included:
- `termux_archive/20261113_152323/project/src/asset_pipe.cpp`
- `termux_archive/20261113_152323/project/src/asset_pipe_v2.cpp`
- `termux_archive/20261113_152323/project/logic/extract_gameplay_il.py`
- `termux_archive/20261113_152323/project/logic/resolve_gameplay_tokens.py`
- `termux_archive/20261113_152323/project/logic/audit_assembly_coverage.py`
- `termux_archive/20261113_152323/project/logic/audit_ceto_full.py`
- `termux_archive/20261113_152323/project/logic/convert_ceto_to_vulkan.py`
- `termux_archive/20261113_152323/project/logic/extract_ceto_ocean_core_il.py`
- `termux_archive/20261113_152323/project/logic/inspect_compute_kernels.py`
- `termux_archive/20261113_152323/project/build/ceto_pipeline_test.cpp`
- `termux_archive/20261113_152323/project/build/ceto_gpu_render_test.cpp`
- `termux_archive/20261113_152323/project/build/ceto_shader_module_test.cpp`

### Asset parser observation
The two inspected asset-pipeline implementations differ in the Texture2D metadata parser. `asset_pipe.cpp` contains Unity-version-conditional skips for fallback-format/downscale/alpha-channel fields before reading dimensions and image size. `asset_pipe_v2.cpp` omits that skip block. For Unity SerializedFile inputs whose Texture2D layout includes those fields (Unity 2017.3+ according to the parser conditions), omitting the skips can shift the read position and corrupt parsed metadata. The actual input-file Unity version must be verified before declaring a specific affected asset.

### Gameplay IL extraction observation
The inspected scripts extract selected gameplay classes and resolve metadata-token references. Recorded report totals were 3,859 assembly types, 26,143 method rows, 23,276 methods with bodies, 887 extracted method entries, 7,791 dependency references, and 2,329 unique references. The 887 entries represent the selected extraction scope, not complete extraction of the assembly. The dependency map is a reference/call map, not a complete control-flow or runtime-semantics reconstruction. The inspected resolver did not handle MethodSpec token table 0x2B; generic method references may therefore remain unresolved.

### Ocean-system research observation
The reference research identified Ceto-related areas for wave spectrum generation, FFT/butterfly lookup tables, GPU/CPU displacement buffers, wave displacement queries, and buoyancy. These observations are research notes only. They are not an implementation specification to copy. CASTAWAY DEPTHS must independently design its own ocean model, algorithms, data structures, and code.

### Vulkan test interpretation
The inspected shader-module test validates SPIR-V shader-module creation. The pipeline test validates graphics-pipeline creation. The offscreen GPU render test performs a small Vulkan render-pass draw and readback using generic test shaders. These tests demonstrate useful Vulkan API milestones, but do not prove that the reference ocean shaders, a complete renderer, or a game renderer works.

## Evidence rules
Use these states in future reports:
- NOT STARTED: no implementation evidence.
- IN PROGRESS: source exists but required validation is incomplete.
- PASS: reproducible test evidence exists and is recorded.
- BLOCKED: a specific dependency or failure prevents progress.
- REFERENCE ONLY: research material that must not be copied into the game.

Do not mark a full subsystem PASS based only on compilation, shader-module creation, or device enumeration. Record build, install, launch, surface, swapchain, submit/present, render-output, gameplay, save/load, and device-compatibility evidence separately.


## Managed DLL / P/Invoke investigation — 2026-11-13

### GitHub evidence inspected
Reference repository: `devmyatmin/Read`, archived Termux project snapshot:
`termux_archive/20261113_152323/project/`.

1. `logic/audit_assembly_coverage.py` hardcodes the input DLL as:
   `/sdcard/Download/3zip/Stranded Deep/Stranded_Deep_Data/Managed/Assembly-CSharp.dll`.
   It reads .NET metadata `TypeDef` and `MethodList`, records type/method counts and method RVAs, and compares class/method names against the selected IL extraction.
2. `logic/extract_gameplay_il.py` also targets `Assembly-CSharp.dll`, with a second historical path under `TERMUX-PRE-CLEAN-BACKUP/stranded_native_port/logic/`. Its selected targets include Beam.Player, inventory/crafting, SaveManager, StrandedWorld, and PlayerCamera classes.
3. `logic/resolve_gameplay_tokens.py` resolves selected TypeDef, TypeRef, MethodDef, MemberRef, Field, and user-string tokens against that same `Assembly-CSharp.dll`.
4. The archived `assembly_method_inventory_v2.txt` and `assembly_unmatched_methods_v2.txt` files currently fetch as empty (0 characters); `assembly_type_coverage.txt` contains type-name/method-count rows. The previously recorded assembly-wide totals are coverage-analysis counts, not P/Invoke counts.

### P/Invoke conclusion
- `Assembly-CSharp.dll` is the only managed DLL explicitly identified by the inspected GitHub scripts as an input to the prior assembly/IL analysis.
- The inspected scripts do **not** enumerate the .NET metadata `ImplMap` table, `PInvokeImpl` method flags, `ModuleRef` rows, or native import names. Therefore GitHub evidence does not establish that `Assembly-CSharp.dll` contains the complete P/Invoke set, nor identify every other DLL containing P/Invoke declarations.
- P/Invoke metadata is stored per managed assembly; it is not necessarily centralized in one DLL. The next evidence-gathering step must inventory the game's managed DLLs (and plugin assemblies) and inspect each assembly's metadata for `ImplMap` / `PInvokeImpl` / `ModuleRef` entries. Record the managed assembly name, imported native module name, entry point, calling convention, and method signature. Do not infer native DLL names from Unity conventions.
- All findings here are REFERENCE ONLY. Do not copy the original game's DLLs, IL, code, or proprietary implementation into CASTAWAY DEPTHS.


## P/Invoke DLL inventory — local binary evidence, 2026-11-13

The user searched the actual Stranded Deep installation and located the managed assemblies and native plugins listed in `docs/research/PINVOKE_DLL_AUDIT.md`. The user also generated a 25 MB audit ZIP containing the complete Managed directory, x86_64 plugin directory, UnityPlayer.dll, and a SHA-256 manifest.

The ZIP has not yet been uploaded for inspection. Consequently, no DLL is currently confirmed to contain all P/Invoke declarations, and no P/Invoke counts, imported module names, entry points, calling conventions, or signatures are verified. The listed DLLs are investigation targets only. Inspect every managed assembly's MethodDef P/InvokeImpl flags, ImplMap table, and ModuleRef table after the archive is provided. Inspect native PE import/export tables as a separate analysis.

Full evidence and SHA-256 values: `docs/research/PINVOKE_DLL_AUDIT.md`.

REFERENCE ONLY: do not import any Stranded Deep DLL, IL, code, or protected content into CASTAWAY DEPTHS.


## ZIP binary audit update — 2026-11-13

The user-provided `CASTAWAY_PInvoke_Audit.zip` was subsequently inspected in the conversation. This supersedes the earlier statement that the archive had not yet been inspected, but it does **not** supersede the conclusion that the exact P/Invoke inventory remains incomplete.

- ZIP integrity: PASS.
- Archive entries reported: 123.
- SHA-256 manifest: 119 entries checked; 119/119 matched.
- Confirmed PE32+ x86-64 Windows binaries: `UnityPlayer.dll`, `steam_api64.dll`, and `Rewired_DirectInput.dll`.
- `UnityPlayer.dll` identified as a Windows Unity 2021.2.7f1 runtime; inspected PE metadata: image base `0x180000000`, eight sections, no CLR header.
- Windows imports observed in UnityPlayer include KERNEL32, USER32, OPENGL32, WINMM, WINHTTP, HID, WS2_32, ADVAPI32, GDI32, and SHELL32.
- Rewired DirectInput plugin imports Windows DirectInput/runtime dependencies, including DINPUT8 and VCRUNTIME140.
- Steam plugin imports Windows modules including IPHLPAPI, WS2_32, ADVAPI32, SHELL32, and KERNEL32.
- Unity runtime backend strings include Vulkan, Direct3D 11/12, OpenGL, and OpenGL ES. These are observations about the Windows runtime, not proof of Android compatibility or a transferable renderer.

### Remaining exact P/Invoke work

No complete per-assembly ECMA-335 P/Invoke inventory has yet been recorded. Every managed DLL still needs inspection of MethodDef P/InvokeImpl flags, ImplMap, and ModuleRef, with declaring type, method signature, module name, entry point, and mapping/calling-convention flags. Native PE import/export analysis remains a separate inventory. See `docs/research/PINVOKE_DLL_AUDIT.md` for the detailed binary audit and evidence limits.

All findings are REFERENCE ONLY. No Stranded Deep code, binaries, IL, assets, shaders, or proprietary implementation are to be used in CASTAWAY DEPTHS.
