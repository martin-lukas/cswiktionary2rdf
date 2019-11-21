package org.cswiktionary2rdf.modelling.modellers;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.cswiktionary2rdf.modelling.mappings.Namespace;
import org.cswiktionary2rdf.modelling.mappings.VerbForm;

public class VerbFormModeller {
    public void model(Model model, Resource resource, VerbForm verbForm) {
        model.add(
                resource,
                model.createProperty(VerbForm.PROPERTY),
                model.createResource(verbForm.getMapping()));
    }
}
