package org.cswiktionary2rdf.modelling.modellers;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.cswiktionary2rdf.modelling.mappings.Tense;

public class VerbTenseModeller {
    
    public void model(Model model, Resource resource, Tense tense) {
        model.add(
                resource,
                model.createProperty(Tense.PROPERTY),
                model.createResource(tense.getMapping()));
    }
}
