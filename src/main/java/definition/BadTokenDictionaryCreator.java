package definition;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.FileUtils;
import model.Entry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BadTokenDictionaryCreator {

    public static void main(String[] args) throws Exception {
        createDictionary();
    }

    public static Map<String, List<Entry>> createDictionary() {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, List<Entry>> map = new HashMap<>();
        try {
            List<String> string = FileUtils.readFromResourcesFile("BadTokensDictionary");
            for (String json : string) {
//                System.out.println(json);
                Entry entry = mapper.readValue(json, Entry.class);
                map.put(entry.word, List.of(entry));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

}
