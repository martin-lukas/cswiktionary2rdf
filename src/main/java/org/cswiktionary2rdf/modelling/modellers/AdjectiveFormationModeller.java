package org.cswiktionary2rdf.modelling.modellers;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.cswiktionary2rdf.modelling.mappings.AdjectiveFormation;

public class AdjectiveFormationModeller {
    public void model(Model model, Resource resource, AdjectiveFormation type) {
        if (type == AdjectiveFormation.NOMINAL) {
            model.add(
                    resource,
                    model.createProperty(AdjectiveFormation.PROPERTY),
                    model.createResource(type.getMapping()));
        }
    }
}
