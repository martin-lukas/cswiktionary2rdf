package org.cswiktionary2rdf.modelling.modellers;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.cswiktionary2rdf.modelling.mappings.Namespace;

public class BaseReferenceModeller {
    public void model(Model model, Resource baseResource, Resource resource) {
        model.add(
                baseResource,
                model.createProperty(Namespace.DBNARY + "describes"),
                resource);
    }
}
