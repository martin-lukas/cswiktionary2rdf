package org.cswiktionary2rdf.modelling.mappings;

import java.util.ArrayList;
import java.util.List;

public enum Tense {
    PAST("m", "past"),
    PRESENT("pre", "present"),
    PRESENT_2("p", "present"),
    FUTURE("fut", "future"),
    FUTURE_2("f", "future");
    
    private String text;
    private String mapping;
    
    public static final String PROPERTY = Namespace.LEXINFO + "tense";
    
    Tense(String text, String mapping) {
        this.text = text;
        this.mapping = mapping;
    }
    
    public static Tense getEnum(String searchedText) {
        for (Tense tense : values()) {
            if (tense.text.equals(searchedText)) {
                return tense;
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
    
    /**
     * Returns only the enum values with the short form text.
     *
     * @return only short form enum values
     */
    public static List<Tense> shortValues() {
        List<Tense> shortValueList = new ArrayList<>();
        for (Tense tense : values()) {
            if (tense.text.length() == 1) {
                shortValueList.add(tense);
            }
        }
        return shortValueList;
    }
}
