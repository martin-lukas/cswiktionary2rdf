package org.cswiktionary2rdf.modelling.modellers;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.cswiktionary2rdf.modelling.mappings.PartOfSpeech;

public class PartOfSpeechModeller {
    
    public void model(Model model, Resource resource, PartOfSpeech partOfSpeech) {
        model.add(
                resource,
                model.createProperty(PartOfSpeech.PROPERTY),
                model.createResource(partOfSpeech.getMapping()));
    }
}
