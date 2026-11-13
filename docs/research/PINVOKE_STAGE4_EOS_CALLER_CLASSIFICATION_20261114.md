# Stage 4 EOS caller classification — 2026-11-14

## Source and scope
User supplied the complete terminal output from the read-only classification command against:
`$HOME/PINVOKE_STAGE3_20261114/il_call_sites.csv`.

The classification covered rows in `Assembly-CSharp.dll` whose `target_type` contains `Epic`. No source DLL or scanner CSV was modified.

## Verified counts
- `TOTAL_EPIC_TARGET_ROWS = 1,668`
- `EOS_WRAPPER_CALLER_ROWS = 1,668`
- `NON_EOS_CALLER_ROWS = 0`
- `BLANK_CALLER_ROWS = 0`
- `UNIQUE_EOS_WRAPPER_TYPES = 317`
- `UNIQUE_NON_EOS_CALLER_TYPES = 0`
- `UNIQUE_GAME_TO_EOS_EDGES = 0`
- The EOS-wrapper caller-type frequency listing was printed; it contains 317 distinct `Epic.OnlineServices.*` types. The output includes long wrapped terminal lines, so the counts above are the normalized facts relied upon here.

The row partition reconciles exactly: 1,668 wrapper callers + 0 non-EOS callers + 0 blank callers = 1,668 total Epic-target rows.

## Interpretation
- Every one of the 1,668 static IL rows targeting an Epic-named type is called from an `Epic.OnlineServices.*` wrapper type.
- This diagnostic found no direct non-EOS/game-specific caller type → EOS-wrapper edge in the scanned IL callsite dataset.
- It also found no blank caller-type rows in this filtered set.
- This does **not** establish that the game never uses EOS. Calls may be indirect or dynamically connected through reflection, delegates, callback registration, string-based lookup, generated bindings, or other mechanisms not represented as direct call edges in this classification.
- Earlier evidence remains: `Assembly-CSharp.dll` contains 382 linked static IL callsites into `EOSSDK-Win64-Shipping`. Those calls originate at the managed EOS wrapper/native boundary; they do not independently prove that game-specific gameplay/UI code invokes the wrappers.
- Static references do not prove runtime execution, startup reachability, EOS necessity for offline play, or Android binary/API compatibility.

## Status
- 🟢 Classification arithmetic and row partition: PASS.
- 🟢 All 1,668 filtered rows have EOS-wrapper callers; no blank callers.
- 🟡 Direct game-specific caller linkage: no edge found by this method; broader usage remains unresolved.
- 🔴 No confirmed build/runtime failure is established by this output.

## Next action
Perform a read-only audit for indirect EOS entry paths in the original managed metadata/IL: reflection APIs and string-based type/method lookup involving `Epic.OnlineServices`, EOS wrapper type/method names, delegate construction/invocation, and callback registration. Correlate any matches to concrete caller methods. Preserve the distinction between static evidence and runtime reachability. Do not modify original Managed DLLs or generated Stage 3 CSVs; do not restart closed stages.
