# Project State — CASTAWAY DEPTHS

Last updated: 2026-11-13

## Identity
- Game: CASTAWAY DEPTHS
- Type: original native Android survival game
- Repository: https://github.com/devmyatmin/Read-CASTAWAY-DEPTHS
- Temporary development app: Editor/Control APK
- Release product: standalone final Android game APK
- Historical codename: NYAMIST only

## Non-negotiable direction
- Build the game from the ground up; this is not porting.
- No Stranded Deep Windows executable/runtime, source code, DLLs, or protected assets.
- Treat Stranded Deep only as a high-level reference.
- Create original gameplay logic, names, art, audio, models, world, UI, and story, or document valid licenses for third-party content.

## APK lifecycle
The temporary Editor/Control APK is a development tool only. It controls and tests the engine and game systems. Once the game is complete and validated, create the final standalone APK and retire the temporary editor APK.

## Target hardware
- Initial performance target: user's Mali-G925-class Android hardware.
- Previous probe enumerated Mali-G925-Immortalis MC12, Vulkan 1.3.278, vendor 0x13B5, device 0xD8300010.
- This proves loader/device enumeration only, not surface presentation or rendering.
- Support other Android devices with capability detection and adaptive quality profiles.

## Baseline at repository initialization
- Private repository, default branch main; admin/push access confirmed.
- Existing README was a placeholder.
- No engine, APK, renderer, gameplay implementation, or final OBB archive is claimed to exist yet.

## Validation
Mark each gate PASS only with captured evidence: BUILD, INSTALL, LAUNCH, VULKAN DEVICE, SURFACE, SWAPCHAIN, SUBMIT/PRESENT, RENDER OUTPUT, GAMEPLAY, SAVE/LOAD, DEVICE COMPATIBILITY.


## Current research activity — 2026-11-13
- Active investigation: forensic inventory of P/Invoke declarations across the user's local Stranded Deep managed assemblies.
- Audit package prepared locally as `CASTAWAY_PInvoke_Audit.zip`; awaiting upload into the analysis conversation before metadata inspection.
- The DLL list and hashes are documented in `docs/research/PINVOKE_DLL_AUDIT.md`.
- This is reference-only research. No third-party DLLs or code are to be added to the original CASTAWAY DEPTHS implementation.
- Historical NYAMIST WSI work remains diagnostic/reference material; no production renderer or visible game rendering is claimed.
