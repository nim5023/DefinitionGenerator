package jmdict;

import javax.xml.stream.*;
import java.io.*;
import java.util.*;
import model.*;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;






public class JMDictParser {

    public static String JMDICT_FILE = "D:\\jmdict\\JMdict_e\\JMdict_e";
    public static String JSON_FILE = "D:\\jmdict\\JMdict_e\\jmdict_index.json";

    private static void test() throws IOException {

        Map<String, List<Entry>> derp = new HashMap<>();
        List<Entry> entry = new ArrayList<>();
        Entry e1 = new Entry();
        e1.readings = List.of("readings");
        e1.kanji = List.of("kanji");
        e1.pri = Set.of("pri");
        e1.senses = new ArrayList<>();
        Sense sense = new Sense();
        sense.pos = List.of("pos");
        sense.glosses = List.of("glosses");
        e1.senses.add(sense);
        entry.add(e1);
        derp.put("derp", entry);

        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(new File(JSON_FILE), derp);

        Map<String, List<Entry>> loaded = mapper.readValue(new File(JSON_FILE),
                new TypeReference<Map<String, List<Entry>>>() {
                });
        System.out.println(loaded);
    }

    public static void main(String[] args) throws Exception {
        List<Entry> index = parse(JMDICT_FILE);
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(new File(JSON_FILE), index);
    }

    public static List<Entry> parse(String filePath) throws Exception {
        List<Entry> index = new ArrayList<>();
        List<String> longwords = new ArrayList<>();

        XMLInputFactory factory = XMLInputFactory.newInstance();
        factory.setProperty("http://www.oracle.com/xml/jaxp/properties/entityExpansionLimit", 1000000);

        XMLStreamReader reader = factory.createXMLStreamReader(new FileInputStream(filePath));

        Entry current = null;
        Sense sense = null;
        String currentElement = null;
        int count = 0;
        int maxSize = 0;
        while (reader.hasNext()) {
            int event = reader.next();

            if (event == XMLStreamConstants.START_ELEMENT) {
                currentElement = reader.getLocalName();

                if (currentElement.equals("entry")) {
                    current = new Entry();
                    System.out.println(count++);
                } else if (currentElement.equals("sense")) {
                    if (current != null) {
                        sense = new Sense();
                        current.senses.add(sense);
                    }

                }

            } else if (event == XMLStreamConstants.CHARACTERS) {
                if (current == null) continue;

                String text = reader.getText().trim();
                if (text.isEmpty()) continue;

                switch (Objects.requireNonNull(currentElement)) {
                    case "keb" -> {

//                        System.out.println("**-KANJI- " + text);
                        int temp = maxSize;
                        maxSize = Math.max(text.length(), maxSize);
                        if(temp < maxSize) longwords.add(text);
                        current.kanji.add(text);
                    }
                    case "reb" ->{
                        int temp = maxSize;
                        maxSize = Math.max(text.length(), maxSize);
                        if(temp < maxSize) longwords.add(text);
//                        System.out.println("**-READING- " + text);
                            current.readings.add(text);
                }
                    case "gloss" -> {
//                        System.out.println(" -DEF- " + text);
                        if (sense != null) {
                            sense.glosses.add(text);
                        } else {
                            System.out.println("weird shit: " + current.readings);
                        }
                    }
                    case "ke_pri", "pri", "re_pri" -> current.pri.add(text);
                    case "pos" -> sense.pos.add(text);

//                    default:
//                        if (text.contains("jlpt")) {
//                            System.out.println("winner!");
//                        }
////                        System.out.println(currentElement + "   " + text);
                }

            } else if (event == XMLStreamConstants.END_ELEMENT) {
                if (reader.getLocalName().equals("entry")) {
                    // index it
//                    System.out.println("----------------------------------");
//                    for (String k : current.kanji) {
//                        index.computeIfAbsent(k, x -> new ArrayList<>()).add(current);
//                    }
//                    for (String r : current.readings) {
//                        index.computeIfAbsent(r, x -> new ArrayList<>()).add(current);
//                    }
                    index.add(current);
                }
            }
        }

        System.out.println(maxSize);
        System.out.println(longwords);
        return index;
    }
}