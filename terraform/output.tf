output "s3_bucket" {
  value = aws_s3_bucket.deploy_bucket.bucket
}

output "lambda_function_name" {
  value = aws_lambda_function.deploy_lambda.function_name
}
