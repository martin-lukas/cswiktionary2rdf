package org.cswiktionary2rdf.modelling.mappings;

public enum Voice {
    ACTIVE("act", "active"),
    PASSIVE("pas", "passive");
    
    private String text;
    private String mapping;
    
    public static final String PROPERTY = Namespace.LEXINFO + "voice";
    
    Voice(String text, String mapping) {
        this.text = text;
        this.mapping = mapping;
    }
    
    public static Voice getEnum(String searchedText) {
        for (Voice participle : values()) {
            if (participle.text.equals(searchedText)) {
                return participle;
            }
        }
        return null;
    }
    
    public String getText() {
        return text;
    }
    
    public String getMapping() {
        return Namespace.LEXINFO + mapping;
    }
}
