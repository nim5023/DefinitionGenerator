import definition.DefinitionExtractor;
import definition.WordExtractor;
import io.FileUtils;
import model.Definition;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class DefinitionPopulator {

    private static final String FILE_IN = "C:\\Users\\Nick\\Desktop\\EXPORT.txt";
    private static final String FILE_OUT = "C:\\Users\\Nick\\Desktop\\Definition_Cards.txt";

    private static final String kanjiRegex = ".*[\\u4E00-\\u9FFF].*";
    private static final Pattern kanjiPattern = Pattern.compile(kanjiRegex);

    public static void main(String[] args) {
        List<String> cards = FileUtils.readFromFile(FILE_IN);
        cards = cleanMeaning(cards);
        cards = addMeaningToCards(cards);

        for(String badToken : WordExtractor.badTokens.keySet()) {
            if (kanjiPattern.matcher(badToken).matches()) {
                List<String> badCards = WordExtractor.badTokens.get(badToken);
                System.out.println();
                System.out.println("-------" + badToken + "-------");
                for (String badCard : badCards) {
                    System.out.println(badCard);
                }
            }
        }

        FileUtils.createFileFromList(FILE_OUT, cards);
    }

    private static List<String> cleanMeaning(List<String> cards) {

        List<String> returnCards = new ArrayList<>();
        for (String card : cards) {
            String[] tagSplits = card.split("\t");
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
            String[] tagSplits = card.split("\t");

            if (tagSplits.length > 2) {

                String expression = tagSplits[3];
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

                    tagSplits[2] += strb.toString();
                }

                returnCards.add(String.join("\t", tagSplits));
            }
        }

        return returnCards;
    }


}
