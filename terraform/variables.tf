variable "aws_region" {
  type    = string
  default = "ap-northeast-2"
}

variable "s3_bucket" {
  type    = string
  default = "home-deploy-bucket-${random_id.bucket_suffix.hex}"
}

variable "lambda_memory" {
  type    = number
  default = 256
}

variable "lambda_timeout" {
  type    = number
  default = 30
}

variable "ssh_private_key" {
  type        = string
  description = "SSH private key contents (recommended to use env var or Secrets Manager)."
  sensitive   = true
  default     = ""
}

variable "ssh_param_name" {
  type    = string
  default = "/deploy/private-key"
}

variable "home_host" {
  type    = string
  default = "myhome.example.com"
}

variable "home_user" {
  type    = string
  default = "deploy"
}
