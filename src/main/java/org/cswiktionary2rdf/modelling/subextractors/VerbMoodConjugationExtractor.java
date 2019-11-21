package org.cswiktionary2rdf.modelling.subextractors;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.cswiktionary2rdf.modelling.misc.ModelUtils;
import org.cswiktionary2rdf.modelling.modellers.*;
import org.cswiktionary2rdf.modelling.mappings.*;
import org.cswiktionary2rdf.modelling.objects.POSObject;
import org.cswiktionary2rdf.modelling.mappings.Number;
import org.cswiktionary2rdf.utils.TextUtils;

import java.util.List;

public class VerbMoodConjugationExtractor {
    public void extract(Model model, Resource posResource, POSObject posObject) {
        EntryTypeModeller entryTypeModeller = new EntryTypeModeller();
        FormVariantModeller formVariantModeller = new FormVariantModeller();
        PartOfSpeechModeller partOfSpeechModeller = new PartOfSpeechModeller();
        VerbMoodModeller verbMoodModeller = new VerbMoodModeller();
        NumberModeller numberModeller = new NumberModeller();
        VerbTenseModeller verbTenseModeller = new VerbTenseModeller();
        PersonModeller personModeller = new PersonModeller();
        
        String conjCode;
        List<Number> numbers = Number.shortValues();
        numbers.remove(Number.DUAL);
        for (Number number : numbers) {
            for (Mood mood : Mood.values()) {
                if (mood == Mood.INFINITIVE){
                    break;
                }
                for (Person person : Person.values()) {
                    if (mood == Mood.INDICATIVE) {
                        for (Tense tense : Tense.values()) {
                            conjCode = number.getText() + tense.getText() + person.getText();
                            List<String> conjVariants = TextUtils.parseEntry(posObject.getText(), conjCode);
                            
                            for (String conjVariant : conjVariants) {
                                String suffix = TextUtils.connectStrings(
                                        posObject.getPOS().getLocalMapping().toLowerCase(),
                                        mood.getLocalMapping().substring(0, 3),
                                        tense.getLocalMapping().substring(0, 3),
                                        number.getText(),
                                        person.getText());
                                Resource conjVariantResource = ModelUtils.createNewResourceWithLabel(
                                        model, posObject.getPage().getTitle(), suffix, conjVariant);
                                
                                entryTypeModeller.model(model, conjVariantResource);
                                formVariantModeller.model(model, posResource, conjVariantResource);
                                partOfSpeechModeller.model(model, conjVariantResource, posObject.getPOS());
                                verbMoodModeller.model(model, conjVariantResource, mood);
                                numberModeller.model(model, conjVariantResource, number);
                                verbTenseModeller.model(model, conjVariantResource, tense);
                                personModeller.model(model, conjVariantResource, person);
                            }
                        }
                    } else {
                        conjCode = number.getText() + mood.getText() + person.getText();
                        List<String> conjVariants = TextUtils.parseEntry(posObject.getText(), conjCode);
                        
                        for (String conjVariant : conjVariants) {
                            String suffix = TextUtils.connectStrings(
                                    posObject.getPOS().getLocalMapping().toLowerCase(),
                                    mood.getLocalMapping().substring(0, 3),
                                    number.getText(),
                                    person.getText());
                            Resource conjVariantResource = ModelUtils.createNewResourceWithLabel(
                                    model, posObject.getPage().getTitle(), suffix, conjVariant);
                            
                            entryTypeModeller.model(model, conjVariantResource);
                            formVariantModeller.model(model, posResource, conjVariantResource);
                            partOfSpeechModeller.model(model, conjVariantResource, posObject.getPOS());
                            verbMoodModeller.model(model, conjVariantResource, mood);
                            numberModeller.model(model, conjVariantResource, number);
                            personModeller.model(model, conjVariantResource, person);
                        }
                    }
                }
            }
        }
    }
}
