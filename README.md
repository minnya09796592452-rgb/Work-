# CASTAWAY DEPTHS

Original native Android survival game — built from the ground up.

## Project identity
- Game title: CASTAWAY DEPTHS
- Repository: https://github.com/devmyatmin/Read-CASTAWAY-DEPTHS
- Historical development codename: NYAMIST (not the game title)

## Core direction
This is original game development, **not a Stranded Deep port**. Do not integrate its Windows runtime, code, proprietary assets, branding, or protected expressive content. It may be studied only as a high-level genre/gameplay reference. Create original systems and content, or use assets with documented redistribution rights.

## Development workflow
1. Build a temporary Android Editor/Control APK for development, inspection, configuration, and testing.
2. Build the native C++ engine, Android lifecycle/JNI layer, Vulkan renderer, asset pipeline, and original gameplay systems.
3. Stage game data under `obb/Castaway Depths/` in Git and the matching Termux workspace.
4. Validate each subsystem on real Android hardware.
5. When complete, produce and validate the final standalone game APK.
6. Retire the temporary Editor/Control APK from the release workflow.

## Documentation index
- `docs/PROJECT_STATE.md` — identity, constraints, and baseline.
- `docs/ARCHITECTURE.md` — runtime layers and packaging boundaries.
- `docs/ASSET_POLICY.md` — original-content and provenance rules.
- `docs/TERMUX_SETUP.md` — Termux workspace setup.
- `docs/LOGIC_AUDIT.md` — current logic/pipeline findings and external research boundaries.
- `docs/IMPLEMENTATION_STATUS.md` — evidence-based subsystem status and validation gates.
- `docs/FILE_PLACEMENT_MAP.md` — exact destination for each future source/data file.
- `docs/LOGIC_FILE_REGISTER.md` — intake register for files uploaded from Termux.

## Packaging rules
- Native `.so` libraries belong inside the APK under the matching ABI, e.g. `lib/arm64-v8a/`.
- `obb/Castaway Depths/` is a development data-staging tree, not an installable OBB archive.
- If legacy OBB is selected, create the correctly named archive for the final application package. Never put executable native libraries or signing keys in OBB.
- Re-evaluate modern asset-delivery options before store distribution.

## Quality gates
Track BUILD, INSTALL, LAUNCH, VULKAN SURFACE/SWAPCHAIN/PRESENT, RENDER OUTPUT, GAMEPLAY, SAVE/LOAD, and DEVICE COMPATIBILITY separately. A compile success is not proof of runtime success.
