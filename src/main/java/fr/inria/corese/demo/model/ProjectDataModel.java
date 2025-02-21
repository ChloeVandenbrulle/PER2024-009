package fr.inria.corese.demo.model;

import fr.inria.corese.core.Graph;
import fr.inria.corese.core.load.Load;
import fr.inria.corese.core.load.LoadException;
import fr.inria.corese.demo.model.fileList.FileItem;
import fr.inria.corese.demo.model.fileList.FileListModel;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Modèle central de gestion de projet pour une application de traitement de données sémantiques.
 *
 * Responsabilités principales :
 * - Gestion du graphe sémantique
 * - Gestion des fichiers du projet
 * - Suivi des journaux d'événements
 * - Chargement, sauvegarde et manipulation de projets
 *
 * Fonctionnalités clés :
 * - Chargement et gestion de fichiers RDF
 * - Gestion des modèles de règles
 * - Journalisation des opérations
 * - Calcul de métriques sur les données sémantiques
 *
 * @author Clervie Causer
 * @version 1.0
 * @since 2025
 */
public class ProjectDataModel {
    private static final Logger logger = Logger.getLogger(ProjectDataModel.class.getName());
    private Graph graph;
    private final FileListModel fileListModel;
    private final List<String> logEntries;
    private RuleModel ruleModel;
    private String projectPath;
    private int rulesLoadedCount = 0;

    /**
     * Constructeur par défaut.
     *
     * Initialise :
     * - Un graphe sémantique vide
     * - Un modèle de liste de fichiers
     * - Un journal d'événements
     * - Un modèle de règles
     */
    public ProjectDataModel() {
        this.graph = Graph.create();
        this.fileListModel = new FileListModel();
        this.logEntries = new ArrayList<>();
        this.ruleModel = new RuleModel();
        // Informer le ruleModel du graphe
        this.ruleModel.setGraph(this.graph);
    }

    /**
     * Récupère le modèle de liste de fichiers.
     *
     * @return Le modèle de liste de fichiers associé au projet
     */
    public FileListModel getFileListModel() {
        return fileListModel;
    }

    /**
     * Récupère le modèle de règles.
     *
     * @return Le modèle de règles associé au projet
     */
    public RuleModel getRuleModel() {
        return ruleModel;
    }

    /**
     * Ajoute une entrée au journal des événements.
     *
     * @param entry L'entrée de journal à ajouter
     */
    public void addLogEntry(String entry) {
        logEntries.add(entry);
        logger.info(entry);
    }

    /**
     * Récupère toutes les entrées du journal.
     *
     * @return Une liste des entrées de journal
     */
    public List<String> getLogEntries() {
        return new ArrayList<>(logEntries);
    }

    public void setRulesLoadedCount(int count) {
        this.rulesLoadedCount = count;
    }

    /**
     * Charge un fichier dans le graphe sémantique.
     *
     * @param file Le fichier à charger
     * @throws LoadException En cas d'erreur lors du chargement du fichier
     */
    public void loadFile(File file) throws LoadException {
        try {
            clearGraph();
            Load ld = Load.create(graph);
            ld.parse(file.getAbsolutePath());
            addLogEntry("File loaded: " + file.getName() + " with " + graph.size() + " triples");

            // Après le chargement du fichier, appliquer les règles
            if (ruleModel != null) {
                ruleModel.process();
                addLogEntry("Applied " + ruleModel.getLoadedRulesCount() + " rules to the graph");
            }
        } catch (LoadException e) {
            addLogEntry("Error loading file: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Ajoute un fichier à la liste des fichiers.
     *
     * @param fileName Le nom du fichier à ajouter
     */
    public void addFile(String fileName) {
        fileListModel.addFile(fileName);
    }

    /**
     * Efface tous les fichiers de la liste.
     */
    public void clearFiles() {
        fileListModel.clearFiles();
    }

    /**
     * Efface le graphe sémantique.
     */
    public void clearGraph() {
        this.graph = Graph.create();

        // Mettre à jour le graphe dans le modèle de règles
        if (ruleModel != null) {
            ruleModel.setGraph(this.graph);
        }

        addLogEntry("Graph cleared");
    }

    /**
     * Recharge tous les fichiers précédemment chargés.
     */
    public void reloadFiles() {
        clearGraph();

        List<FileItem> files = new ArrayList<>(fileListModel.getFiles());
        for (FileItem item : files) {
            try {
                // Supposer que le chemin est relatif au répertoire du projet
                String filePath = projectPath != null
                        ? Paths.get(projectPath, item.getName()).toString()
                        : item.getName();

                File file = new File(filePath);
                if (file.exists()) {
                    Load ld = Load.create(graph);
                    ld.parse(file.getAbsolutePath());
                    addLogEntry("Reloaded file: " + file.getName());
                } else {
                    addLogEntry("Warning: File not found during reload: " + filePath);
                }
            } catch (LoadException e) {
                addLogEntry("Error reloading file " + item.getName() + ": " + e.getMessage());
            }
        }

        // Réappliquer les règles après rechargement
        if (ruleModel != null) {
            ruleModel.process();
            addLogEntry("Reapplied " + ruleModel.getLoadedRulesCount() + " rules to the graph");
        }
    }

    /**
     * Charge un projet à partir d'un répertoire.
     *
     * @param directory Le répertoire contenant le projet
     */
    public void loadProject(File directory) {
        clearGraph();
        clearFiles();
        this.projectPath = directory.getAbsolutePath();
        addLogEntry("Loading project from: " + directory.getAbsolutePath());

        // Charger les fichiers TTL
        File[] ttlFiles = directory.listFiles((dir, name) -> name.toLowerCase().endsWith(".ttl"));
        if (ttlFiles != null) {
            for (File file : ttlFiles) {
                try {
                    Load ld = Load.create(graph);
                    ld.parse(file.getAbsolutePath());
                    fileListModel.addFile(file.getName());
                    addLogEntry("Loaded file: " + file.getName());
                } catch (LoadException e) {
                    addLogEntry("Error loading file " + file.getName() + ": " + e.getMessage());
                }
            }
        }

        // Charger les fichiers de règles
        File rulesDir = new File(directory, "rules");
        if (rulesDir.exists() && rulesDir.isDirectory()) {
            File[] ruleFiles = rulesDir.listFiles((dir, name) -> name.toLowerCase().endsWith(".rul"));
            if (ruleFiles != null) {
                for (File ruleFile : ruleFiles) {
                    try {
                        ruleModel.loadRuleFile(ruleFile);
                        addLogEntry("Loaded rule file: " + ruleFile.getName());
                    } catch (Exception e) {
                        addLogEntry("Error loading rule file " + ruleFile.getName() + ": " + e.getMessage());
                    }
                }
            }
        }

        // Appliquer les règles après chargement
        if (ruleModel != null) {
            ruleModel.process();
            addLogEntry("Applied " + ruleModel.getLoadedRulesCount() + " rules to the graph");
        }
    }

    /**
     * Sauvegarde le projet dans un fichier cible.
     *
     * @param targetFile Le fichier cible pour la sauvegarde
     */
    public void saveProject(File targetFile) {
        String directory = targetFile.getParent();
        String baseName = targetFile.getName();
        if (!baseName.endsWith(".ttl")) {
            baseName += ".ttl";
        }

        try {
            // Créer le répertoire du projet si nécessaire
            Path projectDir = Paths.get(directory);
            Files.createDirectories(projectDir);

            // Sauvegarder le graphe principal
            String ttlContent = graph.toString();
            File graphFile = new File(projectDir.toFile(), baseName);
            try (FileWriter writer = new FileWriter(graphFile)) {
                writer.write(ttlContent);
            }
            addLogEntry("Graph saved to: " + graphFile.getAbsolutePath());

            // Créer le répertoire des règles
            Path rulesDir = projectDir.resolve("rules");
            Files.createDirectories(rulesDir);

            // Sauvegarder l'état des règles prédéfinies dans un fichier de configuration
            StringBuilder configContent = new StringBuilder();
            configContent.append("# Rules configuration\n");
            configContent.append("RDFS_SUBSET=").append(ruleModel.isRDFSSubsetEnabled()).append("\n");
            configContent.append("RDFS_RL=").append(ruleModel.isRDFSRLEnabled()).append("\n");
            configContent.append("OWL_RL=").append(ruleModel.isOWLRLEnabled()).append("\n");
            configContent.append("OWL_RL_EXTENDED=").append(ruleModel.isOWLRLExtendedEnabled()).append("\n");
            configContent.append("OWL_RL_TEST=").append(ruleModel.isOWLRLTestEnabled()).append("\n");
            configContent.append("OWL_CLEAN=").append(ruleModel.isOWLCleanEnabled()).append("\n");

            File configFile = new File(projectDir.toFile(), "rules.config");
            try (FileWriter writer = new FileWriter(configFile)) {
                writer.write(configContent.toString());
            }
            addLogEntry("Rules configuration saved to: " + configFile.getAbsolutePath());

            // Le projet est maintenant sauvegardé
            this.projectPath = projectDir.toString();
            addLogEntry("Project saved successfully to: " + projectDir.toString());

        } catch (IOException e) {
            addLogEntry("Error saving project: " + e.getMessage());
        }
    }

    /**
     * Compte le nombre d'éléments sémantiques dans le graphe.
     *
     * @return Le nombre d'éléments sémantiques
     */
    public int getSemanticElementsCount() {
        if (graph == null) return 0;
        try {
            return graph.size() / 3;
        } catch (Exception e) {
            addLogEntry("Error counting semantic elements: " + e.getMessage());
            return 0;
        }
    }

    /**
     * Récupère le nombre de triplets dans le graphe.
     *
     * @return Le nombre de triplets
     */
    public int getTripletCount() {
        return graph != null ? graph.size() : 0;
    }

    /**
     * Récupère le nombre de graphes.
     *
     * @return Le nombre de graphes
     */
    public int getGraphCount() {
        if (graph == null) return 0;
        try {
            // Méthode simplifiée qui tente d'accéder à la méthode appropriée selon la version
            try {
                java.lang.reflect.Method getNamedGraphsMethod = graph.getClass().getMethod("getNamedGraphs");
                Object result = getNamedGraphsMethod.invoke(graph);
                if (result instanceof java.util.List) {
                    return ((java.util.List<?>) result).size() + 1;
                }
            } catch (NoSuchMethodException e) {
                // La méthode n'existe pas, essayer autre chose
                try {
                    java.lang.reflect.Method getGraphNamesMethod = graph.getClass().getMethod("getGraphNames");
                    Object result = getGraphNamesMethod.invoke(graph);
                    if (result instanceof java.util.List) {
                        return ((java.util.List<?>) result).size() + 1;
                    }
                } catch (NoSuchMethodException ex) {
                    // Aucune des méthodes n'existe
                    return 1; // On suppose qu'il y a au moins le graphe par défaut
                }
            }
            return 1; // Valeur par défaut
        } catch (Exception e) {
            addLogEntry("Error counting graphs: " + e.getMessage());
            return 1; // On suppose qu'il y a au moins le graphe par défaut
        }
    }
}