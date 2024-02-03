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

import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class ChihiroIndexer {
    private final Directory indexDirectory;
    private final LinkedList<Path> subtitleDirectoryPaths;
    private final SubtitleScanner scanner;

    private static final Log log = LogFactory.getLog(SrtSubtitleLoader.class);

    public ChihiroIndexer(Path indexDirectoryPath) throws IOException {
        this.indexDirectory = FSDirectory.open(indexDirectoryPath);
        this.subtitleDirectoryPaths = new LinkedList<>();
        this.scanner = new SubtitleScanner(Arrays.asList("srt"), false);
    }

    public void addSubtitleDirectory(Path directory) {
        this.subtitleDirectoryPaths.add(directory);
    }

    public void makeSubtitleIndex() throws IOException, InvalidDirectoryException {
        IndexWriterConfig config = new IndexWriterConfig(new ChihiroAnalyzer());
        IndexWriter writer = new IndexWriter(indexDirectory, config);

        SrtSubtitleLoader loader = new SrtSubtitleLoader("UTF-8");

        List<Path> subtitles = scanner.scanDirectories(subtitleDirectoryPaths);

        for(Path path : subtitles) {
            List<Document> docs = loader.fromFile(path.toFile());

            for (Document doc : docs) {
                writer.addDocument(doc);
            }
            log.info("Indexed " + path + " (" + docs.size() + " documents)");
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
