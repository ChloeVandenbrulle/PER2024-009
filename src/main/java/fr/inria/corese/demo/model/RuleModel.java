package fr.inria.corese.demo.model;

import fr.inria.corese.core.Graph;
import fr.inria.corese.core.load.RuleLoad;
import fr.inria.corese.core.rule.RuleEngine;
import java.util.List;
import java.util.ArrayList;

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

    public RuleModel() {
        this.graph = Graph.create();
        this.ruleEngine = new RuleEngine();
        this.loadedRules = new ArrayList<>();
    }

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

    public RuleEngine duplicateRule(String ruleName) {
        // TODO: Implement the logic to duplicate a rule
        // This will depend on how rules are stored and managed in corese-core
        return new RuleEngine();
    }

}