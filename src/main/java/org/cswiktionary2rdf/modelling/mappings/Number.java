package org.cswiktionary2rdf.modelling.mappings;

import java.util.ArrayList;
import java.util.List;

public enum Number {
    SINGULAR("jednotné", "singular"),
    SINGULAR_2("s", "singular"),
    PLURAL("množné", "plural"),
    PLURAL_2("p", "plural"),
    DUAL("d", "dual");
    
    private String text;
    private String mapping;
    
    public static final String PROPERTY = Namespace.LEXINFO + "number";
    
    Number(String text, String mapping) {
        this.text = text;
        this.mapping = mapping;
    }
    
    public static Number getEnum(String searchedText) {
        for (Number number : Number.values()) {
            if (number.text.equals(searchedText)) {
                return number;
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
     * Returns a list of short form enums.
     *
     * @return short form enums
     */
    public static List<Number> shortValues() {
        List<Number> shortValueList = new ArrayList<>();
        for (Number number : values()) {
            if (number.text.length() == 1) {
                shortValueList.add(number);
            }
        }
        return shortValueList;
    }
}
