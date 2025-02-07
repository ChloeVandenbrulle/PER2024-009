package fr.inria.corese.demo.model;

import fr.inria.corese.core.Graph;
import fr.inria.corese.core.rule.RuleEngine;
import fr.inria.corese.core.load.Load;
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
            Load.create(graph).load(String.valueOf(Load.RULE_FORMAT));
            if (!loadedRules.contains("RDFS Subset")) {
                loadedRules.add("RDFS Subset");
            }
        } else {
            loadedRules.remove("RDFS Subset");
            reloadRules();
        }
    }

    public void loadRDFSRL() {
        if (isRDFSRLEnabled) {
            Load.create(graph).load(String.valueOf(Load.RULE_FORMAT));
            if (!loadedRules.contains("RDFS RL")) {
                loadedRules.add("RDFS RL");
            }
        } else {
            loadedRules.remove("RDFS RL");
            reloadRules();
        }
    }

    public void loadOWLRL() {
        if (isOWLRLEnabled) {
            Load.create(graph).load(String.valueOf(Load.RULE_FORMAT));
            if (!loadedRules.contains("OWL RL")) {
                loadedRules.add("OWL RL");
            }
        } else {
            loadedRules.remove("OWL RL");
            reloadRules();
        }
    }

    public void loadOWLRLExtended() {
        if (isOWLRLExtendedEnabled) {
            Load.create(graph).load(String.valueOf(Load.RULE_FORMAT));
            if (!loadedRules.contains("OWL RL Extended")) {
                loadedRules.add("OWL RL Extended");
            }
        } else {
            loadedRules.remove("OWL RL Extended");
            reloadRules();
        }
    }

    private void reloadRules() {
        graph = Graph.create();
        ruleEngine = new RuleEngine();
        if (isRDFSSubsetEnabled) Load.create(graph).load(String.valueOf(Load.RULE_FORMAT));
        if (isRDFSRLEnabled) Load.create(graph).load(String.valueOf(Load.RULE_FORMAT));
        if (isOWLRLEnabled) Load.create(graph).load(String.valueOf(Load.RULE_FORMAT));
        if (isOWLRLExtendedEnabled) Load.create(graph).load(String.valueOf(Load.RULE_FORMAT));
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
}