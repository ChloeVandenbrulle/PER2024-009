package fr.inria.corese.demo.manager;

import fr.inria.corese.demo.model.ProjectDataModel;
import fr.inria.corese.demo.model.graph.GraphContext;
import fr.inria.corese.demo.model.graph.SemanticGraph;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Gestionnaire d'état global pour l'application.
 *
 * Cette classe utilise le pattern Singleton pour fournir un accès centralisé
 * et unique à l'état de l'application à travers différentes vues.
 */
public class ApplicationStateManager {
    // Instance unique du gestionnaire d'état
    private static ApplicationStateManager instance;

    // Modèle de données partagé
    private ProjectDataModel projectDataModel;

    // Liste des fichiers chargés
    private List<File> loadedFiles;

    // Liste des règles chargées (plus explicite que juste les fichiers)
    private List<File> loadedRuleFiles;

    // État des règles
    private boolean owlRLEnabled;
    private boolean owlRLExtendedEnabled;
    private boolean rdfsSubsetEnabled;
    private boolean rdfsRLEnabled;
    private boolean owlRLTestEnabled;
    private boolean owlCleanEnabled;

    // Map pour stocker l'état d'activation des règles personnalisées
    private Map<String, Boolean> customRuleStates;

    // Constructeur privé pour empêcher l'instanciation directe
    private ApplicationStateManager() {
        // Initialisation du modèle de données
        projectDataModel = new ProjectDataModel();
        loadedFiles = new ArrayList<>();
        loadedRuleFiles = new ArrayList<>();
        customRuleStates = new HashMap<>();

        // Log de débogage
        System.out.println("ApplicationStateManager initialized");
    }

    /**
     * Méthode statique pour obtenir l'instance unique du gestionnaire.
     *
     * @return L'instance unique de ApplicationStateManager
     */
    public static synchronized ApplicationStateManager getInstance() {
        if (instance == null) {
            instance = new ApplicationStateManager();
        }
        return instance;
    }

    /**
     * Récupère le modèle de données partagé.
     *
     * @return Le ProjectDataModel
     */
    public ProjectDataModel getProjectDataModel() {
        return projectDataModel;
    }

    /**
     * Charge un fichier et met à jour l'état global.
     *
     * @param file Le fichier à charger
     */
    public void loadFile(File file) {
        try {
            // Charger le fichier dans le modèle de données
            projectDataModel.loadFile(file);

            // S'assurer que le fichier est ajouté au FileListModel
            projectDataModel.getFileListModel().addFile(file.getName());

            // Mettre à jour la liste des fichiers chargés
            if (!loadedFiles.contains(file)) {
                loadedFiles.add(file);
            }

            System.out.println("File loaded in ApplicationStateManager: " + file.getName());
        } catch (Exception e) {
            System.err.println("Error loading file: " + e.getMessage());
        }
    }

    /**
     * Charge un fichier de règles et met à jour l'état global.
     *
     * @param file Le fichier de règles à charger
     */
    public void loadRuleFile(File file) {
        try {
            // Charger le fichier de règles dans le modèle de données
            projectDataModel.loadRuleFile(file);

            // Mettre à jour la liste des fichiers de règles chargés
            if (!loadedRuleFiles.contains(file)) {
                loadedRuleFiles.add(file);
            }

            // Marquer cette règle comme activée
            customRuleStates.put(file.getName(), true);

            // Log de débogage
            System.out.println("Rule file loaded in ApplicationStateManager: " + file.getName());
            System.out.println("Total loaded rule files: " + loadedRuleFiles.size());
        } catch (Exception e) {
            System.err.println("Error loading rule file in ApplicationStateManager: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Active/désactive les règles RDFS Subset.
     *
     * @param enabled État d'activation des règles RDFS Subset
     */
    public void setRDFSSubsetEnabled(boolean enabled) {
        projectDataModel.setRDFSSubsetEnabled(enabled);
        this.rdfsSubsetEnabled = enabled;

        // Log de débogage
        System.out.println("RDFS Subset Rules " + (enabled ? "Enabled" : "Disabled"));
    }

    /**
     * Active/désactive les règles RDFS RL.
     *
     * @param enabled État d'activation des règles RDFS RL
     */
    public void setRDFSRLEnabled(boolean enabled) {
        projectDataModel.setRDFSRLEnabled(enabled);
        this.rdfsRLEnabled = enabled;

        // Log de débogage
        System.out.println("RDFS RL Rules " + (enabled ? "Enabled" : "Disabled"));
    }

    /**
     * Active/désactive les règles OWL RL.
     *
     * @param enabled État d'activation des règles OWL RL
     */
    public void setOWLRLEnabled(boolean enabled) {
        projectDataModel.setOWLRLEnabled(enabled);
        this.owlRLEnabled = enabled;

        // Log de débogage
        System.out.println("OWL RL Rules " + (enabled ? "Enabled" : "Disabled"));
    }

    /**
     * Active/désactive les règles OWL RL Extended.
     *
     * @param enabled État d'activation des règles OWL RL Extended
     */
    public void setOWLRLExtendedEnabled(boolean enabled) {
        projectDataModel.setOWLRLExtendedEnabled(enabled);
        this.owlRLExtendedEnabled = enabled;

        // Log de débogage
        System.out.println("OWL RL Extended Rules " + (enabled ? "Enabled" : "Disabled"));
    }

    /**
     * Active/désactive les règles OWL RL Test.
     *
     * @param enabled État d'activation des règles OWL RL Test
     */
    public void setOWLRLTestEnabled(boolean enabled) {
        projectDataModel.setOWLRLTestEnabled(enabled);
        this.owlRLTestEnabled = enabled;

        // Log de débogage
        System.out.println("OWL RL Test Rules " + (enabled ? "Enabled" : "Disabled"));
    }

    /**
     * Active/désactive les règles OWL Clean.
     *
     * @param enabled État d'activation des règles OWL Clean
     */
    public void setOWLCleanEnabled(boolean enabled) {
        projectDataModel.setOWLCleanEnabled(enabled);
        this.owlCleanEnabled = enabled;

        // Log de débogage
        System.out.println("OWL Clean Rules " + (enabled ? "Enabled" : "Disabled"));
    }

    /**
     * Définit l'état d'activation d'une règle personnalisée.
     *
     * @param ruleName Nom de la règle
     * @param enabled État d'activation
     */
    public void setCustomRuleEnabled(String ruleName, boolean enabled) {
        customRuleStates.put(ruleName, enabled);

        // Si la règle est désactivée, on la supprime des règles chargées
        if (!enabled) {
            projectDataModel.removeRule(ruleName);
        } else {
            // Sinon, on la recharge si elle existe
            for (File ruleFile : loadedRuleFiles) {
                if (ruleFile.getName().equals(ruleName)) {
                    try {
                        projectDataModel.loadRuleFile(ruleFile);
                    } catch (Exception e) {
                        System.err.println("Error reloading rule " + ruleName + ": " + e.getMessage());
                    }
                    break;
                }
            }
        }

        // Log de débogage
        System.out.println("Custom rule '" + ruleName + "' " + (enabled ? "Enabled" : "Disabled"));
    }

    /**
     * Récupère l'état d'activation d'une règle personnalisée.
     *
     * @param ruleName Nom de la règle
     * @return true si la règle est activée, false sinon
     */
    public boolean isCustomRuleEnabled(String ruleName) {
        return customRuleStates.getOrDefault(ruleName, false);
    }

    /**
     * Récupère l'état des règles RDFS Subset.
     *
     * @return true si les règles RDFS Subset sont activées
     */
    public boolean isRDFSSubsetEnabled() {
        return rdfsSubsetEnabled;
    }

    /**
     * Récupère l'état des règles RDFS RL.
     *
     * @return true si les règles RDFS RL sont activées
     */
    public boolean isRDFSRLEnabled() {
        return rdfsRLEnabled;
    }

    /**
     * Récupère l'état des règles OWL RL.
     *
     * @return true si les règles OWL RL sont activées
     */
    public boolean isOWLRLEnabled() {
        return owlRLEnabled;
    }

    /**
     * Récupère l'état des règles OWL RL Extended.
     *
     * @return true si les règles OWL RL Extended sont activées
     */
    public boolean isOWLRLExtendedEnabled() {
        return owlRLExtendedEnabled;
    }

    /**
     * Récupère l'état des règles OWL RL Test.
     *
     * @return true si les règles OWL RL Test sont activées
     */
    public boolean isOWLRLTestEnabled() {
        return owlRLTestEnabled;
    }

    /**
     * Récupère l'état des règles OWL Clean.
     *
     * @return true si les règles OWL Clean sont activées
     */
    public boolean isOWLCleanEnabled() {
        return owlCleanEnabled;
    }

    /**
     * Récupère la liste des fichiers chargés.
     *
     * @return Liste des fichiers chargés
     */
    public List<File> getLoadedFiles() {
        return new ArrayList<>(loadedFiles);
    }

    /**
     * Récupère la liste des fichiers de règles chargés.
     *
     * @return Liste des fichiers de règles chargés
     */
    public List<File> getLoadedRuleFiles() {
        return new ArrayList<>(loadedRuleFiles);
    }

    /**
     * Réinitialise l'état global de l'application.
     */
    public void restoreState() {
        ProjectDataModel projectDataModel = getProjectDataModel();

        // Restaurer les fichiers
        if (loadedFiles != null && !loadedFiles.isEmpty()) {
            for (File file : loadedFiles) {
                try {
                    projectDataModel.loadFile(file);
                    System.out.println("Restored file: " + file.getName());
                } catch (Exception e) {
                    System.err.println("Error restoring file: " + file.getName());
                }
            }
        }

        // Restaurer les fichiers de règles
        if (loadedRuleFiles != null && !loadedRuleFiles.isEmpty()) {
            for (File ruleFile : loadedRuleFiles) {
                try {
                    // Vérifier si cette règle est activée avant de la charger
                    if (customRuleStates.getOrDefault(ruleFile.getName(), true)) {
                        projectDataModel.loadRuleFile(ruleFile);
                        System.out.println("Restored rule file: " + ruleFile.getName());
                    }
                } catch (Exception e) {
                    System.err.println("Error restoring rule file: " + ruleFile.getName());
                }
            }
        }

        // Restaurer les règles prédéfinies
        if (rdfsSubsetEnabled) {
            projectDataModel.setRDFSSubsetEnabled(true);
        }
        if (rdfsRLEnabled) {
            projectDataModel.setRDFSRLEnabled(true);
        }
        if (owlRLEnabled) {
            projectDataModel.setOWLRLEnabled(true);
        }
        if (owlRLExtendedEnabled) {
            projectDataModel.setOWLRLExtendedEnabled(true);
        }
        if (owlRLTestEnabled) {
            projectDataModel.setOWLRLTestEnabled(true);
        }
        if (owlCleanEnabled) {
            projectDataModel.setOWLCleanEnabled(true);
        }
    }

    public void processLoadedFile(File file) {
        try {
            ProjectDataModel projectDataModel = getProjectDataModel();

            // Récupérer le graphe sémantique
            SemanticGraph semanticGraph = projectDataModel.getSemanticGraph();

            // Charger le fichier via le graphe sémantique
            semanticGraph.loadFile(file);

            // Mettre à jour la liste des fichiers chargés
            if (!loadedFiles.contains(file)) {
                loadedFiles.add(file);
            }

            // Log de débogage détaillé
            System.out.println("File processed in ApplicationStateManager: " + file.getName());
            System.out.println("Total loaded files: " + semanticGraph.getLoadedFiles().size());
            System.out.println("Triplet count: " + semanticGraph.getTripletCount());
        } catch (Exception e) {
            System.err.println("Error processing file in ApplicationStateManager: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Traite un fichier de règles chargé.
     *
     * @param file Le fichier de règles à traiter
     */
    public void processLoadedRuleFile(File file) {
        try {
            ProjectDataModel projectDataModel = getProjectDataModel();

            // Charger le fichier de règles via le modèle de données
            projectDataModel.loadRuleFile(file);

            // Mettre à jour la liste des fichiers de règles chargés
            if (!loadedRuleFiles.contains(file)) {
                loadedRuleFiles.add(file);
            }

            // Marquer cette règle comme activée
            customRuleStates.put(file.getName(), true);

            // Log de débogage détaillé
            System.out.println("Rule file processed in ApplicationStateManager: " + file.getName());
            System.out.println("Total loaded rule files: " + loadedRuleFiles.size());
        } catch (Exception e) {
            System.err.println("Error processing rule file in ApplicationStateManager: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void saveCurrentState() {
        ProjectDataModel projectDataModel = getProjectDataModel();

        // Sauvegarder les fichiers
        this.loadedFiles = new ArrayList<>(projectDataModel.getLoadedFiles());

        // Sauvegarder les fichiers de règles
        this.loadedRuleFiles = new ArrayList<>(projectDataModel.getLoadedRules());

        // Sauvegarder l'état des règles prédéfinies
        this.rdfsSubsetEnabled = projectDataModel.isRDFSSubsetEnabled();
        this.rdfsRLEnabled = projectDataModel.isRDFSRLEnabled();
        this.owlRLEnabled = projectDataModel.isOWLRLEnabled();
        this.owlRLExtendedEnabled = projectDataModel.isOWLRLExtendedEnabled();
        this.owlRLTestEnabled = projectDataModel.isOWLRLTestEnabled();
        this.owlCleanEnabled = projectDataModel.isOWLCleanEnabled();

        // Log de débogage
        System.out.println("Current state saved:");
        System.out.println("  - Files: " + loadedFiles.size());
        System.out.println("  - Rule files: " + loadedRuleFiles.size());
        System.out.println("  - OWL RL Enabled: " + owlRLEnabled);
        System.out.println("  - OWL RL Extended Enabled: " + owlRLExtendedEnabled);
    }
}