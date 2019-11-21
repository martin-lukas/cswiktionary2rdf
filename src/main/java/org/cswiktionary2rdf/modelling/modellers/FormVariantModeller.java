package org.cswiktionary2rdf.modelling.modellers;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.cswiktionary2rdf.modelling.mappings.Namespace;

public class FormVariantModeller {
    public void model(Model model, Resource posResource, Resource formResource) {
        model.add(
                posResource,
                model.createProperty(Namespace.LEMON + "formVariant"),
                formResource);
    }
}
