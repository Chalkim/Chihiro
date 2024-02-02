package org.chihiro.analyzer;

import org.apache.lucene.analysis.*;
import org.apache.lucene.analysis.ja.*;
import org.apache.lucene.analysis.ja.dict.UserDictionary;

import java.util.Set;


public class ChihiroAnalyzer extends Analyzer {
    private final JapaneseTokenizer.Mode mode;
    private final UserDictionary userDict;

    public ChihiroAnalyzer() {
        this(null,
                JapaneseTokenizer.DEFAULT_MODE);
    }

    public ChihiroAnalyzer(
            UserDictionary userDict, JapaneseTokenizer.Mode mode) {
        super();
        this.userDict = userDict;
        this.mode = mode;
    }

    @Override
    protected TokenStreamComponents createComponents(String fieldName) {
        Tokenizer tokenizer = new JapaneseTokenizer(userDict, true, true, mode);
        TokenStream stream = new JapaneseReadingFormFilter(tokenizer);
        stream = new JapaneseKatakanaStemFilter(stream);
        stream = new LowerCaseFilter(stream);
        return new TokenStreamComponents(tokenizer, stream);
    }
}
