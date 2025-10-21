resource "aws_s3_bucket" "deploy_bucket" {
  bucket = "${var.s3_bucket}-${random_id.bucket_suffix.hex}"
}

# Optionally create SSH key parameter in SSM
resource "aws_ssm_parameter" "ssh_key" {
  count     = length(trim(var.ssh_private_key)) > 0 ? 1 : 0
  name      = var.ssh_param_name
  type      = "SecureString"
  value     = var.ssh_private_key
  overwrite = true
}

# IAM Role for Lambda
data "aws_iam_policy_document" "lambda_assume_role" {
  statement {
    actions = ["sts:AssumeRole"]
    principals {
      type        = "Service"
      identifiers = ["lambda.amazonaws.com"]
    }
  }
}

resource "aws_iam_role" "lambda_role" {
  name               = "lambda-cd-role-${random_id.bucket_suffix.hex}"
  assume_role_policy = data.aws_iam_policy_document.lambda_assume_role.json
}

resource "aws_iam_policy" "lambda_policy" {
  name   = "lambda-cd-policy-${random_id.bucket_suffix.hex}"
  policy = jsonencode({
    Version = "2012-10-17",
    Statement = [
      {
        Effect   = "Allow",
        Action   = ["ssm:GetParameter"],
        Resource = ["arn:aws:ssm:${var.aws_region}:*:${var.ssh_param_name}"]
      },
      {
        Effect   = "Allow",
        Action   = ["s3:GetObject", "s3:GetObjectVersion"],
        Resource = ["${aws_s3_bucket.deploy_bucket.arn}/*"]
      },
      {
        Effect   = "Allow",
        Action   = ["logs:CreateLogGroup", "logs:CreateLogStream", "logs:PutLogEvents"],
        Resource = ["arn:aws:logs:*:*:*"]
      }
    ]
  })
}

resource "aws_iam_role_policy_attachment" "attach_lambda_policy" {
  role       = aws_iam_role.lambda_role.name
  policy_arn = aws_iam_policy.lambda_policy.arn
}

# Lambda function
resource "aws_lambda_function" "deploy_lambda" {
  filename         = "${path.module}/lambda_package/lambda.zip"
  function_name    = "home-deploy-lambda-${random_id.bucket_suffix.hex}"
  role             = aws_iam_role.lambda_role.arn
  handler          = "handler.lambda_handler"
  runtime          = "python3.12"
  source_code_hash = filebase64sha256("${path.module}/lambda_package/lambda.zip")
  memory_size      = var.lambda_memory
  timeout          = var.lambda_timeout

  environment {
    variables = {
      SSM_PARAM_NAME = var.ssh_param_name
      HOME_HOST      = var.home_host
      HOME_USER      = var.home_user
    }
  }
}

resource "aws_lambda_permission" "s3_invoke" {
  statement_id  = "AllowS3Invoke"
  action        = "lambda:InvokeFunction"
  function_name = aws_lambda_function.deploy_lambda.function_name
  principal     = "s3.amazonaws.com"
  source_arn    = aws_s3_bucket.deploy_bucket.arn
}

resource "aws_s3_bucket_notification" "bucket_notification" {
  bucket = aws_s3_bucket.deploy_bucket.id

  lambda_function {
    lambda_function_arn = aws_lambda_function.deploy_lambda.arn
    events              = ["s3:ObjectCreated:Put"]
    filter_prefix       = "build/"
  }

  depends_on = [aws_lambda_permission.s3_invoke]
}
