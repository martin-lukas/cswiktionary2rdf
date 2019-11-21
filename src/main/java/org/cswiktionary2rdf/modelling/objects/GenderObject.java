package org.cswiktionary2rdf.modelling.objects;

import org.cswiktionary2rdf.modelling.mappings.Animacy;
import org.cswiktionary2rdf.modelling.mappings.Gender;

/**
 * Represents an object containing gender (M/F/N) as well as animacy property.
 *
 * @author Martin Lukáš
 */
public class GenderObject {
    private Gender gender;
    private Animacy animacy;
   
    public GenderObject(Gender gender, Animacy animacy) {
        this.gender = gender;
        this.animacy = animacy;
    }
    
    /**
     * Constructs the object from the given code (i.e. "ma" -> masculine animate gender object).
     *
     * @param genderCode gender code containing info about gender AND animacy
     */
    public GenderObject(String genderCode) {
        Gender gender = null;
        Animacy animacy = null;
        switch (genderCode) {
            case "mi":
            case "m":
                gender = Gender.MASCULINE;
                animacy = Animacy.INANIMATE;
                break;
            case "ma":
                gender = Gender.MASCULINE;
                animacy = Animacy.ANIMATE;
                break;
            case "f":
                gender = Gender.FEMININE;
                break;
            case "n":
                gender = Gender.NEUTER;
                break;
        }
        this.gender = gender;
        this.animacy = animacy;
    }
    
    public Gender getGender() {
        return gender;
    }
   
    public Animacy getAnimacy() {
        return animacy;
    }
}
