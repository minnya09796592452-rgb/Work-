# Logic File Register — CASTAWAY DEPTHS

Last updated: 2026-11-13

Use this register for each file brought from the Termux workspace. Do not mark a file as uploaded until it exists in GitHub and its checksum has been checked.

| Original Termux path | File name | Type / purpose | Classification | Repository destination | SHA-256 | Upload status |
|---|---|---|---|---|---|---|
| pending inventory | pending | pending | pending | pending | pending | NOT UPLOADED |

## Classification
- CASTAWAY_SOURCE: original source written for CASTAWAY DEPTHS; place under the matching `src/` subsystem.
- CASTAWAY_TEST: test source written for CASTAWAY DEPTHS; place under `tests/`.
- BUILD_TOOL: setup/build/validation automation; place under `scripts/`.
- PROJECT_REPORT: code-free project report or test evidence; place under `docs/`.
- REFERENCE_ONLY: code-free abstract research note; place under `docs/research/reference-audits/`.
- PROHIBITED_IMPORT: decompiled or copied Stranded Deep code/IL, proprietary assets, binaries, or other protected implementation material; do not upload to this repository.

## Upload procedure
1. Inventory candidate files from Termux before copying anything.
2. Classify each file by origin and purpose.
3. Copy only eligible CASTAWAY DEPTHS source, tests, tools, or code-free reports to the destination defined in `FILE_PLACEMENT_MAP.md`.
4. Preserve original filenames unless a documented naming correction is necessary.
5. Record SHA-256 and verify the uploaded file against the local file.
6. Update this register and the relevant implementation status only after verification.

The ChatGPT conversation transcript is maintained separately and must not be modified as part of this logic-file upload.
