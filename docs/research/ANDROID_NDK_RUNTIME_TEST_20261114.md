# Android ARM64 shared-library runtime load test — 2026-11-14

## Result
User-provided report `CASTAWAY_NDK_RUNTIME_TEST.txt` confirms:
- Test executable compiled successfully: `COMPILE_EXIT=0`.
- Android/Termux runtime loaded the shared library: `DLOPEN=PASS`.
- Exported symbol `castaway_probe` was found and called.
- Function return value: `CASTAWAY_PROBE_RETURN=1`.
- Library closed successfully: `DLCLOSE=PASS`.
- Overall process exit: `RUNTIME_EXIT=0`.

The raw report is preserved at `Recyclebin/CASTAWAY_NDK_RUNTIME_TEST.txt`.

## Qualification
Together with the earlier AArch64 ELF link report, this confirms the small test shared library can be built, loaded by the Termux process runtime, and called through `dlsym`. It does not establish loading from an Android APK process, JNI integration, Android app sandbox packaging, Vulkan device/queue execution, surface/swapchain/presentation, rendering, or gameplay.

## Next gate
Inspect available Vulkan SDK headers and the existing direct-system-loader probe source, then build a narrowly scoped test that selects a graphics-capable queue family and creates/destroys a Vulkan logical device. Keep using `/system/lib64/libvulkan.so`; do not change drivers, vendor files, permissions, symlinks, or global environment. Record exact VkResult values and clean up all Vulkan objects.
