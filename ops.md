# ⚙️ Ops & DevOps – CESIZen
---

## 📂 Contenu
- `backup/backup.sh` → script de sauvegarde
- `backup/restore.sh` → script de restauration
- `network.md` → architecture réseau & règles de sécurité
- `rgpd.md` → conformité RGPD & traitement des données

---

## 🔐 Bonnes pratiques
- **Secrets** : jamais stockés dans le repo → GitHub Secrets, variables d’env, gestion externe (Vault)
- **Sauvegardes** : doivent être chiffrées (AES-256) en production
- **Déploiement** :
  - Via Docker Compose (`make build && make run`)
  - Nginx en reverse proxy sécurisé
- **CI/CD** :
  - Workflows GitHub Actions
  - Artefacts sauvegardés comme preuve de fonctionnement
