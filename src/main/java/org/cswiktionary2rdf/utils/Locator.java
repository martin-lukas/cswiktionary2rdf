package org.cswiktionary2rdf.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Iterator;

/**
 * Class containing methods for locating JSON status file for the dump online, and for locating the file name
 * in that JSON status file.
 *
 * @author Martin Lukáš
 */
public class Locator {
    private static final String WIKIMEDIA_SITE = "https://dumps.wikimedia.org";
    
    public static Archive locateArchive() throws IOException {
        String dumpPageLocation = getDumpPageUrl();
        
        JsonNode fileNode = getFileNode(WIKIMEDIA_SITE + dumpPageLocation + "/dumpstatus.json");
        if (fileNode == null || fileNode.path("url").toString().equals("")) {
            System.err.println("The newest dump is still in progress. Searching for the previous dump...");
            String previousDumpPageLocation = getPreviousDumpPageUrl(dumpPageLocation);
            fileNode = getFileNode(WIKIMEDIA_SITE + previousDumpPageLocation + "/dumpstatus.json");
        }
        
        if (fileNode == null || fileNode.path("url").toString().equals("")) {
            System.err.println("Couldn't find any dump file online.");
            return null;
        }
        
        String filename = fileNode.path("url").toString();
        filename = filename.substring(1, filename.length() - 1);
        String checksum = fileNode.path("sha1").toString();
        checksum = checksum.substring(1, checksum.length() - 1);
        return new Archive(WIKIMEDIA_SITE + filename, checksum);
    }
    
    private static String getDumpPageUrl() throws IOException {
        String indexUrl = WIKIMEDIA_SITE + "/backup-index-bydb.html";
        Document document = Jsoup.connect(indexUrl).get();
        return "/" + document.body().select("a[href*=cswiktionary]").first().attr("href");
    }
    
    private static String getPreviousDumpPageUrl(String currentDumpPage) throws IOException {
        String previousDumpPage = "";
        String indexUrl = WIKIMEDIA_SITE + currentDumpPage;
        Document document = Jsoup.connect(indexUrl).get();
        
        Element pElement = document.body().select("p.previous").first();
        if (pElement != null) {
            previousDumpPage = pElement.select("a[href]").first().attr("href");
            previousDumpPage = "/cswiktionary/"
                    + previousDumpPage.substring(3, previousDumpPage.length() - 1);
            // in format "../date"
        }
        return previousDumpPage;
    }
    
    private static JsonNode getFileNode(String jsonFileUrl) {
        URL jsonUrl = null;
        try {
            jsonUrl = new URL(jsonFileUrl);
            URLConnection request = jsonUrl.openConnection();
            request.connect();
        } catch (MalformedURLException e) {
            System.err.println("The link to the JSON file is invalid.");
            e.printStackTrace();
        } catch (IOException e) {
            System.err.println("Failed to open connection to the JSON file.");
            e.printStackTrace();
        }
        
        if (jsonUrl == null) {
            return null;
        }
        
        JsonNode dumpFileNode = null;
        try {
            JsonNode parent = new ObjectMapper().readTree(jsonUrl);
            Iterator<JsonNode> files = parent.path("jobs")
                    .path("articlesdump")
                    .path("files")
                    .elements();
            
            while (files.hasNext()) {
                JsonNode fileNode = files.next();
                String fileUrl = fileNode.path("url").toString();
                // there might be two files - archive with the dump, and an index file
                if (!fileUrl.contains("index")) {
                    dumpFileNode = fileNode;
                }
            }
        } catch (IOException e) {
            System.out.println("Couldn't find the file in the JSON file '" + jsonUrl.getFile() + "'");
            e.printStackTrace();
        }
        return dumpFileNode;
    }
}
