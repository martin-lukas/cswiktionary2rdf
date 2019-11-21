package org.cswiktionary2rdf.modelling.extractors;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.cswiktionary2rdf.modelling.misc.ModelUtils;
import org.cswiktionary2rdf.modelling.modellers.*;
import org.cswiktionary2rdf.modelling.subextractors.VerbConditionalConjugationExtractor;
import org.cswiktionary2rdf.modelling.subextractors.VerbMoodConjugationExtractor;
import org.cswiktionary2rdf.modelling.subextractors.VerbParticipleConjugationExtractor;
import org.cswiktionary2rdf.modelling.subextractors.VerbTransgressiveConjugationExtractor;
import org.cswiktionary2rdf.modelling.objects.POSObject;
import org.cswiktionary2rdf.modelling.mappings.PartOfSpeech;
import org.cswiktionary2rdf.modelling.mappings.Mood;
import org.cswiktionary2rdf.utils.Page;
import org.cswiktionary2rdf.utils.TextUtils;

public class VerbSectionExtractor implements SectionExtractor {
    @Override
    public void extract(Model model, Resource pageResource, Page page) {
        String suffix = TextUtils.connectString(PartOfSpeech.VERB.getLocalMapping().toLowerCase());
        Resource resource = ModelUtils.createNewResourceWithLabel(model, page.getTitle(), suffix, page.getTitle());
    
        SectionExtractor.addEntryProps(model, pageResource, resource);
    
        new PartOfSpeechModeller().model(model, resource, PartOfSpeech.VERB);

        for (String section : TextUtils.splitSections(page.getText(), 4)) {
            String sectionTitle = TextUtils.getSectionTitle(section, 4);
            if (sectionTitle.equals(TextUtils.CONJUGATION_TITLE)) {
                new VerbMoodModeller().model(model, resource, Mood.INFINITIVE);
                
                POSObject verbObject = new POSObject(page, section, PartOfSpeech.VERB);
                
                new VerbMoodConjugationExtractor().extract(model, resource, verbObject);
                new VerbParticipleConjugationExtractor().extract(model, resource, verbObject);
                new VerbTransgressiveConjugationExtractor().extract(model, resource, verbObject);
                new VerbConditionalConjugationExtractor().extract(model, resource, verbObject);
            }
        }
    }
}
