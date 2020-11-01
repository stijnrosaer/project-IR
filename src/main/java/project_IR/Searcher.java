package project_IR;

import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.es.SpanishAnalyzer;
import org.apache.lucene.analysis.nl.DutchAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.*;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.store.FSDirectory;
import org.javatuples.Pair;


import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.apache.lucene.queryparser.flexible.standard.QueryParserUtil.escape;

public class Searcher {
    /**
     * @param queryString     the query in string form
     * @param k               the max amount that are allowed to be returned
     * @param index_directory the directory with the indexfiles
     * @return a list of pairs containing the top k  documents and their scores
     */
    public static Pair<List<Pair<Document, Float>>, Long> search(String queryString, Integer k, Path index_directory, String[] fields, Similarity similarity) throws ParseException, IOException {

//        Query query = new WildcardQuery(new Term("title", queryString));
        MultiFieldQueryParser parser = new MultiFieldQueryParser(fields, new EnglishAnalyzer());
        Query query = parser.parse(escape(queryString));

        FSDirectory dir = FSDirectory.open(index_directory);
        IndexReader reader = DirectoryReader.open(dir);
        IndexSearcher searcher = new IndexSearcher(reader);
        searcher.setSimilarity(similarity);

        TopDocs docs = searcher.search(query, k);
        ScoreDoc[] scoreDocs = docs.scoreDocs;


        return new Pair<>(scoreDocs_to_docsList(scoreDocs, searcher), docs.totalHits.value);
//        return null;

    }

    /**
     * @param scoreDocs the list of scoredocs
     * @param searcher  the indexSearcher
     * @return a List of Pairs of Documents and teir score
     */
    private static List<Pair<Document, Float>> scoreDocs_to_docsList(ScoreDoc[] scoreDocs, IndexSearcher searcher) throws IOException {

        List<Pair<Document, Float>> documents = new ArrayList<Pair<Document, Float>>();

        for (ScoreDoc scDoc : scoreDocs) {

            documents.add(new Pair<>(searcher.doc(scDoc.doc), scDoc.score));

        }
        return documents;


    }
}
