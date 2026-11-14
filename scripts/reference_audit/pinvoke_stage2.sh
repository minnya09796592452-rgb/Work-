#!/data/data/com.termux/files/usr/bin/bash
set -e

ZIP="${1:-}"
if [ -z "$ZIP" ]; then
    ZIP="$(find /storage/emulated/0/Download -maxdepth 2 -type f \
      -iname '*PInvoke*Full*Audit*.zip' 2>/dev/null | head -n 1)"
fi

if [ -z "$ZIP" ] || [ ! -f "$ZIP" ]; then
    echo "ERROR: Full P/Invoke Audit ZIP not found."
    echo "Usage: bash pinvoke_stage2.sh /path/to/audit.zip"
    exit 1
fi

OUT="$HOME/PINVOKE_STAGE2_$(date +%Y%m%d_%H%M%S)"
mkdir -p "$OUT"

echo "[1/3] Extracting audit CSV..."
unzip -oq "$ZIP" -d "$OUT/extracted"

CSV="$(find "$OUT/extracted" -type f \
  -name 'managed_pinvoke_all_3791.csv' | head -n 1)"

if [ -z "$CSV" ]; then
    echo "ERROR: managed_pinvoke_all_3791.csv not found."
    exit 1
fi

echo "[2/3] Analyzing all declaration rows..."

cat > "$OUT/analyze.py" <<'PY'
import csv, sys, re, collections, os

src, out = sys.argv[1], sys.argv[2]
os.makedirs(out, exist_ok=True)

with open(src, newline='', encoding='utf-8-sig') as f:
    reader = csv.DictReader(f)
    rows = list(reader)
    headers = reader.fieldnames or []

def norm(s):
    return re.sub(r'[^a-z0-9]', '', s.lower())

def find_col(*terms):
    for h in headers:
        n = norm(h)
        if all(t in n for t in terms):
            return h
    return None

def write_counts(path, title, values):
    with open(path, 'w', encoding='utf-8') as f:
        f.write(title + '\n')
        for k, v in values.most_common():
            f.write(f'{v}\t{k}\n')

report = []
report.append("P/INVOKE STAGE 2 — METADATA / ABI PROFILE")
report.append(f"CSV: {src}")
report.append(f"Rows: {len(rows)}")
report.append(f"Columns: {len(headers)}")
report.append("")
report.append("COLUMN NAMES:")
report.extend(headers)
report.append("")

# Profile every field: non-empty count and distinct-value count.
with open(os.path.join(out, "field_profile.tsv"), "w") as f:
    f.write("field\tnon_empty\tdistinct\n")
    for h in headers:
        vals = [str(r.get(h, "") or "").strip() for r in rows]
        nonempty = [v for v in vals if v]
        f.write(f"{h}\t{len(nonempty)}\t{len(set(nonempty))}\n")

# Detect likely ABI-related columns by their header names.
groups = {
    "calling_convention": find_col("calling", "convention"),
    "charset": find_col("charset"),
    "mapping_flags": find_col("mapping", "flag"),
    "entry_point": find_col("entry", "point"),
    "native_module": find_col("module"),
    "signature": find_col("signature"),
    "declaring_type": find_col("type"),
    "managed_method": find_col("method"),
}

for label, col in groups.items():
    report.append(f"{label}: {col or 'NOT FOUND'}")
    if col:
        vals = collections.Counter(
            str(r.get(col, "") or "").strip() or "<EMPTY>" for r in rows
        )
        write_counts(os.path.join(out, label + "_counts.txt"),
                     label, vals)
        report.extend([f"  {k}: {v}" for k, v in vals.most_common(30)])
    report.append("")

sigcol = groups["signature"]
if sigcol:
    patterns = {
        "pointer": r'\*|native int|native unsigned int',
        "byref": r'&|byref',
        "array": r'\[\]|szarray|array',
        "generic": r'generic|!!\d+|!\d+',
        "typespec": r'typespec|0x1b',
        "function_pointer": r'fnptr|function pointer',
        "string": r'\bstring\b',
        "object": r'\bobject\b',
        "valuetype": r'valuetype',
        "class": r'\bclass\b',
    }
    counts = collections.Counter()
    with open(os.path.join(out, "signature_flags.csv"),
              "w", newline='', encoding='utf-8') as f:
        w = csv.writer(f)
        w.writerow(["row_number", "managed_method", "signature", "detected_flags"])
        for i, r in enumerate(rows, 1):
            sig = str(r.get(sigcol, "") or "")
            found = [k for k, p in patterns.items()
                     if re.search(p, sig, re.I)]
            counts.update(found)
            if found:
                method = r.get(groups["managed_method"], "") if groups["managed_method"] else ""
                w.writerow([i, method, sig, ";".join(found)])
    report.append("SIGNATURE PATTERN COUNTS:")
    report.extend([f"{k}: {v}" for k, v in counts.most_common()])

with open(os.path.join(out, "stage2_summary.txt"), "w") as f:
    f.write("\n".join(report) + "\n")

print("\n".join(report))
print("\nReports saved to:", out)
PY

python "$OUT/analyze.py" "$CSV" "$OUT"

echo "[3/3] COMPLETE"
echo "Output directory: $OUT"
echo "Main report: $OUT/stage2_summary.txt"
echo "Field profile: $OUT/field_profile.tsv"
echo "Signature flags: $OUT/signature_flags.csv"
