package integrationtests;

import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import software.amazon.awssdk.core.waiters.WaiterResponse;
import software.amazon.awssdk.services.lambda.model.GetFunctionRequest;
import software.amazon.awssdk.services.lambda.model.GetFunctionResponse;
import software.amazon.awssdk.services.lambda.waiters.LambdaWaiter;

import java.io.IOException;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ProductAppTests extends LocalStackConfig {

    public static String apiGWId;

    @BeforeAll
    public static void setup() throws IOException, InterruptedException {
        LocalStackConfig.setupConfig();

        LocalStackConfig.localStack.followOutput(LocalStackConfig.logConsumer);

        LambdaWaiter waiter = lambdaClient.waiter();
        GetFunctionRequest getFunctionRequest = GetFunctionRequest.builder()
                .functionName("add-product")
                .build();
        WaiterResponse<GetFunctionResponse> waiterResponse = waiter.waitUntilFunctionActiveV2(
                getFunctionRequest);
        waiterResponse.matched().response().ifPresent(response -> LOGGER.info(response.toString()));

        String apiGWData = localStack.execInContainer("awslocal", "apigateway", "get-rest-apis").getStdout();

        JSONObject jsonObject = new JSONObject(apiGWData);
        JSONArray items = jsonObject.getJSONArray("items");
        apiGWId = items.getJSONObject(0).getString("id");
    }


    @Test
    @Order(1)
    void testSuccessfulPostAction() {

        var postUrl =
                localStackEndpoint + "/restapis/" + apiGWId + "/dev/_user_request_/productApi";

        var expectedResponse =
                "Product added/updated successfully.";

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {

            // add headers to a POST request
            var httpPost = new HttpPost(postUrl);
            httpPost.setHeader(new BasicHeader("Content-Type", "application/json"));
            // create the JSON request body
            var jsonRequestBody = "{\n" +
                    "  \"id\": \"34534\",\n" +
                    "  \"name\": \"EcoFriendly Water Bottle\",\n" +
                    "  \"description\": \"A durable, eco-friendly water bottle.\",\n" +
                    "  \"price\": \"29.99\"\n" +
                    "}";

            // set the request body
            var entity = new StringEntity(jsonRequestBody);
            httpPost.setEntity(entity);
            // execute the request
            try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
                String responseBody = EntityUtils.toString(response.getEntity());

                Assertions.assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
                Assertions.assertEquals(expectedResponse, responseBody);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @Order(2)
    void testSuccessfulGetAction() {

        var getUrl =
                localStackEndpoint + "/restapis/" + apiGWId + "/dev/_user_request_/productApi" + "?id=";

        var expectedResponse = "{\"price\":\"29.99\",\"name\":\"EcoFriendly Water Bottle\",\"description\":\"A durable, eco-friendly water bottle.\",\"id\":\"34534\"}";

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {

            // add headers to the GET request
            var httpGet = new HttpGet(getUrl + "34534");
            httpGet.setHeader(new BasicHeader("Content-Type", "application/json"));

            // execute the request
            try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
                String responseBody = EntityUtils.toString(response.getEntity());

                Assertions.assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
                Assertions.assertEquals(expectedResponse, responseBody);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @Order(3)
    void testProductNotFoundOnGetWrongId() {
        var getUrl =
                localStackEndpoint + "/restapis/" + apiGWId + "/dev/_user_request_/productApi" + "?id=";

        var expectedResponse = "Product not found";

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {

            // add headers to the GET request
            var httpGet = new HttpGet(getUrl + "12345");
            httpGet.setHeader(new BasicHeader("Content-Type", "application/json"));

            // execute the request
            try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
                String responseBody = EntityUtils.toString(response.getEntity());

                Assertions.assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatusLine().getStatusCode());
                Assertions.assertEquals(expectedResponse, responseBody);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
