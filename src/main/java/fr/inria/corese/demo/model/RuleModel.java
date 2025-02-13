package fr.inria.corese.demo.model;

import fr.inria.corese.core.Graph;
import fr.inria.corese.core.load.RuleLoad;
import fr.inria.corese.core.rule.RuleEngine;

import java.io.File;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class RuleModel {
    private RuleEngine ruleEngine;
    private Graph graph;
    private List<String> loadedRules;
    private boolean isRDFSSubsetEnabled;
    private boolean isRDFSRLEnabled;
    private boolean isOWLRLEnabled;
    private boolean isOWLRLExtendedEnabled;
    private boolean isOWLRLTestEnabled;
    private boolean isOWLCleanEnabled;
    private boolean isTraceEnabled;
    private boolean isLoadNamedEnabled;
    private boolean isGraphIndexEnabled;

    public RuleModel() {
        this.graph = Graph.create();
        this.ruleEngine = new RuleEngine();
        this.loadedRules = new ArrayList<>();
    }

    public boolean isRDFSSubsetEnabled() { return isRDFSSubsetEnabled; }
    public boolean isRDFSRLEnabled() { return isRDFSRLEnabled; }
    public boolean isOWLRLEnabled() { return isOWLRLEnabled; }
    public boolean isOWLRLExtendedEnabled() { return isOWLRLExtendedEnabled; }
    public boolean isOWLRLTestEnabled() { return isOWLRLTestEnabled; }
    public boolean isOWLCleanEnabled() { return isOWLCleanEnabled; }
    public boolean isTraceEnabled() { return isTraceEnabled; }
    public boolean isLoadNamedEnabled() { return isLoadNamedEnabled; }
    public boolean isGraphIndexEnabled() { return isGraphIndexEnabled; }

    public void loadRDFSSubset() {
        if (isRDFSSubsetEnabled) {
            try {
                RuleEngine ruleEngine = RuleEngine.create(graph);
                RuleLoad ruleLoader = RuleLoad.create(ruleEngine);
                ruleLoader.parse("rdfsubset.rul");

                // Apply rules on graph
                ruleEngine.process();
            } catch (Exception e) {
                System.err.println("Error loading RDFS Subset: " + e.getMessage());
            }
        } else {
            loadedRules.remove("RDFS Subset");
            reloadRules();
        }
    }

    public void loadRDFSRL() {
        if (isRDFSRLEnabled) {
            try {
                RuleEngine ruleEngine = RuleEngine.create(graph);
                RuleLoad ruleLoader = RuleLoad.create(ruleEngine);
                ruleLoader.parse("rdfsrl.rul");

                // Apply rules on graph
                ruleEngine.process();
            } catch (Exception e) {
                System.err.println("Error loading RDFS RL: " + e.getMessage());
            }
        } else {
            loadedRules.remove("RDFS RL");
            reloadRules();
        }
    }

    public void loadOWLRL() {
        if (isOWLRLEnabled) {
            try {
                RuleEngine engine = RuleEngine.create(graph);
                engine.setProfile(RuleEngine.OWL_RL);
                engine.process();
            } catch (Exception e) {
                System.err.println("Error loading OWL RL: " + e.getMessage());
            }
        } else {
            loadedRules.remove("OWL RL");
            reloadRules();
        }
    }

    public void loadOWLRLExtended() {
        if (isOWLRLExtendedEnabled) {
            try {
                RuleEngine engine = RuleEngine.create(graph);
                engine.setProfile(RuleEngine.OWL_RL_EXT);
                engine.process();
            } catch (Exception e) {
                System.err.println("Error loading OWL RL Extended: " + e.getMessage());
            }
        } else {
            loadedRules.remove("OWL RL Extended");
            reloadRules();
        }
    }

    public void loadOWLRLTest() {
        if (isOWLRLTestEnabled) {
            try {
                RuleEngine engine = RuleEngine.create(graph);
                engine.setProfile(RuleEngine.OWL_RL_TEST);
                engine.process();
            } catch (Exception e) {
                System.err.println("Error loading OWL RL Test: " + e.getMessage());
            }
        } else {
            loadedRules.remove("OWL RL Test");
            reloadRules();
        }
    }

    public void loadOWLClean(){
        if (isOWLCleanEnabled) {
            try {
                RuleEngine engine = RuleEngine.create(graph);
                engine.setProfile(String.valueOf(RuleEngine.OWL_CLEAN));
                engine.process();
            } catch (Exception e) {
                System.err.println("Error loading OWL Clean: " + e.getMessage());
            }
        } else {
            loadedRules.remove("OWL Clean");
            reloadRules();
        }
    }

    private void reloadRules() {
        graph = Graph.create();
        ruleEngine = new RuleEngine();
        if (isRDFSSubsetEnabled) loadRDFSSubset();
        if (isRDFSRLEnabled) loadRDFSRL();
        if (isOWLRLEnabled) loadOWLRL();
        if (isOWLRLExtendedEnabled) loadOWLRLExtended();
        if (isOWLRLTestEnabled) loadOWLRLTest();
        if (isOWLCleanEnabled) loadOWLClean();
    }

    public List<String> getLoadedRules() {
        return new ArrayList<>(loadedRules);
    }

    public void setRDFSSubsetEnabled(boolean enabled) {
        this.isRDFSSubsetEnabled = enabled;
        loadRDFSSubset();
    }

    public void setRDFSRLEnabled(boolean enabled) {
        this.isRDFSRLEnabled = enabled;
        loadRDFSRL();
    }

    public void setOWLRLEnabled(boolean enabled) {
        this.isOWLRLEnabled = enabled;
        loadOWLRL();
    }

    public void setOWLRLExtendedEnabled(boolean enabled) {
        this.isOWLRLExtendedEnabled = enabled;
        loadOWLRLExtended();
    }

    public void setOWLRLTestEnabled(boolean enabled) {
        this.isOWLRLTestEnabled = enabled;
        loadOWLRLTest();
    }

    public void setOWLCleanEnabled(boolean enabled) {
        this.isOWLCleanEnabled = enabled;
        loadOWLClean();
    }

    public void setTraceEnabled(boolean enabled) {
        this.isTraceEnabled = enabled;
        if (enabled) {
            // Activer le mode trace dans le RuleEngine
            ruleEngine.setDebug(true);  // Utilise setDebug au lieu d'une méthode inexistante
            loadedRules.add("Trace");
        } else {
            ruleEngine.setDebug(false);
            loadedRules.remove("Trace");
        }
    }

    public void setLoadNamedEnabled(boolean enabled) {
        this.isLoadNamedEnabled = enabled;
        if (enabled) {
            // Pour Load Named, nous devons plutôt utiliser la propriété lors du chargement des règles
            loadedRules.add("Load Named");
        } else {
            loadedRules.remove("Load Named");
        }
        reloadRules(); // Recharger les règles pour appliquer le changement
    }

    public void setGraphIndexEnabled(boolean enabled) {
        this.isGraphIndexEnabled = enabled;
        if (enabled) {
            // Pour Graph Index, on peut utiliser les propriétés du graphe différemment
            graph.init(); // Réinitialise et reconstruit les index
            loadedRules.add("Graph index");
        } else {
            loadedRules.remove("Graph index");
        }
    }

    public RuleEngine duplicateRule(String ruleName) {
        // TODO: Implement the logic to duplicate a rule
        // This will depend on how rules are stored and managed in corese-core
        return new RuleEngine();
    }

    public void loadRuleFile(File file) {
        try {
            // Si le mode trace est activé
            if (isTraceEnabled) {
                ruleEngine.setDebug(true);
            }

            RuleLoad ruleLoader = RuleLoad.create(ruleEngine);

            // Si Load Named est activé, configurer les options appropriées
            if (isLoadNamedEnabled) {
                // Configuration spécifique pour Load Named
                // Par exemple, charger les graphes nommés si nécessaire
            }

            // Si Graph Index est activé
            if (isGraphIndexEnabled) {
                graph.init();
            }

            ruleLoader.parse(file.getAbsolutePath());
            String ruleName = file.getName();
            if (!loadedRules.contains(ruleName)) {
                loadedRules.add(ruleName);
            }

            ruleEngine.process();
        } catch (Exception e) {
            System.err.println("Error loading rule file: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to load rule file: " + e.getMessage());
        }
    }

    public List<String> getActiveRules() {
        return loadedRules.stream()
                .filter(rule -> !isPredefinedRule(rule))
                .collect(Collectors.toList());
    }

    private boolean isPredefinedRule(String ruleName) {
        return ruleName.equals("Trace") ||
                ruleName.equals("Load Named") ||
                ruleName.equals("Graph index");
    }
}