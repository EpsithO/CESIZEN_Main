# Évaluation — Déploiement local & Sécurisation (sans prod)
## Versionning
- Branches: main, develop, feature/*
- Tags: v0.1.0 (exemple)
## CI/CD (dry-run)
- Workflows: .github/workflows/backend-ci-cd.yml & frontend-ci-cd.yml
- Preuves: artefacts (JAR, dist, logs) dans l'onglet Actions
## Sécurité
- CSRF (démo): GET /csrf retourne un token
- TLS & headers: CESIZen_front/nginx.conf (HSTS, nosniff, etc.)
## Sauvegardes
- ops/backup/backup.sh --dry-run --extra CESIZen_front/dist → backups/<timestamp>
## Démo locale (optionnel)
- make build && make run → front http(s)://localhost | api http://localhost:8080
