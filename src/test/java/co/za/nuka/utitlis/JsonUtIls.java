package co.za.nuka.utitlis;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.util.List;

public class JsonUtIls {

    private static final ObjectMapper mapper = new ObjectMapper();

    public static <T>List<T> readJsonToPojp(String filePath, Class<T> clazz){
        try{
            return mapper.readValue(
                    new File(filePath),
                    mapper.getTypeFactory().constructCollectionType(List.class,clazz)
            );

        }catch(Exception e){
            throw new RuntimeException("Error reading Json file: " + filePath, e);
        }
    }

}
