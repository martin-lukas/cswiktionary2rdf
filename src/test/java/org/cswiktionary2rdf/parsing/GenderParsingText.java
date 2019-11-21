package org.cswiktionary2rdf.parsing;

import org.cswiktionary2rdf.modelling.misc.GenderParser;
import org.cswiktionary2rdf.modelling.objects.GenderObject;
import org.cswiktionary2rdf.modelling.mappings.Animacy;
import org.cswiktionary2rdf.modelling.mappings.Gender;
import org.cswiktionary2rdf.utils.FileUtils;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class GenderParsingText {
    @Test
    public void parseGendersTest() throws IOException {
        String text = FileUtils.readFile(FileUtils.TEST_RESOURCES_PATH + "parsing/genders.txt");
        List<GenderObject> genders = GenderParser.parseGenders(text);
        assertEquals(11, genders.size());
        int mascAn = 0;
        int mascInan = 0;
        int fem = 0;
        int neu = 0;
        for (GenderObject genderObject : genders) {
            Gender gender = genderObject.getGender();
            Animacy animacy = genderObject.getAnimacy();
            switch (gender) {
                case MASCULINE:
                    if (animacy == Animacy.ANIMATE) {
                        mascAn++;
                        break;
                    } else if (animacy == Animacy.INANIMATE) {
                        mascInan++;
                        break;
                    }
                    break;
                case FEMININE:
                    fem++;
                    break;
                case NEUTER:
                    neu++;
                    break;
            }
        }
        assertEquals(3, mascAn);
        assertEquals(2, mascInan);
        assertEquals(3, fem);
        assertEquals(3, neu);
    }
}
