package org.cswiktionary2rdf.modelling.mappings;

public enum     Namespace {
    BASE("ex", "http://www.example.com/"),
    CS_DBPEDIA("cs-dbpedia", "http://cs.dbpedia.org/resource/"),
    LEMON("lemon", "http://lemon-model.net/lemon#"),
    LEXINFO("lexinfo", "http://www.lexinfo.net/ontology/2.0/lexinfo#"),
    RDF("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#"),
    RDFS("rdfs", "http://www.w3.org/2000/01/rdf-schema#"),
    DBNARY("dbnary", "http://kaiko.getalp.org/dbnary#"),
    MTE("mte", "http://nl.ijs.si/ME/owl/multext-east.owl#");
    
    private String prefix;
    private String url;
    
    Namespace(String prefix, String url) {
        this.prefix = prefix;
        this.url = url;
    }
    
    public String prefix() {
        return prefix;
    }
    
    public String url() {
        return url;
    }
    
    @Override
    public String toString() {
        return url;
    }
}
