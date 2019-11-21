package org.cswiktionary2rdf.modelling.modellers;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.cswiktionary2rdf.modelling.objects.GenderObject;
import org.cswiktionary2rdf.modelling.mappings.Animacy;
import org.cswiktionary2rdf.modelling.mappings.Gender;

public class GenderModeller {
    public void model(Model model, Resource resource, GenderObject genderObject) {
        model.add(
                resource,
                model.createProperty(Gender.PROPERTY),
                model.createResource(genderObject.getGender().getMapping()));
        if (genderObject.getAnimacy() != null) {
            model.add(
                    resource,
                    model.createProperty(Animacy.PROPERTY),
                    model.createResource(genderObject.getAnimacy().getMapping()));
        }
    }
}
