package definition;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import model.Entry;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DictionaryCreator {


    /*
           READING (not kanji) -
           find the most frequently used entry??

           KANJI (not reading) -
           match frequency to my Kanji...

    */
    public static String JSON_FILE = "D:\\jmdict\\JMdict_e\\jmdict_index.json";

    public static Map<String, List<Entry>> createDictionary() {

        List<Entry> entries = readJmDict();
        Map<String, List<Entry>> dictionary = new HashMap<>();
        for (Entry entry : entries) {
            for (String k : entry.kanji) {
                dictionary.computeIfAbsent(k, x -> new ArrayList<>()).add(entry);
            }
            for (String r : entry.readings) {
                dictionary.computeIfAbsent(r, x -> new ArrayList<>()).add(entry);
            }
        }
        return dictionary;
    }

    private static List<Entry> readJmDict() {
        ObjectMapper mapper = new ObjectMapper();
        List<Entry> entries = null;
        try {
            entries = mapper.readValue(new File(JSON_FILE),
                    new TypeReference<List<Entry>>() {
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }
        return entries;
    }

}
