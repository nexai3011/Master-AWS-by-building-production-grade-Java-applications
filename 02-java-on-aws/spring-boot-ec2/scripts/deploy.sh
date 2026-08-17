#!/usr/bin/env bash
set -euo pipefail

APP_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/../app" && pwd)"

cd "$APP_DIR"
./mvnw clean package -DskipTests

echo "Build complete. Upload the jar to your EC2 instance and start the service."
