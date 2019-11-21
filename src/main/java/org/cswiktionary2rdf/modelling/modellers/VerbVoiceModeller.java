package org.cswiktionary2rdf.modelling.modellers;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.cswiktionary2rdf.modelling.mappings.Voice;

public class VerbVoiceModeller {
    
    public void model(Model model, Resource participleResource, Voice voice) {
        model.add(
                participleResource,
                model.createProperty(Voice.PROPERTY),
                model.createResource(voice.getMapping()));
    }
}
