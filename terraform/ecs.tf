resource "aws_ecs_cluster" "this" {
  name = "${var.app_name}-ec2"
}


resource "aws_ecs_task_definition" "app" {
  family                   = "franchisesEC2TaskFamily"
  network_mode             = "awsvpc"
  requires_compatibilities = ["EC2"]
  tags = {}
  cpu                      = var.task_cpu
  memory                   = var.task_memory
  task_role_arn            = aws_iam_role.ecs_task.arn
  execution_role_arn       = aws_iam_role.ecs_execution.arn
  container_definitions = jsonencode(
    [
      {
        essential = true
        image     = "${aws_ecr_repository.app.repository_url}:${var.image_tag}"
        logConfiguration = {
          logDriver = "awslogs"
          options = {
            awslogs-create-group  = "true"
            awslogs-group         = aws_cloudwatch_log_group.ecs.name
            awslogs-region        = var.region
            awslogs-stream-prefix = "app"
          }
        }
        name = "app"
        portMappings = [
          {
            appProtocol   = "http"
            containerPort = 8080
            hostPort      = 8080
            name          = "app-8080-tcp"
            protocol      = "tcp"
          },
        ]
      },
    ]
  )
  runtime_platform {
    cpu_architecture        = "X86_64"
    operating_system_family = "LINUX"
  }
}

resource "aws_ecs_capacity_provider" "cp" {
  name = "Infra-ECS-Cluster-franchises-api-ec2-7a50ed03-AsgCapacityProvider-irnfH4NTJ95W"
  auto_scaling_group_provider {
    auto_scaling_group_arn         = aws_autoscaling_group.ecs.arn
    managed_termination_protection = "DISABLED"
    managed_scaling {
      status          = "ENABLED"
      target_capacity = 100
    }
  }
}


resource "aws_ecs_cluster_capacity_providers" "attach" {
  cluster_name       = aws_ecs_cluster.this.name
  capacity_providers = [aws_ecs_capacity_provider.cp.name]
  default_capacity_provider_strategy {
    capacity_provider = aws_ecs_capacity_provider.cp.name
    weight            = 1
  }
}

resource "aws_launch_template" "ecs" {
  vpc_security_group_ids = var.public_security_groups
  image_id               = var.image_id
  instance_type = "t3.small"
  iam_instance_profile {
    arn = aws_iam_instance_profile.ecs_instance.arn
  }
  user_data = "IyEvYmluL2Jhc2ggCmVjaG8gRUNTX0NMVVNURVI9ZnJhbmNoaXNlcy1hcGktZWMyID4+IC9ldGMvZWNzL2Vjcy5jb25maWc7CmVjaG8gRUNTX0JBQ0tFTkRfSE9TVD1odHRwczovL2Vjcy51cy1lYXN0LTIuYW1hem9uYXdzLmNvbSA+PiAvZXRjL2Vjcy9lY3MuY29uZmlnOw=="
}

resource "aws_autoscaling_group" "ecs" {
  launch_template {
    name = aws_launch_template.ecs.name
  }
  max_size                  = 5
  min_size                  = 1
  health_check_grace_period = 0

  tag {
    key                 = "AmazonECSManaged"
    propagate_at_launch = true
    value               = ""
  }

  tag {
    key                 = "Name"
    propagate_at_launch = true
    value               = "ECS Instance - franchises-api-ec2"
  }
}
