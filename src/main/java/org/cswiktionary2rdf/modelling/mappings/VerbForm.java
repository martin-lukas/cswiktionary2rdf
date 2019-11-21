package org.cswiktionary2rdf.modelling.mappings;

import java.util.ArrayList;
import java.util.List;

public enum VerbForm {
    DEFAULT("", ""),
    TRANSGRESSIVE("tra", "Transgressive");
    
    private String text;
    private String mapping;
    
    public static final String PROPERTY = Namespace.MTE + "hasVerbForm";
    
    VerbForm(String text, String mapping) {
        this.text = text;
        this.mapping = mapping;
    }
    
    public String getText() {
        return text;
    }
    
    public String getMapping() {
        return Namespace.MTE + mapping;
    }
}
