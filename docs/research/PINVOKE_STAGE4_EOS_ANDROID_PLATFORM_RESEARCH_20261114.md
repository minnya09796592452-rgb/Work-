# Stage 4 — EOS Android Platform Research (2026-11-14)

## Purpose and scope

Public-source research was performed after the user asked to investigate the EOS clues on the web. This report records what Epic's official documentation establishes about Android support and how that affects the forensic interpretation of the Stranded Deep Windows EOS bindings. This is reference-only research for CASTAWAY-DEPTHS; it does not authorize reuse of Stranded Deep code, IL, DLLs, binaries, assets, or EOS SDK files.

## Verified official-source findings

1. Epic publishes an official EOS SDK for Android guide. It describes adding the SDK to an Android Studio project, ensuring the project builds C/C++ with the Android NDK, loading the native library with System.loadLibrary("EOSSDK"), and performing Android-specific SDK initialization. The guide states that EOS calls must be made from the same thread because the SDK is not thread-safe. It also requires application background/foreground status notifications through EOS_Platform_SetApplicationStatus.
   - Source: https://dev.epicgames.com/docs/epic-online-services/platforms/android

2. Epic's official Unity-on-Android guide documents a manual Android integration path: obtain the Android SDK package from the Developer Portal, configure Android dependencies, initialize EOS, create the Platform Interface, tick it, and shut it down/release it. This is evidence that Android integration exists; it is not a recommendation to use Unity or any excluded engine in CASTAWAY-DEPTHS.
   - Source: https://dev.epicgames.com/docs/epic-online-services/game-engines/eos-unity-integration/eos-unity-integration-android

3. Epic's release notes for EOS SDK 1.19.0.3 (2026-02-10) state that this and later releases no longer support 32-bit Android or Android OS 6/7 (32-bit or 64-bit). The notes say projects on those operating systems must remain on 1.18.1.2 or lower, while 1.19.0.3+ requires 64-bit Android above OS 7. They also state that Android DynamicSTDC flavor binaries are no longer provided and projects should use StaticSTDC flavor binaries.
   - Source: https://dev.epicgames.com/docs/epic-online-services/release-notes

4. Epic's Platform Interface documentation states that EOS services require the Platform Interface; the game initializes EOS and obtains the Platform Interface handle before accessing other EOS interfaces. It documents application status transitions, including foreground and background-suspended states.
   - Source: https://dev.epicgames.com/docs/epic-online-services/eos-fundamentals/eos-platform-interface

5. Epic's EOS_Initialize API reference states that initialization must precede other SDK calls, should occur only once, and must have a corresponding EOS_Shutdown call.
   - Source: https://dev.epicgames.com/docs/api-ref/functions/eos-initialize

## Interpretation for the audited Windows assembly

- The audited Assembly-CSharp.dll contains 382 linked static IL callsites to the Windows module EOSSDK-Win64-Shipping, across 33 EOS managed wrapper caller types. The wider caller classification found 1,668 Epic-target IL rows, all within Epic.OnlineServices.* wrapper callers; it found zero direct non-EOS/game-type → EOS-wrapper edges in that classification.
- The Android SDK documentation proves that EOS as a product has an Android integration path. It does not make the Windows binary EOSSDK-Win64-Shipping loadable on Android, and it does not prove the audited game's existing managed wrappers are Android-ready.
- Android support would require an Android-compatible EOS native library/package and compatible managed/native bindings, correct ABI and SDK-version selection, Android lifecycle handling, thread discipline, and the product configuration/credentials required by whichever EOS services are actually used.
- The current official release-note constraint is relevant only if considering EOS SDK 1.19.0.3 or later. The audited game's exact EOS SDK version is not established by the current callsite CSV.
- The static evidence still does not establish whether EOS is reached during startup, required for offline play, or used by gameplay/UI code. No conclusion about runtime necessity is authorized.

## User's latest targeted-screen result

User generated this read-only report in Termux:

$HOME/PINVOKE_STAGE3_20261114/STAGE4_EOS_TARGETED_SCREEN_SUMMARY.txt

Observed console summary:
- CANDIDATE_ROWS = 4562
- UNIQUE_CALLER_TYPES = 656
- EOS_RELATED_ROWS = 255
- STATUS = COMPLETE

The script screened Assembly-CSharp.dll rows from il_call_sites.csv for a broad method-name set (reflection-like methods and delegate/event methods) or ldftn/ldvirtftn opcodes, then marked rows EOS-related when one of four CSV text fields contained Epic.OnlineServices or EOS_. These 255 rows are text-matched candidates only, not confirmed EOS reflection/delegate/callback wiring. The screen does not inspect ldstr operands. The complete TXT file has not yet been uploaded into this repository or the conversation, so only the console counts are currently verified here.

The first attempt used d "$HOME/PINVOKE_STAGE3_20261114" and failed with “No command d found”; Python did not run in that attempt. The user corrected the command to cd, reran the script, and the report was successfully created. The original DLLs and Stage 3 CSVs were not modified.

## Current Stage 4 status

- 🟢 Android EOS SDK availability: confirmed by Epic official documentation.
- 🟢 Termux targeted-screen TXT creation: confirmed by user console output.
- 🟡 255 EOS-related candidates: requires row-level review; not semantic confirmation.
- 🟡 EOS version used by the audited game: unknown.
- 🟡 Actual game-level EOS usage and runtime reachability: unresolved.
- 🟡 Whether EOS is necessary for offline startup/gameplay: unresolved.
- 🟡 Compatibility of the audited Windows wrappers with an Android EOS integration: unproven.
- Stage 4 remains IN PROGRESS. Do not mark complete.

## Next actions

1. Ask the user to upload STAGE4_EOS_TARGETED_SCREEN_SUMMARY.txt (the file itself, not a large terminal paste) and inspect all 255 EOS-related candidate rows, distinguishing actual EOS wrapper references from false positives.
2. Continue the already planned read-only inspection of the original Assembly-CSharp.dll IL for ldstr operands containing EOS identifiers (for example Epic.OnlineServices, EOS_, and known wrapper type/method names), recording the enclosing caller type/method and nearby reflection/delegate/callback instructions.
3. If actual EOS usage is found, identify the exact EOS interfaces/services and determine the audited SDK/API version before assessing Android replacement feasibility.
4. Preserve original Managed DLLs and Stage 3 CSVs read-only. Do not rerun closed stages, infer runtime reachability from static callsites, or claim offline necessity/Android compatibility without independent evidence.

## Evidence references

- Epic EOS SDK for Android: https://dev.epicgames.com/docs/epic-online-services/platforms/android
- Epic EOS SDK and Unity Project on Android: https://dev.epicgames.com/docs/epic-online-services/game-engines/eos-unity-integration/eos-unity-integration-android
- Epic EOS SDK Release Notes: https://dev.epicgames.com/docs/epic-online-services/release-notes
- Epic EOS Platform Interface: https://dev.epicgames.com/docs/epic-online-services/eos-fundamentals/eos-platform-interface
- Epic EOS_Initialize API: https://dev.epicgames.com/docs/api-ref/functions/eos-initialize
