package org.cswiktionary2rdf.utils;

import org.apache.jena.rdf.model.Model;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class ModelWriter {
    
    public static void write(Model model, RdfFormat format, File saveFile) {
        try (FileWriter out = new FileWriter(saveFile, false)) {
            model.write(out, format.getFormatName());
        } catch (IOException ex) {
            System.err.println("RDFModel couldn't write the RDF model into file.");
            ex.printStackTrace();
        }
    }
}
