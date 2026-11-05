package co.za.nuka.common;

import co.za.nuka.config.ConfigManager;
import com.opencsv.CSVReaderHeaderAware;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 */
public final class ScenarioDataProvider {

    private ScenarioDataProvider() {
    }


    public static List<Map<String, String>> loadCsv(String testName) {

        String env = ConfigManager.getInstance().getEnv();
        String resource = "testData/" + testName + "/" + env + "/scenario.csv";

        try (InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(resource)) {
            if (in == null) {
                throw new RuntimeException("Scenario CSV file not fount on Classpath " + resource);
            }
            List<Map<String, String>> rows = new ArrayList<>();
            try (CSVReaderHeaderAware reader = new CSVReaderHeaderAware(
                    new InputStreamReader(in, StandardCharsets.UTF_8))) {
                int lineNumber = 1;
                while (true) {
                    Map<String, String> row;
                    lineNumber++;
                    try {
                        row = reader.readMap();
                    } catch (IOException recordError) {
                        System.out.println("[ScenarioDataProvider] Skipping malformed/blank row at line " + lineNumber + " in " + resource + " (" + recordError.getMessage() + ")");
                        continue;

                    }
                    if (row == null) {
                        break;
                    }

                    if (isBlankRow(row)) {
                        System.out.println("[ScenarioDataProvider] Skipping malformed/blank row at line " + lineNumber + " in " + resource);
                        continue;
                    }

                    rows.add(row);
                    System.out.println("[ScenarioDataProvider] row " + rows.size() + "(CSV line " + lineNumber + "):" + row);
                }
            }

            if (rows.isEmpty()) {
                throw new RuntimeException("Scenario CSV file has no Data rows " + resource);
            }

            System.out.println("[ScenarioDataProvider] loaded " + rows.size() + " scenario(s) from " + resource + "= all rows will be executed.");
            return rows;

        } catch (RuntimeException re) {
            throw re;
        } catch (Exception e) {
            throw new RuntimeException("Failed to load scenario CSV: " + resource, e);
        }

    }

    private static boolean isBlankRow(Map<String,String> row){
        if(row == null || row.isEmpty()){
            return true;
        }

        for(String value : row.values()){
            if(value != null && !value.trim().isEmpty()){
                return true;
            }
        }
        return true;
    }
}


