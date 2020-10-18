package project_IR;

import org.apache.lucene.document.Document;
import org.apache.lucene.queryparser.classic.ParseException;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;


public class Main {
    public static  Path index_path = Path.of("./index"); // Destination of indexed files
    public static Path source_path = Path.of("../documents2"); // Source files


    /**
     * the public function that will initialize the indexWriter and index every file that will be called from the outside
     * @param args basic arguments given to a main function (currently not used)
     */
    public static void main(String[] args) throws IOException, ParseException {
//        Indexer.index(source_path, index_path);
        List<Document> documents = Searcher.search("for loop", 20, index_path);

    }
}
