# Android Editor/Control APK bootstrap

This is the first buildable Android shell for CASTAWAY DEPTHS. It is deliberately a diagnostic bootstrap, not a playable game.

## Pinned build toolchain

- Android Gradle Plugin: 9.4.0
- Gradle: 9.6.0
- JDK: 17
- Compile/target SDK: 36
- Minimum SDK: 26
- NDK: 28.2.13676358
- CMake: 3.22.1
- ABI: arm64-v8a only

The AGP/Gradle/JDK/NDK compatibility values follow the Android Developers AGP 9.4 release notes. The GitHub Actions workflow installs the SDK packages on an Ubuntu runner; no Android SDK installation in Termux is required for the first CI build.

## What this proves after a successful CI run

- Android Gradle project configuration and resource packaging build.
- ARM64 native C++ shared library compiles and is packaged.
- The Java Activity and JNI native entry point are linked into the APK.

## What it does not prove

- APK installation or launch on the user's physical device.
- Vulkan instance/device/surface/swapchain/presentation/render output.
- Engine systems, gameplay, assets, save/load, or final release readiness.

## Build

GitHub Actions runs `.github/workflows/android-build.yml` on pushes to `main` or `feature/**`, pull requests to `main`, and manual dispatch. The debug APK is uploaded as `castaway-depths-editor-arm64-debug`.

The debug application ID is `com.castawaydepths.editor.debug`.
