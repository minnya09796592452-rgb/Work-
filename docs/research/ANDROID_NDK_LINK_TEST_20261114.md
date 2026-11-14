# Android ARM64 shared-library link probe — 2026-11-14

## Result
- Device: Android 16, AArch64, Termux on device model 25060RK16C.
- Compiler: Clang 21.1.8, target reported as `aarch64-unknown-linux-android24`.
- The first link attempt failed because the compiler could not find `asm/types.h`.
- The required header was confirmed at `$PREFIX/include/aarch64-linux-android/asm/types.h`.
- Re-running with `-isystem "$PREFIX/include/aarch64-linux-android"` produced `libcastaway_probe.so`; `LINK_EXIT=0`.
- ELF inspection confirmed ELF64, little-endian, `ET_DYN` shared object, machine AArch64.
- Dynamic dependencies recorded: `liblog.so`, `libdl.so`, `libc.so`; SONAME `libcastaway_probe.so`.
- The `file` utility was absent; `readelf` provided the ELF evidence.
- Clang's `-print-sysroot` option was unsupported by this build. That diagnostic did not prevent the shared-library link.

## Qualification
This proves a minimal AArch64 Android-target shared-library link using the installed Termux NDK-related sysroot/stub packages. It does **not** prove that Android's runtime linker can load this output, that JNI works, that an APK builds/installs/launches, or that Vulkan renders. The reported RUNPATH is `/data/data/com.termux/files/usr/usr/lib`; inspect runtime dependency resolution before treating this artifact as app-ready.

## Next gate
Run an isolated `dlopen`/symbol-call smoke test for the produced library. Keep all source/project files unchanged. If that passes, continue with the Vulkan logical-device/queue execution probe on the direct Android system Vulkan loader.

## Raw diagnostic reports
The original verbose reports were moved to `Recyclebin/` to keep the repository root clean:
- `CASTAWAY_NDK_CHECK.txt`
- `CASTAWAY_NDK_LINK_TEST.txt`
- `CASTAWAY_NDK_LINK_TEST2.txt`
