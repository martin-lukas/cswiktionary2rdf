package org.cswiktionary2rdf.modelling.mappings;

import java.util.ArrayList;
import java.util.List;

public enum Gender {
    MASCULINE("mužský", "masculine"),
    MASCULINE_2("m", "masculine"),
    FEMININE("ženský", "feminine"),
    FEMININE_2("f", "feminine"),
    NEUTER("střední", "neuter"),
    NEUTER_2("n", "neuter");
    
    private String text;
    private String mapping;
    
    public static final String PROPERTY = Namespace.LEXINFO + "gender";
    public static final String[] GENDER_CODES = new String[]{"ma", "mi", "m", "f", "n"};
    
    Gender(String text, String mapping) {
        this.text = text;
        this.mapping = mapping;
    }
    
    public static Gender getEnum(String searchedText) {
        switch (searchedText) {
            case "m":
            case "ma":
            case "mi":
                searchedText = "mužský";
                break;
            case "f":
                searchedText = "ženský";
                break;
            case "n":
                searchedText = "střední";
                break;
        }
        
        for (Gender gender : Gender.values()) {
            if (gender.text.equals(searchedText)) {
                return gender;
            }
        }
        return null;
    }
    
    public static Gender getEnumFromMapping(String searchedMapping) {
        for (Gender gen : values()) {
            if (gen.mapping.equals(searchedMapping)) {
                return gen;
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
     * Returns all the enum instances containing the short form of code from Wiktionary (like m for masculine).
     *
     * @return short form enum instances
     */
    public static List<Gender> shortValues() {
        List<Gender> shortValueList = new ArrayList<>();
        for (Gender gender : values()) {
            if (gender.text.length() == 1) {
                shortValueList.add(gender);
            }
        }
        return shortValueList;
    }
}
