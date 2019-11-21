package org.cswiktionary2rdf.modelling;

import org.apache.jena.query.QuerySolution;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.cswiktionary2rdf.cli.tasks.ExtractTask;
import org.cswiktionary2rdf.utils.FileUtils;
import org.cswiktionary2rdf.utils.SparqlUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class ModellingTest {
    private static Model model;
    
    private static final String PREFIXES = "prefix ex: <http://www.example.com/> " +
            "prefix lemon: <http://lemon-model.net/lemon#> " +
            "prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
            "prefix wiki: <http://cs.wiktionary.org/wiki/> " +
            "prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> " +
            "prefix lexinfo: <http://www.lexinfo.net/ontology/2.0/lexinfo#> " +
            "prefix dbnary: <http://kaiko.getalp.org/dbnary#> " +
            "prefix mte: <http://nl.ijs.si/ME/owl/multext-east.owl#> ";
    
    @BeforeClass
    public static void setUpModel() {
        String dumpFilePath = FileUtils.TEST_RESOURCES_PATH + "modelling/test-pages.xml";
        String outputFilePath = FileUtils.TEST_RESOURCES_PATH + "modelling/test-output.ttl";
        
        ExtractTask extractTask = new ExtractTask("-t", dumpFilePath, outputFilePath);
        extractTask.execute();
        
        model = extractTask.getModel();
    }
    
    @AfterClass
    public static void tearDownModel() {
        model.close();
    }
    
    private static List<QuerySolution> getTestSolutions(String query) {
        return SparqlUtils.getSolutions(model, query);
    }
    
    @Test
    public void rdfsLabelTest() {
        String query = PREFIXES +
                "select ?lab\n" +
                "where {" +
                "   ex:sršeň  rdfs:label  ?lab ." +
                "}";
        List<QuerySolution> solutions = getTestSolutions(query);
        boolean found = false;
        for (QuerySolution solution : solutions) {
            if (solution.getLiteral("lab").getString().equals("sršeň")) {
                found = true;
            }
        }
        assertTrue(found);
    }
    
    @Test
    public void pronunciationTest() {
        String query = PREFIXES +
                "select ?pron\n" +
                "where {" +
                "   ?base  rdfs:label         \"čtyři\"@cs ;" +
                "          lexinfo:pronunciation    ?pron ." +
                "}";
        List<QuerySolution> solutions = getTestSolutions(query);
        assertEquals(1, solutions.size());
        for (QuerySolution solution : solutions) {
            assertEquals("t͡ʃtɪr̝ɪ", solution.getLiteral("pron").getString());
        }
    }
    
    @Test
    public void formVariantTest() {
        String query = PREFIXES +
                "select ?lab\n" +
                "where {" +
                "   ?base  rdfs:label         \"takový\"@cs ;" +
                "          lemon:formVariant  ?var ." +
                "   ?var   lexinfo:case       lexinfo:accusativeCase ;" +
                "          rdfs:label         ?lab ;" +
                "          lexinfo:number     lexinfo:singular ;" +
                "          lexinfo:gender     lexinfo:masculine ;" +
                "          lexinfo:animacy    lexinfo:inanimate" +
                "}";
        List<QuerySolution> solutions = getTestSolutions(query);
        assertEquals(1, solutions.size());
        boolean found = false;
        for (QuerySolution solution : solutions) {
            Literal l = solution.getLiteral("lab");   // Get a result variable - must be a literal
            if (l.getValue().equals("takový")) {
                found = true;
            }
        }
        assertTrue(found);
    }
    
    @Test
    public void genderTest() {
        String query = PREFIXES +
                "select ?lab " +
                "where { " +
                "    ex:sršeň_noun rdfs:label      ?lab ; " +
                "                 lexinfo:gender         lexinfo:masculine, lexinfo:feminine ;" +
                "                 lexinfo:animacy        lexinfo:animate ." +
                "}";
        List<QuerySolution> solutions = getTestSolutions(query);
        assertEquals(1, solutions.size());
    }
    
    @Test
    public void nounExtractionTest() {
        // test for sršeň
        String query = PREFIXES +
                "select ?lab ?no ?case\n" +
                "where {" +
                "   ?entry          rdfs:label       \"sršeň\"@cs ;" +
                "                  lemon:formVariant ?var ." +
                "   ?var           lexinfo:case            ?case ;" +
                "                  rdfs:label        ?lab ;" +
                "                  lexinfo:number          ?no ;" +
                "                  lexinfo:gender          lexinfo:masculine ;" +
                "                  lexinfo:animacy         lexinfo:animate ." +
                "}";
        List<QuerySolution> solutions = getTestSolutions(query);
        assertEquals(18, solutions.size());
        for (QuerySolution solution : solutions) {
            String no = solution.getResource("no").getLocalName();
            String c = solution.getResource("case").getLocalName();
            String lab = solution.getLiteral("lab").getString();
            
            boolean found = false;
            if (no.equals("singular")) {
                if (c.equals("nominativeCase")) {
                    assertEquals("sršeň", lab);
                } else if (c.equals("dativeCase")) {
                    assertTrue((lab.equals("sršni") || lab.equals("sršňovi")));
                } else if (c.equals("instrumentalCase")) {
                    assertEquals("sršněm", lab);
                }
                found = true;
            } else if (no.equals("plural")) {
                if (c.equals("nominativeCase")) {
                    assertTrue((lab.equals("sršni") || lab.equals("sršňové")));
                } else if (c.equals("locativeCase")) {
                    assertEquals("sršních", lab);
                }
                found = true;
            }
            assertTrue(found);
        }
        
        String query2 = PREFIXES +
                "select ?lab ?no ?case \n" +
                "where {" +
                "   ?entry rdfs:label         \"sršeň\"@cs ;" +
                "          lemon:formVariant  ?var ." +
                "   ?var   lexinfo:case             ?case ;" +
                "          rdfs:label         ?lab ;" +
                "          lexinfo:number           ?no ;" +
                "          lexinfo:gender           lexinfo:feminine ." +
                "    filter not exists {" +
                "        ?var lexinfo:animacy ?an ." +
                "    }" +
                "}";
        List<QuerySolution> solutions2 = getTestSolutions(query2);
        assertEquals(14, solutions2.size());
        for (QuerySolution solution : solutions2) {
            String no = solution.getResource("no").getLocalName();
            String c = solution.getResource("case").getLocalName();
            String lab = solution.getLiteral("lab").getString();
            
            boolean found = false;
            if (no.equals("singular")) {
                if (c.equals("nominativeCase")) {
                    assertEquals("sršeň", lab);
                } else if (c.equals("dativeCase")) {
                    assertEquals("sršni", lab);
                } else if (c.equals("instrumentalCase")) {
                    assertEquals("sršní", lab);
                }
                found = true;
            } else if (no.equals("plural")) {
                if (c.equals("nominativeCase")) {
                    assertEquals("sršně", lab);
                } else if (c.equals("instrumentalCase")) {
                    assertEquals("sršněmi", lab);
                }
                found = true;
            }
            assertTrue(found);
        }
    }
    
    @Test
    public void secondaryNounTest() {
        String query = PREFIXES +
                "select ?entry\n" +
                "where {" +
                "   ?base  rdfs:label         \"pes\"@cs ;" +
                "          dbnary:describes      ?entry ." +
                "   ?entry lexinfo:partOfSpeech     lexinfo:noun ." +
                "}";
        List<QuerySolution> solutions = getTestSolutions(query);
        assertEquals(2, solutions.size());
        String query2 = PREFIXES +
                "select ?entry\n" +
                "where {" +
                "   ?entry rdfs:label         \"pes\"@cs ;" +
                "          lexinfo:partOfSpeech     lexinfo:noun." +
                "   ?base  dbnary:describes      ?entry ." +
                "   filter not exists {" +
                "       ?entry   lemon:formVariant ?c." +
                "   }" +
                "}";
        List<QuerySolution> solutions2 = getTestSolutions(query2);
        assertEquals(1, solutions2.size());
    }
    
    @Test
    public void adjectiveExtractionTest() {
        // test for mladý
        String query = PREFIXES +
                "select ?lab ?no ?case ?gen ?an ?form \n" +
                "where {" +
                "    ?base    rdfs:label         \"mladý\"@cs ;" +
                "             lemon:formVariant      ?var ." +
                "    ?var     lexinfo:case             ?case ;" +
                "             lexinfo:partOfSpeech     lexinfo:adjective ;" +
                "             rdfs:label         ?lab ;" +
                "             lexinfo:number           ?no ;" +
                "             lexinfo:gender           ?gen ." +
                "    optional {?var  lexinfo:animacy   ?an }" +
                "    optional {?var  mte:hasAdjectiveFormation ?form }" +
                "}";
        List<QuerySolution> solutions = getTestSolutions(query);
        assertEquals(72, solutions.size()); // 9 cases (7 + 2 jmenné) * 8 cols (genders + number)
        for (QuerySolution solution : solutions) {
            String no = solution.getResource("no").getLocalName();
            String c = solution.getResource("case").getLocalName();
            String gen = solution.getResource("gen").getLocalName();
            String lab = solution.getLiteral("lab").getString();
            String an = (solution.contains("an"))
                    ? an = solution.getResource("an").getLocalName()
                    : null;
            String form = (solution.contains("form"))
                    ? solution.getResource("form").getLocalName()
                    : null;
            
            if (no.equals("singular")) {
                if (c.equals("nominativeCase")) {
                    if (gen.equals("masculine") && an != null && an.equals("animate") && form == null) {
                        assertEquals("mladý", lab);
                    } else if (gen.equals("neuter")) {
                        if (an != null && an.equals("")) {
                            assertEquals("mladé", lab);
                        }
                    } else if (form != null && form.equals("NominalAdjective") && gen.equals("feminine")) {
                        assertEquals("mláda", lab);
                    }
                } else if (c.equals("accusativeCase")) {
                    if (form == null && an != null) {
                        if (gen.equals("masculine")) {
                            if (an.equals("animate")) {
                                assertEquals("mladého", lab);
                            } else if (an.equals("inanimate")) {
                                assertEquals("mladý", lab);
                            }
                        }
                    } else if (form != null && form.equals("NominalAdjective") && gen.equals("feminine")) {
                        assertEquals("mládu", lab);
                    }
                } else if (c.equals("vocativeCase") && gen.equals("feminine")) {
                    assertEquals("mladá", lab);
                }
            } else if (no.equals("plural")) {
                if (c.equals("nominativeCase")) {
                    if (form == null) {
                        if (gen.equals("masculine") && an != null && an.equals("animate")) {
                            assertEquals("mladí", lab);
                        } else if (gen.equals("masculine") && an != null && an.equals("inanimate")) {
                            assertEquals("mladé", lab);
                        }
                    } else if (form.equals("NominalAdjective")) {
                        if (gen.equals("masculine") && an != null && an.equals("animate")) {
                            assertEquals("mládi", lab);
                        } else if (gen.equals("masculine") && an != null && an.equals("inanimate")) {
                            assertEquals("mlády", lab);
                        }
                    }
                } else if (c.equals("accusativeCase") && form != null && form.equals("NominalAdjective")) {
                    if (gen.equals("masculine") && an != null && an.equals("animate")) {
                        assertEquals("mlády", lab);
                    }
                } else if (c.equals("vocativeCase") && gen.equals("masculine") && an != null
                        && an.equals("animate")) {
                    assertEquals("mladí", lab);
                }
            }
        }
        
        String query2 = PREFIXES +
                "select ?lab ?deg\n" +
                "where {" +
                "    ?base    rdfs:label         \"mladý\"@cs ;" +
                "             lemon:formVariant  ?var ." +
                "    ?var     rdfs:label         ?lab ;" +
                "             lexinfo:degree           ?deg." +
                "}";
        List<QuerySolution> solutions2 = getTestSolutions(query2);
        assertEquals(3, solutions2.size());
        for (QuerySolution solution : solutions2) {
            String deg = solution.getResource("deg").getLocalName();
            String lab = solution.getLiteral("lab").getString();
            switch (deg) {
                case "positive":
                    assertEquals("mladý", lab);
                    break;
                case "comparative":
                    assertEquals("mladší", lab);
                    break;
                case "superlative":
                    assertEquals("nejmladší", lab);
                    break;
                default:
                    assert false;
                    break;
            }
        }
    }
    
    @Test
    public void pronounTinyExtractorTest() {
        // word tahle - has singular and plural, but not in the same table
        String query = PREFIXES +
                "select ?lab ?case ?no \n" +
                "where {" +
                "    ?base    rdfs:label         \"tahle\"@cs ;" +
                "             lemon:formVariant  ?var ." +
                "    ?var     lexinfo:case             ?case ;" +
                "             rdfs:label         ?lab; " +
                "             lexinfo:number           ?no" +
                "}";
        List<QuerySolution> solutions = getTestSolutions(query);
        assertEquals(12, solutions.size());
        for (QuerySolution solution : solutions) {
            String c = solution.getResource("case").getLocalName();
            String no = solution.getResource("no").getLocalName();
            String lab = solution.getLiteral("lab").getString();
            
            assertNotEquals("vocativeCase", c);
            if (no.equals("singular")) {
                if (c.equals("nominativeCase")) {
                    assertEquals("tahle", lab);
                } else if (c.equals("accusativeCase")) {
                    assertEquals("tuhle", lab);
                } else if (c.equals("instrumentalCase")) {
                    assertEquals("touhle", lab);
                }
            } else if (no.equals("plural")) {
                if (c.equals("genitiveCase")) {
                    assertEquals("těchhle", lab);
                }
            }
        }
        
        String query2 = PREFIXES +
                "select ?gen ?no\n" +
                "where {" +
                "    ?base    rdfs:label         \"tahle\"@cs ;" +
                "             lexinfo:gender           ?gen . " +
                "    filter not exists {?var lexinfo:formVariant ?base.} " +
                "}";
        List<QuerySolution> solutions2 = getTestSolutions(query2);
        assertEquals(1, solutions2.size());
        for (QuerySolution solution : solutions2) {
            String gen = solution.getResource("gen").getLocalName();
            assertEquals("feminine", gen);
        }
        
        // testing word "já"
        String query3 = PREFIXES +
                "select ?lab ?case \n" +
                "where {" +
                "    ?base    rdfs:label         \"já\"@cs ;" +
                "             lemon:formVariant  ?var ." +
                "    ?var     lexinfo:case             ?case ;" +
                "             rdfs:label         ?lab. " +
                "}";
        List<QuerySolution> solutions3 = getTestSolutions(query3);
        assertEquals(10, solutions3.size());
        for (QuerySolution solution : solutions3) {
            String c = solution.getResource("case").getLocalName();
            String lab = solution.getLiteral("lab").getString();
            
            if (c.equals("nominativeCase")) {
                assertEquals("já", lab);
            } else if (c.equals("genitiveCase")) {
                assertTrue(lab.equals("mě") || lab.equals("mne"));
            } else if (c.equals("vocativeCase")) {
                assertEquals("já", lab);
            }
        }
    }
    
    @Test
    public void pronounExtendedExtractorTest() {
        // test for takový
        String query = PREFIXES +
                "select ?lab ?no ?case ?gen ?an ?jm \n" +
                "where {" +
                "    ?base    rdfs:label         \"takový\"@cs ;" +
                "             lemon:formVariant  ?var ." +
                "    ?var     lexinfo:case             ?case ;" +
                "             lexinfo:partOfSpeech     lexinfo:pronoun ;" +
                "             rdfs:label         ?lab ;" +
                "             lexinfo:number           ?no ;" +
                "             lexinfo:gender           ?gen ." +
                "    optional {?var  lexinfo:animacy   ?an }" +
                "}";
        List<QuerySolution> solutions = getTestSolutions(query);
        assertEquals(48, solutions.size()); // 9x7 - vocative case not available (8)
        for (QuerySolution solution : solutions) {
            String no = solution.getResource("no").getLocalName();
            String c = solution.getResource("case").getLocalName();
            String gen = solution.getResource("gen").getLocalName();
            String an = null;
            if (solution.contains("an")) {
                an = solution.getResource("an").getLocalName();
            }
            String lab = solution.getLiteral("lab").getString();
            
            assertNotEquals("vocativeCase", c);
            
            if (no.equals("singular")) {
                if (c.equals("nominativeCase")) {
                    if (gen.equals("masculine") && an != null && an.equals("animate")) {
                        assertEquals("takový", lab);
                    } else if (gen.equals("neuter") && an == null) {
                        assertEquals("takové", lab);
                    }
                } else if (c.equals("accusativeCase")) {
                    if (an != null) {
                        if (gen.equals("masculine")) {
                            if (an.equals("animate")) {
                                assertEquals("takového", lab);
                            } else if (an.equals("inanimate")) {
                                assertEquals("takový", lab);
                            }
                        }
                    }
                }
            } else if (no.equals("plural")) {
                if (c.equals("nominativeCase")) {
                    if (gen.equals("masculine") && an != null) {
                        if (an.equals("animate")) {
                            assertEquals("takoví", lab);
                        } else if (an.equals("inanimate")) {
                            assertEquals("takové", lab);
                        }
                    }
                }
            }
        }
    }
    
    @Test
    public void numeralTinyExtractorTest() {
        // word čtyři
        String query = PREFIXES +
                "select ?lab ?case \n" +
                "where {" +
                "    ?base    rdfs:label         \"čtyři\"@cs ;" +
                "             lemon:formVariant      ?var ." +
                "    ?var     lexinfo:case             ?case ;" +
                "             rdfs:label         ?lab. " +
                "}";
        List<QuerySolution> solutions = getTestSolutions(query);
        assertEquals(9, solutions.size());
        for (QuerySolution solution : solutions) {
            String c = solution.getResource("case").getLocalName();
            String lab = solution.getLiteral("lab").getString();
            
            if (c.equals("nominativeCase")) {
                assertEquals("čtyři", lab);
            } else if (c.equals("genitiveCase")) {
                assertTrue(lab.equals("čtyř") || lab.equals("čtyřech"));
            } else if (c.equals("vocativeCase")) {
                assertEquals("čtyři", lab);
            }
        }
    }
    
    @Test
    public void numeralNormalExtractorTest() {
        // test for word "sto"
        String query = PREFIXES +
                "select ?lab ?no ?case\n" +
                "where {" +
                "   ?base          rdfs:label       \"sto\"@cs ;" +
                "                  lemon:formVariant ?var ." +
                "   ?var           lexinfo:case           ?case ;" +
                "                  rdfs:label       ?lab ;" +
                "                  lexinfo:number         ?no ." +
                "}";
        List<QuerySolution> solutions = getTestSolutions(query);
        assertEquals(22, solutions.size());
        for (QuerySolution solution : solutions) {
            String no = solution.getResource("no").getLocalName();
            String c = solution.getResource("case").getLocalName();
            String lab = solution.getLiteral("lab").getString();
            
            if (c.equals("nominativeCase")) {
                if (no.equals("singular")) {
                    assertEquals("sto", lab);
                } else if (no.equals("plural")) {
                    assertEquals("sta", lab);
                } else if (no.equals("dual")) {
                    assertEquals("stě", lab);
                }
            } else if (c.equals("accusativeCase")) {
                if (no.equals("singular")) {
                    assertEquals("sto", lab);
                } else if (no.equals("plural")) {
                    assertEquals("sta", lab);
                } else if (no.equals("dual")) {
                    assertTrue(lab.equals("sta") || lab.equals("stě"));
                }
            } else if (c.equals("vocativeCase")) {
                if (no.equals("dual")) {
                    assertEquals("sta", lab);
                }
            }
        }
    }
    
    @Test
    public void numeralExtendedExtractorTest() {
        // test for "kolikátý"
        String query = PREFIXES +
                "select ?lab ?no ?case ?gen ?an \n" +
                "where {" +
                "    ?base    rdfs:label         \"kolikátý\"@cs ;" +
                "             lemon:formVariant      ?var ." +
                "    ?var     lexinfo:case             ?case ;" +
                "             lexinfo:partOfSpeech     lexinfo:numeral ;" +
                "             rdfs:label         ?lab ;" +
                "             lexinfo:number           ?no ;" +
                "             lexinfo:gender           ?gen ." +
                "    optional {?var  lexinfo:animacy   ?an }" +
                "}";
        List<QuerySolution> solutions = getTestSolutions(query);
        assertEquals(56, solutions.size()); // 7 cases x 8
        for (QuerySolution solution : solutions) {
            String no = solution.getResource("no").getLocalName();
            String c = solution.getResource("case").getLocalName();
            String gen = solution.getResource("gen").getLocalName();
            String an = null;
            if (solution.contains("an")) {
                an = solution.getResource("an").getLocalName();
            }
            String lab = solution.getLiteral("lab").getString();
            
            if (no.equals("singular")) {
                if (c.equals("nominativeCase")) {
                    if (gen.equals("masculine") && an != null && an.equals("animate")) {
                        assertEquals("kolikátý", lab);
                    } else if (gen.equals("neuter")) {
                        if (an != null && an.equals("")) {
                            assertEquals("kolikáté", lab);
                        }
                    } else if (gen.equals("feminine")) {
                        assertEquals("kolikátá", lab);
                    }
                } else if (c.equals("accusativeCase")) {
                    if (an != null) {
                        if (gen.equals("masculine")) {
                            if (an.equals("animate")) {
                                assertEquals("kolikátého", lab);
                            } else if (an.equals("inanimate")) {
                                assertEquals("kolikátý", lab);
                            }
                        }
                    } else if (gen.equals("feminine")) {
                        assertEquals("kolikátou", lab);
                    }
                } else if (c.equals("vocativeCase") && gen.equals("feminine")) {
                    assertEquals("kolikátá", lab);
                }
            } else if (no.equals("plural")) {
                if (c.equals("nominativeCase")) {
                    if (gen.equals("masculine") && an != null && an.equals("animate")) {
                        assertEquals("kolikátí", lab);
                    } else if (gen.equals("masculine") && an != null && an.equals("inanimate")) {
                        assertEquals("kolikáté", lab);
                    }
                } else if (c.equals("accusativeCase")) {
                    if (gen.equals("masculine") && an != null && an.equals("animate")) {
                        assertEquals("kolikáté", lab);
                    }
                }
            }
        }
    }
    
    @Test
    public void verbMoodsTest() {
        // test for moods of word word "jíst"
        String query = PREFIXES +
                "select ?lab ?mood ?tense ?no ?per \n" +
                "where {" +
                "   ?base          rdfs:label       \"jíst\"@cs ;" +
                "                  lemon:formVariant ?var ." +
                "   ?var           rdfs:label       ?lab ;" +
                "                  lexinfo:verbFormMood   ?mood ;" +
                "                  lexinfo:number         ?no ;" +
                "                  lexinfo:person         ?per ." +
                "   optional {?var lexinfo:tense          ?tense}" +
                "}";
        List<QuerySolution> solutions = getTestSolutions(query);
        assertEquals(9, solutions.size());
        for (QuerySolution solution : solutions) {
            String mood = solution.getResource("mood").getLocalName();
            String no = solution.getResource("no").getLocalName();
            String per = solution.getResource("per").getLocalName();
            String lab = solution.getLiteral("lab").getString();
            String tense = null;
            if (solution.contains("tense")) {
                tense = solution.getResource("tense").getLocalName();
            }
            
            if (mood.equals("indicative")) {
                if (tense != null) {
                    if (tense.equals("present")) {
                        if (no.equals("singular")) {
                            if (per.equals("firstPerson")) {
                                assertEquals("jím", lab);
                            } else if (per.equals("secondPerson")) {
                                assertEquals("jíš", lab);
                            } else if (per.equals("thirdPerson")) {
                                assertEquals("jí", lab);
                            }
                        } else if (no.equals("plural")) {
                            if (per.equals("firstPerson")) {
                                assertEquals("jíme", lab);
                            } else if (per.equals("secondPerson")) {
                                assertEquals("jíte", lab);
                            } else if (per.equals("thirdPerson")) {
                                assertEquals("jedí", lab);
                            }
                        }
                    } else {
                        assert false;
                    }
                }
            } else if (mood.equals("imperative")) {
                if (no.equals("singular")) {
                    if (per.equals("secondPerson")) {
                        assertEquals("jez", lab);
                    } else {
                        assert false;
                    }
                } else if (no.equals("plural")) {
                    if (per.equals("firstPerson")) {
                        assertEquals("jezme", lab);
                    } else if (per.equals("secondPerson")) {
                        assertEquals("jezte", lab);
                    } else {
                        assert false;
                    }
                }
            } else {
                assert false;
            }
        }
    }
    
    @Test
    public void verbParticiplesTest() {
        // test for participles of word word "jít"
        String query = PREFIXES +
                "select ?lab ?voice ?no ?gen ?an \n" +
                "where {" +
                "   ?base          rdfs:label           \"jít\"@cs ;" +
                "                  lemon:formVariant    ?var ." +
                "   ?var           rdfs:label           ?lab ;" +
                "                  lexinfo:voice              ?voice ;" +
                "                  lexinfo:number             ?no ;" +
                "                  lexinfo:gender             ?gen ." +
                "   optional {?var lexinfo:animacy ?an}" +
                "}";
        List<QuerySolution> solutions = getTestSolutions(query);
        assertEquals(16, solutions.size());
        for (QuerySolution solution : solutions) {
            String voice = solution.getResource("voice").getLocalName();
            String no = solution.getResource("no").getLocalName();
            String gen = solution.getResource("gen").getLocalName();
            String lab = solution.getLiteral("lab").getString();
            String an = null;
            if (solution.contains("an")) {
                an = solution.getResource("an").getLocalName();
            }
            
            if (voice.equals("active")) {
                if (no.equals("singular")) {
                    if (gen.equals("masculine") && an != null) {
                        if (an.equals("animate")) {
                            assertEquals("šel", lab);
                        } else if (an.equals("inanimate")) {
                            assertEquals("šel", lab);
                        }
                    } else if (gen.equals("feminine")) {
                        if (an == null) {
                            assertEquals("šla", lab);
                        } else {
                            assert false;
                        }
                    }
                } else if (no.equals("plural")) {
                    if (gen.equals("masculine") && an != null) {
                        if (an.equals("animate")) {
                            assertEquals("šli", lab);
                        } else if (an.equals("inanimate")) {
                            assertEquals("šly", lab);
                        }
                    } else if (gen.equals("feminine")) {
                        if (an == null) {
                            assertEquals("šly", lab);
                        } else {
                            assert false;
                        }
                    } else if (gen.equals("neuter")) {
                        assertEquals("šla", lab);
                    }
                }
            } else if (voice.equals("passive")) {
                if (no.equals("singular")) {
                    if (gen.equals("feminine")) {
                        assertEquals("jita", lab);
                    }
                } else if (no.equals("plural")) {
                    if (gen.equals("neuter")) {
                        assertEquals("jita", lab);
                    }
                }
            } else {
                assert false;
            }
        }
    }
    
    @Test
    public void verbTransgressivesTest() {
        // test for transgressives of word word "jít"
        String query = PREFIXES +
                "select ?lab ?tense ?no ?gen ?an \n" +
                "where {" +
                "    ?base         rdfs:label                 \"jít\"@cs ;" +
                "                  lemon:formVariant          ?var ." +
                "    ?var          rdfs:label                 ?lab ;" +
                "                  mte:hasVerbForm            mte:Transgressive ;" +
                "                  lexinfo:tense              ?tense ;" +
                "                  lexinfo:number             ?no ;" +
                "                  lexinfo:gender             ?gen ." +
                "   optional {?var lexinfo:animacy ?an}" +
                "}";
        List<QuerySolution> solutions = getTestSolutions(query);
        assertEquals(16, solutions.size());
        for (QuerySolution solution : solutions) {
            String tense = solution.getResource("tense").getLocalName();
            String no = solution.getResource("no").getLocalName();
            String gen = solution.getResource("gen").getLocalName();
            String lab = solution.getLiteral("lab").getString();
            String an = null;
            if (solution.contains("an")) {
                an = solution.getResource("an").getLocalName();
            }
            
            if (tense.equals("present")) {
                if (no.equals("singular")) {
                    if (gen.equals("masculine") && an != null) {
                        if (an.equals("animate")) {
                            assertEquals("jda", lab);
                        } else if (an.equals("inanimate")) {
                            assertEquals("jda", lab);
                        }
                    } else if (gen.equals("feminine")) {
                        if (an == null) {
                            assertEquals("jdouc", lab);
                        } else {
                            assert false;
                        }
                    }
                } else if (no.equals("plural")) {
                    if (gen.equals("masculine") && an != null) {
                        if (an.equals("animate")) {
                            assertEquals("jdouce", lab);
                        } else if (an.equals("inanimate")) {
                            assertEquals("jdouce", lab);
                        }
                    } else if (gen.equals("feminine")) {
                        if (an == null) {
                            assertEquals("jdouce", lab);
                        } else {
                            assert false;
                        }
                    } else if (gen.equals("neuter")) {
                        assertEquals("jdouce", lab);
                    }
                }
            } else if (tense.equals("past")) {
                if (no.equals("singular")) {
                    if (gen.equals("feminine")) {
                        assertEquals("šedši", lab);
                    }
                } else if (no.equals("plural")) {
                    if (gen.equals("neuter")) {
                        assertEquals("šedše", lab);
                    }
                }
            } else {
                assert false;
            }
        }
    }
    
    @Test
    public void verbConditionalsTest() {
        // test for transgressives of word word "jít"
        String query = PREFIXES +
                "select ?lab ?no ?per \n" +
                "where {" +
                "    ?base         rdfs:label           \"být\"@cs ;" +
                "                  lemon:formVariant    ?var ." +
                "    ?var          rdfs:label           ?lab ;" +
                "                  lexinfo:verbFormMood       lexinfo:conditional ; " +
                "                  lexinfo:number             ?no ;" +
                "                  lexinfo:person             ?per ." +
                "}";
        List<QuerySolution> solutions = getTestSolutions(query);
        assertEquals(6, solutions.size());
        for (QuerySolution solution : solutions) {
            String no = solution.getResource("no").getLocalName();
            String per = solution.getResource("per").getLocalName();
            String lab = solution.getLiteral("lab").getString();
            
            if (no.equals("singular")) {
                if (per.equals("firstPerson")) {
                    assertEquals("bych", lab);
                } else if (per.equals("secondPerson")) {
                    assertEquals("bys", lab);
                }
            } else {
                if (per.equals("firstPerson")) {
                    assertEquals("bychom", lab);
                } else if (per.equals("secondPerson")) {
                    assertEquals("byste", lab);
                }
            }
        }
    }
    
    @Test
    public void adverbDegreesTest() {
        String query = PREFIXES +
                "select ?lab ?deg\n" +
                "where {" +
                "    ?base    rdfs:label         \"blízko\"@cs ;" +
                "             lemon:formVariant  ?var ." +
                "    ?var     rdfs:label         ?lab ;" +
                "             lexinfo:degree           ?deg." +
                "}";
        List<QuerySolution> solutions = getTestSolutions(query);
        assertEquals(5, solutions.size());
        for (QuerySolution solution : solutions) {
            String deg = solution.getResource("deg").getLocalName();
            String lab = solution.getLiteral("lab").getString();
            switch (deg) {
                case "positive":
                    assertEquals("blízko", lab);
                    break;
                case "comparative":
                    assertTrue(lab.equals("blíž") || lab.equals("blíže"));
                    break;
                case "superlative":
                    assertTrue(lab.equals("nejblíž") || lab.equals("nejblíže"));
                    break;
                default:
                    assert false;
                    break;
            }
        }
    }
    
    @Test
    public void prepositionExtractionTest() {
        String query = PREFIXES +
                "select ?pos\n" +
                "where {" +
                "    ?base    rdfs:label         \"za\"@cs ;" +
                "             lexinfo:partOfSpeech     ?pos ." +
                "}";
        List<QuerySolution> solutions = getTestSolutions(query);
        assertEquals(1, solutions.size());
        for (QuerySolution solution : solutions) {
            String pos = solution.getResource("pos").getLocalName();
            
            assertEquals("preposition", pos);
        }
    }
    
    @Test
    public void conjunctionExtractionTest() {
        String query = PREFIXES +
                "select ?pos\n" +
                "where {" +
                "    ?base    rdfs:label         \"vždyť\"@cs ;" +
                "             lexinfo:partOfSpeech     ?pos ." +
                "    filter (?pos != lexinfo:adverb)" +
                "}";
        List<QuerySolution> solutions = getTestSolutions(query);
        assertEquals(1, solutions.size());
        for (QuerySolution solution : solutions) {
            String pos = solution.getResource("pos").getLocalName();
            
            assertEquals("conjunction", pos);
        }
    }
    
    @Test
    public void particleExtractionTest() {
        String query = PREFIXES +
                "select ?pos\n" +
                "where {" +
                "    ?base    rdfs:label         \"kéž\"@cs ;" +
                "             lexinfo:partOfSpeech     ?pos ." +
                "}";
        List<QuerySolution> solutions = getTestSolutions(query);
        assertEquals(1, solutions.size());
        for (QuerySolution solution : solutions) {
            String pos = solution.getResource("pos").getLocalName();
            
            assertEquals("particle", pos);
        }
    }
    
    @Test
    public void interjectionExtractionTest() {
        String query = PREFIXES +
                "select ?pos\n" +
                "where {" +
                "    ?base    rdfs:label         \"ach\"@cs ;" +
                "             lexinfo:partOfSpeech     ?pos ." +
                "}";
        List<QuerySolution> solutions = getTestSolutions(query);
        assertEquals(1, solutions.size());
        for (QuerySolution solution : solutions) {
            String pos = solution.getResource("pos").getLocalName();
            
            assertEquals("interjection", pos);
        }
    }
}