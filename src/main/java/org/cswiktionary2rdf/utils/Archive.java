package org.cswiktionary2rdf.utils;

import java.io.*;
import java.security.MessageDigest;

public class Archive {
    private String archiveUrl;
    private String correctChecksum;
    
    Archive(String archiveUrl, String correctChecksum) {
        this.archiveUrl = archiveUrl;
        this.correctChecksum = correctChecksum;
    }
    
    String getArchiveUrl() {
        return archiveUrl;
    }
    
    String getCorrectChecksum() {
        return correctChecksum;
    }
}
