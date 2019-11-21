package org.cswiktionary2rdf.modelling.objects;

import org.cswiktionary2rdf.utils.Page;
import org.cswiktionary2rdf.modelling.mappings.PartOfSpeech;

public class POSObject {
    private Page page;
    private String text;
    private PartOfSpeech pos;
    
    public POSObject(Page page, String text, PartOfSpeech pos) {
        this.page = page;
        this.text = text;
        this.pos = pos;
    }
    
    public Page getPage() {
        return page;
    }
    
    public String getText() {
        return text;
    }
    
    public PartOfSpeech getPOS() {
        return pos;
    }
}
