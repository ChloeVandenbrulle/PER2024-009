package fr.inria.corese.demo.model;

import fr.inria.corese.core.Graph;
import fr.inria.corese.core.load.RuleLoad;
import fr.inria.corese.core.rule.RuleEngine;

import java.io.File;
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
        this.ruleEngine = RuleEngine.create(graph);
        this.loadedRules = new ArrayList<>();
    }

    public boolean isRDFSSubsetEnabled() { return isRDFSSubsetEnabled; }
    public boolean isRDFSRLEnabled() { return isRDFSRLEnabled; }
    public boolean isOWLRLEnabled() { return isOWLRLEnabled; }
    public boolean isOWLRLExtendedEnabled() { return isOWLRLExtendedEnabled; }
    public boolean isOWLRLTestEnabled() { return isOWLRLTestEnabled; }
    public boolean isOWLCleanEnabled() { return isOWLCleanEnabled; }

    public void loadRDFSSubset() {
        //TODO: Implement this method for RDFS Subset
    }

    public void loadRDFSRL() {
        //TODO: Implement this method for RDFS RL
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
        //TODO: Implement this method for OWL Clean
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

    public void loadRuleFile(File file) {
        try {
            // S'assurer que le ruleEngine est correctement initialisé avec un graphe
            if (ruleEngine == null || ruleEngine.getGraph() == null) {
                ruleEngine = RuleEngine.create(graph);
            }

            RuleLoad ruleLoader = RuleLoad.create(ruleEngine);
            ruleLoader.parse(file.getAbsolutePath());

            String ruleName = file.getName();
            if (!loadedRules.contains(ruleName)) {
                loadedRules.add(ruleName);
            }

            // Appliquer les règles sur le graphe
            ruleEngine.process();
        } catch (Exception e) {
            System.err.println("Error loading rule file: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to load rule file: " + e.getMessage());
        }
    }

    public List<String> getLoadedRules() {
        // Renvoie une copie de la liste pour éviter les modifications externes
        return new ArrayList<>(loadedRules);
    }

    /**
     * Retourne le nombre total de règles actuellement chargées dans le modèle.
     * Cela inclut à la fois les règles prédéfinies activées et les règles personnalisées.
     *
     * @return Le nombre de règles chargées
     */
    public int getLoadedRulesCount() {
        int count = 0;

        // Compter les règles personnalisées
        count += loadedRules.size();

        return count;
    }

    public void removeRule(String ruleName) {
        // Supprimer la règle de la liste des règles chargées
        loadedRules.remove(ruleName);

        // Comme nous ne pouvons pas supprimer des règles individuelles du moteur,
        // nous recréons le moteur et rechargeons toutes les règles restantes
        reloadRules();
    }


}