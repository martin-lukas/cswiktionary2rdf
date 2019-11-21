package org.cswiktionary2rdf.modelling.misc;

import org.cswiktionary2rdf.modelling.mappings.Animacy;
import org.cswiktionary2rdf.modelling.mappings.Gender;
import org.cswiktionary2rdf.modelling.objects.GenderObject;
import org.cswiktionary2rdf.utils.TextUtils;

import java.util.ArrayList;
import java.util.List;

public class GenderParser {
    
    public static List<GenderObject> parseGenders(String text) {
        List<GenderObject> genders = new ArrayList<>();
        genders.addAll(parseStringGenders(text));
        genders.addAll(parseTemplateGenders(text));
        return genders;
    }
    
    public static boolean containsGender(GenderObject[] genderObjects, Gender gender) {
        for (GenderObject genderObject : genderObjects) {
            if (genderObject.getGender() == gender) {
                return true;
            }
        }
        return false;
    }
    
    
    private static List<GenderObject> parseStringGenders(String text) {
        List<GenderObject> genderObjects = new ArrayList<>();
        
        for (String genderString : getGenderStrings(text)) {
            for (String singleGender : genderString.split("(nebo| i | a |,)")) {
                Gender foundGender = null;
                Animacy foundAnimacy = null;
                
                String[] words = singleGender.split("\\s+");
                
                for (String word : words) {
                    foundGender = Gender.getEnum(word);
                    if (foundGender != null) {
                        break;
                    }
                }
                
                for (String word : words) {
                    foundAnimacy = Animacy.getEnum(word);
                    if (foundAnimacy != null) {
                        break;
                    }
                }
                
                if (foundGender != null) {
                    genderObjects.add(new GenderObject(foundGender, foundAnimacy));
                }
            }
        }
        
        return genderObjects;
    }
    
    private static List<GenderObject> parseTemplateGenders(String text) {
        List<GenderObject> genderObjects = new ArrayList<>();
        
        List<String> matches = TextUtils.getMatches(text, "(?<=\\{\\{Rod\\|)[^\\}]+(?=\\}\\})");
        for (String match : matches) {
            Gender gender = Gender.getEnum(match);
            if (gender != null) {
                Animacy animacy = Animacy.getEnum(match);
                // if masculine and no animacy - invalid
                if (!(gender == Gender.MASCULINE && animacy == null)) {
                    genderObjects.add(new GenderObject(gender, animacy));
                }
            }
        }
        return genderObjects;
    }
    
    private static List<String> getGenderStrings(String text) {
        List<String> genderStrings = new ArrayList<>();
        for (String property : TextUtils.getProperties(text)) {
            if (property.contains("rod")) {
                genderStrings.add(property.substring(property.indexOf("rod") + 3).trim());
            }
        }
        return genderStrings;
    }
}
