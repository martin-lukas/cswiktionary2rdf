package org.cswiktionary2rdf.modelling.extractors;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.cswiktionary2rdf.modelling.misc.GenderParser;
import org.cswiktionary2rdf.modelling.misc.ModelUtils;
import org.cswiktionary2rdf.modelling.modellers.*;
import org.cswiktionary2rdf.modelling.subextractors.ExtendedDeclensionExtractor;
import org.cswiktionary2rdf.modelling.subextractors.NormalDeclensionExtractor;
import org.cswiktionary2rdf.modelling.subextractors.TinyDeclensionExtractor;
import org.cswiktionary2rdf.modelling.objects.GenderObject;
import org.cswiktionary2rdf.modelling.objects.POSObject;
import org.cswiktionary2rdf.modelling.mappings.PartOfSpeech;
import org.cswiktionary2rdf.utils.Page;
import org.cswiktionary2rdf.utils.TextUtils;

import java.util.List;

public class NumeralSectionExtractor implements SectionExtractor {
    @Override
    public void extract(Model model, Resource pageResource, Page page) {
        String suffix = TextUtils.connectString(PartOfSpeech.NUMERAL.getLocalMapping().toLowerCase());
        Resource resource = ModelUtils.createNewResourceWithLabel(model, page.getTitle(), suffix, page.getTitle());
    
        SectionExtractor.addEntryProps(model, pageResource, resource);
        
        new PartOfSpeechModeller().model(model, resource, PartOfSpeech.NUMERAL);
        
        GenderModeller genderModeller = new GenderModeller();
        List<GenderObject> genders = GenderParser.parseGenders(page.getText());
        for (GenderObject gender : genders) {
            genderModeller.model(model, resource, gender);
        }
    
        ExtendedDeclensionExtractor extendedDeclensionExtractor = new ExtendedDeclensionExtractor();
        NormalDeclensionExtractor normalDeclensionExtractor = new NormalDeclensionExtractor();
        TinyDeclensionExtractor tinyDeclensionExtractor = new TinyDeclensionExtractor();
    
        for (String section : TextUtils.splitSections(page.getText(), 4)) {
            String sectionTitle = TextUtils.getSectionTitle(section, 4);
            if (sectionTitle.equals(TextUtils.DECLENSION_TITLE)) {
                POSObject numeralObject = new POSObject(page, section, PartOfSpeech.NUMERAL);
                // some numerals have the same declension structure as hard adjectives.
                extendedDeclensionExtractor.extract(model, resource, numeralObject, true);
                // when a numeral has similar structure as noun
                normalDeclensionExtractor.extract(model, resource, numeralObject, null);
                // when a numeral has similar structure like some pronouns - one col table
                tinyDeclensionExtractor.extract(model, resource, numeralObject, null);
            }
        }
    }
}
