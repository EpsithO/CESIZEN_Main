#!/usr/bin/env bash
#
#  fix_git.sh — “ça marche ou ça casse”
#  À lancer depuis n’importe où :
#      chmod +x fix_git.sh && ./fix_git.sh
#  (adapté pour le dépôt dans « dossier sans titre »)

set -e  # stoppe au moindre pépin
repo_root="$HOME/Desktop/dossier sans titre"

echo "→ CD dans le dépôt…"
cd "$repo_root" || { echo "❌ impossible de trouver $repo_root"; exit 1; }

echo "→ 1/ Suppression éventuelle du fichier de verrouillage Git"
rm -f .git/index.lock

echo "→ 2/ Ajout (si besoin) de l’exclusion DB au .gitignore"
grep -qxF 'CESIZen/data/db/' .gitignore || echo 'CESIZen/data/db/' >> .gitignore

echo "→ 3/ Reset de l’index (staging) — aucun fichier disque touché"
git reset

echo "→ 4/ Retrait des fichiers DB déjà suivis, sans scan profond"
git ls-files -z | grep -z 'CESIZen/data/db/' \
  | xargs -0 git rm --cached --ignore-unmatch || true

echo "→ 5/ Ajout de tout le reste + .gitignore"
git add .

echo "→ 6/ Commit (s’il y a quelque chose à committer)"
if git diff --cached --quiet; then
  echo "   (rien à committer, tout est propre ✅)"
else
  git commit -m "Nettoyage : exclusion des fichiers MySQL"
fi

echo "→ 7/ Push en sécurité (force mais protégé)"
git push --force-with-lease

echo "✅ Terminé. Le dépôt est clean et la base MySQL n’est plus suivie."
