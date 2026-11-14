#!/usr/bin/env sh
set -eu

ROOT_DIR=$(CDPATH= cd -- "$(dirname -- "$0")/.." && pwd)

cd "$ROOT_DIR/backend"
./mvnw verify

cd "$ROOT_DIR/frontend"
npm ci
npm test
npm run lint
npm run build

echo "Verificación completada correctamente."

