package org.cswiktionary2rdf.modelling.mappings;

/**
 * Extracts the case type property (only one value - "jmenný vzor", or also called the short or nominal form).
 *
 * @author Martin Lukáš
 */
public enum AdjectiveFormation {
    NORMAL_FORM("", ""),
    NOMINAL("-jm", "NominalAdjective");
    
    private final String text;
    private final String mapping;
    
    public static final String PROPERTY = Namespace.MTE + "hasAdjectiveFormation";
    
    AdjectiveFormation(String text, String mapping) {
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
