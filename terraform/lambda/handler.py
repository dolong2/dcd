import boto3
import paramiko
import io
import os

s3 = boto3.client("s3")
ssm = boto3.client("ssm")

def lambda_handler(event, context):
    try:
        record = event["Records"][0]
        bucket = record["s3"]["bucket"]["name"]
        key = record["s3"]["object"]["key"]
    except Exception as e:
        return {"error": "Invalid event", "detail": str(e)}

    param_name = os.environ.get("SSM_PARAM_NAME")
    resp = ssm.get_parameter(Name=param_name, WithDecryption=True)
    private_key = resp["Parameter"]["Value"]
    pkey = paramiko.RSAKey.from_private_key(io.StringIO(private_key))

    host = os.environ.get("HOME_HOST")
    user = os.environ.get("HOME_USER")

    ssh = paramiko.SSHClient()
    ssh.set_missing_host_key_policy(paramiko.AutoAddPolicy())
    ssh.connect(hostname=host, username=user, pkey=pkey, timeout=10)

    cmd = f"bash /home/{user}/deploy.sh {bucket} {key}"
    stdin, stdout, stderr = ssh.exec_command(cmd)
    out = stdout.read().decode()
    err = stderr.read().decode()
    ssh.close()

    return {"stdout": out, "stderr": err}
