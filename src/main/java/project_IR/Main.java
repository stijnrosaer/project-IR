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
            // add the index command
            if (args[0].equals("index")) {

                // check for all the different flags we can give to the index command
                for (int i = 1; i < args.length; i++) {
                    // documents is used to locate the place where the documents are put (has a base value used for development)
                    if (args[i].equals("--documents") || args[i].equals("-d")) {

                        source_path = Path.of(args[i + 1]);
                        i++;

                        // similarity will ask you which similarity to use, okapi, boolean, or classic(standard)
                    } else if (args[i].equals("--similarity") || args[i].equals("-s")) {
                        switch (args[i + 1]) {
                            case "okapi":

                                sim = new BM25Similarity();


                                break;
                            case "classic":

                                sim = new ClassicSimilarity();
                                break;

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
            // the search command
            if (args[0].equals("search")) {
                //standard values for when flags are not used
                String querystring = "";
                String[] fields = new String[]{"tags", "question", "answers"};
                Integer k = 20;

                for (int i = 1; i < args.length; i++) {
                    //the query flag gives the query to ask, this is a required flag
                    // argument parameter for query flag must be in between quotation marks if it's more than 1 word
                    if (args[i].equals("--query") || args[i].equals("-q")) {

                        querystring = args[i + 1];

                        i++;

                    }
                    // the field flag will state which field(s) to look the query for
                    // choice out of title, tags ,question, answers, all but title are standard
                    else if (args[i].equals("--field") || args[i].equals("-f")){
                        List<String> fieldsList = new ArrayList<String>();

                        while (args.length > i + 1 && args[i + 1].charAt(0) != '-') {
                            fieldsList.add(args[i + 1]);
                            i++;

                        }
                        fields = new String[fieldsList.size()];
                        fields = fieldsList.toArray(fields);

                    }
                    // the amount flag tells how many documents will be returned
                    else if (args[i].equals("--amount") || args[i].equals("-a")) {

                        k = Integer.parseInt(args[i + 1]);

                        i++;
                    // the similarity flag asks what similarity to be used
                    // needs to be the same as when indexed
                    } else if (args[i].equals("--similarity") || args[i].equals("-s")) {
                        switch (args[i + 1]) {
                            case "okapi":

                                sim = new BM25Similarity();


                                break;
                            case "classic":

                                sim = new ClassicSimilarity();
                                break;

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
