# P/Invoke Stage 3 Scanner — Original ChatGPT Source Provenance

**Record date:** 2026-11-14  
**Project:** CASTAWAY-DEPTHS  
**Status:** Historical source provenance recorded; original Python source bytes are not yet attached to this repository.

## Purpose

Preserve the provenance and previously reported validation details for the Stage 3 managed-IL/P/Invoke scanner, as recovered from the original ChatGPT conversation history. This record is not a substitute for the scanner source file and does not claim that the source has been restored or independently rerun.

## Original source references recovered

- **Filename:** `pinvoke_stage3_scan.py`
- **Original ChatGPT sandbox path:** `/mnt/data/pinvoke_stage3_scan.py`
- **Termux copy destination previously used:** `/storage/emulated/0/Download/pinvoke_stage3_scan.py`
- **Managed DLL input directory:** `/storage/emulated/0/Download/PC GAMES/StrandedDeep/Stranded_Deep_Data/Managed`
- **Previously reported output directory:** `$HOME/PINVOKE_STAGE3_OUTPUT`
- **Previously reported summary file:** `stage3_summary.json`

## Recorded patched-source digest

A conversation record dated **2026-11-13 18:40 UTC** reports that the scanner was patched.

- **Reported SHA-256:** `72b182648654f21b5ea9a9c19c33978ab544ed498b33dd93dec9f7310874c3cc`
- **Reported patch detail:** use `MemberForwarded.row_index` as the MethodDef RID when resolving P/Invoke declarations to their MethodDef rows.

This digest is a historical conversation record. It has **not yet been recomputed against a recovered source file**, so it must not be treated as a verified digest of any file currently in this repository.

## Previously reported Stage 3 run figures

The original conversation records the following results:

| Measurement | Reported value |
|---|---:|
| Managed DLLs | 119 |
| MethodDef rows | 208,570 |
| Parsed method bodies | 182,514 |
| Call rows | 695,709 |
| P/Invoke / call-site rows | 27 |
| Assembly-CSharp declarations resolved | 360 / 360 |
| Resolution errors | 0 |

An earlier run reportedly showed `pinvoke_declarations_seen: 0` and two `invalid compressed int` warnings. A later patched run reportedly resolved all 360 Assembly-CSharp declarations with zero resolution errors.

These are **historical reported results**, not a fresh execution or independent verification in this commit.

## Recovery and verification requirements

1. Recover the exact original `pinvoke_stage3_scan.py` bytes from the original ChatGPT attachment/runtime or the user's Termux copy.
2. Compute SHA-256 locally and compare it with the reported digest above.
3. If the digest matches, preserve the exact source unchanged and add it to the repository only after the Project Lead explicitly authorizes source-file archival.
4. If it does not match, retain both digests and investigate the source/version difference; do not overwrite or silently relabel the file.
5. Re-run the scanner read-only against the original Managed directory and preserve the complete command, stdout/stderr, exit code, and output manifest.
6. Keep the Stranded Deep assemblies and binaries strictly as forensic reference material; this scanner provenance record does not authorize copying game binaries, IL, assets, or proprietary implementation into CASTAWAY-DEPTHS.

## Scope and limitations

- This commit records provenance and historical claims only.
- The scanner source itself is **not included** in this record.
- No source file was reconstructed from memory.
- No audit was rerun as part of creating this record.
- No project architecture, implementation status, or runtime validation gate is changed by this record.
