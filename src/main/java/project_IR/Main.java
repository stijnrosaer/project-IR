package project_IR;

import org.apache.lucene.document.Document;
import org.apache.lucene.queryparser.classic.ParseException;
import org.javatuples.Pair;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;


public class Main {
    public static Path index_path = Path.of("./index"); // Destination of indexed files
    public static Path source_path = Path.of("../documents2"); // Source files


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


                    } else {

                        System.out.println("tag not recognized:  " + args[i]);
                    }
                }

                Indexer.index(source_path, index_path);
            }
            if (args[0].equals("search")) {
                String querystring = "";

                for (int i = 1; i < args.length; i++) {
                    // argument parameter must be in between quotation marks if it's more than 1 word
                    if (args[i].equals("--query") || args[i].equals("-q")) {

                        querystring = args[i + 1];

                        i++;

                    } else {

                        System.out.println("tag not recognized:  " + args[i]);
                    }
                }

                if (querystring.equals("")) {

                    System.out.println("--query is not an optional argument");
                    return;
                }
                List<Pair<Document, Float>> documents = Searcher.search(querystring, 20, index_path);

                System.out.println("topdocs:  " + documents.toString());
            }
        }
    }
}
