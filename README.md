# Terraform init hooks extension demo
A sample of a Terraform configuration file highlighting its usage with LocalStack init hooks and Testcontainers

![architecture-diagram.png](architecture-diagram.png)

## Prerequisites

- A valid [LocalStack for AWS license](https://localstack.cloud/pricing). Your license provides a [`LOCALSTACK_AUTH_TOKEN`](https://docs.localstack.cloud/getting-started/auth-token/) to activate LocalStack.
- [Docker](https://docs.docker.com/get-docker/) for running LocalStack via Testcontainers.
- [Java 21](https://adoptium.net/) and [Maven](https://maven.apache.org/).

> **Note:** LocalStack is started automatically via Testcontainers in the tests. Export your `LOCALSTACK_AUTH_TOKEN` before running.

```bash
export LOCALSTACK_AUTH_TOKEN=<your-auth-token>
```

Instructions here: https://github.com/localstack/localstack-extensions/tree/main/terraform-init