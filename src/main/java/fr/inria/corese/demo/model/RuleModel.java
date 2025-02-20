package fr.inria.corese.demo.model;

import fr.inria.corese.core.Graph;
import fr.inria.corese.core.load.RuleLoad;
import fr.inria.corese.core.rule.RuleEngine;

import java.io.File;
import java.util.List;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

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

    public void setGraph(Graph graph) {
        this.graph = graph;
        this.ruleEngine = RuleEngine.create(graph);
        // Recharger les règles avec le nouveau graphe
        reloadRules();
    }

    public Graph getGraph() {
        return this.graph;
    }

    public boolean isRDFSSubsetEnabled() { return isRDFSSubsetEnabled; }
    public boolean isRDFSRLEnabled() { return isRDFSRLEnabled; }
    public boolean isOWLRLEnabled() { return isOWLRLEnabled; }
    public boolean isOWLRLExtendedEnabled() { return isOWLRLExtendedEnabled; }
    public boolean isOWLRLTestEnabled() { return isOWLRLTestEnabled; }
    public boolean isOWLCleanEnabled() { return isOWLCleanEnabled; }

    public void loadRDFSSubset() {
        //TODO
    }

    public void loadRDFSRL() {
        //TODO
    }

    public void loadOWLRL() {
        if (isOWLRLEnabled) {
            try {
                RuleEngine engine = RuleEngine.create(graph);
                engine.setProfile(RuleEngine.OWL_RL);
                engine.process();

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
                RuleEngine engine = RuleEngine.create(graph);
                engine.setProfile(RuleEngine.OWL_RL_EXT);
                engine.process();

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

    public void loadOWLRLTest() {
        if (isOWLRLTestEnabled) {
            try {
                RuleEngine engine = RuleEngine.create(graph);
                engine.setProfile(RuleEngine.OWL_RL_TEST);
                engine.process();

                if (!loadedRules.contains("OWL RL Test")) {
                    loadedRules.add("OWL RL Test");
                }
            } catch (Exception e) {
                System.err.println("Error loading OWL RL Test: " + e.getMessage());
            }
        } else {
            loadedRules.remove("OWL RL Test");
            reloadRules();
        }
    }

    public void loadOWLClean() {
        //TODO
    }

    public void reloadRules() {
        // Sauvegarde des règles personnalisées
        Set<String> customRules = new HashSet<>(loadedRules);
        customRules.removeAll(List.of("RDFS Subset", "RDFS RL", "OWL RL", "OWL RL Extended", "OWL RL Test", "OWL Clean"));

        // Réinitialiser la liste des règles
        loadedRules.clear();

        // Recréer le moteur de règles si nécessaire
        if (ruleEngine == null || ruleEngine.getGraph() != graph) {
            ruleEngine = RuleEngine.create(graph);
        }

        // Recharger les règles prédéfinies
        if (isRDFSSubsetEnabled) loadRDFSSubset();
        if (isRDFSRLEnabled) loadRDFSRL();
        if (isOWLRLEnabled) loadOWLRL();
        if (isOWLRLExtendedEnabled) loadOWLRLExtended();
        if (isOWLRLTestEnabled) loadOWLRLTest();
        if (isOWLCleanEnabled) loadOWLClean();

        // Recharger les règles personnalisées
        for (String customRule : customRules) {
            // Essayer de trouver le fichier de règle correspondant
            try {
                // Essayer plusieurs chemins possibles
                String[] possiblePaths = {
                        "rules/" + customRule,
                        "rules/" + customRule + ".rul",
                        customRule,
                        customRule + ".rul"
                };

                boolean loaded = false;
                for (String path : possiblePaths) {
                    File ruleFile = new File(path);
                    if (ruleFile.exists()) {
                        RuleLoad ruleLoader = RuleLoad.create(ruleEngine);
                        ruleLoader.parse(path);
                        loadedRules.add(customRule);
                        loaded = true;
                        break;
                    }
                }

                if (!loaded && graph != null) {
                    // Si on n'a pas trouvé le fichier, mais que la règle était chargée avant,
                    // on l'ajoute quand même à la liste (elle est probablement dans le graphe)
                    loadedRules.add(customRule);
                }
            } catch (Exception e) {
                System.err.println("Error reloading custom rule " + customRule + ": " + e.getMessage());
            }
        }

        // Appliquer toutes les règles
        try {
            ruleEngine.process();
        } catch (Exception e) {
            System.err.println("Error processing rules: " + e.getMessage());
        }
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
        return loadedRules.size();
    }

    public void removeRule(String ruleName) {
        // Supprimer la règle de la liste des règles chargées
        loadedRules.remove(ruleName);

        // Comme nous ne pouvons pas supprimer des règles individuelles du moteur,
        // nous recréons le moteur et rechargeons toutes les règles restantes
        reloadRules();
    }

    /**
     * Exécute le moteur de règles sur le graphe actuel.
     * Cette méthode peut être appelée après avoir modifié le graphe
     * pour appliquer toutes les règles chargées.
     */
    public void process() {
        try {
            if (ruleEngine != null) {
                ruleEngine.process();
            }
        } catch (Exception e) {
            System.err.println("Error processing rules: " + e.getMessage());
            e.printStackTrace();
        }
    }
}