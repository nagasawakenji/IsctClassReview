package NagasawaKenji.IsctClassReview;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;


public class DomPrinterTest {
    public static void main(String[] args) throws Exception {
        Document doc = Jsoup.connect("https://syllabus.s.isct.ac.jp")
                .timeout(3000)
                .get();

        printTree(doc, 0);
    }

    private static void printTree(Node node, int depth) {
        String indent = "  ".repeat(depth);
        System.out.println(indent + "- " + node.nodeName());
        if (node instanceof org.jsoup.nodes.TextNode) {
            String text = ((org.jsoup.nodes.TextNode) node).text().trim();
            if (!text.isEmpty()) {
                 text = text.replaceAll("\\s+", " ");
            }
            System.out.println(indent + "    \"" + text + "\"");
            }

        for (Node child : node.childNodes()) {
            printTree(child, depth + 1);
        }
    }
}
