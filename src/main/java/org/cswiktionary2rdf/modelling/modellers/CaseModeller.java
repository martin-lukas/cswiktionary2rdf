package org.cswiktionary2rdf.modelling.modellers;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.cswiktionary2rdf.modelling.mappings.Case;

public class CaseModeller {
    
    public void model(Model model, Resource caseResource, Case aCase) {
        model.add(
                caseResource,
                model.createProperty(Case.PROPERTY),
                model.createResource(aCase.getMapping()));
    }
}
