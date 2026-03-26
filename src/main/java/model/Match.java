package model;


public class Match {
    public String surfaceSequence;
    public String baseSequence;
    public int startIndex;  // token index
    public int endIndex;    // token index after chain
    public boolean isWorthy = true;    // token index after chain

    public Match(){}
    public Match(String surfaceSequence, String baseSequence, int startIndex, int endIndex) {
        this.surfaceSequence = surfaceSequence;
        this.baseSequence = baseSequence;
        this.startIndex = startIndex;
        this.endIndex = endIndex;
    }

    @Override
    public String toString() {
        return String.format("%s [%s] (%d->%d)", surfaceSequence, baseSequence, startIndex, endIndex);
    }
}