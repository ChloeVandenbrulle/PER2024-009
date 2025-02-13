package fr.inria.corese.demo.model;

import fr.inria.corese.core.Graph;
import fr.inria.corese.core.load.RuleLoad;
import fr.inria.corese.core.rule.Rule;
import fr.inria.corese.core.rule.RuleEngine;
import fr.inria.corese.core.load.Load;

import java.io.File;
import java.time.LocalDateTime;
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
                Graph dataGraph = Graph.create();
                RuleEngine ruleEngine = RuleEngine.create(dataGraph);
                RuleLoad ruleLoader = RuleLoad.create(ruleEngine);
                ruleLoader.parse("input rules file path.rul");

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
                Load.create(graph).loadWE("rule/rdfsrl.rul");
                if (!loadedRules.contains("RDFS RL")) {
                    loadedRules.add("RDFS RL");
                }
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
                Load.create(graph).loadWE("rule/owlrl.rul");
                if (!loadedRules.contains("OWL RL")) {
                    loadedRules.add("OWL RL");
                }
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
                Load.create(graph).loadWE("rule/owlrl-ext.rul");
                if (!loadedRules.contains("OWL RL Extended")) {
                    loadedRules.add("OWL RL Extended");
                }
            } catch (Exception e) {
                System.err.println("Error loading OWL RL Extended: " + e.getMessage());
            }
        } else {
            loadedRules.remove("OWL RL Extended");
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
    }

    public void loadRuleFile(File file) {
        try {
            Load.create(graph).load(file.getPath());
            String ruleName = file.getName();
            if (!loadedRules.contains(ruleName)) {
                loadedRules.add(ruleName);
            }
        } catch (Exception e) {
            System.err.println("Error loading rule file: " + e.getMessage());
        }
    }

    public List<String> getLoadedRules() {
        return new ArrayList<>(loadedRules);
    }

    public RuleEngine getRuleEngine() {
        return ruleEngine;
    }

    public Graph getGraph() {
        return graph;
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
    }

    public void setOWLCleanEnabled(boolean enabled) {
        this.isOWLCleanEnabled = enabled;
    }

    public void clearRules() {
        loadedRules.clear();
        graph = Graph.create();
        ruleEngine = new RuleEngine();
    }

    public void setRDFSRLChecked(boolean selected) {
        this.isRDFSRLEnabled = selected;
    }

    public void setOWLRLChecked(boolean selected) {
        this.isOWLRLEnabled = selected;
    }

    public void setOWLRLExtendedChecked(boolean selected) {
        this.isOWLRLExtendedEnabled = selected;
    }

    public RuleEngine duplicateRule(String ruleName) {
        // TODO: Implement the logic to duplicate a rule
        // This will depend on how rules are stored and managed in corese-core
        return new RuleEngine();
    }

}