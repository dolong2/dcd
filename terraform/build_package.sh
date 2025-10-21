#!/usr/bin/env bash
set -euo pipefail

PKG_DIR=lambda_package
ZIP_PATH=${PKG_DIR}/lambda.zip

rm -rf ${PKG_DIR}
mkdir -p ${PKG_DIR}/python

cat > Dockerfile.build <<'DOCKER'
FROM public.ecr.aws/lambda/python:3.12
WORKDIR /var/task
COPY handler.py ./
RUN pip install --target ./param_deps paramiko
DOCKER

docker build -t lambda-build -f Dockerfile.build .
container_id=$(docker create lambda-build)
docker cp ${container_id}:/var/task/param_deps ${PKG_DIR}/
docker rm ${container_id}

cp handler.py ${PKG_DIR}/
cd ${PKG_DIR}
(cd param_deps && cp -r . ../)
rm -rf param_deps
zip -r ${ZIP_PATH} .
cd ..

echo "✅ Built ${ZIP_PATH}. Run: terraform init && terraform apply"
