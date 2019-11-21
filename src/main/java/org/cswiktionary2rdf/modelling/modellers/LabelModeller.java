package org.cswiktionary2rdf.modelling.modellers;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.cswiktionary2rdf.modelling.mappings.Namespace;

public class LabelModeller {
    
    public void model(Model model, Resource resource, String label) {
        model.add(
                resource,
                model.createProperty(Namespace.RDFS + "label"),
                label, "cs");
    }
}
