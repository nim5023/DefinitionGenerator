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

        WordExtractor.badTokens.stream()
                .filter(w-> kanjiPattern.matcher(w).matches())
                .forEach(System.out::println);

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

                for (Definition definition : definitions) {
                    StringBuilder strb = new StringBuilder();
                    strb.append(definition.word);
                    for (String reading : definition.readings) {
                        strb.append(" (").append(reading).append(")");
                    }
                    strb.append("<br>");
                    for (List<String> defs : definition.definitions) {
                        strb.append("- ");
                        strb.append(String.join("; ", defs));
                        strb.append("<br>");
                    }
                    strb.append("<br>");

                    tagSplits[2] += strb.toString();
                }

                returnCards.add(String.join("\t", tagSplits));
            }
        }

        return returnCards;
    }


}
