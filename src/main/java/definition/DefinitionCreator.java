package definition;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import kuromoji.BaseForm;
import model.Entry;
import model.Match;
import model.Token;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class DefinitionCreator {

    // load dictionary
    // max(length, 37)
    // check surface
    // if hit
    //    split left & right, recursive surface
    // else check base
    //  if hit
    //    split left & right, recursive SURFACE
    public static String JSON_FILE = "D:\\jmdict\\JMdict_e\\jmdict_index.json";

    public static void main(String[] args) throws IOException {

        Map<String, List<Entry>> dictionary = createDictionary();
        List<Token> tokens = BaseForm.findTokens("昨日は雨が降っていたので、私は傘を持って学校に行きましたが、友達は忘れてしまいました。");
        List<String> definitions = findMatches(tokens, dictionary.keySet());
        System.out.println(definitions);
        for (String definition : definitions) {
            List<Entry> entries = dictionary.get(definition);
            for (Entry entry : entries) {
                System.out.println(entry);
                System.out.println("");
            }
        }
    }

    static Map<String, List<Entry>> createDictionary() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        List<Entry> entries = mapper.readValue(new File(JSON_FILE),
                new TypeReference<List<Entry>>() {
                });

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

    static List<String> findMatches(List<Token> tokens, Set<String> dictionary) {
        List<String> definitions = new ArrayList<>();
        int index = 0;
        int window = tokens.size();
        while (index < tokens.size()) {
            Match match = getMatch(tokens, index, window);
            if (dictionary.contains(match.baseSequence)) {
                if (match.isWorthy) definitions.add(match.baseSequence);
                index = getNextIndex(tokens, match);
                window = tokens.size() - index;
            } else if (dictionary.contains(match.surfaceSequence)) {
                if (match.isWorthy) definitions.add(match.surfaceSequence);
                index = getNextIndex(tokens, match);
                window = tokens.size() - index;
            } else {
                window--;
            }
            if (window <= 0) {
                System.out.println("Bad Token: " + tokens.get(index).surface);
                index++;
                window = tokens.size() - index;
            }

        }
        return definitions;
    }

    static int getNextIndex(List<Token> tokens, Match match) {
        int index = match.endIndex;
        boolean finding = true;
        while (finding && index < tokens.size()) {
            index++;
            Token nextToken = tokens.get(index);
            if (!nextToken.isAuxiliary() && !nextToken.isTeForm())
                finding = false;
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

        if(window <= 1) {
            Token token = tokens.get(index);
            if (token.isParticle() || token.isTeForm() || token.isAuxiliary()) {
                match.isWorthy = false;
            }
        }
        return match;
    }


    static List<Match> findMatche123123s(List<Token> tokens, Set<String> dictionary, int maxPhraseLength) {
        List<Match> matches = new ArrayList<>();
        int i = 0;

        while (i < tokens.size()) {
            Token t = tokens.get(i);

            // Check if token is the start of a verb/aux chain
            if (t.isVerb() || t.isAuxiliary()) {
                int j = i;
                StringBuilder surfaceSeq = new StringBuilder();
                StringBuilder baseSeq = new StringBuilder();

                // collapse verb + auxiliaries + te-forms
                while (j < tokens.size() && (tokens.get(j).isVerb() || tokens.get(j).isAuxiliary() || tokens.get(j).isTeForm())) {
                    surfaceSeq.append(tokens.get(j).surface);
                    if (tokens.get(j).base != null) {
                        baseSeq.append(tokens.get(j).base);
                    }
                    j++;
                }

                // Attempt dictionary lookup on multi-token phrases (up to maxPhraseLength)
                boolean found = false;
                int phraseEnd = j;
                for (int len = Math.min(maxPhraseLength, j - i); len > 0; len--) {
                    StringBuilder phraseSurface = new StringBuilder();
                    StringBuilder phraseBase = new StringBuilder();
                    for (int k = 0; k < len; k++) {
                        phraseSurface.append(tokens.get(i + k).surface);
                        if (tokens.get(i + k).base != null) phraseBase.append(tokens.get(i + k).base);
                    }

                    if (dictionary.contains(phraseBase.toString())) {
                        matches.add(new Match(phraseSurface.toString(), phraseBase.toString(), i, i + len));
                        i += len;
                        found = true;
                        break;
                    } else if (dictionary.contains(phraseSurface.toString())) {
                        matches.add(new Match(phraseSurface.toString(), phraseSurface.toString(), i, i + len));
                        i += len;
                        found = true;
                        break;
                    }
                }

                if (!found) {
                    // fallback: collapsed sequence itself
                    matches.add(new Match(surfaceSeq.toString(), baseSeq.toString(), i, j));
                    i = j;
                }

            } else {
                // non-verb token: try single-token dictionary match
                String baseKey = t.base != null ? t.base : t.surface;
                if (dictionary.contains(baseKey)) {
                    matches.add(new Match(t.surface, baseKey, i, i + 1));
                } else {
                    matches.add(new Match(t.surface, null, i, i + 1)); // unknown
                }
                i++;
            }
        }

        return matches;
    }

}
