import definition.DefinitionExtractor;
import definition.WordExtractor;
import io.FileUtils;
import model.Definition;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FishCardCreator {

    //    C:\Users\Nick\Desktop\FFXIV Talk\Items\2\fiishNameCards
//    C:\Users\Nick\Desktop\FFXIV_DATA\instances\names
    private static final String PATH_IN = "C:\\Users\\Nick\\Desktop\\FFXIV_DATA\\instances\\names\\";
    public static final String IN_FILE = "names.txt";
    public static final String OUT_FILE = "DEFINITIONS.txt";

    private static final int UTTERANCE = 0;
    private static final String kanjiRegex = ".*[\\u4E00-\\u9FFF].*";
    private static final Pattern kanjiPattern = Pattern.compile(kanjiRegex);

    public static void main(String[] args) {
        populateDefinitions();
    }

    // C:\Users\Nick\Desktop\FFXIV_DATA\monsters\2   \monsterNames.txt
// C:\Users\Nick\Desktop\FFXIV Talk\Talk\2   \Names.txt
//C:\Users\Nick\Desktop\FFXIV_DATA\PlaceNames\2   \PlaceNames.txt

    public static void populateDefinitions() {

        for (int i = 2; i <= 7; i++) {


            List<String> monsters = FileUtils.readFromFile("C:\\Users\\Nick\\Desktop\\FFXIV_DATA\\monsters\\" + i + "\\monsterNames.txt");
            List<String> npcs = FileUtils.readFromFile("C:\\Users\\Nick\\Desktop\\FFXIV Talk\\Talk\\" + i + "\\Names.txt");
            List<String> places = FileUtils.readFromFile("C:\\Users\\Nick\\Desktop\\FFXIV_DATA\\PlaceNames\\" + i + "\\PlaceNames.txt");
//            cards = cleanMeaning(cards);
//            cards = filterKanji(cards);
//            cards = filterFish(cards);

            Set<String> allCards = new LinkedHashSet<>();
            addKanjiCards(allCards, monsters);
            addKanjiCards(allCards, npcs);
            addKanjiCards(allCards, places);

            List<String> outCards = new ArrayList<>(allCards);
            outCards = addMeaningToCards(outCards);

            FileUtils.createFileFromList("C:\\Users\\Nick\\Desktop\\FINAL_TALK\\FINAL_NAME\\" + i + ".txt", outCards);
        }

        System.out.println("------------------- ALL Bad Tokens ------------------");

        for (String badToken : WordExtractor.badTokens.keySet()) {
            if (kanjiPattern.matcher(badToken).matches()) {

                System.out.println("Bad Token: " + badToken);


//                List<String> badCards = WordExtractor.badTokens.get(badToken);
//                System.out.println();
//                System.out.println("-------" + badToken + "-------");
//                for (String badCard : badCards) {
//                    System.out.println(badCard);
//                }
            }
        }


    }

    private static void addKanjiCards(Set<String> allCards, List<String> inCards) {
        for (String inCard : inCards) {

            Matcher matcher = kanjiPattern.matcher(inCard);
            if (matcher.matches()) {
                if (!inCard.startsWith("無名"))
                    allCards.add(inCard);
            } else {

//                System.out.println(inCard);
            }
        }
    }

    private static List<String> filterFish(List<String> cards) {
        List<String> returnCards = new ArrayList<>();
        for (String card : cards) {
            String[] tagSplits = card.split("\t", -1);
            if (!tagSplits[2].contains("ffx14fish")) {
                returnCards.add(card);
            } else {
                System.out.println("Fish -- " + tagSplits[0]);
            }
        }
        return returnCards;
    }

    private static List<String> filterKanji(List<String> cards) {
        List<String> returnCards = new ArrayList<>();
        for (String card : cards) {
            String[] tagSplits = card.split("\t", -1);
            if (kanjiPattern.matcher(tagSplits[0]).matches()) {
                returnCards.add(card);
            }
        }
        return returnCards;
    }

    private static List<String> cleanMeaning(List<String> cards) {

        List<String> returnCards = new ArrayList<>();
        for (String card : cards) {
            String[] tagSplits = card.split("\t", -1);
            if (tagSplits.length > 2) {
                tagSplits[2] = "";
                returnCards.add(String.join("\t", tagSplits));

            }
        }
        return returnCards;
    }


    private static List<String> addMeaningToCards(List<String> cards) {
        List<String> returnCards = new ArrayList<>();
        for (String card : cards) {
            String[] tagSplits = card.split("\t", -1);

            if (tagSplits.length > 1) {

                StringBuilder meaning = new StringBuilder();
                String expression = tagSplits[UTTERANCE];
                List<Definition> definitions = DefinitionExtractor.findDefinitions(expression);

                int i = 0;
                for (Definition definition : definitions) {
                    StringBuilder strb = new StringBuilder();
                    strb.append(definition.word);
                    for (String reading : definition.readings) {
                        strb.append(" (").append(reading).append(")");
                    }
                    strb.append("<br>");

                    List<String> joinedDefs = new ArrayList<>();
                    for (List<String> defs : definition.definitions) {
                        joinedDefs.add("- " + String.join("; ", defs));
                    }
                    strb.append(String.join("<br>", joinedDefs));

                    if (i++ < definitions.size() - 1) {
                        strb.append("<br><br>");
                    }

                    meaning.append(strb);
                }

                returnCards.add(card + "\t" + meaning);
            } else {
                System.out.println("the fuk: " + card);
            }
        }

        return returnCards;
    }


}
