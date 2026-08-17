# Lab: Containerize and Deploy the Order Management Platform on ECS/Fargate

This lab shows how to package the shared order-management-platform application in Docker and run it on Amazon ECS with Fargate.

## What we are building

A production-style Spring Boot API for order management that can:

- create a new order
- retrieve an order by ID
- list orders
- update the order status
- report health and readiness

The app is the same business domain used across the repository, so learners can see the same platform move from local development to an AWS-managed deployment model.

## Why this matters for Java developers

Most real Java systems are not deployed as a bare JAR on a single VM. They are packaged in containers, pushed to a registry, and orchestrated by a platform such as ECS.

This lab teaches the practical production pattern:

- build a clean container image
- publish it to Amazon ECR
- define a task for ECS
- expose it behind an ALB
- verify health and readiness
- destroy the environment when the lab is finished

## AWS services used

- Docker
- Amazon ECR
- Amazon ECS
- AWS Fargate
- Application Load Balancer
- CloudWatch Logs
- IAM

## Architecture

```mermaid
flowchart LR
    Client[Client / API Consumer] --> ALB[Application Load Balancer]
    ALB --> ECS[ECS Service / Fargate]
    ECS --> Task[Spring Boot Container]
    Task --> Health[/health]
    Task --> Orders[/orders]
    Task --> Logs[CloudWatch Logs]
```

This is the same order lifecycle used elsewhere in the repository:

- customer creates an order
- the API stores the order
- the app exposes order state via REST
- later labs can add PostgreSQL, S3, and SQS around the same platform

## Prerequisites

- Java 21+
- Maven
- Docker installed locally
- AWS CLI configured
- permissions to create ECR repositories, ECS resources, and CloudWatch log groups
- an AWS account with enough quota for the lab

## Local app behavior before deployment

The shared app exposes the following endpoints:

```bash
curl http://localhost:8080/health
curl http://localhost:8080/orders
curl -X POST http://localhost:8080/orders \
  -H "Content-Type: application/json" \
  -d '{"customerId":"customer-123","product":"laptop","quantity":2,"totalAmount":2499.99}'
```

Example response:

```json
{
  "id": 1,
  "customerId": "customer-123",
  "product": "laptop",
  "quantity": 2,
  "totalAmount": 2499.99,
  "status": "CREATED",
  "createdAt": "2026-08-17T12:37:19.540335739Z",
  "updatedAt": "2026-08-17T12:37:19.540335739Z"
}
```

## Project structure

```text
ecs-spring-boot/
├── README.md
├── docker/
│   └── Dockerfile
├── terraform/
│   └── main.tf
├── scripts/
│   ├── build-and-push.sh
│   ├── deploy.sh
│   └── destroy.sh
└── app/
    └── src/
```

The actual application code lives in the shared project here:

- [projects/order-management-platform/README.md](../../projects/order-management-platform/README.md)
- [projects/order-management-platform/app](../../projects/order-management-platform/app)

## Step-by-step implementation

### 1. Build the Spring Boot application

From the repository root:

```bash
cd projects/order-management-platform/app
mvn clean package -DskipTests
```

This produces the runnable JAR used by the container image.

### 2. Create the container Dockerfile

The Dockerfile in this lab packages the JAR into a minimal Java runtime container:

```dockerfile
FROM eclipse-temurin:21-jre
WORKDIR /app

COPY app.jar /app/app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
```

### 3. Build and push the image to Amazon ECR

```bash
cd 06-containers/ecs-spring-boot/scripts
chmod +x build-and-push.sh
./build-and-push.sh
```

This script:

- packages the shared app
- copies the jar into the Docker build context
- builds the image locally
- creates the ECR repository if it does not exist
- authenticates Docker to ECR
- pushes the image to the registry

Alternatively, perform the same steps manually:

```bash
aws ecr get-login-password --region us-east-1 | docker login --username AWS --password-stdin <ACCOUNT>.dkr.ecr.us-east-1.amazonaws.com

docker build -t order-management-platform .
docker tag order-management-platform:latest <ACCOUNT>.dkr.ecr.us-east-1.amazonaws.com/order-management-platform:latest
docker push <ACCOUNT>.dkr.ecr.us-east-1.amazonaws.com/order-management-platform:latest
```

### 4. Create the ECS cluster and task definition

The Terraform file in this lab provisions:

- a cluster
- a log group
- an ECR repository
- an execution role
- a task definition for Fargate
- an ECS service

This is intentionally simple so learners can understand the moving parts before adding production complexity.

### 5. Expose the service behind a load balancer

In a production environment, the ECS task should be placed behind a load balancer with a health check and a target group. The service is then reachable via the ALB hostname rather than a task IP.

Key design decisions:

- Fargate for no-EC2 management overhead
- public or private subnets depending on architecture
- restricted security groups
- health checks on the application endpoint

### 6. Test the deployed app

Once the service is running, call the application through the ALB DNS name:

```bash
curl http://<ALB-DNS>/health
curl http://<ALB-DNS>/orders
```

Then create an order against the live deployment:

```bash
curl -X POST http://<ALB-DNS>/orders \
  -H "Content-Type: application/json" \
  -d '{"customerId":"customer-aws","product":"monitor","quantity":1,"totalAmount":799.00}'
```

## Deployment scripts

The lab includes a small operational flow:

```bash
./scripts/build-and-push.sh
./scripts/deploy.sh
./scripts/destroy.sh
```

These scripts are designed to make the lab repeatable and safe for a hands-on AWS exercise.

## Security

- keep IAM roles least-privilege
- avoid embedding AWS credentials in image layers
- use environment variables or AWS-managed identity where possible
- keep ALB and task ports restricted to required access
- prefer private networking for the task layer in production

## Cost considerations

- shut down the ECS service when the lab is done
- keep the task size minimal
- set CloudWatch log retention low for lab usage
- monitor for unintended autoscaling or repeated deployments

## Production improvements

After this lab, the next steps usually include:

- Application Load Balancer health checks and path routing
- ECS service autoscaling
- private subnets and NAT setup
- RDS PostgreSQL for the order store
- S3 document attachments for orders
- SQS message flow for asynchronous order processing
- Blue/green rollout strategies

## Common mistakes

- forgetting to expose port 8080 in the task definition
- leaving the service running after the lab
- using an image tag not actually pushed to ECR
- ignoring health checks and readiness probes
- exposing the app directly without a load balancer in production

## Cleanup

```bash
cd 06-containers/ecs-spring-boot/scripts
./destroy.sh
```

This removes the ECS service and cluster and deletes the repository if it was created for the lab.

## Related labs

- [02-java-on-aws/spring-boot-ec2/README.md](../../02-java-on-aws/spring-boot-ec2/README.md) — direct EC2 deployment
- [03-databases/rds-postgresql/README.md](../../03-databases/rds-postgresql/README.md) — PostgreSQL persistence
- [04-storage/s3-file-upload/README.md](../../04-storage/s3-file-upload/README.md) — file uploads with S3
- [05-messaging/sqs-order-processing/README.md](../../05-messaging/sqs-order-processing/README.md) — asynchronous event processing
- [projects/order-management-platform/README.md](../../projects/order-management-platform/README.md) — shared business domain and architecture story
