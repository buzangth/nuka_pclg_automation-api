package co.za.nuka.common;

import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.hamcrest.core.StringEndsWith;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static io.restassured.RestAssured.given;

public final class ApiClient {

    public static final String CORRELATION_ID_HEADER = "X-Correlation-ID";

    public enum Method {
        GET, POST, PUT, DELETE, PATCH
    }

    private ApiClient() {
    }

    public static Response post(String apiPath, Object requestBody, int expectedStatus) {
        return post(apiPath, requestBody, expectedStatus, Collections.emptyMap());
    }


    public static Response post(String apiPath, Object requestBody, int expectedStatus, Map<String, String> headers) {

        return execute(Method.POST, apiPath, requestBody, expectedStatus, headers);

    }

    public static Response execute(Method method, String apiPath, Object requestBody, int expectedStatus, Map<String, String> headers) {

//        TestContext ctx = TestContext.get() ;
//            ctx.httpMethod(method, name())
//                    .apiPath(apiPath)
//                    .requestBody(requestBody)
//                    .expectedStatus(expectedStatus);

            Map<String, String> effectiveHeaders = new HashMap<>();
            effectiveHeaders.put(CORRELATION_ID_HEADER, UUID.randomUUID().toString());

            if (headers != null) {
                effectiveHeaders.putAll(headers);
            }

            Response response;
            try {
                RequestSpecification spec = given().headers(effectiveHeaders);
                if (requestBody != null) {
                    spec = spec.body(requestBody);
                }
                RequestSpecification when = spec.when();

                switch (method) {
                    case GET:
                        response = when.get(apiPath).then().extract().response();
                        break;
                    case PUT:
                        response = when.put(apiPath).then().extract().response();
                        break;

                    case PATCH:
                        response = when.patch(apiPath).then().extract().response();
                        break;

                    case DELETE:
                        response = when.delete(apiPath).then().extract().response();
                        break;

                    default:
                        response = when.post(apiPath).then().extract().response();
                        break;
                }

            } catch (Exception e) {
                //ctx.error(e);
                throw e;
            }

//            ctx.actualStatus(response.getStatusCode())
//                    .respondeBody(response.body().asString())
//                    .responseTimeMs(response.getTime())
//                    .contentType(response.getContentType())
//                    .responseHeaders(response.getHeaders().toString());

            int actualStatus = response.getStatusCode();
            if (actualStatus != expectedStatus) {
                AssertionError err = new AssertionError("Expected status " + expectedStatus + "but got " + actualStatus + " for " + method + " " + apiPath + ". Response body " + response.getBody().asString());
                //ctx.error(err);
                throw err;
            }

            return response;

        }
    }
