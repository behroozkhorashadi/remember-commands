package com.khorashadi.index;

import com.khorashadi.models.GeneralRecord;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.nio.file.FileSystems;


public class Indexer {
    private final IndexWriter writer;

    public Indexer(String indexDirectoryPath) throws IOException {
        //this directory will contain the indexes
        Directory indexDirectory =
                FSDirectory.open(FileSystems.getDefault().getPath(indexDirectoryPath));

        Analyzer analyzer = new StandardAnalyzer();
        IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
        iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
        writer = new IndexWriter(indexDirectory, iwc);
    }

    public void close() throws IOException {
        writer.close();
    }

    public void indexDoc(GeneralRecord generalRecord) throws IOException {
        Document doc = new Document();
        doc.add(new StringField(IndexConstants.UUID, generalRecord.getUuid(), Field.Store.YES));
        doc.add(new TextField(
                IndexConstants.TAGS, generalRecord.getUserTagsRaw(), Field.Store.YES));
        doc.add(new TextField(IndexConstants.CONTENTS,
                IndexUtils.extractText(generalRecord.getMainInfo()), Field.Store.YES));
        writer.updateDocument(new Term("id", generalRecord.getUuid()), doc);
    }
}
