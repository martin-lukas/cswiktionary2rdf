package org.cswiktionary2rdf.modelling.subextractors;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.cswiktionary2rdf.modelling.mappings.AdjectiveFormation;
import org.cswiktionary2rdf.modelling.mappings.Case;
import org.cswiktionary2rdf.modelling.mappings.Gender;
import org.cswiktionary2rdf.modelling.mappings.Number;
import org.cswiktionary2rdf.modelling.misc.ModelUtils;
import org.cswiktionary2rdf.modelling.modellers.*;
import org.cswiktionary2rdf.modelling.objects.GenderObject;
import org.cswiktionary2rdf.modelling.objects.POSObject;
import org.cswiktionary2rdf.utils.TextUtils;

import java.util.List;

/**
 * Extracts all the words from extended declension sections (table 8 cols x 7-9 rows).
 *
 * @author Martin Lukáš
 */
public class ExtendedDeclensionExtractor {
    public void extract(Model model, Resource posResource, POSObject posObject, boolean isVocAsNom) {
        EntryTypeModeller entryTypeModeller = new EntryTypeModeller();
        FormVariantModeller formVariantModeller = new FormVariantModeller();
        PartOfSpeechModeller partOfSpeechModeller = new PartOfSpeechModeller();
        NumberModeller numberModeller = new NumberModeller();
        CaseModeller caseModeller = new CaseModeller();
        AdjectiveFormationModeller adjectiveFormationModeller = new AdjectiveFormationModeller();
        GenderModeller genderModeller = new GenderModeller();
        
        // 5th and 1st case is for adjectives the same, except for a few words.
        // That's why on Wiktionary, they almost never list 5th case
        boolean vocativeException = false;
        if (isVocAsNom && !posObject.getText().contains("voc")) {
            vocativeException = true;
        }
        
        for (Case aCase : Case.values()) {
            for (AdjectiveFormation adjectiveFormation : AdjectiveFormation.values()) {
                for (Number number : Number.shortValues()) {
                    for (String genderCode : Gender.GENDER_CODES) {
                        String caseCode = number.getText() + aCase.getText() + genderCode + adjectiveFormation.getText();
                        if (vocativeException && aCase == Case.VOCATIVE && adjectiveFormation == AdjectiveFormation.NORMAL_FORM) {
                            caseCode = caseCode.replaceAll("voc", "nom");
                        }
                        
                        List<String> caseVariants = TextUtils.parseEntry(posObject.getText(), caseCode);
                        
                        for (String caseVariant : caseVariants) {
                            if (!caseVariant.isEmpty()) {
                                String suffix = TextUtils.connectStrings(
                                        posObject.getPOS().getLocalMapping().toLowerCase(),
                                        number.getText());
                                GenderObject genderObject = new GenderObject(genderCode);
                                if (genderObject.getAnimacy() == null) {
                                    suffix = TextUtils.connectStrings(
                                            suffix,
                                            genderObject.getGender().getLocalMapping().substring(0, 1),
                                            aCase.getText());
                                } else {
                                    suffix = TextUtils.connectStrings(
                                            suffix,
                                            genderObject.getGender().getLocalMapping().substring(0, 1) +
                                                    genderObject.getAnimacy().getLocalMapping().substring(0, 1),
                                            aCase.getText());
                                }
                                if (adjectiveFormation == AdjectiveFormation.NOMINAL) {
                                    suffix += "_nom";
                                }
                                Resource caseVariantResource = ModelUtils.createNewResourceWithLabel(
                                        model, posObject.getPage().getTitle(), suffix, caseVariant);
                                
                                entryTypeModeller.model(model, caseVariantResource);
                                formVariantModeller.model(model, posResource, caseVariantResource);
                                partOfSpeechModeller.model(model, caseVariantResource, posObject.getPOS());
                                numberModeller.model(model, caseVariantResource, number);
                                caseModeller.model(model, caseVariantResource, aCase);
                                adjectiveFormationModeller.model(model, caseVariantResource, adjectiveFormation);
                                genderModeller.model(model, caseVariantResource, genderObject);
                            }
                        }
                    }
                }
            }
        }
    }
}
