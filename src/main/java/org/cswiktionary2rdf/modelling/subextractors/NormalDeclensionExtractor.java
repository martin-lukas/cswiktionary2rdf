package org.cswiktionary2rdf.modelling.subextractors;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.cswiktionary2rdf.modelling.misc.ModelUtils;
import org.cswiktionary2rdf.modelling.modellers.*;
import org.cswiktionary2rdf.modelling.objects.GenderObject;
import org.cswiktionary2rdf.modelling.objects.POSObject;
import org.cswiktionary2rdf.modelling.mappings.Case;
import org.cswiktionary2rdf.modelling.mappings.Number;
import org.cswiktionary2rdf.utils.TextUtils;

import java.util.List;

/**
 * Extracts all the forms in declension sections which differentiate cases and numbers.
 *
 * @author Martin Lukáš
 */
public class NormalDeclensionExtractor {
    
    public void extract(Model model, Resource posResource, POSObject posObject, GenderObject genderObject) {
        EntryTypeModeller entryTypeModeller = new EntryTypeModeller();
        FormVariantModeller formVariantModeller = new FormVariantModeller();
        PartOfSpeechModeller partOfSpeechModeller = new PartOfSpeechModeller();
        GenderModeller genderModeller = new GenderModeller();
        CaseModeller caseModeller = new CaseModeller();
        NumberModeller numberModeller = new NumberModeller();
        
        for (Case aCase : Case.values()) {
            for (Number number : Number.shortValues()) {
                String caseCode = number.getText() + aCase.getText();
                List<String> caseVariants = TextUtils.parseEntry(posObject.getText(), caseCode);
                
                for (String caseVariant : caseVariants) {
                    if (!caseVariant.isEmpty()) {
                        String suffix = TextUtils.connectStrings(
                                posObject.getPOS().getLocalMapping().toLowerCase(),
                                number.getText(),
                                aCase.getText());
                        Resource caseVariantResource = ModelUtils.createNewResourceWithLabel(
                                model, posObject.getPage().getTitle(), suffix, caseVariant);
                        
                        entryTypeModeller.model(model, caseVariantResource);
                        formVariantModeller.model(model, posResource, caseVariantResource);
                        partOfSpeechModeller.model(model, caseVariantResource, posObject.getPOS());
                        if (genderObject != null) {
                            genderModeller.model(model, caseVariantResource, genderObject);
                        }
                        caseModeller.model(model, caseVariantResource, aCase);
                        numberModeller.model(model, caseVariantResource, number);
                    }
                }
            }
        }
    }
}