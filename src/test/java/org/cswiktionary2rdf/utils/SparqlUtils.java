package org.cswiktionary2rdf.utils;

import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Model;

import java.util.ArrayList;
import java.util.List;

public class SparqlUtils {
    
    public static List<QuerySolution> getSolutions(Model model, String queryString) {
        List<QuerySolution> solutions = new ArrayList<>();
        Query query = QueryFactory.create(queryString);
        try (QueryExecution qexec = QueryExecutionFactory.create(query, model)) {
            ResultSet results = qexec.execSelect();
            while (results.hasNext()) {
                solutions.add(results.nextSolution());
            }
        }
        return solutions;
    }
}
