package org.chihiro.analyzer;

import org.apache.lucene.analysis.*;
import org.apache.lucene.analysis.cjk.CJKWidthFilter;
import org.apache.lucene.analysis.ja.*;


public class ChihiroAnalyzer extends Analyzer {
    private final JapaneseTokenizer.Mode mode;

    public ChihiroAnalyzer() {
        this(JapaneseTokenizer.DEFAULT_MODE);
    }

    public ChihiroAnalyzer(JapaneseTokenizer.Mode mode) {
        super();
        this.mode = mode;
    }

    @Override
    protected TokenStreamComponents createComponents(String fieldName) {
        Tokenizer tokenizer = new JapaneseTokenizer(
                null,
                true,
                true, mode
        );

        TokenStream stream = new JapaneseBaseFormFilter(tokenizer);
        stream = new CJKWidthFilter(stream);
        stream = new JapaneseKatakanaStemFilter(stream);
        stream = new JapaneseReadingFormFilter(stream, true);
        stream = new LowerCaseFilter(stream);
        return new TokenStreamComponents(tokenizer, stream);
    }
}
