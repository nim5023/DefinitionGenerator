package model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Entry {
    public String word;
    public List<String> kanji = new ArrayList<>();
    public List<String> readings = new ArrayList<>();
    public Set<String> pri = new HashSet<>();
    public List<Sense> senses = new ArrayList<>();

    @Override
    public String toString() {
        return "Entry{" +
                "kanji=" + kanji +
                ", readings=" + readings +
                ", pri=" + pri +
                ", senses=" + senses +
                '}';
    }
}