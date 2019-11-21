package org.cswiktionary2rdf.modelling.subextractors;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.cswiktionary2rdf.modelling.misc.ModelUtils;
import org.cswiktionary2rdf.modelling.modellers.*;
import org.cswiktionary2rdf.modelling.objects.POSObject;
import org.cswiktionary2rdf.modelling.mappings.Case;
import org.cswiktionary2rdf.modelling.mappings.Number;
import org.cswiktionary2rdf.utils.TextUtils;

import java.util.List;

/**
 * Extracts all the variants in a declension section differentiating only 7 cases and nothing else.
 *
 * @author Martin Lukáš
 */
public class TinyDeclensionExtractor {
    
    public void extract(Model model, Resource posResource, POSObject posObject, Number number) {
        EntryTypeModeller entryTypeModeller = new EntryTypeModeller();
        FormVariantModeller formVariantModeller = new FormVariantModeller();
        PartOfSpeechModeller partOfSpeechModeller = new PartOfSpeechModeller();
        NumberModeller numberModeller = new NumberModeller();
        CaseModeller caseModeller = new CaseModeller();
        
        for (Case aCase : Case.values()) {
            List<String> caseVariants = TextUtils.parseEntry(posObject.getText(), aCase.getText());
            
            for (String caseVariant : caseVariants) {
                if (!caseVariant.isEmpty()) {
                    String suffix = TextUtils.connectString(
                            posObject.getPOS().getLocalMapping().toLowerCase());
                    if (number != null) {
                        suffix = TextUtils.connectStrings(
                                suffix,
                                number.getLocalMapping().substring(0, 1),
                                aCase.getText());
                    } else {
                        suffix = TextUtils.connectStrings(suffix, aCase.getText());
                    }
                    Resource caseVariantResource = ModelUtils.createNewResourceWithLabel(
                            model, posObject.getPage().getTitle(), suffix, caseVariant);
                    
                    entryTypeModeller.model(model, caseVariantResource);
                    formVariantModeller.model(model, posResource, caseVariantResource);
                    partOfSpeechModeller.model(model, caseVariantResource, posObject.getPOS());
                    if (number != null) {
                        numberModeller.model(model, caseVariantResource, number);
                    }
                    caseModeller.model(model, caseVariantResource, aCase);
                }
            }
        }
    }
}
