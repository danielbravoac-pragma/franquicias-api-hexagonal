resource "aws_lb" "app" {
  name               = "franchisesApiLoadBalancer"
  subnets            = var.public_subnet_ids
  load_balancer_type = "application"
  ip_address_type    = "ipv4"
  security_groups    = var.public_security_groups
}

resource "aws_lb_target_group" "app" {
  name        = "targetGroupForFranchisesApi"
  port        = var.container_port
  protocol    = "HTTP"
  target_type = "ip"
  vpc_id      = var.vpc_id

  health_check {
    enabled             = true
    healthy_threshold   = 5
    interval            = 30
    matcher             = "200"
    path                = var.health_check_path
    port                = "traffic-port"
    protocol            = "HTTP"
    timeout             = 5
    unhealthy_threshold = 2
  }
}

resource "aws_lb_listener" "http" {
  load_balancer_arn = aws_lb.app.arn
  port              = 80
  protocol          = "HTTP"
  default_action {
    type             = "forward"
    target_group_arn = aws_lb_target_group.app.arn
    forward {
      stickiness {
        duration = 1
      }
      target_group {
        arn = aws_lb_target_group.app.arn
        weight = 1
      }
    }
  }
}
