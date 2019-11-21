# cswiktionary2rdf - Czech Wiktionary Extractor
This program extracts information from the Czech Wiktionary project into 
a machine-readable format RDF. For now it focuses on inflected forms of words.

Information being extracted:

* parts of speech (POS)
* pronunciation
* gender and animacy
* number
* inflected forms

Inflected form structure greatly differs based on the POS
of the word. For example noun inflected forms are differentiated based on
number and case. In rare cases (e.g. sršeň), differentiating with gender is necessary too.
Here are common patterns in inflected forms for different POSes:

* nouns: number and case
* adjective: number, gender + animacy and case, (degree)
* pronoun: (number), (gender + animacy), case
* numeral: (number), (gender + animacy), case
* verb: number, (person), (gender + animacy), mood, voice, case etc.
* adverb: degree

This is only a rough overview. The elements in paretheses appear only in certain groups of words.

## Using the program
RDF-Wiktionary project enables extraction from unstructured data into a structured RDF format.

Available formats of RDF output are: RDF/XML, Turtle and n-triples.

You can choose the format by typing \<-x\>/\<-t\>/\<-n\> respectively into the format argument.

There are several possible scenarion for running this program. 

They always start with "cswiktionary2rdf.jar -jar"
1. Download the dump file from the internet into default download directory:
   - -d

2. Download the dump file into a specified directory:
   - -d \<dir path\>

3. Extract a specified dump file into a specified output file:
   - -e \<format\> \<dump file\> \<output file\>

Example of the execution:

* cswiktionary2rdf.jar -jar -e -t dump.xml output.ttl

For viewing this help again, type in "-h".

## Running the Fuseki server

I attached Fuseki server to the program for easily testing the resulting dataset.
You can launch it by running the "fuseki-server.bat" batch file. The administration
interface is accessible through URL http://localhost:3030/. There you go to "Manage datasets",
create a new dataset (e.g. /wiki), and upload the output file from the extractor.

After the upload finishes, the SPARQL endpoint of the file is ready (e.g. http://localhost:3030/wiki/sparql).

## Example applications

I also created a demo program "A search engine of word forms for the Czech Wiktionary", which you can find at

https://martin-lukas.github.io/cswiktionary2rdf-tools/search.html

It lists all the pages where the desired word form appears, and if it appeared in one of the tables of inflected forms, 
it also gives a description of the form.

I also created a similar style SPARQL endpoint for convenience, which you can find at

https://martin-lukas.github.io/cswiktionary2rdf-tools/endpoint.html

Source code for the above tools:

https://github.com/martin-lukas/cswiktionary2rdf-tools


