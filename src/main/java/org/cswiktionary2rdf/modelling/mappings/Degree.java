package org.cswiktionary2rdf.modelling.mappings;

public enum Degree {
    POSITIVE("poz", "positive"),
    COMPARATIVE("komp", "comparative"),
    SUPERLATIVE("sup", "superlative");
    
    private String text;
    private String mapping;
    
    public static final String PROPERTY = Namespace.LEXINFO + "degree";
    
    Degree(String text, String mapping) {
        this.text = text;
        this.mapping = mapping;
    }
    
    public static Degree getEnum(String searchedText) {
        for (Degree degree : Degree.values()) {
            if (degree.text.equals(searchedText)) {
                return degree;
            }
        }
        return null;
    }
    
    public String getText() {
        return text;
    }
    
    public String getMapping() {
        return Namespace.LEXINFO +  mapping;
    }
}
