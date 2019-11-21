package org.cswiktionary2rdf.cli.tasks;

public class HelpTask implements Task {
    @Override
    public void execute() {
        System.out.println("RDF-Wiktionary project enables extraction from unstructured data into" +
                "a structured RDF format.\n\n" +
                "Available formats of RDF output are: RDF/XML, Turtle and n-triples.\n" +
                "You can choose the format by typing <-x>/<-t>/<-n> respectively into the format argument.\n" +
                "There are several possible scenarion for running this program. They always start with\n" +
                "\"cswiktionary2rdf.jar -jar\"\n\n" +
                "1. Download the dump file from the internet into default download directory:\n" +
                "   -d\n" +
                "2. Download the dump file into a specified directory:\n" +
                "   -d <dir path>\n" +
                "3. Extract a specified dump file into a specified output file:\n" +
                "   -e <format> <dump file> <output file>\n\n" +
                "Example of the execution:\n" +
                "cswiktionary2rdf.jar -jar -e -t dump.xml output.ttl\n\n" +
                "For viewing this help again, type in \"-h\".");
    }
}
