package org.cswiktionary2rdf.modelling.extractors;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.cswiktionary2rdf.modelling.modellers.PronunciationModeller;
import org.cswiktionary2rdf.utils.Page;
import org.cswiktionary2rdf.utils.TextUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PronunciationSectionExtractor implements SectionExtractor {
    
    @Override
    public void extract(Model model, Resource resource, Page page) {
        PronunciationModeller pronunciationModeller = new PronunciationModeller();
        
        List<String> pronunciationVariants = new ArrayList<>();
        List<String> matches = TextUtils.getMatches(page.getText(), "(?<=IPA\\|)(.*?)(?=})");
        for (String match : matches) {
            pronunciationVariants.addAll(Arrays.asList(match.split("\\|")));
        }
        
        for (String pronunciationVariant : pronunciationVariants) {
            // when someone puts two separators there (||)
            String trimmedPronunciation = pronunciationVariant.trim().replaceAll("\\|", "");
            pronunciationModeller.model(model, resource, trimmedPronunciation);
        }
    }
}
