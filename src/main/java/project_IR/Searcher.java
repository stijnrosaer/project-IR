package project_IR;

import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.store.FSDirectory;
import org.javatuples.Pair;


import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class Searcher {
    /**
     *
     * @param queryString the query in string form
     * @param k the max amount that are allowed to be returned
     * @param index_directory the directory with the indexfiles
     * @return a list of pairs containing the top k  documents and their scores
     */
    public static List<Pair<Document, Float>> search(String queryString, Integer k, Path index_directory) throws ParseException, IOException {

        MultiFieldQueryParser parser = new MultiFieldQueryParser(new String[]{"title","tags" ,"question", "answers" }, new SimpleAnalyzer());
        Query query = parser.parse(queryString);

        FSDirectory dir = FSDirectory.open(index_directory);
        IndexReader reader = DirectoryReader.open(dir);
        IndexSearcher searcher = new IndexSearcher(reader);

        ScoreDoc[] scoreDocs = searcher.search(query, k).scoreDocs;


        List<Pair<Document, Float>> documents = scoreDocs_to_docsList(scoreDocs, searcher);

        System.out.println("topdocs:  " + documents.toString());

        return documents;


    }

    private static List<Pair<Document, Float>> scoreDocs_to_docsList(ScoreDoc[] scoreDocs, IndexSearcher searcher) throws IOException {

        List<Pair<Document, Float>> documents = new ArrayList<Pair<Document, Float>>();

        for(ScoreDoc scDoc: scoreDocs){

            documents.add(new Pair<Document, Float>(searcher.doc(scDoc.doc), scDoc.score));

        }
        return documents;




    }
}
