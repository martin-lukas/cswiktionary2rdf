package org.cswiktionary2rdf.cli.tasks;

import org.apache.jena.rdf.model.Model;
import org.cswiktionary2rdf.modelling.ModelBuilder;
import org.cswiktionary2rdf.utils.ModelWriter;
import org.cswiktionary2rdf.utils.Page;
import org.cswiktionary2rdf.utils.Parser;
import org.cswiktionary2rdf.utils.RdfFormat;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.IllegalFormatException;
import java.util.List;

public class ExtractTask implements Task {
    private RdfFormat format;
    private File dumpFile;
    private File saveFile;
    
    private Model model;
    
    public ExtractTask(String formatString, String dumpPath, String outputPath) {
        try {
            this.format = getFormat(formatString);
            this.dumpFile = getDumpFile(dumpPath);
            this.saveFile = getSaveFile(outputPath);
        } catch (IllegalArgumentException ex) {
            System.err.println("The specified RDF format isn't available.\n");
            new HelpTask().execute();
        } catch (IOException ex) {
            System.err.println("Check that all the provided file paths are correct.\n");
            new HelpTask().execute();
        }
    }
    
    @Override
    public void execute() {
        System.out.println("Parsing pages...");
        
        List<Page> pages = null;
        try {
            pages = Parser.parsePages(dumpFile);
        } catch (IOException ex) {
            System.err.println("Couldn't parse file " + dumpFile.getAbsolutePath());
            ex.printStackTrace();
        } catch (SAXException ex) {
            System.err.println("There is something wrong with the provided dump file.");
            ex.printStackTrace();
        } catch (ParserConfigurationException ex) {
            System.err.println("There was a problem in the parser configuration");
            ex.printStackTrace();
        }
    
        if (pages != null) {
            System.out.println("Finished. Parsed pages: " + pages.size());

            System.out.println("Building RDF model...");
            ModelBuilder modelBuilder = new ModelBuilder(pages);
            modelBuilder.buildModel();
            System.out.println("Model finished.");

            System.out.println("Saving model into file...");
            ModelWriter.write(modelBuilder.getModel(), format, saveFile);
            System.out.println("Saving finished.");
            System.out.println();

            this.model = modelBuilder.getModel(); // for testing purposes
        }
    }
    
    /**
     * For testing purposes.
     */
    public Model getModel() {
        return model;
    }
    
    private RdfFormat getFormat(String argument) throws IllegalFormatException {
        RdfFormat format = null;
        switch (argument) {
            case "-x":
                format = RdfFormat.RDF_XML;
                break;
            case "-t":
                format = RdfFormat.TURTLE;
                break;
            case "-n":
                format = RdfFormat.N_TRIPLES;
                break;
        }
        
        if (format == null) {
            throw new IllegalArgumentException();
        }
        
        return format;
    }
    
    private File getDumpFile(String arg) throws IOException {
        File dumpFile = new File(arg);
        if (!dumpFile.exists()) {
            throw new FileNotFoundException();
        }
        return dumpFile;
    }
    
    private File getSaveFile(String arg) throws IOException {
        File saveFile = new File(arg);
        if (!saveFile.exists()) {
            boolean succeeded = saveFile.createNewFile();
            if (!succeeded) {
                System.err.println("Couldn't create output file.");
            }
        }
        
        if (!saveFile.exists()) {
            throw new FileNotFoundException();
        }
        
        return saveFile;
    }
}
