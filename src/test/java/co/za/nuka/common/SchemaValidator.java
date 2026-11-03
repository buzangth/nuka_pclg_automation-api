package co.za.nuka.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.mapper.ObjectMapperDeserializationContext;
import io.restassured.mapper.ObjectMapperSerializationContext;
import org.hamcrest.Matcher;
import org.hamcrest.StringDescription;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;

public final class SchemaValidator {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    public SchemaValidator() {
    }

    public static void validateRequest(Object payload, String schemaPath, String scenarioName){
        String json;

        try{
            json = MAPPER.writeValueAsString(payload);
        } catch (Exception e){
            throw  new AssertionError("Failed to serialise ....... '" + scenarioName + "' : " + e.getMessage(), e);
        }

        Matcher<String> schemaMatcher = matchesJsonSchemaInClasspath(schemaPath);
        if(!schemaMatcher.matches(json)){
            StringDescription desc = new StringDescription();
            schemaMatcher.describeMismatch(json,desc);
            throw new AssertionError("Request schema validation failed for scenario '" + scenarioName
            + "'.\n" + desc.toString()
            + "\n\nRequest body:\n" + json
            + "\n\nSchema: " + schemaPath);
        }
    }
}
