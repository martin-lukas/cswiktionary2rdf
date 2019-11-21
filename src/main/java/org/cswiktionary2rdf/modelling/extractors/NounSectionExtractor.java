package org.cswiktionary2rdf.modelling.extractors;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.cswiktionary2rdf.modelling.misc.GenderParser;
import org.cswiktionary2rdf.modelling.misc.ModelUtils;
import org.cswiktionary2rdf.utils.Page;
import org.cswiktionary2rdf.modelling.modellers.*;
import org.cswiktionary2rdf.modelling.subextractors.NormalDeclensionExtractor;
import org.cswiktionary2rdf.modelling.objects.GenderObject;
import org.cswiktionary2rdf.modelling.objects.POSObject;
import org.cswiktionary2rdf.modelling.mappings.PartOfSpeech;
import org.cswiktionary2rdf.utils.TextUtils;

import java.util.List;

public class NounSectionExtractor implements SectionExtractor {
    
    @Override
    public void extract(Model model, Resource pageResource, Page page) {
        String suffix = TextUtils.connectString(PartOfSpeech.NOUN.getLocalMapping().toLowerCase());
        Resource resource = ModelUtils.createNewResourceWithLabel(model, page.getTitle(), suffix, page.getTitle());
        
        SectionExtractor.addEntryProps(model, pageResource, resource);
        
        new PartOfSpeechModeller().model(model, resource, PartOfSpeech.NOUN);
        
        GenderModeller genderModeller = new GenderModeller();
        
        List<GenderObject> allGenders = GenderParser.parseGenders(page.getText());
        for (GenderObject genderObject : allGenders){
            genderModeller.model(model, resource, genderObject);
        }
        GenderObject mainGenderObject = (allGenders.size() > 0) ? allGenders.get(0) : null;
        
        NormalDeclensionExtractor normalDeclensionExtractor = new NormalDeclensionExtractor();
        
        for (String section : TextUtils.splitSections(page.getText(), 4)) {
            String sectionTitle = TextUtils.getSectionTitle(section, 4);
            if (sectionTitle.equals(TextUtils.DECLENSION_TITLE)) {
                POSObject nounObject = new POSObject(page, section, PartOfSpeech.NOUN);
                /*
                    If there are more declensions for one noun, and these have different genders (e.g. sršeň),
                    this should match the correct gender to each section. If there are more declension
                    sections, but a common gender (e.g. Martina), it should work correctly too.
                 */
                List<GenderObject> sectionGenders = GenderParser.parseGenders(section);
                GenderObject declensionSpecificGender = (sectionGenders.size() > 0)
                        ? sectionGenders.get(0)
                        : mainGenderObject;
                
                normalDeclensionExtractor.extract(model, resource, nounObject, declensionSpecificGender);
            }
        }
    }
}
