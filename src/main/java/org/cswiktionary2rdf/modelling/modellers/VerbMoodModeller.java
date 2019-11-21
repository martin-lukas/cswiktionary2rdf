package org.cswiktionary2rdf.modelling.modellers;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.cswiktionary2rdf.modelling.mappings.Mood;

public class VerbMoodModeller {
    
    public void model(Model model, Resource moodResource, Mood mood) {
        model.add(
                moodResource,
                model.createProperty(Mood.PROPERTY),
                model.createResource(mood.getMapping()));
    }
}
