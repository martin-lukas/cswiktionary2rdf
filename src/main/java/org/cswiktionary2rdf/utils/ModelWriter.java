package org.cswiktionary2rdf.utils;

import org.apache.jena.rdf.model.Model;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class ModelWriter {
    
    public static void write(Model model, RdfFormat format, File saveFile) {
        try (FileWriter out = new FileWriter(saveFile,StandardCharsets.UTF_8, false)) {
            model.write(out, format.getFormatName());
        } catch (IOException ex) {
            System.err.println("RDFModel couldn't write the RDF model into file.");
            ex.printStackTrace();
        }
    }
}
