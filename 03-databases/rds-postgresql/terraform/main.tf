terraform {
  required_version = ">= 1.5.0"

  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 5.0"
    }
  }
}

provider "aws" {
  region = "us-east-1"
}

resource "aws_db_instance" "postgres" {
  identifier             = "java-demo-postgres"
  engine                 = "postgres"
  engine_version         = "16.3"
  instance_class         = "db.t3.micro"
  allocated_storage      = 20
  db_name                = "appdb"
  username               = "appuser"
  password               = "ChangeThisPassword123!"
  publicly_accessible    = false
  skip_final_snapshot    = true
  backup_retention_period = 0
  vpc_security_group_ids = []
  db_subnet_group_name  = null
}

output "endpoint" {
  value = aws_db_instance.postgres.address
}
