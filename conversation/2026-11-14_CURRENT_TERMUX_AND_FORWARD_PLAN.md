# CASTAWAY-DEPTHS — 2026-11-14 continuation checkpoint

## User direction (current, authoritative)
- The user states that the earlier work has already succeeded and explicitly wants to leave the old paths behind.
- Do not repeatedly re-audit or redo completed historical work.
- Establish a forward path using the currently prepared environment and continue from the current stage.
- Keep responses concise; avoid long explanations and repeated diagnostic requests.
- User explicitly requests that progress and decisions be archived in this GitHub repository so they do not need to repeat instructions.
- Do not modify the device, drivers, project files, or repository implementation unless the user directs/approves the specific change.

## Current Termux workspace reported by user
- Working directory: `~/PINVOKE_STAGE3_20261114`
- Device: `25060RK16C`
- Android: 16
- Platform/SoC property: `mt6991`
- Toolchain/packages reported installed: Clang 21.1.8, CMake 4.4.5, Ninja 1.13.2, Gradle 9.9.1, OpenJDK 21.0.13, Python 3.14.10, glslang 16.6.0, shaderc 2026.4, SPIRV-Tools 1.4.363.0, Vulkan headers 1.4.368, Vulkan tools 1.4.368, Vulkan loader packages, Git 2.56.1, Git LFS 3.8.1.
- Available commands confirmed: `vulkaninfo`, `glslangValidator`, `glslc`, `spirv-val`.
- Existing project/work directories visible in the user's HOME include `stranded_native_port`, `Read`, `Castaway-Depths`, `PINVOKE_STAGE3_20261114`, `PINVOKE_STAGE3_TEST`, `PINVOKE_STAGE3_TEST_OUT`, and `PINVOKE_STAGE4_LDSTR_20261114`. Their contents/status were not re-audited in this checkpoint.

## Hardware Vulkan result — confirmed by direct probe
The user compiled and ran `~/vk_probe.cpp` using:
```sh
clang++ -std=c++20 -O2 ~/vk_probe.cpp -ldl -o ~/vk_probe
~/vk_probe
```
The probe explicitly loaded `/system/lib64/libvulkan.so`, created a Vulkan instance, and enumerated:
- `Mali-G925-Immortalis MC12`
- Device type: integrated GPU
- Vulkan API: 1.3.278
- Vendor ID: `0x13b5`
- Device ID: `0xd8300010`
- Driver version: `0x0c401000`
- 152 device extensions

The Android system properties also report `ro.hardware.vulkan=mali`, `ro.hardware.egl=mali`, and `ro.hwui.use_vulkan=true`. Vendor driver files are present at `/vendor/lib64/hw/mt6991/vulkan.mali.so`, `/vendor/lib64/hw/vulkan.mali.so`, and `/vendor/lib64/egl/mt6991/libGLES_mali.so`.

## Important distinction retained
The normal Termux `vulkaninfo --summary` invocation previously enumerated Mesa llvmpipe (CPU), while the direct probe through Android's system Vulkan loader enumerated the real Mali-G925. These are different loader paths. The direct probe proves hardware enumeration via the system loader; it does not by itself prove swapchain/presentation or completed rendering. Do not misstate this distinction.

## Forward work checkpoint
- The immediate proposed next stage is native Vulkan execution on the Mali path: create a logical device, find a graphics-capable queue family, submit a minimal command buffer, and verify completion.
- The assistant last asked the user to show `sed -n '1,260p' ~/vk_probe.cpp` so the next probe can be based on the actual current source. The user has not yet supplied that output in this checkpoint.
- Avoid restarting the old inventory or re-running already completed hardware enumeration unless a new failure makes it necessary.
- Before implementation, inspect the existing current-stage files relevant to Stage 4, especially `~/PINVOKE_STAGE4_LDSTR_20261114`, only as needed. Prefer a minimal, isolated test and preserve existing work.
- No Stage 4 logical-device/queue/command-buffer test result is claimed yet.

## APK Editor / game port direction
The user's larger intended sequence remains: first build and add the APK Editor utility; only after the required stages succeed, package/connect the real game build. Multiplayer is not a current requirement; if ever considered, it should be optional and user-owned/custom rather than a dependency. The user has also stated that final shaders should be newly authored/created for the target path, not simply reuse original game shaders. Rigorous validation is a priority. Do not jump ahead to final game packaging before the staged validation path is ready.

## Repository archive provenance
This document records user-provided live Termux output and the conversation's current decisions as of 2026-11-14. It is an archive checkpoint, not an assertion that unrun Stage 4 tests have passed.


## 2026-11-14 — Termux recovery session update

- Termux was reset; user rebuilt the environment in `~/Castaway-Depths`.
- Repository clone succeeded at commit `c854a31`; setup script reported workspace READY; `git status --short` clean.
- Toolchain verified: Python 3.14.10, Clang 21.1.8 (Android aarch64 API 24 target), CMake 4.5.0, Ninja 1.13.2, Git 2.56.1, OpenJDK 21.0.13, Gradle 9.9.1, AAPT, D8. NDK sysroot/multilib packages installed.
- Android SDK Platform not present in searched Termux prefix; `sdkmanager` and `adb` missing; SDK environment variables unset. `pkg show android-sdk` failed because package is unavailable by that name.
- Next requested diagnostic is `command -v curl; command -v unzip; df -h "$HOME"`. Then choose a verified Termux-compatible Android SDK installation path; no SDK or APK build yet.
- No project source/data changes were made. No Vulkan device/driver/system changes were made.
- Follow-up instructions must remain short and sequential. Preserve completed historical stages and use the GitHub handoff as source of truth.


## 2026-11-14 — latest Termux prerequisite confirmation and continuation
- User supplied fresh output from `~/Castaway-Depths`:
  - `curl`: `/data/data/com.termux/files/usr/bin/curl` (available).
  - `unzip`: `/data/data/com.termux/files/usr/bin/unzip` (available).
  - HOME filesystem: 233 GB total, 152 GB used, 81 GB available (66% used).
- These checks confirm download and ZIP extraction tools are present and storage headroom is ample for the next SDK-platform acquisition step.
- Earlier finding remains: `pkg show android-sdk` failed with “Unable to locate package”; do not retry that package name.
- Android SDK Platform / `android.jar` is still not confirmed installed. No SDK package download or APK build has been performed yet.
- No local project/device changes were made by the assistant. Continue with a verified Termux-compatible method, first checking architecture and installed Android build tools, then obtain only the missing SDK platform components. Keep commands sequential and ask the user to return output before proceeding.


## 2026-11-14 — Android SDK Platform 36 and NDK/toolchain verification

### Android device and SDK platform
- User's fresh Termux output from `~/Castaway-Depths` confirms device architecture `aarch64`, Android API level `36`, and Termux prefix `/data/data/com.termux/files/usr`.
- OpenJDK 21.0.13 is available.
- Termux commands available: `aapt`, `d8`, `gradle`, and `cmake`; later checks also confirmed `aapt2`, `apksigner`, `zipalign`, `clang`, `clang++`, and `llvm-ar`.
- `android.jar` was initially absent and `ANDROID_HOME`/ `ANDROID_SDK_ROOT` were unset.
- Downloaded Google Android SDK Platform 36 archive from `https://dl.google.com/android/repository/platform-36_r02.zip` to `$HOME/android-platform-36.zip`.
- SHA-1 verification succeeded: `2c1a80dd4d9f7d0e6dd336ec603d9b5c55a6f576`; archive listing confirmed `android-36/android.jar` (27,768,026 bytes).
- User extracted platform to `$HOME/android-sdk/platforms/android-36`. Confirmed file: `$HOME/android-sdk/platforms/android-36/android.jar` (27 MB). User output confirmed `ANDROID_HOME=/data/data/com.termux/files/home/android-sdk` and `ANDROID PLATFORM 36: READY`.

### Build tools and NDK findings
- No SDK-local Build Tools binaries were found under `$ANDROID_HOME` at the checked depth. Termux-hosted equivalents are present:
  - `aapt`, `aapt2`, `d8`, `apksigner`, `zipalign`, `clang`, `clang++`, `llvm-ar`.
- Installed packages include `aapt 16.0.0.4-4`, `aapt2 16.0.0.4-4`, `apksigner 37.0.0`, `d8 37.0.0`, `clang 21.1.8-3`, `llvm 21.1.8-3`, `ndk-multilib 30`, `ndk-multilib-native-stubs 30`, and `ndk-sysroot 30`.
- `$PREFIX/opt/ndk-multilib` contains architecture directories including `aarch64-linux-android`; its AArch64 library directory includes Android system libraries/stubs such as `libandroid.so`, `libc.so`, `liblog.so`, `libvulkan.so`, `libEGL.so`, and GLES libraries.
- `jni.h` was found at `$PREFIX/include/jni.h` and in the OpenJDK 21 include directory. The attempted header search emitted a warning because `-name android/api-level.h` incorrectly contains a slash; no `android_native_app_glue.h` result was shown.
- Clang 21.1.8 accepted `--target=aarch64-linux-android36`; preprocessing and a simple C compilation both exited 0. The compile produced `$HOME/ndk_test.o`, but the user could not inspect it because the Termux `file` utility is not installed. This verifies object compilation only, not successful linking against the intended Android NDK sysroot or a runnable shared library.
- The earlier `find` under `$PREFIX/opt/ndk-multilib/aarch64-linux-android` showed libraries but no headers in that directory. The full `dpkg -L ndk-sysroot` output was not included in the user's latest message, so exact sysroot header location and Clang sysroot wiring remain to be established.

### Current verified state and next action
- Android Platform 36 is installed and its `android.jar` is verified.
- Java, Android packaging/signing tools, Clang/LLVM, and Termux NDK-related packages are installed.
- Do not install duplicate SDK Build Tools or NDK packages yet.
- Next: inspect the complete `dpkg -L ndk-sysroot` output and actual sysroot paths; then run a minimal AArch64 Android shared-library compile/link test using explicit, verified headers and libraries. Do not claim NDK linking, APK build, GPU rendering, or game port completion until each is tested.
- User asked to archive this conversation and continue in a new conversation, treating this private GitHub repository as the project memory/source of truth.

- Conversation transcript companion: `conversation/2026-11-14_CHATGPT.txt` (verified on `main`; covers the Android SDK 36 / Termux / NDK exchange available in this conversation context).
