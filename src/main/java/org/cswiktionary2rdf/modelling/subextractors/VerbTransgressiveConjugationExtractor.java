package org.cswiktionary2rdf.modelling.subextractors;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.cswiktionary2rdf.modelling.misc.ModelUtils;
import org.cswiktionary2rdf.modelling.modellers.*;
import org.cswiktionary2rdf.modelling.mappings.*;
import org.cswiktionary2rdf.modelling.objects.GenderObject;
import org.cswiktionary2rdf.modelling.objects.POSObject;
import org.cswiktionary2rdf.modelling.mappings.Number;
import org.cswiktionary2rdf.utils.TextUtils;

import java.util.*;
import java.util.List;

public class VerbTransgressiveConjugationExtractor {
    // exception - fields for being able to extract parts of code for readability
    private Model model;
    private POSObject posObject;
    
    private Tense tense;
    private Number number;
    
    public void extract(Model model, Resource posResource, POSObject posObject) {
        this.model = model;
        this.posObject = posObject;
        
        EntryTypeModeller entryTypeModeller = new EntryTypeModeller();
        LabelModeller labelModeller = new LabelModeller();
        FormVariantModeller formVariantModeller = new FormVariantModeller();
        PartOfSpeechModeller partOfSpeechModeller = new PartOfSpeechModeller();
        VerbFormModeller verbFormModeller = new VerbFormModeller();
        VerbTenseModeller verbTenseModeller = new VerbTenseModeller();
        NumberModeller numberModeller = new NumberModeller();
        
        String conjCode;
        
        List<String> genderCodes = new ArrayList<>();
        for (Gender gender : Gender.shortValues()) {
            genderCodes.add(gender.getText());
        }
        Collections.addAll(genderCodes, "p");
        
        List<Number> numbers = Number.shortValues();
        numbers.remove(Number.DUAL);
        for (Number number : numbers) {
            this.number = number;
            for (Tense tense : Tense.shortValues()) {
                this.tense = tense;
                for (String genderCode : genderCodes) {
                    // tra - from 'transgressive' which is alternative way of 'gerundive' or 'gerund'
                    // Wiktionary chooses transgressive, but in LexInfo, they only have gerundive
                    conjCode = tense.getText() + VerbForm.TRANSGRESSIVE.getText() + genderCode;
                    List<String> conjVariants = TextUtils.parseEntry(posObject.getText(), conjCode);
                    
                    for (String conjVariant : conjVariants) {
                        if (!conjVariant.isEmpty()) {
                            List<Resource> resources = new ArrayList<>();
                            // reason for this code is the joining of fields with always the same values
                            if (number == Number.SINGULAR_2) {
                                if (genderCode.equals("m")) {
                                    resources.addAll(extractGendersAndReturnResources(new GenderObject[]{
                                            new GenderObject(Gender.MASCULINE, Animacy.ANIMATE),
                                            new GenderObject(Gender.MASCULINE, Animacy.INANIMATE)
                                    }));
                                } else if (genderCode.equals("f")) {
                                    resources.addAll(extractGendersAndReturnResources(new GenderObject[]{
                                            new GenderObject(Gender.FEMININE, null),
                                            new GenderObject(Gender.NEUTER, null)
                                    }));
                                }
                            } else {
                                if (genderCode.equals("p")) {
                                    resources.addAll(extractGendersAndReturnResources(new GenderObject[]{
                                            new GenderObject(Gender.MASCULINE, Animacy.ANIMATE),
                                            new GenderObject(Gender.MASCULINE, Animacy.INANIMATE),
                                            new GenderObject(Gender.FEMININE, null),
                                            new GenderObject(Gender.NEUTER, null)
                                    }));
                                }
                            }
                            
                            for (Resource resource : resources) {
                                entryTypeModeller.model(model, resource);
                                labelModeller.model(model, resource, conjVariant);
                                partOfSpeechModeller.model(model, resource, posObject.getPOS());
                                formVariantModeller.model(model, posResource, resource);
                                verbFormModeller.model(model, resource, VerbForm.TRANSGRESSIVE);
                                verbTenseModeller.model(model, resource, tense);
                                numberModeller.model(model, resource, number);
                            }
                        }
                    }
                }
            }
        }
    }
    
    private List<Resource> extractGendersAndReturnResources(GenderObject[] genderObjects) {
        List<Resource> resourceList = new ArrayList<>();
        String suffix = TextUtils.connectStrings(
                posObject.getPOS().getLocalMapping().toLowerCase(),
                VerbForm.TRANSGRESSIVE.getText(),
                tense.getLocalMapping().substring(0, 3),
                number.getText().substring(0, 1));
        for (GenderObject genderObject : genderObjects) {
            String genSuffix = TextUtils.connectStrings(
                    suffix,
                    genderObject.getGender().getLocalMapping().substring(0, 1));
            if (genderObject.getAnimacy() != null) {
                genSuffix += genderObject.getAnimacy().getLocalMapping().substring(0, 1);
            }
            Resource resource = ModelUtils.createNewResource(model, posObject.getPage().getTitle(), genSuffix);
            new GenderModeller().model(model, resource, genderObject);
            resourceList.add(resource);
        }
        return resourceList;
    }
}
