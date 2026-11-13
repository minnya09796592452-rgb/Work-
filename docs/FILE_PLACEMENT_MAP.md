# File Placement Map — CASTAWAY DEPTHS

Last updated: 2026-11-13

This map defines where future original project files belong. It does not claim that the directories or implementations already exist. Create a directory when its first real file is ready.

## Repository root
- `README.md`: project entry point and links to current documentation.
- `docs/`: project state, architecture, policies, audits, test evidence, and file-placement rules.
- `scripts/`: Termux setup, build, validation, packaging, and maintenance scripts.
- `src/`: original native source code only.
- `tests/`: repeatable unit, integration, Vulkan, and device tests.
- `obb/Castaway Depths/`: game data staging only; not native source code and not yet a final OBB archive.

## Native source-code placement
| File responsibility | Required destination |
|---|---|
| Android Activity, lifecycle, permissions, window/surface, JNI bridge | `src/android/` |
| Engine entry point, runtime loop, logging, configuration, memory/resource management | `src/engine/` |
| Vulkan instance/device/capability discovery | `src/render/vulkan/device/` |
| Vulkan surface, swapchain, synchronization, command submission, presentation | `src/render/vulkan/presentation/` |
| Render passes, pipelines, descriptors, buffers, images, materials | `src/render/vulkan/core/` |
| Original GLSL/HLSL shader source | `src/render/shaders/source/` |
| Compiled SPIR-V produced from CASTAWAY DEPTHS shader source | `src/render/shaders/compiled/` |
| Original gameplay systems | `src/gameplay/` |
| Player controller and interaction | `src/gameplay/player/` and `src/gameplay/interaction/` |
| Survival, inventory, crafting | `src/gameplay/survival/`, `src/gameplay/inventory/`, `src/gameplay/crafting/` |
| World, terrain, biome simulation | `src/world/` |
| Original ocean and water simulation | `src/world/ocean/` |
| Physics and collision | `src/physics/` |
| Audio runtime and mixer | `src/audio/` |
| Save/load and serialization | `src/save/` |
| Editor/Control APK tooling | `tools/editor/` |

## Test and evidence placement
- C++ unit/integration tests: `tests/unit/` and `tests/integration/`.
- Vulkan API/device tests: `tests/vulkan/`.
- Device logs, screenshots, and measured test evidence: `docs/evidence/<subsystem>/<date>/`.
- Research summaries and audit notes: `docs/research/`.
- Do not put experimental test executables or temporary build output in `src/`.

## Game-data staging placement
The existing staging root is `obb/Castaway Depths/`.

| Data type | Destination |
|---|---|
| Original textures | `assets/textures/` |
| Original meshes/models | `assets/meshes/` |
| Materials and material definitions | `assets/materials/` |
| Audio files | `assets/audio/` |
| Animation data | `assets/animations/` |
| UI images/fonts/data | `assets/ui/` |
| Biome definitions | `worlds/biomes/` |
| Location/region definitions | `worlds/locations/` |
| Terrain/world chunks | `worlds/terrain/` |
| Item definitions | `definitions/items/` |
| Character definitions | `definitions/characters/` |
| Creature definitions | `definitions/creatures/` |
| Crafting recipes | `definitions/crafting/` |
| Survival tuning/data | `definitions/survival/` |
| Shader source intended for data-driven loading | `shaders/source/` |
| Compiled shader data intended for runtime loading | `shaders/compiled/` |
| Runtime configuration | `config/` |
| Localization | `localization/` |
| Asset manifest and checksums | `manifests/asset_manifest.csv` and `manifests/checksums.sha256` |

Native executable libraries such as `.so` belong in the APK ABI library location, not in OBB. The final expansion archive or asset-delivery package must be produced by a later packaging step.

## Reference-only research material
- Do not upload decompiled game code, IL dumps, proprietary game assets, or copied implementation files into the CASTAWAY DEPTHS source tree.
- If a research note is needed, write an abstract, code-free summary under `docs/research/reference-audits/`.
- Every research note must label the source as REFERENCE ONLY and state that CASTAWAY DEPTHS implementation must be independently authored.
- Do not place reference-game scripts in `src/gameplay/`, `src/render/`, or `obb/Castaway Depths/`.

## Intake rule for Termux uploads
Before moving a logic file, identify its origin and purpose:
1. Original CASTAWAY DEPTHS source -> the matching `src/` subsystem above.
2. CASTAWAY DEPTHS test code -> matching `tests/` subsystem.
3. Build/setup/validation automation -> `scripts/`.
4. Code-free analysis/report -> `docs/research/` or `docs/evidence/`.
5. Third-party/reference-game extracted code or assets -> do not add to this repository.

Keep the original filename where practical. Record the source path, destination path, file type, and SHA-256 in the upload log/manifest.
