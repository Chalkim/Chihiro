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
import org.chihiro.exception.InvalidDirectoryException;
import org.chihiro.index.scanner.SubtitleScanner;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class ChihiroIndexer {
    private final Directory indexDirectory;
    private final LinkedList<File> subtitleDirectories;
    private final SubtitleScanner scanner;

    private static final Log log = LogFactory.getLog(SrtSubtitleLoader.class);

    public ChihiroIndexer(String indexDirectory) throws IOException {
        this.indexDirectory = FSDirectory.open(Paths.get(indexDirectory));
        this.subtitleDirectories = new LinkedList<>();
        this.scanner = new SubtitleScanner(Arrays.asList("srt"), false);
    }

    public void addSubtitleDirectory(File directory) {
        subtitleDirectories.add(directory);
    }

    public void makeSubtitleIndex() throws IOException, InvalidDirectoryException {
        IndexWriterConfig config = new IndexWriterConfig(new ChihiroAnalyzer());
        IndexWriter writer = new IndexWriter(indexDirectory, config);

        SrtSubtitleLoader loader = new SrtSubtitleLoader("UTF-8");

        List<File> subtitles = scanner.scanDirectories(subtitleDirectories);

        for(File file : subtitles) {
            List<Document> docs = loader.fromFile(file);

            for (Document doc : docs) {
                writer.addDocument(doc);
            }
            log.info("Indexed " + file.getPath() + " (" + docs.size() + " documents)");
        }

        writer.commit();
        writer.close();
    }

    public void cleanIndex() throws IOException {
        IndexWriterConfig config = new IndexWriterConfig(new ChihiroAnalyzer());
        IndexWriter writer = new IndexWriter(indexDirectory, config);
        writer.deleteAll();
        writer.commit();
        writer.close();
    }
}
