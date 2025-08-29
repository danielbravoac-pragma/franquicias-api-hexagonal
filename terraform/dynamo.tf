resource "aws_dynamodb_table" "franchises" {
  billing_mode = "PAY_PER_REQUEST"
  name         = var.dynamodb_table_name
  table_class  = "STANDARD"
  hash_key     = "pk"
  range_key    = "sk"

  ##attributes
  attribute {
    name = "branchId"
    type = "S"
  }
  attribute {
    name = "franchiseId"
    type = "S"
  }
  attribute {
    name = "productId"
    type = "S"
  }
  attribute {
    name = "stock"
    type = "N"
  }
  attribute {
    name = "pk"
    type = "S"
  }
  attribute {
    name = "sk"
    type = "S"
  }


  ## GSIs
  global_secondary_index {
    hash_key        = "branchId"
    name            = "GSI_BranchById"
    range_key       = "franchiseId"
    projection_type = "ALL"
  }
  global_secondary_index {
    hash_key        = "branchId"
    name            = "GSI_BranchProductsByStock"
    projection_type = "ALL"
    range_key       = "stock"
  }
  global_secondary_index {
    hash_key        = "productId"
    name            = "GSI_ProductById"
    projection_type = "ALL"
  }
}
