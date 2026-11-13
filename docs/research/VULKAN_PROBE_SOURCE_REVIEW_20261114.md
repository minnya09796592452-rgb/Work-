# 2026-11-14 — Direct Android system Vulkan probe source review

## User-supplied source
- Local path: `~/vk_probe.cpp`
- Working directory: `~/PINVOKE_STAGE3_20261114`
- Scope: read-only source review of the C++ probe that opens `/system/lib64/libvulkan.so`, creates a Vulkan instance, enumerates physical devices, prints properties, and lists device extensions.
- The source was supplied in the 2026-11-14 conversation. No device execution output for this exact source revision was included with the source paste.

## Source review — positive findings
- Uses the Android system Vulkan loader by absolute path: `/system/lib64/libvulkan.so`.
- Uses `RTLD_NOW | RTLD_LOCAL`, then resolves `vkGetInstanceProcAddr`.
- Vulkan structure type values are correct: `VK_STRUCTURE_TYPE_APPLICATION_INFO = 0`; `VK_STRUCTURE_TYPE_INSTANCE_CREATE_INFO = 1`.
- Requested API version encoding `(1 << 22) | (3 << 12)` is Vulkan 1.3.0 (variant 0).
- Physical-device type enum mapping 0–4 is correct: OTHER, INTEGRATED_GPU, DISCRETE_GPU, VIRTUAL_GPU, CPU.
- The custom properties struct preserves the standard fields through `pipelineCacheUUID` and reserves a large tail buffer for the remaining properties. The currently printed fields precede that tail.

## Required robustness fixes before treating it as a reliable diagnostic
1. Check that `dlsym(lib, "vkGetInstanceProcAddr")` is non-null before calling it.
2. Check the returned `vkCreateInstance` pointer before invoking it.
3. Check every function pointer returned by `vkGetInstanceProcAddr` (`vkEnumeratePhysicalDevices`, `vkGetPhysicalDeviceProperties`, `vkEnumerateDeviceExtensionProperties`) before use.
4. Check every Vulkan `VkResult`, including both physical-device enumeration calls and both extension enumeration calls. The current code ignores several errors and may print misleading counts or use incomplete vectors.
5. Handle a zero physical-device count and a failed second enumeration explicitly.
6. Query the loader's supported instance API version with `vkEnumerateInstanceVersion` when available, or retry with a lower supported API version if Vulkan 1.3 instance creation fails. A hard-coded 1.3 request can fail on a loader that supports a lower version.
7. Call `vkDestroyInstance` before `dlclose` after successful instance creation.
8. Report the actual `VkResult` integer and the failing function name instead of the generic `vkCreateInstance failed` message.

## ABI/layout caution
The hand-written Vulkan declarations are fragile. In particular, `VkPhysicalDeviceProperties` is a manually approximated ABI structure. The current prefix offsets appear intentionally laid out for the fields printed, but this must be validated against the Vulkan headers/ABI for the target NDK/device before relying on the full structure. Prefer official Vulkan headers where available. Do not interpret the 1,024-byte tail as a typed or validated definition of `VkPhysicalDeviceLimits` or `VkPhysicalDeviceSparseProperties`.

## Evidence boundary
This source can establish hardware enumeration through the Android system loader only if it compiles and executes successfully and the reported device properties are captured. It does not test queue-family selection, logical-device creation, command submission, surface creation, swapchain creation, presentation, or rendered output. It also does not explain why the Termux `vulkaninfo` path previously selected llvmpipe.

Earlier archived evidence already records a successful direct system-loader probe that enumerated Mali-G925-Immortalis MC12 (Vulkan 1.3.278, vendor 0x13b5, device 0xd8300010, 152 device extensions), while Termux `vulkaninfo --summary` selected Mesa llvmpipe. This new source review must not be mistaken for a new execution result or a replacement for that earlier evidence.

## Current status
- Direct Android system-loader hardware enumeration: GREEN from the previously archived execution evidence.
- This newly pasted source revision: source reviewed; runtime result for this exact revision not yet supplied.
- Surface/swapchain/presentation/rendering: YELLOW / not yet tested.
- Termux default Vulkan loader path selecting Mali rather than llvmpipe: YELLOW / unresolved.
- No driver, system property, permission, symlink, or vendor-file changes authorized or performed.

## Next action
Wait for the user to compile/run this exact `vk_probe.cpp` revision and provide the complete stdout/stderr plus the compile command/result. Review that evidence, then proceed only to the next isolated Vulkan capability/queue/surface test as directed. Keep system and vendor configuration unchanged.
