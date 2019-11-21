package org.cswiktionary2rdf.modelling.extractors;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.cswiktionary2rdf.modelling.mappings.Namespace;
import org.cswiktionary2rdf.modelling.modellers.ExternalModeller;
import org.cswiktionary2rdf.utils.Page;
import org.cswiktionary2rdf.utils.TextUtils;

import java.util.List;

public class ExternalSectionExtractor implements SectionExtractor {
    @Override
    public void extract(Model model, Resource resource, Page page) {
        ExternalModeller externalModeller = new ExternalModeller();
        
        for (String section : TextUtils.splitSections(page.getText(), 2)) {
            String sectionTitle = TextUtils.getSectionTitle(section, 2);
            if (sectionTitle.equals("externí odkazy")) {
                List<String> matches = TextUtils.getMatches(section,
                        "\\{\\{\\s*Wikipedie\\s*\\|\\s*článek\\s*=\\s*(.+)(?=\\}\\})");
                for (String match : matches) {
                    match = match.substring(match.indexOf("=") + 1).trim();
                    String uri = Namespace.CS_DBPEDIA + TextUtils.connectString(match);
                    externalModeller.model(model, resource, uri);
                }
            }
        }
    }
}
