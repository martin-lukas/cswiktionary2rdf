package org.cswiktionary2rdf.modelling.extractors;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.cswiktionary2rdf.modelling.misc.ModelUtils;
import org.cswiktionary2rdf.modelling.mappings.PartOfSpeech;
import org.cswiktionary2rdf.modelling.modellers.PartOfSpeechModeller;
import org.cswiktionary2rdf.utils.Page;
import org.cswiktionary2rdf.utils.TextUtils;

public class InterjectionSectionExtractor implements SectionExtractor {
    @Override
    public void extract(Model model, Resource pageResource, Page page) {
        String suffix = TextUtils.connectString(PartOfSpeech.INTERJECTION.getLocalMapping().toLowerCase());
        Resource resource = ModelUtils.createNewResourceWithLabel(model, page.getTitle(), suffix, page.getTitle());
    
        SectionExtractor.addEntryProps(model, pageResource, resource);
        
        new PartOfSpeechModeller().model(model, resource, PartOfSpeech.INTERJECTION);
    }
}
