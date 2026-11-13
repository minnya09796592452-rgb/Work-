# Termux setup

Canonical working directory: `$HOME/Castaway-Depths`

Data staging directory: `$HOME/Castaway-Depths/obb/Castaway Depths/`

```bash
pkg update
pkg install -y git
cd "$HOME"
git clone https://github.com/devmyatmin/Read-CASTAWAY-DEPTHS.git Castaway-Depths
cd "$HOME/Castaway-Depths"
bash scripts/setup_termux.sh
```

After changes:

```bash
git status --short
git add -A
git commit -m "chore: update Castaway Depths workspace"
git push origin main
```

Never commit APK signing keys, passwords, access tokens, private keystores, or generated build output.
