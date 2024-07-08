//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package lambda;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.util.Objects;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.core.client.config.ClientOverrideConfiguration;
import software.amazon.awssdk.core.retry.RetryPolicy;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbClientBuilder;

public class ProductApi {
    private static final String AWS_ENDPOINT_URL = System.getenv("AWS_ENDPOINT_URL");
    protected static final String AWS_REGION = System.getenv("AWS_REGION");
    protected ObjectMapper objectMapper = new ObjectMapper();
    RetryPolicy customRetryPolicy = RetryPolicy.builder().numRetries(3).build();
    ClientOverrideConfiguration clientOverrideConfig;
    protected DynamoDbClient ddb;

    public ProductApi() {
        this.clientOverrideConfig = ClientOverrideConfiguration.builder().retryPolicy(this.customRetryPolicy).build();
        this.ddb = Objects.isNull(AWS_ENDPOINT_URL) ?
                ((DynamoDbClient.builder().endpointDiscoveryEnabled(true)).overrideConfiguration(this.clientOverrideConfig)).build() :
                (((((DynamoDbClient.builder().endpointOverride(URI.create(AWS_ENDPOINT_URL))).credentialsProvider(EnvironmentVariableCredentialsProvider.create())).region(Region.of("us-east-1"))).endpointDiscoveryEnabled(true)).overrideConfiguration(this.clientOverrideConfig)).build();
    }
}
