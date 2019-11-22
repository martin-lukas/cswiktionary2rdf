package org.cswiktionary2rdf.parsing;

import org.cswiktionary2rdf.utils.FileUtils;
import org.cswiktionary2rdf.utils.Page;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class PageParsingTest {
    @Test
    public void parseBarePageTest() {
        List<Page> pages = FileUtils.getPagesFromFile(FileUtils.TEST_RESOURCES_PATH + "parsing/bare-page.xml");
        assertEquals(1, pages.size());
        for (Page page : pages) {
            assertEquals("bare page", page.getTitle());
            assertEquals("== čeština ==\ntext", page.getText());
        }
    }
    
    @Test
    public void parsePageWithContextTest() {
        List<Page> pages = FileUtils.getPagesFromFile(FileUtils.TEST_RESOURCES_PATH + "parsing/page-with-context.xml");
        assertEquals(1, pages.size());
        for (Page page : pages) {
            assertEquals("bare page", page.getTitle());
            assertEquals("== čeština ==\ntext", page.getText());
        }
    }
    
    @Test
    public void parseCompletePageTest() {
        List<Page> pages = FileUtils.getPagesFromFile(FileUtils.TEST_RESOURCES_PATH + "parsing/page-pes-complete.xml");
        assertEquals(1, pages.size());
        for (Page page : pages) {
            assertEquals("pes", page.getTitle());
            
            String pageText = page.getText();
            assertEquals(5851, pageText.length());
            assertFalse(pageText.contains("== latina =="));
            assertTrue(pageText.contains("== externí odkazy =="));
        }
    }
}
