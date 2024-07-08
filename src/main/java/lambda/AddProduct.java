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
import com.fasterxml.jackson.databind.JsonMappingException;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.ConditionalCheckFailedException;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;

import java.util.HashMap;
import java.util.Map;

public class AddProduct extends ProductApi implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    private static final String TABLE_NAME = "Products";
    private static final String PRODUCT_ID = "id";

    public AddProduct() {
    }

    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent requestEvent, Context context) {
        Map productData;
        try {
            productData = this.objectMapper.readValue(requestEvent.getBody(), HashMap.class);
        } catch (JsonMappingException jsonMappingException) {
            throw new RuntimeException(jsonMappingException);
        } catch (JsonProcessingException jsonProcessingException) {
            throw new RuntimeException(jsonProcessingException);
        }

        HashMap<String, AttributeValue> itemValues = new HashMap();
        itemValues.put("id", AttributeValue.builder().s((String)productData.get(PRODUCT_ID)).build());
        itemValues.put("name", AttributeValue.builder().s((String)productData.get("name")).build());
        itemValues.put("price", AttributeValue.builder().n((String)productData.get("price")).build());
        itemValues.put("description", AttributeValue.builder().s((String)productData.get("description")).build());
        PutItemRequest putItemRequest = PutItemRequest.builder().tableName(TABLE_NAME).item(itemValues).conditionExpression("attribute_not_exists(id) OR id = :id").expressionAttributeValues(Map.of(":id", (AttributeValue)AttributeValue.builder().s((String)productData.get("id")).build())).build();
        Map<String, String> headers = new HashMap();
        headers.put("Content-Type", "application/json");

        try {
            this.ddb.putItem(putItemRequest);
            return (new APIGatewayProxyResponseEvent()).withStatusCode(200).withBody("Product added/updated successfully.").withIsBase64Encoded(false).withHeaders(headers);
        } catch (ConditionalCheckFailedException conditionalCheckFailedException) {
            return (new APIGatewayProxyResponseEvent()).withStatusCode(409).withBody("Product with the given ID already exists.").withIsBase64Encoded(false).withHeaders(headers);
        } catch (AwsServiceException awsServiceException) {
            context.getLogger().log("AwsServiceException exception: " + awsServiceException.getMessage());
            return (new APIGatewayProxyResponseEvent()).withStatusCode(500).withBody(awsServiceException.getMessage()).withIsBase64Encoded(false).withHeaders(headers);
        } catch (RuntimeException runtimeException) {
            context.getLogger().log("Runtime exception: " + runtimeException.getMessage());
            return (new APIGatewayProxyResponseEvent()).withStatusCode(500).withBody(runtimeException.getMessage()).withIsBase64Encoded(false).withHeaders(headers);
        } catch (Exception exception) {
            context.getLogger().log("Generic exception: " + exception.getMessage());
            return (new APIGatewayProxyResponseEvent()).withStatusCode(500).withBody(exception.getMessage()).withIsBase64Encoded(false).withHeaders(headers);
        }
    }
}
