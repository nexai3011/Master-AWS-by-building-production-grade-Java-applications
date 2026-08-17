#!/usr/bin/env bash
set -euo pipefail

AWS_REGION="${AWS_REGION:-us-east-1}"
CLUSTER_NAME="${CLUSTER_NAME:-order-management-platform-cluster}"
SERVICE_NAME="${SERVICE_NAME:-order-management-platform-service}"
TASK_FAMILY="${TASK_FAMILY:-order-management-platform-task}"
IMAGE_NAME="${IMAGE_NAME:-order-management-platform}"
ACCOUNT_ID="$(aws sts get-caller-identity --query Account --output text)"
IMAGE_URI="$ACCOUNT_ID.dkr.ecr.$AWS_REGION.amazonaws.com/$IMAGE_NAME:latest"

aws ecs describe-clusters --clusters "$CLUSTER_NAME" --region "$AWS_REGION" >/dev/null 2>&1 || \
  aws ecs create-cluster --cluster-name "$CLUSTER_NAME" --region "$AWS_REGION" >/dev/null

echo "Deployment target: $IMAGE_URI"
echo "Next step: register a task definition and create an ECS service using the image above."
echo "Example: aws ecs register-task-definition --family $TASK_FAMILY --network-mode awsvpc --cpu 256 --memory 512 --requires-compatibilities FARGATE --execution-role-arn <EXEC_ROLE_ARN> --container-definitions '[{"name":"order-management-platform","image":"'$IMAGE_URI'","portMappings":[{"containerPort":8080,"hostPort":8080,"protocol":"tcp"}],"logConfiguration":{"logDriver":"awslogs","options":{"awslogs-group":"/ecs/order-management-platform","awslogs-region":"'$AWS_REGION'","awslogs-stream-prefix":"ecs"}}}]'"
