package definition;

import frequency.FrequencyCreator;
import model.Definition;
import model.Entry;
import model.Sense;

import java.util.*;

public class DefinitionExtractor {

    private static final Map<String, List<Entry>> dictionary = DictionaryCreator.createDictionary();
    private static final Map<String, Map<String, Double>> frequencies = FrequencyCreator.createFrequency();
    private static final double FREQUENCY_THRESHOLD = 50.0;

    public static void main(String[] args) {

        List<Definition> definitions = findDefinitions("昨日は雨が降っていたので、私は傘を持って学校に行きましたが、友達は忘れてしまいました。");
        System.out.println(definitions);
    }

    public static List<Definition> findDefinitions(String text) {

        List<Definition> definitions = new ArrayList<>();
        List<String> words = WordExtractor.findWords(text);
        for (String word : words) {
            if (!isLowFrequency(word)) {
                List<Entry> entries = dictionary.get(word);
                if (entries.size() > 0) {
                    Definition definition = createDefinition(entries.get(0), word);
                    definitions.add(definition);
                }
            }
        }
        Comparator<Definition> comparator = Comparator.comparingDouble(d -> d.frequency);
        definitions.sort(comparator.reversed());
        return definitions;
    }

    private static boolean isLowFrequency(String word) {
        Map<String, Double> freq = frequencies.get(word);
        if (freq == null) return false;

        double min = Collections.min(freq.values());
        return min < FREQUENCY_THRESHOLD;
    }

    private static Definition createDefinition(Entry entry, String word) {

        Definition definition = new Definition();

        Map<String, Double> freq = frequencies.get(word);
        if (freq != null) {
            definition.frequency = Collections.min(freq.values());
        }

        for (String reading : entry.readings)
            if (word.equalsIgnoreCase(reading)) {
                definition.readings = findReadings(reading);
                if(definition.readings.isEmpty() && !entry.kanji.isEmpty())
                    definition.readings.add(entry.kanji.get(0));
            }

        for (String kanji : entry.kanji)
            if (word.equalsIgnoreCase(kanji)) {
                definition.readings = findReadings(kanji);
                if(definition.readings.isEmpty() && !entry.readings.isEmpty())
                    definition.readings.add(entry.readings.get(0));
            }

        definition.word = word;

        for (Sense sense : entry.senses)
            if (sense.glosses != null && !sense.glosses.isEmpty())
                definition.definitions.add(sense.glosses);


        return definition;
    }

    private static List<String> findReadings(String word) {
        List<String> readings = new ArrayList<>();
        Map<String, Double> freq = frequencies.get(word);
        if (freq != null) {
            double min = Collections.min(freq.values());
            for (String key : freq.keySet()) {
                if (freq.get(key) == min) {
                    readings.add(key);
                }
            }
        }
        return readings;
    }

        /*

                 System.out.println("-----");
            System.out.println(definition);
            for (Entry entry : entries) {
                System.out.println(entry);
                for (String k : entry.kanji)
                    System.out.println(k + " " + FrequencyCreator.frequency.get(k));
                for (String k : entry.readings)
                    System.out.println(k + " " + FrequencyCreator.frequency.get(k));
                System.out.println("");
            }
        }
         */


}
