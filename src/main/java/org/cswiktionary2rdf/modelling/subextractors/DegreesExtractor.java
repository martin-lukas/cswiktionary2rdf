package org.cswiktionary2rdf.modelling.subextractors;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.cswiktionary2rdf.modelling.misc.ModelUtils;
import org.cswiktionary2rdf.modelling.modellers.*;
import org.cswiktionary2rdf.modelling.objects.POSObject;
import org.cswiktionary2rdf.modelling.mappings.Degree;
import org.cswiktionary2rdf.utils.TextUtils;

import java.util.List;

/**
 * Handles extraction of degrees of adjectives or adverbs.
 *
 * @author Martin Lukáš
 */
public class DegreesExtractor {
    
    public void extract(Model model, Resource posResource, POSObject posObject) {
        EntryTypeModeller entryTypeModeller = new EntryTypeModeller();
        FormVariantModeller formVariantModeller = new FormVariantModeller();
        PartOfSpeechModeller posModeller = new PartOfSpeechModeller();
        DegreeModeller degreeModeller = new DegreeModeller();
        
        String[] numberCodes = new String[]{"", "2"};
        String degreeCode;
        for (Degree degree : Degree.values()) {
            for (String numberCode : numberCodes) {
                degreeCode = degree.getText() + numberCode;
                List<String> degreeVariants = TextUtils.parseEntry(posObject.getText(), degreeCode);
    
                for (String degreeVariant : degreeVariants) {
                    if (!degreeVariant.isEmpty()) {
                        String suffix = TextUtils.connectStrings(
                                posObject.getPOS().getLocalMapping().toLowerCase(),
                                degree.getText().toLowerCase().substring(0, 3));
                        Resource degreeVariantResource = ModelUtils.createNewResourceWithLabel(
                                model, posObject.getPage().getTitle(), suffix, degreeVariant);
                        
                        entryTypeModeller.model(model, degreeVariantResource);
                        formVariantModeller.model(model, posResource, degreeVariantResource);
                        posModeller.model(model, degreeVariantResource, posObject.getPOS());
                        degreeModeller.model(model, degreeVariantResource, degree);
                    }
                }
            }
        }
    }
}
