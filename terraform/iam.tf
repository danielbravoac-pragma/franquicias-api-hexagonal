resource "aws_iam_role" "ecs_execution" {
  name        = "ecsTaskExecutionRoleFranchisesApi"
  description = "Allows ECS tasks to call AWS services on your behalf."
  assume_role_policy = jsonencode(
    {
      Statement = [
        {
          Action = "sts:AssumeRole"
          Effect = "Allow"
          Principal = {
            Service = "ecs-tasks.amazonaws.com"
          }
          Sid = ""
        },
      ]
      Version = "2012-10-17"
    }
  )
}

resource "aws_iam_role" "ecs_task" {
  name        = "ecsTaskRoleForFranchisesApi"
  description = "Allows ECS tasks to call AWS services on your behalf."
  assume_role_policy = jsonencode(
    {
      Statement = [
        {
          Action = "sts:AssumeRole"
          Effect = "Allow"
          Principal = {
            Service = "ecs-tasks.amazonaws.com"
          }
          Sid = ""
        },
      ]
      Version = "2012-10-17"
    }
  )
}

resource "aws_iam_role" "ecs_instance" {
  name        = "ecsInstanceRoleForFranchisesApi"
  description = "Allows EC2 instances to call AWS services on your behalf."
  assume_role_policy = jsonencode(
    {
      Statement = [
        {
          Action = "sts:AssumeRole"
          Effect = "Allow"
          Principal = {
            Service = "ec2.amazonaws.com"
          }
        },
      ]
      Version = "2012-10-17"
    }
  )
}

resource "aws_iam_instance_profile" "ecs_instance" {
  name = "ecsInstanceRoleForFranchisesApi"
  role = aws_iam_role.ecs_instance.name
}

resource "aws_iam_role_policy_attachment" "ecs_execution_policy" {
  role       = aws_iam_role.ecs_execution.name
  policy_arn = "arn:aws:iam::aws:policy/service-role/AmazonECSTaskExecutionRolePolicy"
}

resource "aws_iam_role_policy_attachment" "ecs_instance_attach" {
  role       = aws_iam_role.ecs_instance.name
  policy_arn = "arn:aws:iam::aws:policy/service-role/AmazonEC2ContainerServiceforEC2Role"
}

resource "aws_iam_role_policy" "ecs_task_dynamo" {
  name = "dynamoPolicyForFranchisesApi"
  role = aws_iam_role.ecs_task.id
  policy = jsonencode(
    {
      Statement = [
        {
          Action = [
            "dynamodb:PutItem",
            "dynamodb:UpdateItem",
            "dynamodb:DeleteItem",
            "dynamodb:GetItem",
            "dynamodb:Query",
            "dynamodb:DescribeTable",
          ]
          Effect = "Allow"
          Resource = [
            "arn:aws:dynamodb:us-east-2:875388088606:table/FranchisesNetwork",
            "arn:aws:dynamodb:us-east-2:875388088606:table/FranchisesNetwork/index/*",
          ]
        },
      ]
      Version = "2012-10-17"
    }
  )
}
