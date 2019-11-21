package org.cswiktionary2rdf.modelling.extractors;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.cswiktionary2rdf.modelling.misc.GenderParser;
import org.cswiktionary2rdf.modelling.misc.ModelUtils;
import org.cswiktionary2rdf.modelling.misc.NumberParser;
import org.cswiktionary2rdf.modelling.modellers.*;
import org.cswiktionary2rdf.modelling.subextractors.ExtendedDeclensionExtractor;
import org.cswiktionary2rdf.modelling.subextractors.TinyDeclensionExtractor;
import org.cswiktionary2rdf.modelling.objects.GenderObject;
import org.cswiktionary2rdf.modelling.objects.POSObject;
import org.cswiktionary2rdf.modelling.mappings.Number;
import org.cswiktionary2rdf.modelling.mappings.PartOfSpeech;
import org.cswiktionary2rdf.utils.Page;
import org.cswiktionary2rdf.utils.TextUtils;

import java.util.List;

public class PronounSectionExtractor implements SectionExtractor {
    
    @Override
    public void extract(Model model, Resource pageResource, Page page) {
        String suffix = TextUtils.connectString(PartOfSpeech.PRONOUN.getLocalMapping().toLowerCase());
        Resource resource = ModelUtils.createNewResourceWithLabel(model, page.getTitle(), suffix, page.getTitle());
    
        SectionExtractor.addEntryProps(model, pageResource, resource);
    
        new PartOfSpeechModeller().model(model, resource, PartOfSpeech.PRONOUN);
    
        GenderModeller genderModeller = new GenderModeller();
        List<GenderObject> genders = GenderParser.parseGenders(page.getText());
        for (GenderObject gender : genders) {
            genderModeller.model(model, resource, gender);
        }
        
        ExtendedDeclensionExtractor extendedDeclensionExtractor = new ExtendedDeclensionExtractor();
        TinyDeclensionExtractor tinyDeclensionExtractor = new TinyDeclensionExtractor();
        
        for (String section : TextUtils.splitSections(page.getText(), 4)) {
            String sectionTitle = TextUtils.getSectionTitle(section, 4);
            if (sectionTitle.equals(TextUtils.DECLENSION_TITLE)) {
                POSObject pronounObject = new POSObject(page, section, PartOfSpeech.PRONOUN);
                // some pronouns have the same declension structure as hard adjectives.
                extendedDeclensionExtractor.extract(model, resource, pronounObject, false);
                // when two tables for singular and plural separately
                String[] declensionSections = section.split("(?=\\*\\s*'')");
                for (String declensionSection : declensionSections) {
                    POSObject partialPronounObject = new POSObject(page, declensionSection, PartOfSpeech.PRONOUN);
                    List<Number> partialSectionNumbers = NumberParser.parseNumbers(partialPronounObject.getText());
                    Number number = (partialSectionNumbers.size() > 0) ? partialSectionNumbers.get(0) : null;
                    
                    tinyDeclensionExtractor.extract(model, resource, partialPronounObject, number);
                }
            }
        }
    }
}
