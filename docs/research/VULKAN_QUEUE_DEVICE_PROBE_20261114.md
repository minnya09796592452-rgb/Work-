# Android Vulkan queue-family and logical-device probe — 2026-11-14

## Preflight evidence

User supplied `CASTAWAY_VULKAN_PREFLIGHT.txt` (raw copy retained at `Recyclebin/CASTAWAY_VULKAN_PREFLIGHT.txt`).

- Android release/API: 16 / 36.
- ABI / hardware: `arm64-v8a` / `mt6991`.
- Android system Vulkan loader: `/system/lib64/libvulkan.so`; ELF64, little-endian, AArch64, shared object.
- Official Vulkan headers: `$PREFIX/include/vulkan/vulkan.h`.
- Clang and Clang++: 21.1.8, target `aarch64-unknown-linux-android24`.
- Installed supporting packages include `vulkan-headers`, `vulkan-loader`, `vulkan-loader-generic`, `ndk-sysroot`, `ndk-multilib`, and `ndk-multilib-native-stubs`.
- The system loader exports the expected entry points including `vkGetInstanceProcAddr`, `vkCreateInstance`, `vkEnumeratePhysicalDevices`, and `vkCreateDevice`.

This confirms the local prerequisites and loader symbols only. It does not prove a logical device can be created.

## Probe files committed

- C++ source: `native/vulkan/vulkan_device_probe.cpp`.
- Python build/run harness: `scripts/vulkan_device_probe.py`.

The C++ probe uses official Vulkan headers, opens the Android system loader by absolute path, resolves Vulkan entry points with `vkGetInstanceProcAddr`, checks function pointers and VkResult values, queries the loader API version, enumerates physical devices and queue families, selects a queue family with `VK_QUEUE_GRAPHICS_BIT`, creates one logical device with no optional extensions/features, retrieves queue 0, and destroys the device, instance, and loader handle.

The Python harness compiles the source with Termux `clang++` and `-ldl`, runs it, captures stdout/stderr and exit codes, and writes a combined report to `$HOME/CASTAWAY_VULKAN_DEVICE_TEST.txt` by default.

## How to run on the device

From the repository root in Termux:

```sh
python scripts/vulkan_device_probe.py
```

The script prints the report path and compile/runtime exit codes. Upload the resulting `CASTAWAY_VULKAN_DEVICE_TEST.txt` to the repository root on GitHub and send its URL for review. Do not paste a large terminal dump.

## Current evidence status

- 🟢 Android Vulkan preflight: PASS (environment/header/loader-symbol inspection).
- 🟢 Earlier direct-system-loader physical-device enumeration: PASS from separately archived live evidence (Mali-G925-Immortalis MC12; Vulkan 1.3.278; 152 device extensions).
- 🟡 Queue-family enumeration/graphics-family selection: probe prepared; runtime result pending.
- 🟡 Logical-device create/destroy and queue retrieval: probe prepared; runtime result pending.
- 🟡 Surface, swapchain, submit/present, render output: not tested.
- 🟡 APK/JNI build/install/launch, gameplay, and save/load: not demonstrated.
- 🔴 No reproducible blocker identified by this preflight.

## Safety and scope

The probe is diagnostic only. It does not modify drivers, vendor files, system properties, permissions, symlinks, or global environment. It does not create a surface or swapchain and does not submit rendering commands. Do not mark the Vulkan device gate GREEN until the uploaded runtime report confirms successful logical-device creation, queue retrieval, and cleanup.
