package org.cswiktionary2rdf.modelling;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.cswiktionary2rdf.modelling.mappings.Namespace;
import org.cswiktionary2rdf.utils.Page;

import java.util.List;

/**
 * This class handles the creating of the RDF model from pages for generating the RDF file.
 *
 * @author Martin Lukáš
 */
public class ModelBuilder {
    private final List<Page> pages;
    private final Model model;
    
    public ModelBuilder(List<Page> pages) {
        this.pages = pages;
        model = ModelFactory.createDefaultModel();
        setNamespaces();
    }
    
    public void buildModel() {
        for (Page page : pages) {
            new PageExtractor().extract(model, page);
        }
        System.out.println("Triples: " + model.size());
    }
    
    public Model getModel() {
        return model;
    }
    
    private void setNamespaces() {
        for (Namespace ns : Namespace.values()) {
            model.setNsPrefix(ns.prefix(), ns.url());
        }
    }
}
