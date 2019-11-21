package org.cswiktionary2rdf.modelling.modellers;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.cswiktionary2rdf.modelling.mappings.Degree;

public class DegreeModeller {
    
    public void model(Model model, Resource degreeResource, Degree degree) {
        model.add(
                degreeResource,
                model.createProperty(Degree.PROPERTY),
                model.createResource(degree.getMapping()));
    }
}
