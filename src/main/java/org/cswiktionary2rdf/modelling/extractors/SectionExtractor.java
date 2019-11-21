package org.cswiktionary2rdf.modelling.extractors;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.cswiktionary2rdf.utils.Page;
import org.cswiktionary2rdf.modelling.modellers.EntryTypeModeller;
import org.cswiktionary2rdf.modelling.modellers.BaseReferenceModeller;

public interface SectionExtractor {
    void extract(Model model, Resource resource, Page page);
    
    public static void addEntryProps(Model model, Resource pageResource, Resource sectionResource) {
        new EntryTypeModeller().model(model, sectionResource);
        new BaseReferenceModeller().model(model, pageResource, sectionResource);
    }
}
