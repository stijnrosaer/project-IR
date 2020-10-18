package project_IR;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.FSDirectory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

public class Indexer {

    public static Analyzer analyzer = new StandardAnalyzer(); // Basic analyzer needed for the indexWriter

    /**
     * the public function that will initialize the indexWriter and index every file that will be called from the outside
     * @param source_path path where source documents are located
     * @param index_path path where the index files should be written to
     * @throws IOException
     */
    public static void index(Path source_path, Path index_path) throws IOException {
        IndexWriterConfig config = new IndexWriterConfig(analyzer); // Basic Configuration for the indexWriter
        IndexWriter writer = new IndexWriter(FSDirectory.open(index_path), config); // Object that will generate the indexes of all files
        final Integer[] index = {0};
        if (Files.isDirectory(source_path)) {
            Files.walkFileTree(source_path, new SimpleFileVisitor<Path>() { //loop over all files in source_path
                @Override //override a function used while walking over files
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    try {
                        indexFile(file, writer); //index the file using the indexWriter
                        if (index[0] % 10000 == 0) {
                            System.out.println("Indexed " + index[0].toString() + " files");
                        }
                        index[0]++;
                    } catch (IOException | ParserConfigurationException | SAXException ignore) {

                    }
                    return FileVisitResult.CONTINUE;
                }
            });
        }
        writer.commit();
        writer.close();

        System.out.println("Finished indexing "+ index[0].toString() +" files");

    }

    /**
     * use the indexWriter to index the given file
     * @param file_path the path to the file to be indexed
     * @param writer the indexWriter
     * @throws IOException
     * @throws ParserConfigurationException
     * @throws SAXException
     */
    static void indexFile(Path file_path, IndexWriter writer) throws IOException, ParserConfigurationException, SAXException {

        File file = file_path.toFile();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        org.w3c.dom.Document file_doc = builder.parse(file);
        file_doc.getDocumentElement().normalize();

        String title = file_doc.getElementsByTagName("Title").item(0).getTextContent(); // get the title of the question
        String question_body = file_doc.getElementsByTagName("Body").item(0).getTextContent(); // get the body of the question
        String tags = file_doc.getElementsByTagName("Tags").item(0).getTextContent(); // get the tags of the question

        NodeList nodeList = file_doc.getDocumentElement().getChildNodes();
        List<String > answers = new ArrayList<>(); // run over all answers to the question and make them a list
        for (int i = 3; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);// get an answer
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;

                String answer_body = element.getElementsByTagName("Body").item(0).getTextContent(); // get the body of the answer
                answers.add(answer_body); // add it to the list of answers
            }

        }

        Document doc = new Document();
        FieldType Stored_Not_indexed = new FieldType();
        Stored_Not_indexed.setStored(true);
        doc.add(new Field("id", file_path.getFileName().toString(), Stored_Not_indexed)); // we should not look up on id, but maybe return te ID to help where to find the file
        doc.add(new TextField("title", title, Field.Store.YES)); // we should be able to look up on the title, but should also return it, as such, we store the field
        doc.add(new TextField("tags", tags, Field.Store.NO)); //we should be able to look up on the body field or answers, but these should not be all returned by the query
        doc.add(new TextField("question", question_body, Field.Store.NO));
        doc.add(new TextField("answers", String.join("\n", answers), Field.Store.NO));

        writer.addDocument(doc); // index the document with the writer
    }
}
