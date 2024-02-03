package org.chihiro.search;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.StoredFields;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.chihiro.analyzer.ChihiroAnalyzer;

import java.io.IOException;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;

public class ChihiroSearch {
    private final Directory indexDirectory;
    private final QueryParser parser;

    private static final Log log = LogFactory.getLog(ChihiroSearch.class);

    public ChihiroSearch(Path indexDirectoryPath) throws IOException {
        Analyzer analyzer = new ChihiroAnalyzer();

        this.indexDirectory = FSDirectory.open(indexDirectoryPath);
        this.parser = new QueryParser("text", analyzer);
    }

    public List<Document> search(String keyword) throws ParseException, IOException {
        log.info("Searching for " + keyword);

        IndexReader indexReader = DirectoryReader.open(indexDirectory);
        IndexSearcher indexSearcher = new IndexSearcher(indexReader);
        Query query = parser.parse(keyword);

        TopDocs topDocs = indexSearcher.search(query, 10);
        ScoreDoc[] hits = topDocs.scoreDocs;
        StoredFields storedFields = indexSearcher.storedFields();

        List<Document> documents = new LinkedList<>();

        for (ScoreDoc hit : hits) {
            Document doc = storedFields.document(hit.doc);
            log.info("doc=" + hit.doc + " score=" + hit.score);

            documents.add(doc);
        }

        indexReader.close();

        return documents;
    }
}
