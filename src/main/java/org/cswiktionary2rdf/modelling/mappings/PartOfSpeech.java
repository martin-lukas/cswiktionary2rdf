package org.cswiktionary2rdf.modelling.mappings;

public enum PartOfSpeech {
    NOUN("podstatné jméno", "noun"),
    ADJECTIVE("přídavné jméno", "adjective"),
    PRONOUN("zájmeno", "pronoun"),
    NUMERAL("číslovka", "numeral"),
    VERB("sloveso", "verb"),
    ADVERB("příslovce", "adverb"),
    PREPOSITION("předložka", "preposition"),
    CONJUNCTION("spojka", "conjunction"),
    PARTICLE("částice", "particle"),
    INTERJECTION("citoslovce", "interjection");
    
    private String text;
    private String mapping;
    
    public static final String PROPERTY = Namespace.LEXINFO + "partOfSpeech";
    
    PartOfSpeech(String text, String mapping) {
        this.text = text;
        this.mapping = mapping;
    }
    
    public static PartOfSpeech getEnum(String searchedText) {
        for (PartOfSpeech pos : values()) {
            if (pos.text.equals(searchedText)) {
                return pos;
            }
        }
        return null;
    }
    
    public static PartOfSpeech getEnumFromMapping(String searchedMapping) {
        for (PartOfSpeech pos : values()) {
            if (pos.mapping.equals(searchedMapping)) {
                return pos;
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
