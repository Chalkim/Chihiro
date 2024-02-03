package org.chihiro;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.document.Document;
import org.chihiro.index.ChihiroIndexer;
import org.chihiro.search.ChihiroSearch;

import java.nio.file.Path;
import java.util.List;

public class Main {
    private static final Log log = LogFactory.getLog(Main.class);

    public static void main(String[] args) {
        Path indexDirectoryPath = Path.of("./index");

        try {
            ChihiroIndexer indexer = new ChihiroIndexer(indexDirectoryPath);
            indexer.cleanIndex();
            indexer.addSubtitleDirectory(Path.of("./data"));
            indexer.makeSubtitleIndex();
        } catch (Exception e) {
            log.error("Error making index", e);
        }

        try {
            ChihiroSearch search = new ChihiroSearch(indexDirectoryPath);
            List<Document> res = search.search("カエル");
            for (Document doc : res) {
                String message = String.format("Path: %s\n%s\t%s\n%s",
                        doc.get("path"),
                        doc.get("start_time"), doc.get("end_time"), doc.get("text"));
                log.info(message);
            }
        } catch (Exception e) {
            log.error("Error searching index", e);
        }
    }
}
