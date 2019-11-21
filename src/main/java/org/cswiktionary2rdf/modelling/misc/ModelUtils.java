package org.cswiktionary2rdf.modelling.misc;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.cswiktionary2rdf.modelling.mappings.Namespace;
import org.cswiktionary2rdf.modelling.modellers.LabelModeller;
import org.cswiktionary2rdf.utils.TextUtils;

public class ModelUtils {
    public static Resource createNewResourceWithLabel(Model model, String base, String suffix, String label) {
        Resource newResource = createNewResource(model, base, suffix);
        
        new LabelModeller().model(model, newResource, label);
        
        return newResource;
    }
    
    public static Resource createNewResource(Model model, String base, String suffix) {
        String wordPlusSuffix = TextUtils.connectString(base);
        if (suffix != null) {
            wordPlusSuffix = TextUtils.connectStrings(
                    wordPlusSuffix,
                    suffix);
        }
        Resource wordResource = model.createResource(Namespace.BASE + wordPlusSuffix);
        int order = 2;
        while (model.containsResource(wordResource)) {
            String resourceUri;
            if (order == 2) {
                resourceUri = wordResource.getURI();
            } else {
                resourceUri = wordResource.getURI();
                resourceUri = resourceUri.substring(0, resourceUri.lastIndexOf("_"));
            }
            wordResource = model.createResource(resourceUri + "_" + order++);
        }
        return wordResource;
    }
}
