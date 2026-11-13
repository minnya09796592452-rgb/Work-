# Stage 4 EOS zero-result diagnostic — 2026-11-14

## Source and scope
User supplied the Termux output from the read-only diagnostic against:
`$HOME/PINVOKE_STAGE3_20261114/il_call_sites.csv`.

This records only the values printed by that command. It does not alter the source CSV or any original Managed DLL.

## Observed results
- Total CSV rows: **695,709**.
- Rows where `assembly == Assembly-CSharp.dll`: **82,804**.
- Rows whose `target_type` contains the string `Epic`: **1,668**.
- Rows whose `target_assembly` contains `Epic`: **0**.
- Rows matching either Epic target field: **1,668**.
- For the 82,804 game-assembly rows, target assembly values:
  - empty string: **48,640**
  - `Assembly-CSharp|0.0.0.0|neutral|null`: **34,164**
- Game-assembly target kinds:
  - MemberRef: **41,144**
  - MethodDef: **34,164**
  - MethodSpec: **7,496**
- The 30-row sample of Epic-target rows shows target types such as `Epic.OnlineServices.BoxedData`, `Epic.OnlineServices.Handle`, and `Epic.OnlineServices.Helper`; the displayed caller types are also `Epic.OnlineServices.*`. One sample call in `Epic.OnlineServices.Helper` targets the native P/Invoke entrypoint `EOS_EResult_IsOperationComplete` through `EOSSDK-Win64-Shipping`.

## Interpretation
- The earlier `STAGE4_GAME_TO_EOS_WRAPPER_AUDIT` result (0 matches) is consistent with its exclusion of callers whose type begins `Epic.OnlineServices.`: the displayed target sample is wrapper-internal EOS code, not evidence of game-specific callers.
- The current diagnostic establishes **1,668 rows targeting a type whose name contains Epic** inside `Assembly-CSharp.dll`; it does not yet establish how many have non-wrapper caller types. The command printed only the first 30 samples, not a complete caller-type classification.
- `target_assembly` contains no Epic string because these EOS wrapper types are defined inside the same `Assembly-CSharp.dll`; the assembly identity shown is `Assembly-CSharp|0.0.0.0|neutral|null`.
- These are static IL references. They do not prove runtime invocation, startup dependency, offline-play necessity, or Android compatibility.

## Status
- 🟢 CSV diagnostic completed and raw counts captured.
- 🟡 Game-type-to-EOS-wrapper call graph remains unresolved until all 1,668 Epic-target rows are classified by caller type (EOS wrapper vs non-wrapper).
- 🔴 No confirmed failure is established by this diagnostic.

## Next action
Run a read-only full classification over all 1,668 Epic-target rows in `il_call_sites.csv`: count EOS-wrapper callers versus non-EOS callers; list every non-EOS caller type and its target wrapper type/method; separately count calls whose caller type is blank or malformed. Do not modify original DLLs or CSVs, rerun closed stages, or infer runtime reachability.
