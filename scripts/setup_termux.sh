#!/data/data/com.termux/files/usr/bin/bash
set -Eeuo pipefail
ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
DATA_ROOT="$ROOT/obb/Castaway Depths"
dirs=(
  assets/textures assets/meshes assets/materials assets/audio assets/animations assets/ui
  worlds/biomes worlds/locations worlds/terrain
  definitions/items definitions/characters definitions/creatures definitions/crafting definitions/survival
  shaders/source shaders/compiled config localization manifests
)
for rel in "${dirs[@]}"; do mkdir -p "$DATA_ROOT/$rel"; done
[ -f "$DATA_ROOT/manifests/asset_manifest.csv" ] || printf 'asset_id,path,source,license,license_url,modified_on,sha256\n' > "$DATA_ROOT/manifests/asset_manifest.csv"
touch "$DATA_ROOT/manifests/checksums.sha256"
echo "CASTAWAY DEPTHS workspace READY"
echo "Repo: $ROOT"
echo "Data: $DATA_ROOT"
echo "Git status:"
git -C "$ROOT" status --short
