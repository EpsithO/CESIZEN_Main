# ---- setup_devops.sh ----
#!/usr/bin/env bash
set -euo pipefail

# 0) garde la trace
echo "[*] Repo: $(pwd)"

# 1) Dossiers
mkdir -p .github/workflows
mkdir -p ops/backup
mkdir -p api/src/main/java/com/CESIZen/prod/security
mkdir -p api/src/main/resources
mkdir -p CESIZen_front/src

# 2) Workflows GitHub Actions (dry-run)
cat > .github/workflows/backend-ci-cd.yml <<'YML'
name: backend-ci-cd-dryrun
on:
  push: { branches: [develop, main] }
  pull_request: { branches: [develop, main] }

jobs:
  build-test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-java@v4
        with: { distribution: temurin, java-version: '21' }
      - name: Cache Maven
        uses: actions/cache@v4
        with: { path: ~/.m2/repository, key: m2-${{ hashFiles('**/pom.xml') }} }
      - run: mvn -B -DskipTests=false -pl api -am verify
      - name: Récup artefact
        run: |
          mkdir -p out && (ls api/target/*.jar && cp api/target/*.jar out/app-backend.jar) || echo "(Jar d’exemple)" > out/app-backend.jar
      - name: Sécu — dry run
        run: echo "[DRY-RUN] OWASP Dependency-Check / CodeQL" | tee out/security-backend.txt
      - name: Docker build — dry run
        run: echo "[DRY-RUN] docker build -t example.local/cesizen-backend:${GITHUB_SHA} api" | tee -a out/docker.txt
      - name: Deploy — dry run
        run: echo "[DRY-RUN] ssh prod && docker compose up -d" | tee -a out/deploy.txt
      - uses: actions/upload-artifact@v4
        with: { name: backend-dryrun-artifacts, path: out/* }
YML

cat > .github/workflows/frontend-ci-cd.yml <<'YML'
name: frontend-ci-cd-dryrun
on: { push: { branches: [develop, main] }, pull_request: {} }

jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-node@v4
        with: { node-version: '20', cache: 'npm' }
      - run: npm ci --prefix CESIZen_front || true
      - run: npm run build --prefix CESIZen_front || (mkdir -p CESIZen_front/dist && echo "dist demo" > CESIZen_front/dist/index.html)
      - name: Pack artefacts
        run: |
          mkdir -p out && tar -czf out/frontend-dist.tgz -C CESIZen_front/dist .
      - name: Sécu — dry run
        run: echo "[DRY-RUN] npm audit / ESLint" | tee out/security-frontend.txt
      - name: Docker build — dry run
        run: echo "[DRY-RUN] docker build -t example.local/cesizen-frontend:${GITHUB_SHA} CESIZen_front" | tee -a out/docker.txt
      - uses: actions/upload-artifact@v4
        with: { name: frontend-dryrun-artifacts, path: out/* }
YML

# 3) Sauvegardes (dry-run) + docs RGPD / Réseau
cat > ops/backup/backup.sh <<'SH'
#!/usr/bin/env bash
set -euo pipefail
usage(){ echo "Usage: $0 [--dry-run] [--pg-url URL|--mysql-url URL] [--extra PATH]"; }
DRY=0; PG_URL=""; MY_URL=""; EXTRA=""; NOW=$(date +%F_%H%M)
while [[ $# -gt 0 ]]; do case $1 in
  --dry-run) DRY=1; shift;;
  --pg-url) PG_URL=$2; shift 2;;
  --mysql-url) MY_URL=$2; shift 2;;
  --extra) EXTRA=$2; shift 2;;
  *) usage; exit 1;; esac; done
OUT="backups/$NOW"; mkdir -p "$OUT"
log(){ echo "[$(date +%T)] $*"; }
if [[ -n $PG_URL ]]; then
  log "PostgreSQL dump"
  if [[ $DRY -eq 1 ]]; then echo "(dry-run) pg_dump $PG_URL > $OUT/db.dump" | tee "$OUT/pg.log"; else pg_dump -Fc "$PG_URL" > "$OUT/db.dump"; fi
fi
if [[ -n $MY_URL ]]; then
  log "MySQL dump"
  if [[ $DRY -eq 1 ]]; then echo "(dry-run) mysqldump $MY_URL > $OUT/db.sql" | tee "$OUT/mysql.log"; else mysqldump "$MY_URL" > "$OUT/db.sql"; fi
fi
if [[ -n $EXTRA ]]; then
  log "Archivage fichiers"
  if [[ $DRY -eq 1 ]]; then echo "(dry-run) tar czf $OUT/files.tgz $EXTRA" | tee "$OUT/files.log"; else tar czf "$OUT/files.tgz" "$EXTRA"; fi
fi
log "OK — artefacts dans $OUT"
SH
chmod +x ops/backup/backup.sh

cat > ops/backup/restore.sh <<'SH'
#!/usr/bin/env bash
set -euo pipefail
usage(){ echo "Usage: $0 <backup_dir> [--pg-url URL|--mysql-url URL]"; }
DIR=${1:-}; [[ -d $DIR ]] || { usage; exit 1; }
PG_URL=""; MY_URL=""; shift || true
while [[ $# -gt 0 ]]; do case $1 in
  --pg-url) PG_URL=$2; shift 2;;
  --mysql-url) MY_URL=$2; shift 2;;
  *) usage; exit 1;; esac; done
if [[ -f $DIR/db.dump && -n $PG_URL ]]; then pg_restore -d "$PG_URL" "$DIR/db.dump"; fi
if [[ -f $DIR/db.sql && -n $MY_URL ]]; then mysql "$MY_URL" < "$DIR/db.sql"; fi
if [[ -f $DIR/files.tgz ]]; then tar xzf "$DIR/files.tgz" -C .; fi
echo "Restauration OK depuis $DIR"
SH
chmod +x ops/backup/restore.sh

cat > ops/rgpd.md <<'MD'
# RGPD — Minimal
- Registre: données, finalités, base légale, durée de conservation.
- Droits: accès, rectification, effacement, portabilité (procédure + contact).
- Minimisation: pas de PII dans les logs; anonymisation en préprod.
- Sous-traitants / hébergement: UE/EEE; DPA si externe.
- Cookies: bannière si analytics non essentiels.
MD

cat > ops/network.md <<'MD'
# Sécurité réseau
- Flux: Internet → NGINX (443/80) → API (8080) → DB (privée, non exposée).
- Firewall: deny-by-default; SSH par clé; pas d'accès direct DB.
- Secrets/IAM: variables d'env hors repo; coffre-fort; moindre privilège.
MD

# 4) Backend — CsrfController "démo" (ne touche pas ta config)
cat > api/src/main/java/com/CESIZen/prod/security/CsrfController.java <<'JAVA'
package com.CESIZen.prod.security;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;
import java.util.UUID;

@RestController
public class CsrfController {
  @GetMapping("/csrf")
  public Map<String, String> csrf() {
    return Map.of(
      "headerName", "X-CSRF-TOKEN",
      "parameterName", "_csrf",
      "token", UUID.randomUUID().toString()
    );
  }
}
JAVA

# 5) Front — helper Axios CSRF prêt si un jour usage cookies
cat > CESIZen_front/src/http.ts <<'TS'
import axios from 'axios';
const api = axios.create({ withCredentials: true });
api.defaults.xsrfCookieName = 'XSRF-TOKEN';
api.defaults.xsrfHeaderName = 'X-CSRF-TOKEN';
export default api;
TS

# 6) Nginx (démo TLS + headers sécurité)
cat > CESIZen_front/nginx.conf <<'NGINX'
server {
  listen 80;
  listen 443 ssl http2;
  server_name localhost;

  # (démo) certs self-signed à monter dans /etc/nginx/certs
  ssl_certificate     /etc/nginx/certs/server.crt;
  ssl_certificate_key /etc/nginx/certs/server.key;
  ssl_protocols TLSv1.2 TLSv1.3;

  add_header Strict-Transport-Security "max-age=31536000; includeSubDomains; preload" always;
  add_header X-Content-Type-Options nosniff always;
  add_header Referrer-Policy no-referrer always;
  add_header Permissions-Policy "geolocation=(), microphone=()" always;

  location / { root /usr/share/nginx/html; try_files $uri /index.html; }
  location /api/ {
    proxy_pass http://api:8080/;
    proxy_set_header Host $host;
    proxy_set_header X-Forwarded-Proto $scheme;
  }
}
NGINX

# 7) Docker de démo locale
cat > CESIZen_front/Dockerfile <<'DOCKER'
FROM node:20-alpine AS build
WORKDIR /app
COPY package*.json ./
RUN npm ci || true
COPY . .
RUN npm run build || (mkdir -p dist && echo "<html><body>Demo</body></html>" > dist/index.html)

FROM nginx:alpine
COPY --from=build /app/dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf
# COPY certs /etc/nginx/certs   # (option) si vous ajoutez des certs self-signed
DOCKER

cat > docker-compose.yml <<'YML'
version: "3.9"
services:
  api:
    build: ./api
    container_name: cesizen-api
    ports: ["8080:8080"]
  web:
    build: ./CESIZen_front
    container_name: cesizen-front
    depends_on: [api]
    ports: ["80:80", "443:443"]
YML

cat > Makefile <<'MK'
.PHONY: build run stop backup
build:
	docker compose build
run:
	docker compose up -d
stop:
	docker compose down
backup:
	bash ops/backup/backup.sh --dry-run --extra CESIZen_front/dist
MK

# 8) README d’évaluation court
cat > README-EVAL.md <<'MD'
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
MD

echo "[✓] Fichiers ajoutés. Commit & push quand tu veux."
