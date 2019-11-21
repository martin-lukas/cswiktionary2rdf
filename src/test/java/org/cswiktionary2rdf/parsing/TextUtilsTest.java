package org.cswiktionary2rdf.parsing;

import org.cswiktionary2rdf.utils.FileUtils;
import org.cswiktionary2rdf.utils.TextUtils;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.*;

public class TextUtilsTest {
    @Test
    public void declensionsTest() throws IOException {
        String declensionText = FileUtils.readFile(FileUtils.TEST_RESOURCES_PATH + "parsing/declensions.txt");
        declensionText = declensionText.replaceAll("&lt;", "<");
        declensionText = declensionText.replaceAll("&gt;", ">");
        
        List<String> sGenMa = TextUtils.parseEntry(declensionText, "sgenma");
        assertEquals(1, sGenMa.size());
        assertEquals("mladého", sGenMa.get(0));
        List<String> pInsF = TextUtils.parseEntry(declensionText, "pinsf");
        assertEquals(1, pInsF.size());
        assertEquals("mladými", pInsF.get(0));
        List<String> sAccFSF = TextUtils.parseEntry(declensionText, "saccf-jm");
        assertEquals(1, sAccFSF.size());
        assertEquals("mládu", sAccFSF.get(0));
        
        List<String> sLoc = TextUtils.parseEntry(declensionText, "sloc");
        assertEquals(2, sLoc.size());
        assertEquals("psovi", sLoc.get(0));
        assertEquals("psu", sLoc.get(1));
        List<String> pDat = TextUtils.parseEntry(declensionText, "pdat");
        assertEquals(1, pDat.size());
        assertEquals("psům", pDat.get(0));
        List<String> pIns = TextUtils.parseEntry(declensionText, "pins");
        assertEquals(1, pIns.size());
        assertEquals("psy", pIns.get(0));
        
        List<String> nom = TextUtils.parseEntry(declensionText, "nom");
        assertEquals(1, nom.size());
        List<String> gen = TextUtils.parseEntry(declensionText, "gen");
        assertEquals(2, gen.size());
        assertEquals("mě", gen.get(0));
        assertEquals("mne", gen.get(1));
        List<String> voc = TextUtils.parseEntry(declensionText, "voc");
        assertEquals(1, voc.size());
        assertEquals("já", voc.get(0));
    }
    
    @Test
    public void conjugationsTest() throws IOException {
        String conjugationText = FileUtils.readFile(FileUtils.TEST_RESOURCES_PATH + "parsing/conjugations.txt");
        conjugationText = conjugationText.replaceAll("&lt;", "<");
        conjugationText = conjugationText.replaceAll("&gt;", ">");
        
        List<String> spre3 = TextUtils.parseEntry(conjugationText, "spre3");
        assertEquals(1, spre3.size());
        assertEquals("jí", spre3.get(0));
        List<String> pimp2 = TextUtils.parseEntry(conjugationText, "pimp2");
        assertEquals(1, pimp2.size());
        assertEquals("jezte", pimp2.get(0));
        List<String> mtraf = TextUtils.parseEntry(conjugationText, "mtraf");
        assertEquals(0, mtraf.size());
        List<String> mtra = TextUtils.parseEntry(conjugationText, "mtra");
        assertEquals(0, mtraf.size());
    }
    
    @Test
    public void getMatchTest() {
        String text = "sometext here\n another text";
        String match = TextUtils.getMatch(text, "\\w+ text");
        assertEquals("another text", match);
        
        String text2 = "";
        String match2 = TextUtils.getMatch(text2, "\\w+");
        assertNull(match2);
    }
    
    @Test
    public void getMatchesTest() {
        String text = "sometext here\n another text\n third text";
        List<String> matches = TextUtils.getMatches(text, "\\w+ text");
        assertEquals(2, matches.size());
        assertEquals("another text", matches.get(0));
        assertEquals("third text", matches.get(1));
        String text2 = "";
        List<String> matches2 = TextUtils.getMatches(text2, "\\w+");
        assertEquals(0, matches2.size());
    }
    
    @Test
    public void getPropertiesTest() throws IOException {
        String propertiesText = FileUtils.readFile(FileUtils.TEST_RESOURCES_PATH + "parsing/properties.txt");
        List<String> properties = TextUtils.getProperties(propertiesText);
        assertEquals(2, properties.size());
        assertEquals("is a property", properties.get(0));
        assertEquals("another property", properties.get(1));
    }
    
    @Test
    public void connectStringTest() {
        assertEquals("string_gap", TextUtils.connectString("string gap"));
        assertEquals("string_gap", TextUtils.connectString("string_gap"));
        assertEquals("first_second", TextUtils.connectStrings("first", "second"));
        assertEquals("first_second_third", TextUtils.connectStrings("first", "second", "third"));
        assertEquals("first_second_third", TextUtils.connectStrings("first_second", "third"));
        assertEquals("přes střechy;_řeřichy", TextUtils.connectStrings("přes střechy;", "řeřichy"));
    }
    
    @Test
    public void splitSectionsTest() {
        String sectionText = "== čeština ==\n" +
                "něco českého\n" +
                "=== dělení ===\n" +
                "roz-dě-le-né\n" +
                "== slovenština ==\n" +
                "něco slovenského";
        String[] sections = TextUtils.splitSections(sectionText, 2);
        assertEquals(2, sections.length);
        String firstSectionExpected = "== čeština ==\nněco českého\n=== dělení ===\nroz-dě-le-né\n";
        assertEquals(firstSectionExpected, sections[0]);
        assertEquals("== slovenština ==\nněco slovenského", sections[1]);
        
        String[] sections2 = TextUtils.splitSections(sectionText, 4);
        assertEquals(1, sections2.length); // couldn't split it on 4th level - only the original text
        
        String lowerSectionsText = "=== podstatné jméno ===\n" +
                "něco\n" +
                "=== sloveso ===\n" +
                "něco jiného";
        String[] posSections = TextUtils.splitSections(lowerSectionsText, 3);
        assertEquals(2, posSections.length);
    }
    
    @Test
    public void getSectionTitle() {
        String section = "=== podstatné jméno (1) ===\n" +
                "něco\n" +
                "==== skloňování ====\n" +
                "něco";
        String title = TextUtils.getSectionTitle(section, 3);
        assertEquals("podstatné jméno", title);
        String title2 = TextUtils.getSectionTitle(section, 4);
        assertEquals("skloňování", title2);
    }
    
    @Test
    public void parseRawEntryTest() throws IOException {
        // testing conditional forms of word 'být'
        String rawTableText = FileUtils.readFile(FileUtils.TEST_RESOURCES_PATH + "parsing/raw-table.txt");
        rawTableText = rawTableText.replaceAll("&lt;", "<");
        rawTableText = rawTableText.replaceAll("&gt;", ">");
        List<List<String>> cells = TextUtils.parseRawEntry(rawTableText);
        assertEquals(6, cells.size());
        
        List<String> cell1 = cells.get(0);
        assertEquals(1, cell1.size());
        assertEquals("bych", cell1.get(0));
        
        List<String> cell2 = cells.get(1);
        assertEquals(1, cell2.size());
        assertEquals("bychom", cell2.get(0));
        
        List<String> cell4 = cells.get(3);
        assertEquals(1, cell4.size());
        assertEquals("byste", cell4.get(0));
        
        List<String> cell6 = cells.get(5);
        assertEquals(1, cell6.size());
        assertEquals("by", cell6.get(0));
    }
}