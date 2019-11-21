package org.cswiktionary2rdf.modelling.mappings;

import java.util.ArrayList;
import java.util.List;

public enum Case {
    NOMINATIVE("nom", "nominative"),
    GENITIVE("gen", "genitive"),
    DATIVE("dat", "dative"),
    ACCUSATIVE("acc", "accusative"),
    VOCATIVE("voc", "vocative"),
    LOCATIVE("loc", "locative"),
    INSTRUMENTAL("ins", "instrumental");
    
    private final String text;
    private final String mapping;
    
    public static final String PROPERTY = Namespace.LEXINFO + "case";
    
    Case(String text, String mapping) {
        this.text = text;
        this.mapping = mapping;
    }
    
    public static Case getEnum(String searchedText) {
        for (Case caseEnum : values()) {
            if (caseEnum.text.equals(searchedText)) {
                return caseEnum;
            }
        }
        return null;
    }
    
    public static List<String> getCaseMappings() {
        List<String> mappings = new ArrayList<>();
        for (Case value : values()) {
            mappings.add(value.mapping);
        }
        return mappings;
    }
    
    public String getText() {
        return text;
    }
    
    public String getLocalMapping() {
        return mapping;
    }
    
    public String getMapping() {
        return Namespace.LEXINFO + mapping + "Case";
    }
}
