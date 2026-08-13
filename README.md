# Terraform init hooks extension demo
A sample of a Terraform configuration file highlighting its usage with LocalStack init hooks and Testcontainers

![architecture-diagram.png](architecture-diagram.png)

## Prerequisites

- A valid [LocalStack for AWS license](https://localstack.cloud/pricing). Your license provides a [`LOCALSTACK_AUTH_TOKEN`](https://docs.localstack.cloud/aws/getting-started/auth-token/) to activate LocalStack.
- [Docker](https://docs.docker.com/get-docker/).
- [`lstk`](https://docs.localstack.cloud/aws/developer-tools/running-localstack/lstk/), the LocalStack CLI. Install it with `make install`, or `brew install localstack/tap/lstk`.
- [Java 21](https://adoptium.net/) and [Maven](https://maven.apache.org/).

Authenticate once with `lstk login`, or export your token:

```bash
export LOCALSTACK_AUTH_TOKEN=<your-auth-token>
```

## Running with `lstk`

`lstk` reads the container's environment variables and bind mounts from [`.lstk/config.toml`](.lstk/config.toml),
which installs the `localstack-extension-terraform-init` extension and mounts `terraform/` into
`/etc/localstack/init/ready.d` so `main.tf` is applied as a READY init hook.

```bash
make build   # builds the Lambda JAR and stages it into terraform/target/
make start   # lstk start
```

Once the `Ready.` message appears, exercise the stack:

```bash
lstk status        # list the deployed resources
./invoke.sh        # POST two products, then GET one back
```

Stop it with `make stop`.

## Running with Docker Compose

`docker-compose.yml` is an alternative to `lstk` and mounts the same paths:

```bash
docker compose up
```
