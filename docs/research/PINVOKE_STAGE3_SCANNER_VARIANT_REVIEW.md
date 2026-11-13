# P/Invoke Stage 3 Scanner — Source Variant Review

**Review date:** 2026-11-14  
**Repository:** `devmyatmin/Read-CASTAWAY-DEPTHS`  
**Branch:** `main`  
**Scope:** Read-only source comparison. No scanner was executed against Managed DLLs and no source was modified.

## Sources located

### 1. Personal Library source

- Filename: `pinvoke_stage3_scan.py`
- Size: 11,417 bytes
- Lines: 236
- Library created: `2026-11-13T18:42:49Z`
- Library modified: `2026-11-13T18:42:52Z`
- The full text was readable through the Files source. Raw-byte materialization was unavailable, so no independent SHA-256 was computed for this Library copy.

### 2. Uploaded source variant A

- Filename received: `pinvoke_stage3_scan(1)(1).py`
- Runtime path at review: `/mnt/data/pinvoke_stage3_scan(1)(1).py`
- Size: 10,931 bytes; 227 lines
- File timestamp: `2026-11-13 19:33:23 UTC`
- SHA-256: `f97cda39548cf76700b178edb54d23c6008de489d34d1bc6fb56a999dd944601`
- RID mapping code builds `method_rid_by_obj` from `MethodDef.rows`, then tries object-identity lookup for `MemberForwarded.row`, falling back to `rid_of(mr)`.

### 3. Uploaded source variant B

- Filename received: `pinvoke_stage3_scan(2).py`
- Runtime path at review: `/mnt/data/pinvoke_stage3_scan(2).py`
- Size: 10,825 bytes; 226 lines
- File timestamp: `2026-11-13 19:33:23 UTC`
- SHA-256: `347b2989687d5220fc5101e50bc21512b134497d7e6efc5db61ed967e0a130bf`
- RID mapping code uses `rid_of(mr)` directly for `MemberForwarded.row`; it does not build the object-identity fallback map.

## Source differences and assessment

- Variant A and B differ in the ImplMap-to-MethodDef RID resolution block. The other observed source content is unchanged in the diff.
- Variant A has an additional object-identity fallback. Variant B removes that fallback and relies directly on the returned row's `row_index`/integer conversion.
- The Library source contains a more explicit RID resolution sequence: check that `MemberForwarded` targets the MethodDef table, use the coded-index RID from `MemberForwarded`, then fall back to the MethodDef object map and row RID. It also documents why the returned row wrapper may not expose a usable `row_index` or be object-identical to the table row.
- The previously recorded SHA-256 `72b182648654f21b5ea9a9c19c33978ab544ed498b33dd93dec9f7310874c3cc` does **not** match either uploaded variant's SHA-256. It has not been verified against the Library copy.

## Validation performed

- Python bytecode syntax compilation: **PASS** for both uploaded variants.
- Runtime `--help` invocation: **NOT RUN successfully**; the environment lacks the required `dnfile` package (`ModuleNotFoundError`). The scanner has not been executed against the original Managed directory during this review.
- No new Stage 3 scan results are claimed.
- No source has been designated as the canonical active version by this review.

## Preservation rule

Keep all three source versions distinct until the Project Lead identifies the currently used version or authorizes a canonical selection. Do not overwrite, merge, or silently rename any source. Before claiming a source matches the previously reported patched SHA-256, recover its raw bytes and verify the digest.

The original source provenance record remains unchanged:
`docs/research/PINVOKE_STAGE3_SCANNER_SOURCE_PROVENANCE.md`.


## Follow-up static code audit (2026-11-14)

Reviewed the uploaded variants line-by-line. The same findings apply to both uploaded variants and the readable Library source unless noted.

### Confirmed defects / risks

1. **CSV output is written before MemberRef/MethodSpec matching.** In `main()`, all three CSV files are written at lines 197–199, but MemberRef/MethodSpec name matching runs afterward at lines 200–212. That later pass mutates `calls` and appends matched rows to `pinvokes`, but neither `il_call_sites.csv` nor `pinvoke_callsite_and_declarations.csv` is written again. Consequently, the summary can count rows and mappings that are absent from the corresponding CSV files.
2. **Name-only matching can produce false associations.** The declaration index key is only lowercased managed type + method name. It does not compare assembly identity, method signature/parameter types, generic instantiation, or metadata token identity. Overloads or same-named types across assemblies can therefore be incorrectly associated. The code itself labels this a name match, not a proven call edge.
3. **ImplMap resolution errors do not fail the process.** Exceptions increment `implmap_resolution_errors`, but the final nonzero-exit condition checks only `file_errors` and `method_body_parse_errors`. A run may exit 0 while P/Invoke declarations failed to resolve.
4. **No internal completeness reconciliation.** The scanner does not compare the P/InvokeImpl flags on MethodDef rows against successfully mapped ImplMap rows, nor assert that every ImplMap row was resolved exactly once. Therefore `pinvoke_declarations_seen` alone is not a completeness proof.

### Verification status

- Confirmed by direct source inspection; no source code changed.
- Python syntax compilation passed for both uploaded variants.
- Runtime execution remains unverified because `dnfile` and `dncil` are unavailable in the current environment.
- No fresh scan, CSV output, or Stage 3 result is claimed.
- These findings are recorded for the Project Lead; no patch or canonical-source selection has been made.
