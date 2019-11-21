package org.cswiktionary2rdf.modelling.mappings;

public enum Person {
    FIRST("1", "firstPerson"),
    SECOND("2", "secondPerson"),
    THIRD("3", "thirdPerson");
    
    private String text;
    private String mapping;
    
    public static final String PROPERTY = Namespace.LEXINFO + "person";
    
    Person(String text, String mapping) {
        this.text = text;
        this.mapping = mapping;
    }
    
    public static Person getEnum(String searchedText) {
        for (Person person : values()) {
            if (person.text.equals(searchedText)) {
                return person;
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
