# Next Conversation Handoff — CASTAWAY DEPTHS

**Read this file first at the start of the next conversation.** It is the current concise handoff; then consult `docs/PROJECT_STATE.md` and the detailed P/Invoke report.

## Project authority and boundaries
- User is Project Lead / Chief Engineer. Follow the user's lead; implement only requested work and do not redesign unsolicited.
- Assistant role: implementation engineer; preserve the user's decisions and evidence.
- Project: CASTAWAY DEPTHS, original native Android survival game.
- Repository: `devmyatmin/Read-CASTAWAY-DEPTHS`, branch `main`.
- This is original development, not a Stranded Deep port. P/Invoke investigation is reference-only. Do not import or reuse Stranded Deep code, IL, DLLs, binaries, assets, branding, or protected implementation.
- Do not introduce Godot unless the user explicitly reintroduces it.

## ဝါစိမ်းနီ status key
- 🟢 GREEN = confirmed by evidence / PASS.
- 🟡 YELLOW = caution; further inspection required.
- 🔴 RED = required fix; blocking issue.

## Current milestone state — 2026-11-14
| Stage | Status | Meaning |
|---|---|---|
| Stage 1 | 🟢 PASS | Completed |
| Stage 2 | 🟢 PASS | 3,791 P/Invoke rows grouped and reported |
| Stage 3 | 🟡 IN PROGRESS | Assembly-CSharp EOS IL call-site scanner work |
| Stage 4 | 🔴 INCOMPLETE | Not completed; do not mark PASS |

### Stage 3 Scanner — current evidence
- 🟢 Python Variant A and Variant B pass syntax compilation; source differences have been identified and the GitHub review recorded.
- 🟡 Method association based only on method name is not sufficiently robust.
- 🟡 ImplMap errors currently can result in exit code 0; failures must be surfaced as failures.
- 🟡 A declaration-to-ImplMap completeness check is still required.
- 🔴 CSV output is written before MemberRef processing; fix the processing/output order so the required MemberRef data is available before CSV emission.
- No successful end-to-end runtime scan result has been recorded yet. Syntax compilation is not runtime validation.
- Two Stage 3 scanner script variants were supplied in the conversation. The user said the next Termux output would follow; wait for that output and inspect it before claiming a scan result.

## Next action — continue, do not restart
1. Wait for and inspect the user's next Termux output.
2. Review the two supplied scanner variants against the RED/YELLOW findings above.
3. Fix only the identified scanner defects, following the user's direction.
4. Run the scanner against the verified original `Assembly-CSharp.dll` read-only.
5. Verify declaration/ImplMap coverage, MemberRef association, IL operand results, exit status, and generated CSV/report files.
6. Mark Stage 3 GREEN only after successful runtime evidence and all required checks pass. Keep Stage 4 INCOMPLETE until separately completed.

## Prior verified baseline
- Original `Assembly-CSharp.dll` SHA-256: `d5501ae7a2c09499cdf569c5b52578401f039c2f4aa5e7e9240c22faeef214bc`.
- Metadata validation: 26,143 MethodDef rows, 6,078 MemberRef rows, 1 ModuleRef row, 360 ImplMap/P/Invoke rows.
- Stage 2 grouping: 3,791 rows, 66 native-module groups, 17 assemblies, 33 ABI combinations, 1,976 exact signatures, 196 repeated DLL + entry-point keys, 0 missing-field rows.
- Detailed technical evidence: `docs/research/PINVOKE_GROUP_AUDIT.md`.

**Resume point:** continue from Stage 3 Scanner validation and wait for the user's promised output. Do not restart earlier stages or infer runtime success from syntax checks.
