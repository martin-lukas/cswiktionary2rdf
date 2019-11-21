package org.cswiktionary2rdf.utils;

public enum RdfFormat {
    RDF_XML("rdf/xml"),
    TURTLE("turtle"),
    N_TRIPLES("n-triples");
    
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
