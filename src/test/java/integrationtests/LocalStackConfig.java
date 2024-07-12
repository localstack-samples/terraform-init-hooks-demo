package integrationtests;

import org.junit.jupiter.api.BeforeAll;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.lambda.LambdaClient;

import java.net.URI;
import java.time.Duration;
import java.time.temporal.ChronoUnit;

@Testcontainers
public class LocalStackConfig {

  @Container
  protected static LocalStackContainer localStack =
      new LocalStackContainer(DockerImageName.parse("localstack/localstack-pro:latest"))
          .withEnv("LAMBDA_REMOVE_CONTAINERS", "1")
              .withEnv("EXTENSION_AUTO_INSTALL", "localstack-extension-terraform-init")
          .withEnv("LOCALSTACK_AUTH_TOKEN", System.getenv("LOCALSTACK_AUTH_TOKEN"))
          .withFileSystemBind("./target/product-lambda.jar",
              "/etc/localstack/init/ready.d/target/product-lambda.jar")
          .withFileSystemBind("./terraform",
              "/etc/localstack/init/ready.d")
          .withEnv("DEBUG", "1")
          .withStartupTimeout(Duration.of(2, ChronoUnit.MINUTES));

  protected static final Logger LOGGER = LoggerFactory.getLogger(LocalStackConfig.class);
  protected static Slf4jLogConsumer logConsumer = new Slf4jLogConsumer(LOGGER);
  protected static URI localStackEndpoint;
  protected static LambdaClient lambdaClient;



  @BeforeAll()
  protected static void setupConfig() {
    localStackEndpoint = localStack.getEndpoint();

    lambdaClient = LambdaClient.builder()
        .region(Region.of(localStack.getRegion()))
        .endpointOverride(localStackEndpoint)
        .credentialsProvider(StaticCredentialsProvider.create(
            AwsBasicCredentials.create(localStack.getAccessKey(), localStack.getSecretKey())))
        .build();
  }

}


