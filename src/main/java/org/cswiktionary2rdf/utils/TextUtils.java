package org.cswiktionary2rdf.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextUtils {
    private static final String SEPARATORS_REGEX = "(,|/)";
    public static final String DECLENSION_TITLE = "skloňování";
    public static final String DEGREE_TITLE = "stupňování";
    public static final String CONJUGATION_TITLE = "časování";
    
    /**
     * Extracts table matches for the specified entry code (e.g. snom, mtram etc.). Usually the format
     * is like " | snom = variant".
     *
     * @param text      text of the parsed section
     * @param entryCode code for identifying a specific cell of the table
     * @return list of matches
     */
    public static List<String> parseEntry(String text, String entryCode) {
        // matches " | snom = pes"
        String match = TextUtils.getMatch(text, "(?<=\\| " + entryCode + " = ).+");
        if (match == null) {
            return new ArrayList<>();
        }
        
        List<String> rawVariants = Arrays.asList(cleanString(match).split(SEPARATORS_REGEX));
        List<String> checkedVariants = checkForIllegalChars(rawVariants);
        List<String> trimmedVariants = trimStrings(checkedVariants);
        return removeControlKeywords(trimmedVariants);
    }
    
    /**
     * Extracts variants from a raw table. Raw table is a table with cells unspecified by codes,
     * i.e. " | variant". E.g. word "být", specifically the conditionals table.
     *
     * @param text text of the table
     * @return all table cells
     */
    public static List<List<String>> parseRawEntry(String text) {
        // matches " | snom = pes"
        List<String> matches = TextUtils.getMatches(text, "(?<=\\|)\\S.+");
        
        List<String> cleanedStrings = cleanStrings(matches);
        List<List<String>> cellsOfVariants = new ArrayList<>();
        for (String cleanedString : cleanedStrings) {
            String[] variants = cleanedString.split(SEPARATORS_REGEX);
            cellsOfVariants.add(Arrays.asList(variants));
        }
        
        List<List<String>> trimmedCellsOfVariants = new ArrayList<>();
        for (List<String> cell : cellsOfVariants) {
            List<String> trimmedCell = trimStrings(cell);
            trimmedCellsOfVariants.add(trimmedCell);
        }
        
        List<List<String>> filteredCellsOfVariants = new ArrayList<>();
        for (List<String> trimmedCell : trimmedCellsOfVariants) {
            List<String> filteredCell = filterInvalidVariants(trimmedCell);
            if (filteredCell.size() > 0) {
                filteredCellsOfVariants.add(filteredCell);
            }
        }
        return filteredCellsOfVariants;
    }
    
    public static String getMatch(String text, String regex) {
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(text);
        if (m.find()) {
            return m.group();
        }
        return null;
    }
    
    public static List<String> getMatches(String text, String regex) {
        List<String> matches = new ArrayList<>();
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(text);
        while (m.find()) {
            matches.add(m.group());
        }
        return matches;
    }
    
    /**
     * Parses and returns a list of all properties of words from the text (content between * '' and '').
     *
     * @param text parsed text
     * @return property strings
     */
    public static List<String> getProperties(String text) {
        List<String> properties = new ArrayList<>();
        
        List<String> matches = TextUtils.getMatches(text, "\\*\\s*''(.+)(?='')");
        for (String match : matches) {
            properties.add(match.substring(match.indexOf("''") + 2).trim());
        }
        
        return properties;
    }
    
    public static String connectString(String title) {
        return String.join("_", title.split("\\s+"));
    }
    
    public static String connectStrings(String... strings) {
        return String.join("_", strings);
    }
    
    /**
     * Returns a string that matches the title regex on that level. If no title is found, returns {@code null}.
     *
     * @param section searched section
     * @param level   level of the section ("==" = 2, "===" = 3 etc.)
     * @return section title, or empty string if no title is found
     */
    public static String getSectionTitle(String section, int level) {
        String levelEquals = "=".repeat(level);
        String titleRegex = "(?<=" + levelEquals + "\\s)[^=\\(]+?(?=\\s*(\\(\\d\\))?\\s*" + levelEquals + "\\n)";
        String title = TextUtils.getMatch(section, titleRegex);
        return (title != null) ? title.trim() : "";
    }
    
    /**
     * Splits text into sections based on the level provided. Level 2 means separate the text based on
     * titles surrounded by "==".
     *
     * @param text  text to be split
     * @param level level of splitting
     * @return sections split at the given level
     */
    public static String[] splitSections(String text, int level) {
        String levelEquals = "=".repeat(level);
        return text.split("(?=" + levelEquals + "[^=]+?" + levelEquals + "\\n)");
    }
    
    
    public static String removeLinks(String text) {
        return text.replaceAll("(\\[\\[|\\]\\])", "");
    }
    
    
    private static String cleanString(String str) {
        // removes references to sources around words (e.g. [1], which is actually <ref>something</ref>)
        if (str.contains("<")) str = removeTags(str);
        // removes templates marked by being surrounded with {{ }}
        if (str.contains("{{")) str = removeTemplates(str);
        // removes notes around words contained in '' ''
        if (str.contains("''")) str = removeQuoteNotes(str);
        // removes details after words, e.g. (zřídka), (hovorově)...
        if (str.contains("(")) str = removeRoundNotes(str);
        
        str = str.replaceAll("[—\\*–\\]\\)\\}]", "");
        
        return str;
    }
    
    private static List<String> cleanStrings(List<String> strings) {
        List<String> cleanedStrings = new ArrayList<>();
        for (String str : strings) {
            cleanedStrings.add(cleanString(str));
        }
        return cleanedStrings;
    }
    
    private static List<String> removeControlKeywords(List<String> variants) {
        List<String> validVariants = new ArrayList<>();
        for (String variant : variants) {
            if (!(variant.equals("skrýt") || variant.equals("zobrazit"))) {
                validVariants.add(variant);
            }
        }
        return validVariants;
    }
    
    private static List<String> checkForIllegalChars(List<String> variants) {
        List<String> legalVariants = new ArrayList<>();
        for (String variant : variants) {
            String legalVariant = (variant.contains("|")) // for the variants containing something like word#čeština|word
                    ? variant.substring(variant.indexOf("|") + 1)
                    : variant;
            legalVariants.add(legalVariant);
        }
        return legalVariants;
    }
    
    private static List<String> filterInvalidVariants(List<String> variants) {
        List<String> filteredVariants = new ArrayList<>();
        for (String variant : variants) {
            if (!variant.startsWith("-")) { // for the variants described as e.g. -i (gymnasté, -i)
                filteredVariants.add(variant);
            }
        }
        return filteredVariants;
    }
    
    private static List<String> trimStrings(List<String> untrimmedStrings) {
        List<String> trimmedStrings = new ArrayList<>();
        for (String str : untrimmedStrings) {
            String trimmedStr = str.trim();
            trimmedStrings.add(trimmedStr);
        }
        return trimmedStrings;
    }
    
    private static String removeTags(String str) {
        do {
            int start = str.indexOf("<");
            int end = 0;
            int shortElement = str.indexOf("/>");
            
            if (shortElement == -1) {
                int secondOpeningBracket = str.indexOf("</", start + 1);
                if (secondOpeningBracket == -1) {
                    str = str.substring(0, start);
                } else {
                    end = str.indexOf(">", secondOpeningBracket);
                }
            } else {
                end = shortElement + 1;
            }
            if (end != 0) {
                str = removeSubstring(str, start, end);
            }
        } while (str.contains("<"));
        return str;
    }
    
    private static String removeTemplates(String str) {
        do {
            int start = str.indexOf("{{");
            int end = str.indexOf("}}") + 1;
            str = removeSubstring(str, start, end);
        } while (str.contains("{{"));
        return str;
    }
    
    private static String removeQuoteNotes(String str) {
        do {
            int start = str.indexOf("''");
            int end = str.indexOf("''") + 1;
            str = removeSubstring(str, start, end);
        } while (str.contains("''"));
        return str;
    }
    
    private static String removeRoundNotes(String str) {
        do {
            int start = str.indexOf("(");
            int end = str.indexOf(")");
            str = removeSubstring(str, start, end);
        } while (str.contains("("));
        return str;
    }
    
    private static String removeSubstring(String str, int startIndex, int endIndex) {
        String partBefore = str.substring(0, startIndex);
        String partAfter = str.substring(endIndex + 1);
        return partBefore + partAfter;
    }
}
