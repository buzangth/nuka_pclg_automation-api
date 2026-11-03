package co.za.nuka;

import co.za.nuka.config.ConfigManager;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.config.HttpClientConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import org.testng.annotations.BeforeClass;

public class BaseConfig {

    protected ConfigManager config;

    @BeforeClass
    public void beforeClass(){
        config = ConfigManager.getInstance();

        if(config.isSslEnabled()){
            RestAssured.useRelaxedHTTPSValidation();
        }

        RestAssuredConfig restAssuredConfig = RestAssured.config()
                .httpClient(HttpClientConfig.httpClientConfig()
                        .setParam("http.connection.timeout",config.getConnectionTimeOut())
                                .setParam("http.secure.timeout",config.getReadTimeout()));

        RequestSpecBuilder requestSpecBuilder = new RequestSpecBuilder()
                .setBaseUri(config.getBaseUrl())
                .setContentType(ContentType.JSON)
                .setConfig(restAssuredConfig)
                .log(LogDetail.ALL);
        RestAssured.requestSpecification = requestSpecBuilder.build();

    }
}
