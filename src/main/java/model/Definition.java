package model;

import java.util.ArrayList;
import java.util.List;

public class Definition {
    public String word;  // base / surface of utterance
    public double frequency = 200.0;
    public List<String> readings = new ArrayList<>();;   // Could be kanji or readings depending on word.  rank by frequency with threshold.
    public List<List<String>> definitions = new ArrayList<>();
}
