package org.cswiktionary2rdf.modelling.extractors;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.cswiktionary2rdf.modelling.misc.ModelUtils;
import org.cswiktionary2rdf.utils.Page;
import org.cswiktionary2rdf.modelling.modellers.*;
import org.cswiktionary2rdf.modelling.subextractors.DegreesExtractor;
import org.cswiktionary2rdf.modelling.subextractors.ExtendedDeclensionExtractor;
import org.cswiktionary2rdf.modelling.objects.POSObject;
import org.cswiktionary2rdf.modelling.mappings.PartOfSpeech;
import org.cswiktionary2rdf.utils.TextUtils;

public class AdjectiveSectionExtractor implements SectionExtractor {
    
    @Override
    public void extract(Model model, Resource pageResource, Page page) {
        String suffix = TextUtils.connectString(PartOfSpeech.ADJECTIVE.getLocalMapping().toLowerCase());
        Resource resource = ModelUtils.createNewResourceWithLabel(
                model, page.getTitle(), suffix, page.getTitle());
        
        SectionExtractor.addEntryProps(model, pageResource, resource);
        
        new PartOfSpeechModeller().model(model, resource, PartOfSpeech.ADJECTIVE);
        
        for (String section : TextUtils.splitSections(page.getText(), 4)) {
            POSObject adjectiveObject = new POSObject(page, section, PartOfSpeech.ADJECTIVE);
            String sectionTitle = TextUtils.getSectionTitle(section, 4);
            
            if (sectionTitle.equals(TextUtils.DECLENSION_TITLE)) {
                new ExtendedDeclensionExtractor().extract(model, resource, adjectiveObject, true);
                
            } else if (sectionTitle.equals(TextUtils.DEGREE_TITLE)) {
                new DegreesExtractor().extract(model, resource, adjectiveObject);
            }
        }
    }
}
