locals {
  name              = var.app_name
  log_group         = "/ecs/franchises"
  ecr_repo_name     = var.app_name
  table_arn         = "arn:aws:dynamodb:${var.region}:${data.aws_caller_identity.current.account_id}:table/${var.dynamodb_table_name}"
  table_indexes_arn = "${local.table_arn}/index/*"
}
