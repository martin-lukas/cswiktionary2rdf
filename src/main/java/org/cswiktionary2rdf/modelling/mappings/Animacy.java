package org.cswiktionary2rdf.modelling.mappings;

public enum Animacy {
    ANIMATE("životný", "animate"),
    INANIMATE("neživotný", "inanimate");
    
    private String text;
    private String mapping;
    
    public static final String PROPERTY = Namespace.LEXINFO + "animacy";
    
    Animacy(String text, String mapping) {
        this.text = text;
        this.mapping = mapping;
    }
    
    public static Animacy getEnum(String searchedText) {
        switch (searchedText) {
            case "mi":
                searchedText = "neživotný";
                break;
            case "ma":
                searchedText = "životný";
                break;
                
        }
        
        for (Animacy animacy : Animacy.values()) {
            if (animacy.text.equals(searchedText)) {
                return animacy;
            }
        }
        return null;
    }
    
    public static Animacy getEnumFromMapping(String searchedMapping) {
        for (Animacy an : values()) {
            if (an.mapping.equals(searchedMapping)) {
                return an;
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
