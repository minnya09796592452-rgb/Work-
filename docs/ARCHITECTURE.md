# Architecture — CASTAWAY DEPTHS

## Runtime layers
1. Android shell: Activity/lifecycle, permissions, window/surface, input, JNI.
2. Native engine: C++ runtime, memory/resource management, logging, configuration, asset loader.
3. Renderer: Android system Vulkan loader/driver, device capability discovery, surface, swapchain, command submission, synchronization, presentation, shaders/materials, adaptive resolution.
4. Original game systems: world simulation, survival, inventory, crafting, interaction, physics, ocean, weather, AI, audio, save/load.
5. Game data: original or properly licensed assets, definitions, world data, localization, manifests, checksums.

## Temporary Editor/Control APK
Development-only interface for diagnostics, configuration, asset inspection, test scenes, and controlled subsystem tests. Do not ship its development-only tools in the final release unless deliberately designed as player-facing functionality.

## Final APK
A separate production build containing the Android shell, native engine libraries, required runtime resources, and completed original game.

## Native library placement
Package native libraries in the APK under the ABI path, e.g. `lib/arm64-v8a/libcastaway_engine.so`. Do not place executable `.so` files in OBB.

## Data staging and OBB
`obb/Castaway Depths/` is the repository/Termux staging directory, not a final OBB binary. A later packaging step must create the Android-compatible expansion archive or use a modern asset-delivery method. The final packaging choice depends on distribution channel and package ID.

## Graphics
Use capability-based profiles (Ultra, High, Balanced, Compatibility). Select texture formats, shader variants, render resolution, and effects using device capabilities and measured performance; do not assume every Android device matches the initial target GPU.
