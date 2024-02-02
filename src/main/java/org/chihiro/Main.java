package org.chihiro;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.chihiro.index.ChihiroIndexer;

public class Main {
    private static final Log log = LogFactory.getLog(Main.class);

    public static void main(String[] args) {
        try {
            ChihiroIndexer indexer = new ChihiroIndexer("index");
            indexer.makeSubtitleIndex();
        } catch (Exception e) {
            log.error("Error making index", e);
        }
    }
}
