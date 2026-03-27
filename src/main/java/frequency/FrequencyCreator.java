package frequency;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FrequencyCreator {
    public static String JSON_FILE = "D:\\jmdict\\vn_freq\\term_meta_bank_1.json";

    public static Map<String, Map<String , Double>> createFrequency() {
        ObjectMapper mapper = new ObjectMapper();

        Map<String, Map<String , Double>> result = new HashMap<>();
        try {
            String jsonString = Files.readString(Path.of(JSON_FILE));

            List<List<Object>> raw = mapper.readValue(jsonString, List.class);

            for (List<Object> item : raw) {
                String word = (String) item.get(0);
                String freq = (String) item.get(1);
                if(!"freq".equalsIgnoreCase(freq)) {
                    System.out.println(freq);
                }

                Map<String, Object> freqMap = (Map<String, Object>) item.get(2);

                double frequency = ((Number) freqMap.get("frequency")).doubleValue();
                String reading = freqMap.get("reading").toString();

                result.computeIfAbsent(word, k -> new HashMap<>()).put(reading, frequency);
                result.computeIfAbsent(reading, k -> new HashMap<>()).put(word, frequency);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

}
