package org.cswiktionary2rdf.utils;

public enum RdfFormat {
    RDF_XML("RDF/XML"),
    TURTLE("TURTLE"),
    N_TRIPLES("N-TRIPLES");
    
    private String formatName;
    
    RdfFormat(String formatName) {
        this.formatName = formatName;
    }
    
    public static RdfFormat getRDFFormat(String searchedText) {
        for (RdfFormat format : values()) {
            if (format.formatName.equals(searchedText)) {
                return format;
            }
        }
        return null;
    }
    
    public String getFormatName() {
        return formatName;
    }
}
