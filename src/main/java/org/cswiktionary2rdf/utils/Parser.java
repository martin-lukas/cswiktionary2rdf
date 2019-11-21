package org.cswiktionary2rdf.utils;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parser {
    /**
     * Parses the provided dump file line by line using SAX parser. Gradually builds up
     * list of pages containing all the useful information about each wiki page. The parser
     * ignores pages not concerning lexemes, as well as pages not containing the section about
     * the czech language.
     *
     * @return {@code Map} containing pairs of page titles and page texts
     */
    public static List<Page> parsePages(File file)
            throws ParserConfigurationException, SAXException, IOException {
        DumpHandler dumpHandler = new DumpHandler();
        SAXParser saxParser = getSAXParser();
        saxParser.parse(file, dumpHandler);
        
        return dumpHandler.pages;
    }
    
    private static SAXParser getSAXParser() throws ParserConfigurationException, SAXException {
        return SAXParserFactory.newInstance().newSAXParser();
    }
    
    private static class DumpHandler extends DefaultHandler {
        private boolean bTitle = false;
        private boolean bNamespace = false;
        private boolean bText = false;
        private boolean isNSAllowed = false;
    
        private List<Page> pages = new ArrayList<>();
        private String pageTitle = "";
        private StringBuilder pageText = new StringBuilder();
        
        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) {
            switch (qName) {
                case "title":
                    bTitle = true;
                    break;
                case "ns":
                    bNamespace = true;
                    break;
                case "text":
                    bText = true;
                    break;
            }
        }
        
        @Override
        public void characters(char[] ch, int start, int length) {
            String parsedString = new String(ch, start, length);
            if (bTitle) {
                pageTitle = parsedString;
                bTitle = false;
            }
            
            if (bNamespace) {
                // Value of <ns> tag - 0 for normal word page
                isNSAllowed = parsedString.equals("0");
                bNamespace = false;
            }
            
            if (bText) {
                if (isNSAllowed) {
                    pageText.append(parsedString);
                }
            }
        }
        
        @Override
        public void endElement(String uri, String localName, String qName) {
            if (qName.equals("text")) {
                bText = false;
            } else if (qName.equals("page")) {
                if (isNSAllowed) {
                    String czechExtSection = getCzechAndExtSection(pageText.toString());
                    if (czechExtSection.contains("== čeština ==")) {
                        // to make it easier later for extracting the actual information
                        pages.add(new Page(pageTitle, TextUtils.removeLinks(czechExtSection)));
                    }
                }
                
                resetValues();
            }
        }
        
        private void resetValues() {
            isNSAllowed = false;
            pageTitle = "";
            pageText.setLength(0);
        }
        
        private String getCzechAndExtSection(String pageText) {
            String czechSection = "";
            String extSection = "";
            
            Pattern czechHeadingPattern = Pattern.compile("== čeština ==");
            Pattern extHeadingPattern = Pattern.compile("== externí odkazy ==");
    
            String[] sections = TextUtils.splitSections(pageText, 2);
            for (String section : sections) {
                Matcher czechM = czechHeadingPattern.matcher(section);
                if (czechM.find()) {
                    czechSection = section;
                    break;
                }
            }
            
            for (String section : sections) {
                Matcher extM = extHeadingPattern.matcher(section);
                if (extM.find()) {
                    extSection = section;
                    break;
                }
            }
            
            return czechSection + extSection;
        }
    }
}
