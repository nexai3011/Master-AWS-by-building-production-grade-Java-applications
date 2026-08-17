#!/usr/bin/env bash
set -euo pipefail

AWS_REGION="${AWS_REGION:-us-east-1}"
CLUSTER_NAME="${CLUSTER_NAME:-order-management-platform-cluster}"
SERVICE_NAME="${SERVICE_NAME:-order-management-platform-service}"
IMAGE_NAME="${IMAGE_NAME:-order-management-platform}"

if aws ecs describe-services --cluster "$CLUSTER_NAME" --services "$SERVICE_NAME" --region "$AWS_REGION" >/dev/null 2>&1; then
  aws ecs update-service --cluster "$CLUSTER_NAME" --service "$SERVICE_NAME" --desired-count 0 --region "$AWS_REGION" >/dev/null
  aws ecs delete-service --cluster "$CLUSTER_NAME" --service "$SERVICE_NAME" --force --region "$AWS_REGION" >/dev/null
fi

aws ecs delete-cluster --cluster "$CLUSTER_NAME" --region "$AWS_REGION" >/dev/null 2>&1 || true

aws ecr delete-repository --repository-name "$IMAGE_NAME" --force --region "$AWS_REGION" >/dev/null 2>&1 || true

echo "Cleanup complete for the ECS lab."
