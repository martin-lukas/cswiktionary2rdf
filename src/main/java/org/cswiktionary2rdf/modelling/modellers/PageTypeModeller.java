package org.cswiktionary2rdf.modelling.modellers;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.cswiktionary2rdf.modelling.mappings.Namespace;

public class PageTypeModeller {

    public void model(Model model, Resource resource) {
        new RdfTypeModeller().model(model, resource, Namespace.DBNARY + "Page");
    }
}
