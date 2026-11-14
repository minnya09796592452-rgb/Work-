# Implementation Status — CASTAWAY DEPTHS

Last updated: 2026-11-13

Status reflects evidence present in the repository at commit 785213af0030143ab9008957a623a8883edb267d plus the project facts recorded on this date. It is not a claim that uncommitted Termux files have been uploaded.

| Subsystem / milestone | Status | Evidence / qualification |
|---|---|---|
| Main GitHub repository | PASS | Private repo; main branch; push permission confirmed |
| Termux workspace setup | PASS | `scripts/setup_termux.sh` reported READY |
| Git working tree | PASS at last recorded check | User reported clean and up to date; re-check before upload |
| Vulkan loader access | PASS | Android system Vulkan loader loaded and physical device enumerated |
| Vulkan surface creation | NOT STARTED / UNVERIFIED | No evidence recorded in this repository |
| Swapchain creation | NOT STARTED / UNVERIFIED | No evidence recorded in this repository |
| Command submit and present | NOT STARTED / UNVERIFIED | No evidence recorded in this repository |
| Render output | NOT STARTED / UNVERIFIED | Device enumeration is not render proof |
| Android shell / JNI | NOT STARTED | No implementation committed |
| Native engine core | NOT STARTED | No implementation committed |
| Full renderer | NOT STARTED | No complete renderer committed |
| Gameplay systems | NOT STARTED | No original gameplay implementation committed |
| Game content | NOT STARTED | Staging directories exist; no completed original content claimed |
| Save/load | NOT STARTED | No implementation or test evidence |
| Editor/Control APK | NOT STARTED | No APK artifact claimed |
| Final standalone APK | NOT STARTED | No release APK claimed |
| Final OBB / asset package | NOT STARTED | Existing `obb/Castaway Depths/` is staging only |

## Required validation gates
Track each gate independently. Attach logs or other reproducible evidence before changing a status to PASS:
1. BUILD
2. INSTALL
3. LAUNCH
4. VULKAN DEVICE
5. SURFACE
6. SWAPCHAIN
7. SUBMIT/PRESENT
8. RENDER OUTPUT
9. GAMEPLAY
10. SAVE/LOAD
11. DEVICE COMPATIBILITY

## Update protocol
- Update this table when a real source file or test result changes a subsystem's state.
- Include commit SHA, device model/GPU, Android version, command used, result, and evidence path for device tests.
- A successful compile does not imply runtime success.
- A successful Vulkan device enumeration does not imply presentation or rendering success.


## 2026-11-13 evidence update
- P/Invoke binary audit package prepared on the user's Android device; awaiting upload for inspection. No P/Invoke inventory is verified yet. See `docs/research/PINVOKE_DLL_AUDIT.md`.
- Historical NYAMIST Android WSI source was inspected in the conversation, including a one-shot swapchain image transition and present path. This is not a CASTAWAY DEPTHS implementation and no device runtime log was supplied for this source. Therefore SURFACE, SWAPCHAIN, SUBMIT/PRESENT, and RENDER OUTPUT remain NOT STARTED / UNVERIFIED for CASTAWAY DEPTHS.
- No source, binary, APK, or asset was imported from the historical project into the CASTAWAY DEPTHS implementation.


## 2026-11-13 ZIP audit evidence update
- `CASTAWAY_PInvoke_Audit.zip` integrity check: PASS; 123 archive entries reported.
- SHA-256 manifest: 119/119 entries matched.
- Confirmed Windows PE32+ x86-64 binaries: `UnityPlayer.dll`, `steam_api64.dll`, `Rewired_DirectInput.dll`.
- Windows runtime/plugin dependency findings are documented in `docs/research/PINVOKE_DLL_AUDIT.md`.
- This is a reference-binary audit only. It does not change CASTAWAY DEPTHS implementation status and does not establish a complete P/Invoke metadata inventory.
- Next audit gate: enumerate MethodDef P/InvokeImpl, ImplMap, and ModuleRef across every managed assembly, then publish exact per-assembly counts and signatures. Inspect native PE imports/exports separately.


## Forensic P/Invoke audit update — 2026-11-13

Completed full metadata inventory of the supplied audit ZIP: 119/119 Managed DLLs parsed and 119/119 manifest hashes verified; 3,791 MethodDef P/InvokeImpl flags match 3,791 ImplMap rows across 17 assemblies. Three native binaries were confirmed Windows PE32+ x86-64, not Android ELF. This does not change CASTAWAY DEPTHS implementation status or authorize reuse of the source game's materials. See [full P/Invoke inventory](research/PINVOKE_FULL_METADATA_INVENTORY.md).

 
## 2026-11-14 Android ARM64 native-link evidence
- User-provided report confirms an AArch64 ELF64 shared object was produced with `LINK_EXIT=0` using Clang 21.1.8 and the installed Termux Android sysroot/stub packages.
- `readelf` confirms `ET_DYN`, machine AArch64, SONAME `libcastaway_probe.so`, and dependencies `liblog.so`, `libdl.so`, `libc.so`.
- This is a compile/link gate only. Runtime `dlopen`, JNI, APK build/install/launch, Vulkan presentation/rendering, and gameplay remain unverified.
- Evidence: `docs/research/ANDROID_NDK_LINK_TEST_20261114.md`; raw logs retained under `Recyclebin/`.
- Next gate: runtime `dlopen` + exported symbol call, then continue Vulkan logical-device/queue testing.

 
## 2026-11-14 Android native runtime load evidence
- User-provided Termux report: compile exit 0; `dlopen` PASS; `dlsym("castaway_probe")` and function invocation PASS; return value 1; `dlclose` PASS; process exit 0.
- This validates the small test library in the Termux process only. It does not validate APK/JNI loading or Vulkan.
- Evidence: `docs/research/ANDROID_NDK_RUNTIME_TEST_20261114.md`; raw report retained under `Recyclebin/`.
- Next gate: Vulkan header/source preflight, then queue-family selection and logical-device create/destroy test using the Android system Vulkan loader.

## 2026-11-14 Vulkan preflight and queue/device probe preparation
- User-provided preflight confirms Android 16/API 36, AArch64 `arm64-v8a`, device platform `mt6991`, official Vulkan headers at `$PREFIX/include/vulkan/vulkan.h`, Clang/Clang++ 21.1.8, and the direct Android system loader `/system/lib64/libvulkan.so`.
- Loader ELF is AArch64; required Vulkan loader symbols are present. Raw report retained at `Recyclebin/CASTAWAY_VULKAN_PREFLIGHT.txt`.
- Added official-header C++ diagnostic: `native/vulkan/vulkan_device_probe.cpp`.
- Added Python build/run harness: `scripts/vulkan_device_probe.py`. It compiles, runs, captures stdout/stderr, and writes `$HOME/CASTAWAY_VULKAN_DEVICE_TEST.txt`.
- Preflight and probe-source preparation are complete; the C++/Python probe has NOT yet been run on-device.
- Status: preflight 🟢; prior physical-device enumeration 🟢 from earlier evidence; queue-family selection and logical-device create/destroy 🟡 pending runtime report. Surface/swapchain/present/render output, APK/JNI, gameplay, and save/load remain 🟡 unverified. No 🔴 blocker identified.
- Research record: `docs/research/VULKAN_QUEUE_DEVICE_PROBE_20261114.md`.
