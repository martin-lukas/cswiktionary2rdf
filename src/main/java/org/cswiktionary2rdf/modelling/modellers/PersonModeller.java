package org.cswiktionary2rdf.modelling.modellers;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.cswiktionary2rdf.modelling.mappings.Person;

public class PersonModeller {
    
    public void model(Model model, Resource resource, Person person) {
        model.add(
                resource,
                model.createProperty(Person.PROPERTY),
                model.createResource(person.getMapping()));
    }
}
