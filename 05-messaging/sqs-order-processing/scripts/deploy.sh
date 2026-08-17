#!/usr/bin/env bash
set -euo pipefail

APP_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/../app" && pwd)"

cd "$APP_DIR"
./mvnw clean package -DskipTests

echo "Application built. Ensure the SQS queue exists and the queue URL is configured before running the service."
