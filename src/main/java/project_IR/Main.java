package project_IR;

import org.apache.lucene.document.Document;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.search.similarities.BooleanSimilarity;
import org.apache.lucene.search.similarities.ClassicSimilarity;
import org.apache.lucene.search.similarities.Similarity;
import org.javatuples.Pair;

import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;


public class Main {
    public static Path index_path = Path.of("./index"); // Destination of indexed files
    public static Path source_path = Path.of("../documents2"); // Source files
    public static Similarity sim = new ClassicSimilarity();


    /**
     * the public function that will initialize the indexWriter and index every file that will be called from the outside
     *
     * @param args basic arguments given to a main function (currently not used)
     */
    public static void main(String[] args) throws IOException, ParseException {
        if (args.length > 0) {
            if (args[0].equals("index")) {

                for (int i = 1; i < args.length; i++) {
                    if (args[i].equals("--documents") || args[i].equals("-d")) {

                        source_path = Path.of(args[i + 1]);
                        i++;


                    } else if (args[i].equals("--similarity") || args[i].equals("-s")) {
                        switch (args[i + 1]) {
                            case "okapi":

                                sim = new BM25Similarity();


                                break;
                            case "classic":

                                i++;
                                continue;

                            case "boolean":

                                sim = new BooleanSimilarity();
                                break;
                        }
                        i++;


                    } else {

                        System.out.println("tag not recognized:  " + args[i]);
                    }
                }

                Indexer.index(source_path, index_path, sim);
            }
            if (args[0].equals("search")) {
                String querystring = "";
                String[] fields = new String[]{"tags", "question", "answers"};
                Integer k = 20;

                for (int i = 1; i < args.length; i++) {
                    // argument parameter must be in between quotation marks if it's more than 1 word
                    if (args[i].equals("--query") || args[i].equals("-q")) {

                        querystring = args[i + 1];

                        i++;

                    } else if (args[i].equals("--field") || args[i].equals("-f")) {
                        List<String> fieldsList = new ArrayList<String>();

                        while (args.length > i + 1 && args[i + 1].charAt(0) != '-') {
                            fieldsList.add(args[i + 1]);
                            i++;

                        }
                        fields = new String[fieldsList.size()];
                        fields = fieldsList.toArray(fields);

                    } else if (args[i].equals("--amount") || args[i].equals("-a")) {

                        k = Integer.parseInt(args[i + 1]);

                        i++;

                    } else if (args[i].equals("--similarity") || args[i].equals("-s")) {
                        switch (args[i + 1]) {
                            case "okapi":

                                sim = new BM25Similarity();


                                break;
                            case "classic":

                                i++;
                                continue;

                            case "boolean":

                                sim = new BooleanSimilarity();
                                break;
                        }
                        i++;


                    } else {

                        System.out.println("tag not recognized:  " + args[i]);
                    }
                }

                if (querystring.equals("")) {

                    System.out.println("--query is not an optional argument");
                    return;
                }
                List<Pair<Document, Float>> documents = Searcher.search(querystring, k, index_path, fields, sim);

                for (Pair<Document, Float> doc : documents) {

                    System.out.println("title: " + doc.getValue0().get("title") + "\nfile:" + doc.getValue0().get("id")
                            + "        score:" + doc.getValue1() + "\n");

                }


            }
        }
    }
}
