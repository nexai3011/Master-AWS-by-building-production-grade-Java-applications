#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/../../.." && pwd)"
APP_DIR="$ROOT_DIR/projects/order-management-platform/app"
LAB_DIR="$ROOT_DIR/06-containers/ecs-spring-boot"
IMAGE_NAME="${IMAGE_NAME:-order-management-platform}"
AWS_REGION="${AWS_REGION:-us-east-1}"

mkdir -p "$LAB_DIR/docker"

mvn -f "$APP_DIR/pom.xml" clean package -DskipTests
cp "$APP_DIR/target/order-management-platform-0.0.1-SNAPSHOT.jar" "$LAB_DIR/docker/app.jar"

docker build -t "$IMAGE_NAME:latest" "$LAB_DIR/docker"

ACCOUNT_ID="$(aws sts get-caller-identity --query Account --output text)"
REPO_URI="$ACCOUNT_ID.dkr.ecr.$AWS_REGION.amazonaws.com/$IMAGE_NAME"

aws ecr describe-repositories --repository-names "$IMAGE_NAME" --region "$AWS_REGION" >/dev/null 2>&1 || \
  aws ecr create-repository --repository-name "$IMAGE_NAME" --region "$AWS_REGION" >/dev/null

aws ecr get-login-password --region "$AWS_REGION" | docker login --username AWS --password-stdin "$ACCOUNT_ID.dkr.ecr.$AWS_REGION.amazonaws.com"

docker tag "$IMAGE_NAME:latest" "$REPO_URI:latest"
docker push "$REPO_URI:latest"

echo "Pushed image: $REPO_URI:latest"
