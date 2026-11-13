# 🚨🚨🚨 မူရင်း FULL P/INVOKE 100% အတိအကျရှာဖွေခြင်း 🚨🚨🚨

**ဤအစီအစဉ်သည် နောက် Conversation များအတွက် အဓိကလုပ်ငန်းမှတ်တမ်းဖြစ်သည်။**
**APK / Android port / CASTAWAY DEPTHS implementation ကို ယခုအစီအစဉ်တွင် မစဉ်းစားရ။**
**Project Lead ၏ အမိန့်ကိုသာ လိုက်နာပြီး အဆင့်တစ်ခုချင်းစီကို သက်သေအထောက်အထားဖြင့် စစ်ဆေးရမည်။**

## အခြေအနေအမှတ်အသား
- 🟢 **FULL SUCCESS** — စစ်ဆေးပြီး၊ ရလဒ်အတည်ပြုပြီး။
- 🟡 **စစ်ဆေးနေဆဲ** — အလုပ်စစ်ဆေးဆဲ သို့မဟုတ် အချို့ edge cases မပြီးသေး။
- 🔴 **မပြီးသေး** — မစတင်ရသေး / အတည်ပြုနိုင်သောရလဒ် မရှိသေး။

## အဆင့်လိုက်အစီအစဉ်

### 1. Managed DLL အားလုံး၏ P/Invoke Metadata အပြည့်အစုံ စစ်ဆေးခြင်း
<badge color="success">🟢 FULL SUCCESS</badge>
- Managed DLL 119/119 ကို parse လုပ်ပြီး။
- MethodDef rows 208,570 ခု scan လုပ်ပြီး။
- P/InvokeImpl 3,791 ခု နှင့် ImplMap 3,791 rows ကို တိုက်စစ်ပြီး အရေအတွက်တိတိကျကျ ကိုက်ညီ။
- P/Invoke ပါသော Assembly 17 ခု၊ မပါသော Assembly 102 ခု။
- Native ModuleRef အမည် 66 ခု၊ Module + Entry Point pair 3,563 ခု။
- Signature decode errors 0၊ metadata parse errors 0။
- Assembly အားလုံး၏ zero-result ပါဝင်မှုကိုပါ စစ်ဆေးပြီး။
**အဆင့် 1 သည် metadata declaration inventory အတွက် FULL SUCCESS ဖြစ်သည်။**

### 2. P/Invoke Declaration တစ်ခုချင်းစီ၏ အဓိပ္ပာယ်နှင့် ABI အသေးစိတ် အတည်ပြုခြင်း
<badge color="warning">🟡 စစ်ဆေးနေဆဲ</badge>
- Declaring type, method, return type, parameter types, native module, entry point တို့ကို row-by-row ပြန်စိစစ်ရန်။
- Calling convention, charset, ExactSpelling, SetLastError, BestFitMapping, ThrowOnUnmappableChar နှင့် raw MappingFlags များကို ခွဲခြားအတည်ပြုရန်။
- Structure layout, pointer width, string marshalling, callback/delegate, ownership/lifetime ဆိုင်ရာ metadata clues များကို မှတ်တမ်းတင်ရန်။
- Generic TypeSpec references နှင့် signature edge cases များကို ဖြေရှင်းရန်။
- managed_pinvoke_all_3791.csv နှင့် managed_module_refs_all.csv ကို အဓိက row-level evidence အဖြစ် အသုံးပြုရန်။

### 3. Runtime Call-Site နှင့် Dynamic Native Loading စစ်ဆေးခြင်း
<badge color="warning">🟡 စစ်ဆေးနေဆဲ — ပထမ IL scan ရလဒ်ရရှိ၊ P/Invoke join မပြည့်စုံသေး</badge>
- Declaration 3,791 ခုထဲမှ ဘယ် methods တွေကို call-site မှ တကယ်ခေါယ်သလဲ စစ်ရန်။
- Call graph / caller-callee chain များကို ရှာဖွေပြီး runtime reachability ကို ခွဲခြားရန်။
- Platform conditionals, feature flags, optional integrations, offline/online pathways များကို စိစစ်ရန်။
- LoadLibrary, GetProcAddress, dlopen, dlsym နှင့် အလားတူ dynamic-loading လမ်းကြောင်းများကို ရှာဖွေရန်။
- Metadata declaration ရှိခြင်းနှင့် runtime တွင် အမှန်တကယ် load/call ဖြစ်ခြင်းကို သီးခြား status သတ်မှတ်ရန်။

#### Stage 3 — Termux ပထမ full-scan ရလဒ် (2026-11-14)
User ran `pinvoke_stage3_scan.py` against the original Managed directory. The first attempt was manually interrupted with Ctrl-C while parsing IL; the subsequent run completed.

**Observed output:**
- DLL files found: 119
- Managed files scanned: 119
- MethodDefs seen: 208,570
- Method bodies parsed: 182,514
- IL call instruction rows: 695,709
- Opcode counts: call 355,546; callvirt 266,203; newobj 67,817; ldftn 5,933; ldvirtftn 210
- P/Invoke callsite/declaration CSV rows: 27
- Dynamic loader CSV rows: 27
- `pinvoke_declarations_seen`: **0** (unexpected and inconsistent with the independently verified Stage 1 inventory of 3,791 declarations)
- Two warnings appeared: `invalid compressed int: leading byte: 0xfb`.

**Follow-up dnfile diagnostic on Assembly-CSharp.dll:**
- MethodDef rows: 26,143
- ImplMap rows: **360**
- First five ImplMap rows successfully exposed MemberForwarded method names, ImportName and ImportScope DLL name `EOSSDK-Win64-Shipping`.
- `MemberForwarded.row` returned a MethodDef row object, but `getattr(m, "row_index", None)` returned None. The current scanner's RID lookup relies on Python object identity and/or `row_index`; the zero declaration count indicates this join is failing.
- This diagnostic proves the metadata tables and 360 ImplMap rows are readable in Assembly-CSharp.dll. It does not yet prove the scanner has correctly mapped the methods to MethodDef RIDs.
- The 27 CSV rows must not be interpreted as all P/Invoke declarations or complete dynamic-loader coverage.

**Corrected RID diagnosis (2026-11-14):** User inspected the actual dnfile objects. `MemberForwarded` is `dnfile.codedindex.MemberForwarded`; `vars(mf)` contains `row_index: 2381` and `table: MethodDef`. `mf.row` is a `MethodDefRow`, but its attributes do not expose `row_index`; `dir(m)` confirms no RID/index attribute. Therefore the stable RID is on the coded-index object `mf.row_index`, not on `mf.row`. `mf.table` identifies the target table as `MethodDef`. The scanner's previous object-identity lookup was therefore incorrect. A patched scanner now reads `mf.row_index` only when `mf.table` is the current `md.MethodDef`, and validates the RID against the MethodDef row count. Patched local script SHA-256: `72b182648654f21b5ea9a9c19c33978ab544ed498b33dd93dec9f7310874c3cc`. Python syntax compilation passed locally; runtime validation on Termux is pending.

**Next required action:** replace the Termux scanner with the patched script and run an Assembly-CSharp-only test first. Require `pinvoke_declarations_seen = 360` and `implmap_resolution_errors = 0`; verify the resolved MethodDef RID, managed method name, import DLL, and entry point for all 360 rows. Only then re-run all 119 DLLs. Investigate the two compressed-int warnings separately; do not silently count malformed signatures as successful parse coverage.

**Stage 3 status remains 🟡.** Static IL inventory is now available, but P/Invoke declaration joining and runtime reachability are not complete.

### 4. Native Module / Export Symbol / Function Implementation ဆက်စပ်စစ်ဆေးခြင်း
<badge color="danger">🔴 မပြီးသေး</badge>
- Native module 66 ခုကို တစ်ခုချင်းစီ ခွဲခြားပြီး ရရှိနိုင်သော binary evidence နှင့် ချိတ်ဆက်ရန်။
- PE import/export inventories ကို managed ModuleRef နှင့် P/Invoke entry points များနှင့် cross-reference လုပ်ရန်။
- Named exports နှင့် ordinal-only exports ကို ခွဲခြားစစ်ရန်။
- လိုအပ်သည့် native binary များတွင် function body, calling convention, parameter ABI, data structure နှင့် callback behavior ကို reverse-engineering evidence ဖြင့် အတည်ပြုရန်။
- Native function body မရနိုင်သည့်အခါ ထိုအချက်ကို evidence မရှိ ဟု တိတိကျကျ မှတ်တမ်းတင်ရန်။ ခန့်မှန်းချက်ကို အတည်ပြုချက်အဖြစ် မတင်ပြရ။

## လက်ရှိအတည်ပြုထားသော Audit Artifacts
- FULL_PINVOKE_AUDIT.md
- audit_summary.json
- managed_assembly_summary.csv
- managed_module_refs_all.csv
- managed_pinvoke_all_3791.csv
- native_module_target_summary.csv
- native_pe_export_symbols.csv
- native_pe_import_symbols.csv
- native_pe_summary.csv

## လက်ရှိအခြေအနေ အကျဉ်း
- **အဆင့် 1 — 🟢 FULL SUCCESS**
- **အဆင့် 2 — 🟡 စစ်ဆေးနေဆဲ**
- **အဆင့် 3 — 🟡 ပထမ IL scan ရရှိ၊ P/Invoke RID join ပြင်ရန်ကျန်**
- **အဆင့် 4 — 🔴 မပြီးသေး**

**ပြီးဆုံးမှုကို အရေအတွက်တစ်ခုတည်းဖြင့် မဆုံးဖြတ်ရ။ အဆင့်တိုင်းတွင် reproducible evidence နှင့် cross-check ရှိမှသာ 🟢 FULL SUCCESS သတ်မှတ်ရမည်။**

**ဤ Plan သည် မူရင်း Full P/Invoke ကို အပြည့်အစုံ၊ တိတိကျကျ စုံစမ်းစစ်ဆေးရန်သာ ဖြစ်သည်။ APK ထုတ်လုပ်ခြင်း၊ Android port လုပ်ခြင်း၊ CASTAWAY DEPTHS ထဲသို့ ပေါင်းစည်းခြင်းတို့ မပါဝင်ပါ။**

## Stage 2 — First Termux ABI/metadata profile (2026-11-13)

User ran `$HOME/pinvoke_stage2.sh` successfully against the audit ZIP.

**Observed output (3,791 rows; 21 columns):**
- Calling convention: Winapi 2,033; Cdecl 1,159; StdCall 599.
- Charset: Not specified 3,384; Unicode 177; Ansi 153; Auto 77.
- Signature pattern hits: pointer 2,997; valuetype 2,215; byref 1,191; class 523; array 375; string 221; object 1.
- The report confirms the CSV contains explicit columns for `dll`, `entrypoint`, `mapping_flags`, `method_flags`, `impl_flags`, `calling_convention`, `charset`, `exact_spelling`, `supports_last_error`, `best_fit`, `throw_on_unmappable_char`, `mapping_flags_hex`, `method_flags_hex`, `impl_flags_hex`, `pinvoke_impl_flag`, `signature_raw_hex`, and `metadata_method_rid`.
- The first helper script incorrectly reported native module as NOT FOUND because it searched for a column named like `module`; the actual CSV column is `dll`. Future scripts must use `dll` as the module-name field.
- The signature-pattern totals are heuristic string matches, not a full ECMA-335 signature/ABI proof. Calling-convention and charset values are metadata observations, not runtime ABI validation.

**Artifacts created on the user's Termux device:**
`$HOME/PINVOKE_STAGE2_20261113_235016/`
- `stage2_summary.txt`
- `field_profile.tsv`
- `signature_flags.csv`

**Stage status remains 🟡 စစ်ဆေးနေဆဲ.** Next review must use the actual CSV schema and inspect raw metadata flags and signatures row-by-row. Do not mark Stage 2 complete based only on this aggregate profile.


## 2026-11-14 — Current handoff / waiting state

User's isolated Termux test of the patched Stage 3 scanner against a directory containing only `Assembly-CSharp.dll` returned `pinvoke_declarations_seen: 0` (26,143 MethodDefs, 23,276 method bodies parsed, 82,804 IL call instructions). This means the local patch has not passed runtime validation, despite syntax compilation passing.

The user was then asked to run a direct first-five-row comparison of:
- `MemberForwarded.row_index`
- `MemberForwarded.table` type and identity against `md.MethodDef`
- `mf.row.Name`
- `MethodDef.rows[rid - 1].Name`
- whether the names match

**WAITING FOR THE USER TO SEND THAT NEXT DIAGNOSTIC OUTPUT.** Do not issue another command or patch before reviewing it. Continue with the one-DLL Assembly-CSharp validation first; do not repeat the 119-DLL scan until declaration mapping is proven.

Current status remains:
- Stage 1 🟢
- Stage 2 🟡
- Stage 3 🟡 (mapping still fails; diagnostic output pending)
- Stage 4 🟡 (partial symbol-level evidence only)


## 2026-11-14 — Stage 3 MemberForwarded RID diagnostic update

User supplied the requested direct Termux diagnostic for the first five ImplMap rows in `Assembly-CSharp.dll`.

**Observed output:**
- ImplMap rows: 360
- MethodDef rows: 26,143
- `MemberForwarded` object type: `dnfile.mdtable.MethodDef`
- `MemberForwarded.table` type: `dnfile.mdtable.MethodDef`
- `MemberForwarded.table is md.MethodDef`: True
- For all five displayed rows, `MemberForwarded.row_index` was a valid RID and `methods[rid - 1].Name` exactly matched the target method name.
- Confirmed examples include `EOS_EResult_IsOperationComplete`, `EOS_ContinuanceToken_ToString`, `EOS_EpicAccountId_FromString`, `EOS_EpicAccountId_ToString`, and `EOS_EpicAccountId_IsValid`.

**Interpretation:** The RID-to-MethodDef indexing rule `MethodDef.rows[rid - 1]` is directly validated for these five examples. This is strong evidence that the metadata row mapping itself is sound for the sampled records. It does **not** yet prove all 360 rows resolve correctly, nor that the scanner's extraction/join logic is fixed. Do not mark Stage 3 complete.

**Next action — full 360-row mapping validation (Assembly-CSharp.dll only):**
Run a read-only validation over every ImplMap row. For each row, verify:
1. `MemberForwarded.table is md.MethodDef`;
2. RID is an integer in the range 1..len(MethodDef.rows);
3. `MethodDef.rows[rid - 1]` is the same method represented by `MemberForwarded` (compare method name and, where practical, row object identity/signature);
4. collect unresolved rows, duplicate RIDs, and name mismatches;
5. report totals and a small sample of failures.

Expected outcome for a clean mapping check: 360/360 valid table references, 360/360 in-range RIDs, 0 name mismatches. Duplicate RIDs must be reported, not automatically treated as errors, until their semantics are checked.

Only after this direct 360-row validation passes should the scanner be instrumented to report per-ImplMap extraction counters (rows iterated, table accepted, RID accepted, MethodDef resolved, declaration emitted, rejection reason). Do not rerun all 119 DLLs yet.

**Current Stage 3 status:** 🟡 RID mapping confirmed on 5/360 sampled rows; full 360-row validation and scanner extraction diagnosis pending.


## 2026-11-14 — Stage 3 full Assembly-CSharp RID validation PASS

User ran the full read-only RID validation against:
`Stranded_Deep_Data/Managed/Assembly-CSharp.dll`.

**Exact observed output:**
- ImplMap rows: **360**
- MethodDef rows: **26,143**
- `methoddef_table_ok`: **360**
- `valid_rid`: **360**
- `name_match`: **360**
- `duplicate RID values`: **0**
- `duplicate RID occurrences`: **0**
- Error samples: none
- `Total errors`: **0**
- Final result: **RID mapping: PASS**

**Conclusion:** All 360 ImplMap rows in Assembly-CSharp.dll resolve to the corresponding MethodDef through `MemberForwarded.row_index` and `MethodDef.rows[rid - 1]`, with matching method names, no invalid RIDs, no wrong tables, no name mismatches, and no duplicate RIDs. This supersedes the earlier 5/360 sampled-only status.

**Important scope limit:** This proves the metadata RID mapping for the 360 ImplMap rows in Assembly-CSharp.dll only. It does not yet prove that the Stage 3 scanner emits all 360 declarations, nor that all 3,791 declarations across 119 managed DLLs are joined correctly. Stage 3 therefore remains 🟡.

**Next action — inspect the exact scanner implementation before changing it:**
1. Locate the existing `pinvoke_stage3_scan.py` on the user's Termux device.
2. Print the code around `pinvoke_declarations_seen`, `ImplMap`, `MemberForwarded`, and the P/Invoke declaration join.
3. Identify the precise branch/counter that rejects or skips valid ImplMap rows.
4. Make the smallest evidence-based correction, then run Assembly-CSharp.dll alone and require 360 declarations with zero resolution errors.
5. Only after the isolated test passes, rerun the 119-DLL scan.

Do not replace the scanner wholesale, alter original game files, or run the full scan before inspecting the current code.

**Updated Stage 3 status:** 🟡 Full RID mapping PASS for Assembly-CSharp.dll (360/360); scanner extraction diagnosis pending.
