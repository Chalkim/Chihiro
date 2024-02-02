package org.chihiro.index;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.chihiro.analyzer.ChihiroAnalyzer;
import org.chihiro.document.subtitle.SrtSubtitleLoader;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class ChihiroIndexer {
    private final Directory indexDirectory;
    private ArrayList<String> documentDirectory;

    private static final Log log = LogFactory.getLog(SrtSubtitleLoader.class);

    public ChihiroIndexer(String indexDirectory) throws IOException {
        this.indexDirectory = FSDirectory.open(Paths.get(indexDirectory));
    }

    public void makeSubtitleIndex() throws IOException {
        ChihiroAnalyzer analyzer = new ChihiroAnalyzer();
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        IndexWriter writer = new IndexWriter(indexDirectory, config);

        SrtSubtitleLoader loader = new SrtSubtitleLoader("UTF-8");
        String path = "./data/[LoliHouse] Higurashi no Naku Koro ni Gou - 01 [WebRip 1080p HEVC-10bit AAC SRTx4].srt";
        List<Document> docs = loader.fromFile(path);

        for (Document doc : docs) {
            log.debug("Adding document to index" + doc);
            writer.addDocument(doc);
        }
        // info
        log.info("Committing index: " + path);

        writer.commit();
        writer.close();

        analyzer.close();
    }
}
