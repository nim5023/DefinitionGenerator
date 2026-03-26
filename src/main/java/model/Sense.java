package model;

import java.util.ArrayList;
import java.util.List;

public class Sense {
    public List<String> glosses = new ArrayList<>();
    public List<String> pos = new ArrayList<>();

    @Override
    public String toString() {
        return "Sense{" +
                "glosses=" + glosses +
                ", pos=" + pos +
                '}';
    }
}