# Production vs Tutorial

This document clarifies the difference between learning examples and production-safe architecture.

## Examples

### EC2

- Tutorial: EC2 instance with public IP and direct SSH access
- Production: ALB in a private subnet with Auto Scaling and restricted ingress

### Databases

- Tutorial: single RDS instance in one AZ
- Production: multi-AZ setup with backups, monitoring, failover, and read replicas

### Security

- Tutorial: AWS credentials stored locally
- Production: IAM role, OIDC, workload identity, and secrets injection

### Networking

- Tutorial: default VPC and permissive security groups
- Production: private subnets, controlled security group rules, private endpoints, and NAT strategy

## Rule of thumb

Tutorials are for learning how the service works. Production architecture is about safety, scalability, operational reliability, and cost control.
