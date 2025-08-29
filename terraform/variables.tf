variable "region" { default = "us-east-2" }

variable "app_name" { default = "franchises-api" }

variable "vpc_id" { type = string }
variable "public_subnet_ids" { type = list(string) }
variable "public_security_groups" { type = list(string) }

variable "task_cpu" { default = "1024" }
variable "task_memory" { default = "1024" }
variable "container_port" { default = 8080 }

variable "image_tag" { default = "0.0.1" }

variable "instance_type" { default = "t3.small" }
variable "desired_capacity" { default = 1 }
variable "min_size" { default = 1 }
variable "max_size" { default = 2 }

variable "health_check_path" { default = "/swagger-ui/index.html" }

variable "dynamodb_table_name" { default = "FranchisesNetwork" }

variable "image_id"{default = "ami-0f98c273b8171e6d0"}
