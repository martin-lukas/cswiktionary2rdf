package org.cswiktionary2rdf.modelling.misc;

import org.cswiktionary2rdf.modelling.mappings.Number;
import org.cswiktionary2rdf.utils.TextUtils;

import java.util.ArrayList;
import java.util.List;

public class NumberParser {
    public static List<Number> parseNumbers(String text) {
        List<Number> numbers = new ArrayList<>();
        
        List<String> numberStrings = new ArrayList<>();
        for (String property : TextUtils.getProperties(text)) {
            if (property.contains("číslo")) {
                numberStrings.add(property.substring(property.indexOf("číslo") + 5).trim());
            }
        }
        
        for (String numberString : numberStrings) {
            for (String singleNumber : numberString.split("(nebo| i | a |,| či )")) {
                Number number;
                // find a valid number in the string
                for (String word : singleNumber.split("\\s+")) {
                    number = Number.getEnum(word);
                    if (number != null) {
                        numbers.add(number);
                    }
                }
            }
        }
        return numbers;
    }
}
