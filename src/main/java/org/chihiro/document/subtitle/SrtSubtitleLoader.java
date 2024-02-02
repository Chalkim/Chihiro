package org.chihiro.document.subtitle;

import fr.noop.subtitle.model.SubtitleCue;
import fr.noop.subtitle.model.SubtitleParsingException;
import fr.noop.subtitle.srt.SrtObject;
import fr.noop.subtitle.srt.SrtParser;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.KeywordField;
import org.apache.lucene.document.TextField;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SrtSubtitleLoader {
    private final String charset;

    private static final Log log = LogFactory.getLog(SrtSubtitleLoader.class);

    public SrtSubtitleLoader(String charset) {
        this.charset = charset;
    }

    public List<Document> fromFile(File file) {
        List<Document> documents = new ArrayList<>();
        try {
            SrtParser parser = new SrtParser(this.charset);
            SrtObject subtitle = parser.parse(new FileInputStream(file));

            if(subtitle != null) {
                for (SubtitleCue cue : subtitle.getCues()) {
                    Document doc = new Document();
                    doc.add(new KeywordField("path", file.getPath(), Field.Store.YES));
                    doc.add(new TextField("text", cue.getText(), Field.Store.YES));
                    doc.add(new TextField("start_time", cue.getStartTime().toString(), Field.Store.YES));
                    doc.add(new TextField("end_time", cue.getEndTime().toString(), Field.Store.YES));

                    documents.add(doc);
                }
            }

        } catch(SubtitleParsingException | IOException e) {
            log.error("Error processing subtitle file", e);
        }
        return documents;
    }
}
