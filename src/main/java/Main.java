import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.*;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.document.*;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static  Path index_path = Path.of("./index"); // Destination of indexed files
    public static Path source_path = Path.of("./documents"); // Sourse files

    public static Analyzer analyzer = new StandardAnalyzer();

    public static void main(String[] args) throws IOException {
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        IndexWriter writer = new IndexWriter(FSDirectory.open(index_path), config);

        if (Files.isDirectory(source_path)) {
            Files.walkFileTree(source_path, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    try {
                        indexFile(file, writer);
                    } catch (IOException | ParserConfigurationException |SAXException ignore) {

                    }
                    return FileVisitResult.CONTINUE;
                }
            });
        }

    }

    static void indexFile(Path file_path, IndexWriter writer) throws IOException, ParserConfigurationException, SAXException {
        File file = file_path.toFile();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        org.w3c.dom.Document file_doc = builder.parse(file);
        file_doc.getDocumentElement().normalize();

        String title = file_doc.getElementsByTagName("Title").item(0).getTextContent();
        String question_body = file_doc.getElementsByTagName("Body").item(0).getTextContent();
        String tags = file_doc.getElementsByTagName("Tags").item(0).getTextContent();

        NodeList nodeList = file_doc.getDocumentElement().getChildNodes();
        List<String > answers = new ArrayList<>();
        for (int i = 3; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;

                String answer_body = element.getElementsByTagName("Body").item(0).getTextContent();
                answers.add(answer_body);
            }

        }

        Document doc = new Document();
        FieldType Stored_Not_indexed = new FieldType();
        Stored_Not_indexed.setStored(true);
        doc.add(new Field("id", file_path.getFileName().toString(), Stored_Not_indexed));
        doc.add(new TextField("title", title, Field.Store.YES));
        doc.add(new TextField("tags", tags, Field.Store.NO));
        doc.add(new TextField("question", question_body, Field.Store.NO));
        doc.add(new TextField("answers", String.join("\n", answers), Field.Store.NO));

        writer.addDocument(doc);
    }
}
