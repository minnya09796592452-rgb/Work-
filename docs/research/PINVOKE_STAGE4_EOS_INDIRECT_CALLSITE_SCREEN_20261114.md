# Stage 4 — Indirect EOS Callsite Screening (2026-11-14)

## Source and scope

User supplied the output of a read-only first-pass screen over `$HOME/PINVOKE_STAGE3_20261114/il_call_sites.csv`, filtered to `assembly == Assembly-CSharp.dll`. The screen searched call targets by a broad reflection method-name set, delegate/event method names or `ldftn/ldvirtftn` opcodes, and callback-like prefixes (`AddNotify`, `RemoveNotify`, `Register`, `Unregister`). It printed only the first 120 matched rows.

## Observed results

- Reflection candidates: **1,586 rows**, **398** unique caller types.
- Delegate candidates: **2,980 rows**, **494** unique caller types.
- Callback candidates: **17 rows**, **5** unique caller types.
- Total matched CSV rows after per-row category de-duplication: **4,579**.
- Category totals sum to **4,583**, four higher than the 4,579 unique matched rows; therefore at least four rows were counted in more than one category. Category counts must not be summed as distinct rows.
- Only the first 120 candidate rows were printed; no complete candidate listing or EOS-specific filtered partition was supplied.

## Interpretation and limitations

These are broad screening candidates, **not confirmed EOS indirect calls**.

The sample contains clear non-EOS patterns:
- `Beam.Serialization.Json.JObject.GetField` is a JSON/object field accessor, not evidence of .NET reflection.
- `GetTypeFromHandle` is metadata/type-handle access; by itself it does not show EOS type lookup.
- `ldftn` commonly creates ordinary managed delegates to game methods such as `GameTime_HourTick`, `Camera_OnPreCull`, and event handlers; it does not prove an EOS callback.
- `Combine`, `Remove`, and `Invoke` are ordinary C# event/delegate operations.
- Callback-like prefixes can match unrelated registration APIs; the 17 rows have not been shown to target EOS.

The `reflection` bucket also includes generic `Invoke` matches that are actually delegate invocation in the sample. The screen is method-name/opcode based and does not resolve declaring assemblies/types robustly enough to classify semantics.

No EOS-specific reflection, string-based type/method lookup, EOS delegate wiring, or EOS callback registration is confirmed by this output. The output does not include `ldstr` operands because it was generated from the call-instruction CSV; string-based lookup requires a separate original-IL inspection.

## Status

- Stage 1: 🟢 PASS.
- Stage 2: 🟢 PASS / CLOSED.
- Stage 3 scanner/declaration validation: 🟢 PASS; MemberRef/MethodSpec association completeness remains 🟡 (300,068 incomplete identities); two compressed-integer warnings remain unattributed.
- Stage 4 EOS wrapper/native boundary: 🟢 verified (382 linked P/Invoke callsites across 33 EOS wrapper caller types).
- Stage 4 direct non-wrapper game-type → EOS-wrapper call edges: 🟡 none found in the 1,668 Epic-target rows examined.
- Stage 4 broad indirect callsite screen: 🟢 first-pass scan executed; semantic/EOS attribution remains 🟡 unresolved.
- No confirmed runtime reachability, startup dependency, offline necessity, or Android compatibility conclusion.

## Next action

Do a targeted, read-only original-IL inspection of `Assembly-CSharp.dll` for `ldstr` operands containing EOS-related identifiers (for example `Epic.OnlineServices`, `EOS_`, and known wrapper type/method names), and correlate each hit with its enclosing caller type/method and nearby reflection/delegate/callback instructions. Separately narrow the current callsite candidates by resolving the actual target declaring type/assembly and excluding ordinary JSON accessors and ordinary game-event delegates. Do not modify original DLLs or Stage 3 CSVs; do not mark Stage 4 complete from this broad screen.
