package org.cswiktionary2rdf.modelling.subextractors;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.cswiktionary2rdf.modelling.misc.ModelUtils;
import org.cswiktionary2rdf.modelling.modellers.*;
import org.cswiktionary2rdf.modelling.objects.POSObject;
import org.cswiktionary2rdf.modelling.mappings.Number;
import org.cswiktionary2rdf.modelling.mappings.Person;
import org.cswiktionary2rdf.modelling.mappings.Mood;
import org.cswiktionary2rdf.utils.TextUtils;

import java.util.List;

public class VerbConditionalConjugationExtractor {
    
    public void extract(Model model, Resource posResource, POSObject posObject) {
        String[] sections = posObject.getText().split("class=\"konjugace verbum\"");
        if (sections.length > 1) {
            String[] subsections = sections[1].split("\\|\\}");
            if (subsections.length > 1) {
                String tableSection = subsections[0];
                List<List<String>> cells = TextUtils.parseRawEntry(tableSection);
            
                EntryTypeModeller entryTypeModeller = new EntryTypeModeller();
                FormVariantModeller formVariantModeller = new FormVariantModeller();
                PartOfSpeechModeller partOfSpeechModeller = new PartOfSpeechModeller();
                VerbMoodModeller verbMoodModeller = new VerbMoodModeller();
                NumberModeller numberModeller = new NumberModeller();
                PersonModeller personModeller = new PersonModeller();
            
                Mood mood = Mood.CONDITIONAL;
                
                for (int i = 0; i < cells.size(); i++) {
                    List<String> variants = cells.get(i);
                
                    Number number = (i % 2 == 0)
                            ? Number.SINGULAR
                            : Number.PLURAL;
                    Person person = Person.FIRST;
                    switch (i) {
                        case 2:
                        case 3:
                            person = Person.SECOND;
                            break;
                        case 4:
                        case 5:
                            person = Person.THIRD;
                            break;
                    }
                
                    for (String variant : variants) {
                        String suffix = TextUtils.connectStrings(
                                posObject.getPOS().getLocalMapping().toLowerCase(),
                                mood.getLocalMapping().substring(0, 3),
                                number.getLocalMapping().substring(0, 1),
                                person.getText());
                        Resource variantResource = ModelUtils.createNewResourceWithLabel(
                                model, posObject.getPage().getTitle(), suffix, variant);
                    
                        entryTypeModeller.model(model, variantResource);
                        formVariantModeller.model(model, posResource, variantResource);
                        partOfSpeechModeller.model(model, variantResource, posObject.getPOS());
                        verbMoodModeller.model(model, variantResource, mood);
                        numberModeller.model(model, variantResource, number);
                        personModeller.model(model, variantResource, person);
                    }
                }
            }
        }
    }
}
