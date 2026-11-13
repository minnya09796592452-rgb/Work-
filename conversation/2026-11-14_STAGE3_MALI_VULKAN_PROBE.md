# 2026-11-14 — Stage 3 live Mali Vulkan probe (Termux)

## Verified workspace
- Termux working directory: `~/PINVOKE_STAGE3_20261114`
- Device/platform from earlier live diagnostics: Android 16, model `25060RK16C`, SoC/platform `mt6991`.
- This entry records only the new read-only diagnostics and probe output supplied on 2026-11-14. No device configuration or project files were changed by these commands.

## Kernel device nodes
Command:
```sh
ls -l /dev/mali* /dev/kgsl* 2>/dev/null
```
Observed: no matching device-node lines were printed. This does not by itself prove the GPU is unavailable; access may be mediated by Android/vendor services and the system Vulkan loader.

## Vendor/system driver evidence
The search under `/vendor/lib64`, `/system/lib64`, `/odm/lib64`, and `/apex` found:
- `/vendor/lib64/egl/libGLES_mali.so`
- `/vendor/lib64/egl/mt6991/libGLES_mali.so`
- `/vendor/lib64/hw/mt6991/vulkan.mali.so`
- `/vendor/lib64/hw/vulkan.mali.so`
- `/system/lib64/libvulkan.so`
- Additional ARM Mali platform/config libraries.

Live Android properties:
- `ro.board.platform=mt6991`
- `ro.hardware=mt6991`
- `ro.hardware.egl=mali`
- `ro.hardware.vulkan=mali`
- `ro.hwui.use_vulkan=true`
- `init.svc.gpu=running`
- `ro.opengles.version=196610`
These properties corroborate that the Android system graphics stack is configured for Mali. They are not, alone, proof that a particular Termux process is using the hardware driver.

## Direct system-loader Vulkan probe
A C++ probe was written to `~/vk_probe.cpp`, compiled with:
```sh
clang++ -std=c++20 -O2 ~/vk_probe.cpp -ldl -o ~/vk_probe
~/vk_probe
```
The probe explicitly called `dlopen("/system/lib64/libvulkan.so", RTLD_NOW | RTLD_LOCAL)`, resolved Vulkan entry points, created an instance, and enumerated physical devices and device extensions.

Observed output:
- `[OK] Loaded /system/lib64/libvulkan.so`
- Non-fatal diagnostic: `Could not open module param file '/sys/module/mali_kbase/parameters/large_page_conf'`
- Physical devices found: 1
- Device: `Mali-G925-Immortalis MC12`
- Device type: integrated GPU
- Vulkan API: 1.3.278
- Vendor ID: `0x13b5` (ARM)
- Device ID: `0xd8300010`
- Driver version: `0x0c401000`
- Device extensions: 152

The output lists support for, among others, dynamic rendering, synchronization2, timeline semaphores, buffer device address, ASTC HDR texture compression, fragment shading rate, Android hardware-buffer external memory, swapchain, and ARM shader-core extensions. The exact extension list is preserved in the originating conversation output.

## Reconciliation with the earlier Termux Vulkan report
Earlier in the same live session, `$PREFIX/bin/vulkaninfo --summary` enumerated:
- `llvmpipe (LLVM 21.1.8, 128 bits)`
- `DRIVER_ID_MESA_LLVMPIPE`
- CPU device type.

Therefore the evidence now establishes two distinct outcomes:
1. The direct probe using Android's `/system/lib64/libvulkan.so` enumerates the real Mali-G925-Immortalis MC12 hardware.
2. The Termux `vulkaninfo` invocation enumerated Mesa llvmpipe software Vulkan.

This is a loader/driver-selection-path discrepancy. Do not describe ordinary Termux `vulkaninfo` as hardware accelerated until that path is separately made to select and verify the Android system loader.

## What is proven — and what is not
**Proven by this probe**
- The Android system Vulkan loader can be opened from the Termux process.
- Through that loader, Vulkan instance creation and physical-device enumeration identify the Mali-G925-Immortalis MC12.
- The enumerated Mali device reports Vulkan 1.3.278 and 152 device extensions.

**Not yet proven**
- Surface creation, swapchain creation, queue submission, presentation, or rendered output.
- Stable GPU execution under the intended application/runtime lifecycle.
- Correct operation of any custom renderer or game engine on this device.
- That the default Termux Vulkan loader path uses Mali rather than llvmpipe.

## Safe next diagnostic gate
Keep the current device and driver configuration unchanged. Next, validate the direct system-loader path with a small, isolated Vulkan capability/queue/surface test, and separately determine why the Termux loader selects llvmpipe. Do not modify symlinks, environment variables globally, vendor files, permissions, or drivers until the read-only loader/ICD inspection is complete and the user explicitly authorizes a change.

## Provenance
All device outputs and probe results above were supplied directly by the user in the 2026-11-14 conversation. This archive does not claim independent execution on the device.
