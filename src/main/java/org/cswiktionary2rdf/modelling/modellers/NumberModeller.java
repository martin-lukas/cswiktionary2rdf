package org.cswiktionary2rdf.modelling.modellers;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.cswiktionary2rdf.modelling.mappings.Number;

public class NumberModeller {
    public void model(Model model, Resource resource, Number number) {
        model.add(
                resource,
                model.createProperty(Number.PROPERTY),
                model.createResource(number.getMapping()));
    }
}
