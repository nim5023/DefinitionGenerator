
import io.FileUtils;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class FindKanji {

    private static final String FILE_IN_KANJI = "C:\\Users\\Nick\\Desktop\\漢字.txt";
    //    private static final String FILE_IN_FFXIV = "D:\\anki_export\\_2026\\_FFXIV_ALL_TEXT.txt";
//    private static final String FILE_IN_FFXIV = "C:\\Users\\Nick\\Desktop\\FF_QUESTS\\Final\\allCards.txt";
    private static final String FILE_IN_FFXIV = "C:\\Users\\Nick\\Desktop\\ARR_CARDS_ALL.txt";


    //    private static final String FILE_OUT = "C:\\Users\\Nick\\Desktop\\FFXIV_Kanji.txt";
    private static final String FILE_OUT = "C:\\Users\\Nick\\Desktop\\ARR_Unknown_Kanji.txt";


    private static final String kanjiRegex = ".*[\\u4E00-\\u9FFF].*";
    private static final Pattern kanjiPattern = Pattern.compile(kanjiRegex);


    public static void main(String[] args) {

        Set<Character> knownKanji = new HashSet<>();

        Set<String> kanjis = new HashSet<>(FileUtils.readFromFile(FILE_IN_KANJI));

        for (String kanji : kanjis) {
            String[] tokens = kanji.split("\t");
            if (tokens.length > 2) {
                knownKanji.add(tokens[0].toCharArray()[0]);
            }
        }

        Map<Character, Integer> frequency = new HashMap<>();

        List<String> lines = FileUtils.readFromFile(FILE_IN_FFXIV);

        for (String line : lines) {
            for (char c : line.toCharArray()) {
                frequency.put(c, frequency.getOrDefault(c, 0) + 1);
            }
        }


//        frequency.keySet().removeAll(knownKanji);

        List<Map.Entry<Character, Integer>> list = new ArrayList<>(frequency.entrySet());
        list.sort(Map.Entry.comparingByValue());
        Collections.reverse(list);


        Set<Character> unUsedKanji = knownKanji.stream()
                .filter(e -> !frequency.containsKey(e))
                .collect(Collectors.toSet());

        System.out.println(unUsedKanji);


        List<String> output = new ArrayList<>();
        for (Map.Entry<Character, Integer> entry : list) {
            output.add(entry.getKey().toString());

            if (knownKanji.contains(entry.getKey())) {
                System.out.print(entry.getKey().toString());
            }

        }

        System.out.println("");
        System.out.println("");
        System.out.println("");
        filterKanji(output);

        FileUtils.createFileFromList(FILE_OUT, output);
    }


    private static void filterKanji(List<String> words) {
        List<String> shitlist = new ArrayList<>();
        for (Iterator<String> itr = words.iterator(); itr.hasNext(); ) {
            String word = itr.next();
            Matcher matcher = kanjiPattern.matcher(word);
            if (!matcher.matches()) {
                shitlist.add(word);
                itr.remove();
            }
        }
        Collections.sort(shitlist);
        for (String shit : shitlist) {
            System.out.print(shit);
        }
    }


}
