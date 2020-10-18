package project_IR;

import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;


import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class Searcher {
    public static List<Document> search(String queryString, Integer k, Path index_directory) throws ParseException, IOException {

        MultiFieldQueryParser parser = new MultiFieldQueryParser(new String[]{"title","tags" ,"question", "answers" }, new SimpleAnalyzer());
        Query query = parser.parse(queryString);

        FSDirectory dir = FSDirectory.open(index_directory);
        IndexReader reader = DirectoryReader.open(dir);
        IndexSearcher searcher = new IndexSearcher(reader);

        ScoreDoc[] scoreDocs = searcher.search(query, k).scoreDocs;


        List<Document> documents = scoreDocs_to_docsList(scoreDocs, searcher);

        System.out.println("topdocs:  " + documents.toString());

        return documents;


    }

    private static List<Document> scoreDocs_to_docsList(ScoreDoc[] scoreDocs, IndexSearcher searcher) throws IOException {

        List<Document> documents = new ArrayList<Document>();

        for(ScoreDoc scDoc: scoreDocs){

            documents.add(searcher.doc(scDoc.doc));

        }
        return documents;




    }
}
