package org.cswiktionary2rdf.modelling.subextractors;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.cswiktionary2rdf.modelling.misc.GenderParser;
import org.cswiktionary2rdf.modelling.misc.ModelUtils;
import org.cswiktionary2rdf.modelling.modellers.*;
import org.cswiktionary2rdf.modelling.objects.GenderObject;
import org.cswiktionary2rdf.modelling.objects.POSObject;
import org.cswiktionary2rdf.modelling.mappings.Animacy;
import org.cswiktionary2rdf.modelling.mappings.Gender;
import org.cswiktionary2rdf.modelling.mappings.Number;
import org.cswiktionary2rdf.modelling.mappings.Voice;
import org.cswiktionary2rdf.utils.TextUtils;

import java.util.*;

public class VerbParticipleConjugationExtractor {
    // exception - fields for being able to extract parts of code for readability
    private Model model;
    private POSObject posObject;
    
    private Voice voice;
    private Number number;
    
    public void extract(Model model, Resource posResource, POSObject posObject) {
        this.model = model;
        this.posObject = posObject;
    
        EntryTypeModeller entryTypeModeller = new EntryTypeModeller();
        LabelModeller labelModeller = new LabelModeller();
        FormVariantModeller formVariantModeller = new FormVariantModeller();
        PartOfSpeechModeller partOfSpeechModeller = new PartOfSpeechModeller();
        VerbVoiceModeller verbVoiceModeller = new VerbVoiceModeller();
        NumberModeller numberModeller = new NumberModeller();
        
        String conjCode;
        List<Number> numbers = Number.shortValues();
        numbers.remove(Number.DUAL);
        for (Number number : numbers) {
            this.number = number;
            for (Voice voice : Voice.values()) {
                this.voice = voice;
                for (Gender gender : Gender.shortValues()) {
                    conjCode = number.getText() + voice.getText() + gender.getText();
                    List<String> conjVariants = TextUtils.parseEntry(posObject.getText(), conjCode);
                    
                    for (String conjVariant : conjVariants) {
                        if (!conjVariant.isEmpty()) {
                            List<Resource> resources = new ArrayList<>();
                            // reason for this code is the joining of fields with always the same values
                            // in Wiktionary templates (e.g. plural neuter active is always equal to sing. fem. ...)
                            if (number == Number.SINGULAR_2) {
                                if (gender == Gender.MASCULINE_2) {
                                    resources.addAll(extractGendersAndReturnResources(new GenderObject[]{
                                            new GenderObject(gender, Animacy.ANIMATE),
                                            new GenderObject(gender, Animacy.INANIMATE)
                                    }));
                                } else if (gender == Gender.FEMININE_2) {
                                    // because of equality of sing.fem.act. and plur.neu.act.
                                    resources.addAll(extractGendersAndReturnResources(new GenderObject[]{
                                            new GenderObject(gender, null),
                                            new GenderObject(Gender.NEUTER_2, null)
                                    }));
                                } else {
                                    resources.addAll(extractGendersAndReturnResources(new GenderObject[]{
                                            new GenderObject(gender, null)
                                    }));
                                }
                            } else {
                                if (gender == Gender.MASCULINE_2) {
                                    resources.addAll(extractGendersAndReturnResources(new GenderObject[]{
                                            new GenderObject(gender, Animacy.ANIMATE)
                                    }));
                                } else if (gender == Gender.FEMININE_2) {
                                    resources.addAll(extractGendersAndReturnResources(new GenderObject[]{
                                            new GenderObject(Gender.MASCULINE_2, Animacy.INANIMATE),
                                            new GenderObject(gender, null)
                                    }));
                                }
                            }
                            
                            for (Resource resource : resources) {
                                entryTypeModeller.model(model, resource);
                                labelModeller.model(model, resource, conjVariant);
                                partOfSpeechModeller.model(model, resource, posObject.getPOS());
                                formVariantModeller.model(model, posResource, resource);
                                verbVoiceModeller.model(model, resource, voice);
                                // because of equality of sing.fem.act. and plur.neu.act. - I already assigned number there
                                if (!model.contains(resource, model.createProperty(Number.PROPERTY))) {
                                    numberModeller.model(model, resource, number);
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    private List<Resource> extractGendersAndReturnResources(GenderObject[] genderObjects) {
        List<Resource> resourceList = new ArrayList<>();
    
        boolean containsFeminineGender = GenderParser.containsGender(genderObjects, Gender.FEMININE_2);
        boolean containsNeuterGender = GenderParser.containsGender(genderObjects, Gender.NEUTER_2);
        
        boolean neuterException = (containsFeminineGender && containsNeuterGender);
        
        String suffix = TextUtils.connectStrings(
                posObject.getPOS().getLocalMapping().toLowerCase(),
                voice.getText().substring(0, 3),
                number.getText().substring(0, 1));
        for (GenderObject genderObject : genderObjects) {
            Resource resource = null;
            String genSuffix = suffix;
            if (genderObject.getGender() == Gender.NEUTER_2 && neuterException) {
                genSuffix = suffix.substring(0, suffix.length() - 1); // remove number letter
                genSuffix += Number.PLURAL_2.getText();
                resource = ModelUtils.createNewResource(model, posObject.getPage().getTitle(), genSuffix);
                new NumberModeller().model(model, resource, Number.PLURAL_2);
            }
            genSuffix = TextUtils.connectStrings(
                    genSuffix,
                    genderObject.getGender().getLocalMapping().substring(0, 1));
            if (genderObject.getAnimacy() != null) {
                genSuffix += genderObject.getAnimacy().getLocalMapping().substring(0, 1);
            }
            if (resource == null) {
                resource = ModelUtils.createNewResource(model, posObject.getPage().getTitle(), genSuffix);
            }
            new GenderModeller().model(model, resource, genderObject);
            resourceList.add(resource);
        }
        return resourceList;
    }
}