#!/usr/bin/env bash
set -euo pipefail

APP_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/../app" && pwd)"

cd "$APP_DIR"
./mvnw clean package -DskipTests

echo "Package built. Configure your RDS database host and credentials before running the service."
