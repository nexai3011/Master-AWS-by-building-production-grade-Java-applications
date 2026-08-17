# Lab Index

This file maps the learning journey across the repository and shows how the labs connect to a single shared application model.

## Core learning sequence

1. AWS basics and setup
2. Spring Boot deployment on EC2
3. PostgreSQL with Amazon RDS
4. File storage with Amazon S3
5. Async messaging with Amazon SQS
6. Containerization with Docker
7. AWS container orchestration with ECS and Fargate
8. Observability, security, and operations
9. Terraform, CI/CD, and production deployment pipelines
10. System design and interview preparation

## Shared application: order-management-platform

The repository uses a common product lifecycle so learners can see how the same business problem evolves across AWS technology choices.

### Business flow

- Customer places order
- Order is validated
- Database stores order details
- S3 stores order documents or attachments
- SQS handles asynchronous order processing
- Monitoring tracks health, latency, and throughput

### Architecture progression

- EC2: direct Spring Boot deployment
- RDS: relational persistence for orders
- S3: document uploads and media objects
- SQS: asynchronous inventory and notification workflows
- Docker/ECS: containerized deployment
- production stack: ALB, autoscaling, private networking, logs, and alerts

## Lab map

| Phase | Topic | Lab |
| --- | --- | --- |
| 1 | AWS setup | [00-getting-started/README.md](../00-getting-started/README.md) |
| 2 | AWS fundamentals | [01-aws-fundamentals/README.md](../01-aws-fundamentals/README.md) |
| 3 | EC2 deployment | [02-java-on-aws/spring-boot-ec2/README.md](../02-java-on-aws/spring-boot-ec2/README.md) |
| 4 | Database integration | [03-databases/rds-postgresql/README.md](../03-databases/rds-postgresql/README.md) |
| 5 | File storage | [04-storage/s3-file-upload/README.md](../04-storage/s3-file-upload/README.md) |
| 6 | Messaging | [05-messaging/sqs-order-processing/README.md](../05-messaging/sqs-order-processing/README.md) |
| 7 | Containers | [06-containers/README.md](../06-containers/README.md) |
| 8 | Security and observability | [08-security-observability/README.md](../08-security-observability/README.md) |
| 9 | IaC and automation | [09-cicd-iac/README.md](../09-cicd-iac/README.md) |
| 10 | Architecture | [10-production-architecture/README.md](../10-production-architecture/README.md) |
| 11 | System design | [11-system-design/README.md](../11-system-design/README.md) |
| 12 | Interview prep | [12-interview-prep/README.md](../12-interview-prep/README.md) |

## Recommended completion flow

- Learn the AWS fundamentals
- Deploy a Spring Boot app to EC2
- Add database persistence with RDS
- Add document storage with S3
- Add async business workflows with SQS
- Package the app in Docker
- Deploy to ECS or Fargate
- Harden networking, security, and monitoring
- Revisit the design in a production architecture context
