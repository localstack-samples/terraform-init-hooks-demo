SHELL := /bin/bash

usage:		## Show this help
	@fgrep -h "##" $(MAKEFILE_LIST) | fgrep -v fgrep | sed -e 's/\\$//' | sed -e 's/##//'

install:	## Install dependencies
	@which lstk || npm install -g @localstack/lstk

build:		## Build the Lambda JAR and stage it for the Terraform init hook
	mvn clean package -DskipTests
	mkdir -p terraform/target
	cp target/product-lambda.jar terraform/target/product-lambda.jar

start:		## Start LocalStack with the Terraform init hook (see .lstk/config.toml)
	@test -f terraform/target/product-lambda.jar || (echo "terraform/target/product-lambda.jar is missing. Run 'make build' first."; exit 1)
	lstk start

stop:		## Stop LocalStack
	lstk stop

test:		## Run integration tests (LocalStack started via Testcontainers)
	@test -n "${LOCALSTACK_AUTH_TOKEN}" || (echo "LOCALSTACK_AUTH_TOKEN is not set. Find your token at https://app.localstack.cloud/workspace/auth-token"; exit 1)
	mvn test

.PHONY: usage install build start stop test
