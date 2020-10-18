package project_IR;

import java.io.IOException;
import java.nio.file.*;


public class Main {
    public static  Path index_path = Path.of("./index"); // Destination of indexed files
    public static Path source_path = Path.of("C:/Users/stijn/Documents/Personal Projects/xml-splitter/splits2"); // Source files


    /**
     * the public function that will initialize the indexWriter and index every file that will be called from the outside
     * @param args basic arguments given to a main function (currently not used)
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        Indexer.index(source_path, index_path);
    }
}
