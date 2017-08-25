package com.khorashadi.index;


import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class RecordSearcher {
    private final IndexSearcher searcher;
    private final QueryParser queryParser;

    public RecordSearcher(String indexDirPath) throws IOException {
        IndexReader reader = DirectoryReader.open(FSDirectory.open(Paths.get(indexDirPath)));
        searcher = new IndexSearcher(reader);
        queryParser = new QueryParser(IndexConstants.TAGS, new StandardAnalyzer());
    }

    public Map<String, Float> runQuery(String queryString, int numResultPerPage)
            throws ParseException, IOException {
        Query query = queryParser.parse(queryString);
        TopDocs topDocs = searcher.search(query, numResultPerPage);
        Map<String, Float> result = new HashMap<>((int) (topDocs.totalHits / .75));
        for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
            Document doc = searcher.doc(scoreDoc.doc);
            result.put(doc.get(IndexConstants.UUID), scoreDoc.score * 100);
        }
        return result;
    }
}
