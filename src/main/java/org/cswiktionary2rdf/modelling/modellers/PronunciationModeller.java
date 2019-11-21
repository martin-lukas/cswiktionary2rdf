package org.cswiktionary2rdf.modelling.modellers;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.cswiktionary2rdf.modelling.mappings.Namespace;

public class PronunciationModeller {
    public void model(Model model, Resource resource, String pronunciation) {
        model.add(
                resource,
                model.createProperty(Namespace.LEXINFO + "pronunciation"),
                pronunciation);
    }
}
