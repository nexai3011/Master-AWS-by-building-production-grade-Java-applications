# Lab 1: Deploy Spring Boot on EC2

This lab shows how to package a Spring Boot application, install it on an EC2 instance, and expose it through a basic production-style deployment setup.

## What we are building

A simple Spring Boot REST API that exposes health information and is deployed to an EC2 instance running on AWS.

## Why this matters for Java developers

EC2 is the simplest and most direct deployment model for Java applications. It helps Java engineers understand fundamentals such as:

- Java runtime installation
- process management
- package deployment
- security groups
- environment variables
- CloudWatch monitoring

## AWS services used

- EC2
- IAM
- Security Groups
- CloudWatch
- VPC

## Architecture

```mermaid
flowchart LR
    User[Client] --> Internet[Internet / SSH / HTTP]
    Internet --> EC2[EC2 Instance]
    EC2 --> App[Spring Boot App on Port 8080]
    App --> Logs[CloudWatch Logs]
```

## Prerequisites

- AWS account
- EC2 key pair
- Java 21 installed locally
- Maven installed locally
- AWS CLI configured

## Project structure

```text
spring-boot-ec2/
├── README.md
├── scripts/
│   ├── deploy.sh
│   └── destroy.sh
├── app/
│   ├── pom.xml
│   └── src/
└── terraform/
    └── main.tf
```

## Step-by-step implementation

### 1. Build the application

```bash
cd app
./mvnw clean package
```

### 2. Launch an EC2 instance

Create a Linux EC2 instance with:

- Amazon Linux 2023 or Ubuntu 22.04
- port 8080 open to inbound HTTP
- port 22 open for SSH
- IAM role if needed for CloudWatch access

### 3. Install Java on the EC2 instance

```bash
sudo yum update -y
sudo yum install -y java-21-amazon-corretto
java -version
```

### 4. Copy the JAR to the instance

```bash
scp target/spring-boot-ec2-demo-0.0.1-SNAPSHOT.jar ec2-user@<PUBLIC_IP>:/home/ec2-user/
```

### 5. Run the application as a system service

```bash
sudo nano /etc/systemd/system/spring-boot-demo.service
```

Example service:

```ini
[Unit]
Description=Spring Boot EC2 Demo
After=network.target

[Service]
User=ec2-user
WorkingDirectory=/home/ec2-user
ExecStart=/usr/bin/java -jar /home/ec2-user/spring-boot-ec2-demo-0.0.1-SNAPSHOT.jar
Restart=always
RestartSec=10
Environment=JAVA_OPTS="-Xms256m -Xmx512m"

[Install]
WantedBy=multi-user.target
```

Then:

```bash
sudo systemctl daemon-reload
sudo systemctl enable spring-boot-demo
sudo systemctl start spring-boot-demo
sudo systemctl status spring-boot-demo
```

## Testing

```bash
curl http://<PUBLIC_IP>:8080/health
```

Expected output:

```json
{"status":"UP","service":"spring-boot-ec2-demo"}
```

## Monitoring

- CloudWatch Logs
- systemd logs
- `/var/log/messages`
- actuator health endpoint

## Security

- restrict SSH access to trusted IPs
- use least-privilege IAM roles
- avoid storing secrets directly in the app
- keep Java and packages updated

## Cost considerations

- stop or terminate EC2 instances when not in use
- ensure security groups are limited to required ports
- avoid public exposure unless necessary

## Production improvements

- use an Application Load Balancer
- move to private subnets
- add Auto Scaling
- use managed services such as ECS or EKS
- enable CloudWatch alarms and logs

## Common mistakes

- leaving SSH open to 0.0.0.0/0
- running app as root
- exposing app directly without a load balancer in production
- forgetting runtime logs and health checks

## Cleanup

```bash
aws ec2 terminate-instances --instance-ids <INSTANCE_ID>
```

## Related labs

- Spring Boot + RDS PostgreSQL
- Spring Boot + S3 file upload
- Spring Boot + SQS order processing
