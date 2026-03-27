package definition;

import frequency.FrequencyCreator;
import kuromoji.BaseForm;
import model.Definition;
import model.Entry;
import model.Match;
import model.Token;

import java.io.IOException;
import java.util.*;

public class WordExtractor {

    private static Map<String, List<Entry>> dictionary = DictionaryCreator.createDictionary();

    public static void main(String[] args) throws IOException {

        findWords("昨日は雨が降っていたので、私は傘を持って学校に行きましたが、友達は忘れてしまいました。");

    }

    public static List<String> findWords(String text) {
        List<Token> tokens = BaseForm.findTokens(text);
        return findMatches(tokens, dictionary.keySet());
    }

    public static Set<String> badTokens = new HashSet<>();

    static List<String> findMatches(List<Token> tokens, Set<String> dictionary) {
        List<String> definitions = new ArrayList<>();
        int index = 0;

        while (index < tokens.size()) {
            int window = tokens.size() - index;
            while (window > 0) {
                Match match = getMatch(tokens, index, window);
                String found = findInDictionary(match, dictionary);

                if (found != null) {
                    if (match.isWorthy) definitions.add(found);
                    index = getNextIndex(tokens, match);
                    break;
                }

                window--;
            }

            if (window == 0) {
                System.out.println("Bad Token: " + tokens.get(index).surface);
                badTokens.add(tokens.get(index).surface);
                index++;
            }
        }

        System.out.println();
        return definitions;
    }


    private static String findInDictionary(Match match, Set<String> dict) {
        if (dict.contains(match.baseSequence)) return match.baseSequence;
        if (dict.contains(match.surfaceSequence)) return match.surfaceSequence;
        return null;
    }


    static int getNextIndex(List<Token> tokens, Match match) {
        int index = match.endIndex + 1;

        while (index < tokens.size()) {
            Token t = tokens.get(index);
            if (!t.isAuxiliary() && !t.isTeForm()) {
                break;
            }
            index++;
        }

        return index;
    }

    static Match getMatch(List<Token> tokens, int index, int window) {
        StringBuilder surfaceSeq = new StringBuilder();
        StringBuilder baseSeq = new StringBuilder();

        for (int i = index; i < index + window; i++) {
            Token token = tokens.get(i);
            surfaceSeq.append(token.surface);
            baseSeq.append(token.base == null ? token.surface : token.base);
        }

        Match match = new Match();
        match.baseSequence = baseSeq.toString();
        match.surfaceSequence = surfaceSeq.toString();
        match.startIndex = index;
        match.endIndex = index + window - 1;

        if (window <= 1) {
            Token token = tokens.get(index);
            if (token.isParticle() || token.isTeForm() || token.isAuxiliary()) {
                match.isWorthy = false;
            }
        }
        return match;
    }

}
