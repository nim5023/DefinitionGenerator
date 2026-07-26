import definition.DefinitionExtractor;
import definition.WordExtractor;
import io.FileUtils;
import model.Definition;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class DefinitionPopulator {

    private static final String FILE_IN = "C:\\Users\\Nick\\Desktop\\ADD_DEFINITION";
    private static final String FILE_OUT = "C:\\Users\\Nick\\Desktop\\ADD_DEFINITION\\Definitions";

    private static final String kanjiRegex = ".*[\\u4E00-\\u9FFF].*";
    private static final Pattern kanjiPattern = Pattern.compile(kanjiRegex);

    private static final int UTTERANCE = 1;


    public static void main(String[] args) throws Exception {
        Path inputDir = Paths.get(FILE_IN);
        Path outputDir = Paths.get(FILE_OUT);

        Files.list(inputDir)
                .filter(Files::isRegularFile)
                .forEach(path -> {
                    String outputPath = outputDir.resolve(path.getFileName()).toString();
                    populateDefinitions(path.toString(), outputPath);
                });
    }


    public static void populateDefinitions(String inFile, String outFile) {
        List<String> cards = FileUtils.readFromFile(inFile);
//        cards = cleanMeaning(cards);
        cards = addMeaningToCards(cards);

        for (String badToken : WordExtractor.badTokens.keySet()) {
            if (kanjiPattern.matcher(badToken).matches()) {
                System.out.println(badToken);
//                List<String> badCards = WordExtractor.badTokens.get(badToken);
//                System.out.println();
//                System.out.println("-------" + badToken + "-------");
//                for (String badCard : badCards) {
//                    System.out.println(badCard);
//                }
            }
        }

        FileUtils.createFileFromList(outFile, cards);
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

            if (tagSplits.length > 2) {

                String expression = tagSplits[UTTERANCE];
                List<Definition> definitions = DefinitionExtractor.findDefinitions(expression);

                StringBuilder defStb = new StringBuilder();
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

                    defStb.append(strb);
                }

                returnCards.add(card + "\t" + defStb);
            }
        }

        return returnCards;
    }


}
