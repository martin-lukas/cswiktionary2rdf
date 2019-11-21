package org.cswiktionary2rdf.modelling.mappings;

public enum Mood {
    INDICATIVE("", "indicative"),
    IMPERATIVE("imp", "imperative"),
    INFINITIVE("", "infinitive"),
    CONDITIONAL("", "conditional");
    
    private String text;
    private String mapping;
    
    public static final String PROPERTY = Namespace.LEXINFO + "verbFormMood";
    
    Mood(String text, String mapping) {
        this.text = text;
        this.mapping = mapping;
    }
    
    public static Mood getEnum(String searchedText) {
        for (Mood mood : values()) {
            if (mood.text.equals(searchedText)) {
                return mood;
            }
        }
        return null;
    }
    
    public String getText() {
        return text;
    }
    
    public String getLocalMapping() {
        return mapping;
    }
    
    public String getMapping() {
        return Namespace.LEXINFO + mapping;
    }
}
