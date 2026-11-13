# Stage 4 — targeted EOS screen, original IL string scan, and platform evidence

Date: 2026-11-14
Source repository: https://github.com/devmyatmin/Read-CASTAWAY-DEPTHS
Branch: main
Evidence scope: user-supplied Termux output and web research reviewed in the 2026-11-14 conversation.

## 1. Targeted EOS screen report

The user supplied the GitHub file:
https://github.com/devmyatmin/Read-CASTAWAY-DEPTHS/blob/10bc86d178caaffe9ace979646d133c31a5dcf9c/STAGE4_EOS_TARGETED_SCREEN_SUMMARY.txt

Previously recorded summary:
- Candidate rows: 4,562
- Unique caller types: 656
- EOS-related text-match rows: 255

The 255 matches are a candidate screen, not proof that 255 calls are EOS API usage. The visible candidate population includes Epic.OnlineServices wrapper types such as SessionsInterface, LobbyInterface, ConnectInterface, and AuthInterface. Wrapper-internal reflection/delegate/callback machinery must not be mistaken for game-specific gameplay callers.

Important schema caveat recorded in the prior audit: il_call_sites.csv records call instructions, not ldstr operands. The targeted text screen is not a substitute for original IL string analysis.

## 2. Original Assembly-CSharp.dll identity

The user searched Download and found five copies of Assembly-CSharp.dll. All five had identical size and SHA-256:
- Size: 3,432,448 bytes
- SHA-256: d5501ae7a2c09499cdf569c5b52578401f039c2f4aa5e7e9240c22faeef214bc

Paths:
1. /storage/emulated/0/Download/PC GAMES/StrandedDeep/Stranded_Deep_Data/Managed/Assembly-CSharp.dll
2. /storage/emulated/0/Download/PC GAMES/Game Zip/Stranded Deep/Stranded_Deep_Data/Managed/Assembly-CSharp.dll
3. /storage/emulated/0/Download/PC GAMES/Game play/Stranded Deep/Stranded_Deep_Data/Managed/Assembly-CSharp.dll
4. /storage/emulated/0/Download/3zip/Stranded Deep/Stranded_Deep_Data/Managed/Assembly-CSharp.dll
5. /storage/emulated/0/Download/TERMUX-PRE-CLEAN-BACKUP/stranded_native_port/logic/Assembly-CSharp.dll

The selected audit input was the Game Zip copy (path 2). The working directory was ~/PINVOKE_STAGE3_20261114, so a relative ./Stranded_Deep_Data/... path was incorrect; the absolute source path was used thereafter.

## 3. Read-only original IL ldstr scan — user-run result

The user created $HOME/eos_ldstr_audit.py. Syntax compilation and dependency import checks passed:
- python -m py_compile: PASS
- dnfile/dncil import: PASS
- SCRIPT_CHECK=PASS

The script checked the DLL SHA-256 against the expected baseline and scanned the 23,276 MethodDef rows with method bodies. User-supplied result:

- status: COMPLETE
- methoddef_rows: 26,143
- methods_with_rva_scanned: 23,276
- methods_with_parsed_bodies: 23,276
- eos_ldstr_hits: 0
- parse_error_count: 0
- parse_errors: []
- DLL size: 3,432,448 bytes
- DLL SHA-256: d5501ae7a2c09499cdf569c5b52578401f039c2f4aa5e7e9240c22faeef214bc

Output files:
- $HOME/PINVOKE_STAGE4_LDSTR_20261114/EOS_LDSTR_HITS.csv
- $HOME/PINVOKE_STAGE4_LDSTR_20261114/EOS_LDSTR_SUMMARY.json
- $HOME/PINVOKE_STAGE4_LDSTR_20261114/TERMUX_RUN.txt

The CSV contains only its header, confirming zero matched rows.

Interpretation: no strings matching the scanner's EOS pattern were found in ldstr operands of the scanned Assembly-CSharp.dll method bodies. This is a bounded negative result only. It does NOT rule out TypeRef/MemberRef/AssemblyRef metadata, ldtoken, reflection by nonmatching strings, generated bindings, delegates/callbacks, native plugin loading, or EOS references in other managed/native files. It also does not disprove the previously verified 382 Assembly-CSharp → EOS native P/Invoke wrapper callsites; those are a separate static boundary.

## 4. Web evidence review — conflicting EOS evidence, stronger Photon/Bolt clues

The conversation reviewed SteamDB public Windows/macOS/Linux depot manifests and public middleware references.

Reported managed networking-related filenames in desktop manifests:
- Photon3Unity3D.dll
- PhotonBolt.dll
- PhotonRealtime.dll
- bolt.dll
- bolt.user.dll
- udpkit.dll
- udpkit.platform.photon.dll
- udpkit.platform.null.dll

These suggest Photon/Bolt/UDPKit are important networking dependencies in the reviewed desktop builds. They are web-derived clues and still need exact-build local verification. Do not assert that they replace EOS without correlating the user's actual installed build.

EOS evidence conflicts:
- PCGamingWiki was reported to list EOSSDK-Win64-Shipping.dll under Stranded_Deep_Data/Plugins/x86_64 and to describe EOS as multiplayer middleware.
- The reviewed SteamDB manifests reportedly did not show that native EOS binary in their listed Windows/macOS/Linux depot contents.
- This may be a version/build mismatch, incomplete manifest interpretation, or outdated third-party documentation. It is unresolved.
- The game's 2021 online co-op announcement establishes co-op feature history, not which networking provider implements it in the user's exact build.

Platform/build evidence:
- Public desktop depots were found for Windows, macOS, and Linux; no Android depot was identified in the reviewed evidence.
- The audited game uses Unity Mono managed assemblies; desktop Unity runtime and graphics/native plugin dependencies are not directly Android-compatible.
- Epic official documentation confirms an Android EOS SDK exists. This does not make EOSSDK-Win64-Shipping.dll usable on Android or prove that this game requires EOS.
- Previously recorded official Epic SDK release-note caveat: EOS SDK 1.19.0.3+ drops 32-bit Android and Android OS 6/7 support; requires 64-bit Android above OS 7 and StaticSTDC flavor. Exact game SDK version remains unknown.

## 5. Current evidence-based interpretation

- Assembly-CSharp EOS native P/Invoke wrapper boundary: 382 linked static callsites previously verified.
- Direct non-EOS/game-specific caller → EOS-wrapper edges: 0 in the prior full classification of 1,668 Epic-target call rows; all were wrapper-internal.
- Targeted EOS text screen: 255 candidates among 4,562 rows; not all proven EOS calls.
- Original Assembly-CSharp ldstr scan: 0 EOS-pattern string hits; 23,276/23,276 method bodies parsed; zero parse errors.
- Photon/Bolt/UDPKit: strong desktop-manifest clues; exact local inventory still pending.
- EOS runtime reachability, startup necessity, offline-play necessity, and Android compatibility: NOT PROVEN.
- No basis yet to remove EOS, replace Photon/EOS, or change architecture.

## 6. Next action — exact-build local inventory, then metadata audit

User has been given a single Termux command to inventory filenames under:
 /storage/emulated/0/Download/PC GAMES/Game Zip/Stranded Deep/Stranded_Deep_Data

It searches for EOS/Epic/Photon/Bolt/UDPKit/Steam-related files, lists all top-level Managed DLL names, and rechecks Assembly-CSharp.dll SHA-256.

When the user provides that output:
1. Compare exact local files with desktop manifest clues; distinguish absent files from files in other locations and version mismatch.
2. Inspect TypeRef, MemberRef, AssemblyRef, MethodSpec, ModuleRef, and ImplMap in Assembly-CSharp.dll read-only.
3. Inventory and hash relevant native plugin files; identify platform-specific binaries from filenames and metadata, not assumptions.
4. Correlate managed wrappers with game callers and plugin loading paths.
5. Only then classify EOS/Photon/Bolt/Steam dependencies by feature and platform. Keep runtime reachability separate from static references.

Do not rerun closed Stage 1/2. Preserve original Managed DLLs and Stage 3 CSVs read-only. No third-party Stranded Deep code, IL, binaries, or assets may be imported into CASTAWAY DEPTHS. CASTAWAY DEPTHS remains an original native Android survival game; the Stranded Deep audit is reference-only. Follow the user's lead and wait for their next instruction/output.
