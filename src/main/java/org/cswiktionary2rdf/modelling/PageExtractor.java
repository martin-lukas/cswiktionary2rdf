package org.cswiktionary2rdf.modelling;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.cswiktionary2rdf.modelling.extractors.*;
import org.cswiktionary2rdf.modelling.misc.ModelUtils;
import org.cswiktionary2rdf.modelling.modellers.*;
import org.cswiktionary2rdf.modelling.mappings.PartOfSpeech;
import org.cswiktionary2rdf.utils.Page;
import org.cswiktionary2rdf.utils.TextUtils;

import java.util.*;

public class PageExtractor {
    private static final Map<String, SectionExtractor> sectionExtractors = getSectionExtractors();
    
    private boolean hasPOSSection = false;
    
    public void extract(Model model, Page page) {
        Resource resource = ModelUtils.createNewResourceWithLabel(model, page.getTitle(), null, page.getTitle());
        new PageTypeModeller().model(model, resource);
        
        // also sets hasPOSSection flag if it registers a POS section
        extractPageSections(model, resource, page);
        
        // if page doesn't have a single recognized POS section, remove page resources from RDF model
        if (!hasPOSSection) {
            model.removeAll(resource, null, null);
        }
    }
    
    
    private void extractPageSections(Model model, Resource resource, Page page) {
        // extract once per page
        new ExternalSectionExtractor().extract(model, resource, page);
        
        // use extractors possibly several times
        for (String section : TextUtils.splitSections(page.getText(), 3)) {
            String thirdLevelTitle = TextUtils.getSectionTitle(section, 3);
            
            if (PartOfSpeech.getEnum(thirdLevelTitle) != null) {
                hasPOSSection = true;
            }
            
            SectionExtractor chosenSectionExtractor = sectionExtractors.get(thirdLevelTitle);
            if (chosenSectionExtractor != null) {
                chosenSectionExtractor.extract(model, resource, new Page(page.getTitle(), section));
            }
        }
    }
    
    private static Map<String, SectionExtractor> getSectionExtractors() {
        Map<String, SectionExtractor> extractorMap = new HashMap<>();
        
        // POS sections
        extractorMap.put("podstatné jméno", new NounSectionExtractor());
        extractorMap.put("přídavné jméno", new AdjectiveSectionExtractor());
        extractorMap.put("zájmeno", new PronounSectionExtractor());
        extractorMap.put("číslovka", new NumeralSectionExtractor());
        extractorMap.put("sloveso", new VerbSectionExtractor());
        extractorMap.put("příslovce", new AdverbSectionExtractor());
        extractorMap.put("předložka", new PrepositionSectionExtractor());
        extractorMap.put("spojka", new ConjunctionSectionExtractor());
        extractorMap.put("částice", new ParticleSectionExtractor());
        extractorMap.put("citoslovce", new InterjectionSectionExtractor());
        
        // other - you can add more 3rd level extractors here (=== XXX ===)
        extractorMap.put("výslovnost", new PronunciationSectionExtractor());
        
        return extractorMap;
    }
}
