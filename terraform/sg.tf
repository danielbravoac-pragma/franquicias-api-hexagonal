resource "aws_security_group" "alb" {
  name        = "default"
  description = "default VPC security group"
  vpc_id      = var.vpc_id
  ingress {
    cidr_blocks = [
      "0.0.0.0/0",
    ]
    description      = null
    from_port        = 80
    ipv6_cidr_blocks = []
    prefix_list_ids  = []
    protocol         = "tcp"
    security_groups  = []
    self             = false
    to_port          = 80
  }
  ingress {
    cidr_blocks = [
      "38.250.153.47/32",
    ]
    description      = null
    from_port        = 3306
    ipv6_cidr_blocks = []
    prefix_list_ids  = []
    protocol         = "tcp"
    security_groups  = []
    self             = false
    to_port          = 3306
  }
  ingress {
    cidr_blocks      = []
    description      = null
    from_port        = 0
    ipv6_cidr_blocks = []
    prefix_list_ids  = []
    protocol         = "-1"
    security_groups  = []
    self             = true
    to_port          = 0
  }
  ingress {
    cidr_blocks = []
    description = null
    from_port   = 443
    ipv6_cidr_blocks = [
      "::/0",
    ]
    prefix_list_ids = []
    protocol        = "tcp"
    security_groups = []
    self            = false
    to_port         = 443
  }
  ingress {
    cidr_blocks = []
    description = null
    from_port   = 8080
    ipv6_cidr_blocks = [
      "::/0",
    ]
    prefix_list_ids = []
    protocol        = "tcp"
    security_groups = []
    self            = false
    to_port         = 8080
  }

  egress {
    cidr_blocks = [
      "0.0.0.0/0",
    ]
    description      = null
    from_port        = 0
    ipv6_cidr_blocks = []
    prefix_list_ids  = []
    protocol         = "-1"
    security_groups  = []
    self             = false
    to_port          = 0
  }
}


resource "aws_security_group" "tasks" {
  name        = "default"
  vpc_id      = var.vpc_id
  description = "default VPC security group"

  ingress {
    cidr_blocks = [
      "0.0.0.0/0",
    ]
    description      = null
    from_port        = 80
    ipv6_cidr_blocks = []
    prefix_list_ids  = []
    protocol         = "tcp"
    security_groups  = []
    self             = false
    to_port          = 80
  }
  ingress {
    cidr_blocks = [
      "38.250.153.47/32",
    ]
    description      = null
    from_port        = 3306
    ipv6_cidr_blocks = []
    prefix_list_ids  = []
    protocol         = "tcp"
    security_groups  = []
    self             = false
    to_port          = 3306
  }
  ingress {
    cidr_blocks      = []
    description      = null
    from_port        = 0
    ipv6_cidr_blocks = []
    prefix_list_ids  = []
    protocol         = "-1"
    security_groups  = []
    self             = true
    to_port          = 0
  }
  ingress {
    cidr_blocks = []
    description = null
    from_port   = 443
    ipv6_cidr_blocks = [
      "::/0",
    ]
    prefix_list_ids = []
    protocol        = "tcp"
    security_groups = []
    self            = false
    to_port         = 443
  }
  ingress {
    cidr_blocks = []
    description = null
    from_port   = 8080
    ipv6_cidr_blocks = [
      "::/0",
    ]
    prefix_list_ids = []
    protocol        = "tcp"
    security_groups = []
    self            = false
    to_port         = 8080
  }

  egress {
    cidr_blocks = [
      "0.0.0.0/0",
    ]
    description      = null
    from_port        = 0
    ipv6_cidr_blocks = []
    prefix_list_ids  = []
    protocol         = "-1"
    security_groups  = []
    self             = false
    to_port          = 0
  }
}

resource "aws_security_group" "ecs_instances" {
  name        = "default"
  description = "default VPC security group"
  vpc_id      = var.vpc_id
  ingress {
    cidr_blocks = [
      "0.0.0.0/0",
    ]
    description      = null
    from_port        = 80
    ipv6_cidr_blocks = []
    prefix_list_ids  = []
    protocol         = "tcp"
    security_groups  = []
    self             = false
    to_port          = 80
  }
  ingress {
    cidr_blocks = [
      "38.250.153.47/32",
    ]
    description      = null
    from_port        = 3306
    ipv6_cidr_blocks = []
    prefix_list_ids  = []
    protocol         = "tcp"
    security_groups  = []
    self             = false
    to_port          = 3306
  }
  ingress {
    cidr_blocks      = []
    description      = null
    from_port        = 0
    ipv6_cidr_blocks = []
    prefix_list_ids  = []
    protocol         = "-1"
    security_groups  = []
    self             = true
    to_port          = 0
  }
  ingress {
    cidr_blocks = []
    description = null
    from_port   = 443
    ipv6_cidr_blocks = [
      "::/0",
    ]
    prefix_list_ids = []
    protocol        = "tcp"
    security_groups = []
    self            = false
    to_port         = 443
  }
  ingress {
    cidr_blocks = []
    description = null
    from_port   = 8080
    ipv6_cidr_blocks = [
      "::/0",
    ]
    prefix_list_ids = []
    protocol        = "tcp"
    security_groups = []
    self            = false
    to_port         = 8080
  }

  egress {
    cidr_blocks = [
      "0.0.0.0/0",
    ]
    description      = null
    from_port        = 0
    ipv6_cidr_blocks = []
    prefix_list_ids  = []
    protocol         = "-1"
    security_groups  = []
    self             = false
    to_port          = 0
  }
}
