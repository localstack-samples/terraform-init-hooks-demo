//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.Map;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.GetItemResponse;

public class GetProduct extends ProductApi implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    private static final String TABLE_NAME = "Products";
    private static final String PRODUCT_ID = "id";
    private final ObjectMapper objectMapper = new ObjectMapper();

    public GetProduct() {
    }

    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent requestEvent, Context context) {
        String productId = requestEvent.getQueryStringParameters().get(PRODUCT_ID);
        System.out.println("PRODUCT ID: " + productId);
        HashMap<String, AttributeValue> valueMap = new HashMap();
        valueMap.put(PRODUCT_ID, AttributeValue.fromS(productId));
        GetItemRequest getItemRequest = GetItemRequest.builder().tableName(TABLE_NAME).key(valueMap).build();

        try {
            GetItemResponse getItemResponse = this.ddb.getItem(getItemRequest);
            if (getItemResponse.item() != null && !getItemResponse.item().isEmpty()) {
                Map<String, Object> responseBody = new HashMap();
                getItemResponse.item().forEach((k, v) -> {
                    responseBody.put(k, this.convertAttributeValue(v));
                });
                return (new APIGatewayProxyResponseEvent()).withStatusCode(200).withBody(this.objectMapper.writeValueAsString(responseBody));
            } else {
                return (new APIGatewayProxyResponseEvent()).withStatusCode(404).withBody("Product not found");
            }
        } catch (JsonProcessingException | DynamoDbException exception) {
            context.getLogger().log("Error: " + exception.getMessage());
            return (new APIGatewayProxyResponseEvent()).withStatusCode(500).withBody("Internal server error");
        }
    }

    private Object convertAttributeValue(AttributeValue value) {
        if (value.s() != null) {
            return value.s();
        } else if (value.n() != null) {
            return value.n();
        } else {
            return value.b() != null ? value.b() : null;
        }
    }
}
