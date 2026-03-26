package model;

public class Token {
    public    String surface;
    public   String base;
    public   String pos;

    public Token () {}

    public Token(String surface, String base, String pos) {
        this.surface = surface;
        this.base = base;
        this.pos = pos;
    }

    public   boolean isVerb() {
        return pos.startsWith("動詞");
    }

    public  boolean isAuxiliary() {
        return pos.startsWith("助動詞") || pos.equals("動詞-非自立");
    }

    public  boolean isTeForm() {
        return pos.equals("助詞-接続助詞");
    }
}
