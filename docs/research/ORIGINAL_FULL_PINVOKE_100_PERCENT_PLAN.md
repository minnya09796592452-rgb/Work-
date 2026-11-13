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
<badge color="danger">🔴 မပြီးသေး</badge>
- Declaration 3,791 ခုထဲမှ ဘယ် methods တွေကို call-site မှ တကယ်ခေါ်သလဲ စစ်ရန်။
- Call graph / caller-callee chain များကို ရှာဖွေပြီး runtime reachability ကို ခွဲခြားရန်။
- Platform conditionals, feature flags, optional integrations, offline/online pathways များကို စိစစ်ရန်။
- LoadLibrary, GetProcAddress, dlopen, dlsym နှင့် အလားတူ dynamic-loading လမ်းကြောင်းများကို ရှာဖွေရန်။
- Metadata declaration ရှိခြင်းနှင့် runtime တွင် အမှန်တကယ် load/call ဖြစ်ခြင်းကို သီးခြား status သတ်မှတ်ရန်။

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
- **အဆင့် 3 — 🔴 မပြီးသေး**
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
