package kuromoji;

import model.Token;
import org.apache.lucene.analysis.ja.JapaneseTokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.ja.tokenattributes.BaseFormAttribute;
import org.apache.lucene.analysis.ja.tokenattributes.PartOfSpeechAttribute;


import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public class BaseForm {


    public static JapaneseTokenizer tokenizer = new JapaneseTokenizer(null, false, JapaneseTokenizer.Mode.NORMAL);

    public static void main(String[] args) throws IOException {
        JapaneseTokenizer tokenizer = new JapaneseTokenizer(null, false, JapaneseTokenizer.Mode.NORMAL);

//        tokenizer.setReader(new StringReader("とくていどくりつぎょうせいほうじんとうのろうどうかんけいにかんするほうりつ。"));
        tokenizer.setReader(new StringReader("昨日は雨が降っていたので、私は傘を持って学校に行きましたが、友達は忘れてしまいました。"));
        tokenizer.reset();


        StringBuilder stb = new StringBuilder();

        while (tokenizer.incrementToken()) {
            CharTermAttribute surfaceAttr = tokenizer.getAttribute(CharTermAttribute.class);
            String surface = surfaceAttr.toString();
            System.out.println(surface);

            System.out.print("base: ");
            BaseFormAttribute base = tokenizer.getAttribute(BaseFormAttribute.class);
            String baseForm = base.getBaseForm();
            System.out.println(baseForm);

//            System.out.print("pos: ");
            PartOfSpeechAttribute posAttr = tokenizer.getAttribute(PartOfSpeechAttribute.class);
            String pos = posAttr.getPartOfSpeech();
            System.out.println("pos: " + pos);
            System.out.println("---");

            stb.append(baseForm == null ? surface : baseForm);

//            System.out.println("");
        }
        System.out.println(stb.toString());
    }

    public static List<Token> findTokens(String utterance) throws IOException {
        tokenizer.setReader(new StringReader(utterance));
        tokenizer.reset();
        List<Token> tokens = new ArrayList<>();

        while (tokenizer.incrementToken()) {
            Token token = new Token();
            BaseFormAttribute base = tokenizer.getAttribute(BaseFormAttribute.class);
            token.base = base.getBaseForm();

            PartOfSpeechAttribute posAttr = tokenizer.getAttribute(PartOfSpeechAttribute.class);
            token.pos = posAttr.getPartOfSpeech();

            CharTermAttribute surfaceAttr = tokenizer.getAttribute(CharTermAttribute.class);
            token.surface = surfaceAttr.toString();
            tokens.add(token);
        }
        return tokens;

    }

}
