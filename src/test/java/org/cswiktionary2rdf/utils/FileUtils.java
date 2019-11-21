package org.cswiktionary2rdf.utils;

import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class FileUtils {
    
    public static final String TEST_RESOURCES_PATH = "./src/test/resources/";
    
    public static List<Page> getPagesFromFile(String filePath) {
        List<Page> pages = new ArrayList<>();
        try {
            pages = Parser.parsePages(new File(filePath));
        } catch (IOException ex) {
            System.err.println("Couldn't parse file " + filePath);
            ex.printStackTrace();
        } catch (SAXException ex) {
            System.err.println("There is something wrong with the provided dump file.");
            ex.printStackTrace();
        } catch (ParserConfigurationException ex) {
            System.err.println("There was a problem in the parser configuration");
            ex.printStackTrace();
        }
        return pages;
    }
    
    public static void writeFile(String path, String content) throws IOException {
        Files.writeString(Paths.get(path), content, StandardCharsets.UTF_8);
    }
    
    public static String readFile(String path) throws IOException {
        String content = Files.readString(Paths.get(path));
        String[] lines = content.split("\r\n");
        return String.join("\n", lines);
    }
}
