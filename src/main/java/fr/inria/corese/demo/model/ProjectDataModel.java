package fr.inria.corese.demo.model;

import fr.inria.corese.core.Graph;
import fr.inria.corese.core.kgram.core.Mappings;
import fr.inria.corese.core.load.Load;
import fr.inria.corese.core.load.RuleLoad;
import fr.inria.corese.core.print.ResultFormat;
import fr.inria.corese.core.query.QueryProcess;
import fr.inria.corese.core.rule.Rule;
import fr.inria.corese.core.rule.RuleEngine;
import fr.inria.corese.demo.model.graph.CoreseGraph;
import fr.inria.corese.demo.model.fileList.FileItem;
import fr.inria.corese.demo.model.fileList.FileListModel;
import fr.inria.corese.demo.model.graph.SemanticGraph;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

public class ProjectDataModel {
    private static final Logger logger = Logger.getLogger(ProjectDataModel.class.getName());

    // Graphe et moteur de requêtes
    private Graph graph;
    private QueryProcess exec;

    // Moteur de règles
    private RuleEngine ruleEngine;

    // Liste des fichiers
    private final FileListModel fileListModel;

    // Logs et chemins
    private final List<String> logEntries;
    private String projectPath;

    // Graphe sémantique
    private final SemanticGraph semanticGraph;

    // Liste des règles chargées
    private List<String> loadedRules;

    // États d'activation des règles prédéfinies
    private boolean isRDFSSubsetEnabled;
    private boolean isRDFSRLEnabled;
    private boolean isOWLRLEnabled;
    private boolean isOWLRLExtendedEnabled;
    private boolean isOWLRLTestEnabled;
    private boolean isOWLCleanEnabled;

    public ProjectDataModel() {
        this.semanticGraph = new CoreseGraph();
        this.fileListModel = new FileListModel();
        this.logEntries = new ArrayList<>();
        this.loadedRules = new ArrayList<>();

        initGraph();
    }

    /**
     * Initialise le graphe et le moteur de règles
     */
    private void initGraph() {
        this.graph = Graph.create();
        this.exec = QueryProcess.create(graph);
        this.ruleEngine = RuleEngine.create(graph);
    }

    /**
     * Exécute une requête SPARQL sur le graphe
     */
    public Object[] executeQuery(String queryString) throws Exception {
        String queryType = determineQueryType(queryString);
        Mappings mappings = exec.query(queryString);
        Object formattedResult;

        switch (queryType) {
            case "SELECT" -> {
                formattedResult = ResultFormat.create(mappings, ResultFormat.format.MARKDOWN_FORMAT).toString();
                addLogEntry("SELECT query executed successfully");
            }
            case "CONSTRUCT" -> {
                Graph resultGraph = (Graph) mappings.getGraph();
                formattedResult = resultGraph != null ?
                        ResultFormat.create(resultGraph, ResultFormat.format.TRIG_FORMAT).toString() :
                        "No results.";
                addLogEntry("CONSTRUCT query executed successfully");
            }
            case "ASK" -> {
                formattedResult = !mappings.isEmpty();
                addLogEntry("ASK query executed successfully");
            }
            case "DESCRIBE" -> {
                Graph resultGraph = (Graph) mappings.getGraph();
                formattedResult = resultGraph != null ?
                        ResultFormat.create(resultGraph, ResultFormat.format.TRIG_FORMAT).toString() :
                        "No results.";
                addLogEntry("DESCRIBE query executed successfully");
            }
            default -> throw new Exception("Unsupported query type: " + queryType);
        }

        return new Object[]{formattedResult, queryType};
    }

    /**
     * Charge un fichier dans le graphe
     */
    public void loadFile(File file) throws Exception {
        try {
            Load loader = Load.create(graph);

            // Détecter le format basé sur l'extension du fichier
            String fileName = file.getName().toLowerCase();
            if (fileName.endsWith(".ttl")) {
                loader.parse(file.getAbsolutePath(), Load.format.TURTLE_FORMAT);
            } else if (fileName.endsWith(".rdf") || fileName.endsWith(".xml")) {
                loader.parse(file.getAbsolutePath(), Load.format.RDFXML_FORMAT);
            } else if (fileName.endsWith(".jsonld")) {
                loader.parse(file.getAbsolutePath(), Load.format.JSONLD_FORMAT);
            } else {
                // Par défaut, essayer Turtle
                loader.parse(file.getAbsolutePath(), Load.format.TURTLE_FORMAT);
            }

            addLogEntry("File loaded successfully: " + file.getName());

            // Appliquer les règles après le chargement
            processRules();
            addLogEntry("Applied " + getLoadedRulesCount() + " rules to the graph");

        } catch (Exception e) {
            addLogEntry("Error loading file: " + e.getMessage());
            throw e;
        }
    }

    private String determineQueryType(String queryString) {
        // Supprimer les espaces et les prefixes avant de vérifier le type
        String cleanedQuery = queryString.replaceAll("^\\s*PREFIX\\s+[^:]+:\\s*<[^>]+>\\s*", "")
                .replaceAll("^\\s*PREFIX\\s+[^:]+:\\s*<[^>]+>\\s*", "")  // Permet plusieurs PREFIX
                .trim()
                .toUpperCase();

        if (cleanedQuery.startsWith("SELECT")) return "SELECT";
        if (cleanedQuery.startsWith("CONSTRUCT")) return "CONSTRUCT";
        if (cleanedQuery.startsWith("ASK")) return "ASK";
        if (cleanedQuery.startsWith("DESCRIBE")) return "DESCRIBE";
        return "UNKNOWN";
    }

    /**
     * Efface le graphe et réinitialise le moteur de règles
     */
    public void clearGraph() {
        initGraph();
        addLogEntry("Graph cleared");
    }

    public void addFile(String fileName) {
        fileListModel.addFile(fileName);
    }

    public void clearFiles() {
        fileListModel.clearFiles();
    }

    /**
     * Recharge tous les fichiers du projet
     */
    public void reloadFiles() {
        clearGraph();
        for (FileItem item : fileListModel.getFiles()) {
            try {
                String filePath = projectPath != null
                        ? Paths.get(projectPath, item.getName()).toString()
                        : item.getName();

                File file = new File(filePath);
                if (file.exists()) {
                    loadFile(file);
                    addLogEntry("Reloaded file: " + file.getName());
                } else {
                    addLogEntry("Warning: File not found during reload: " + filePath);
                }
            } catch (Exception e) {
                addLogEntry("Error reloading file " + item.getName() + ": " + e.getMessage());
            }
        }
    }

    /**
     * Charge un projet à partir d'un répertoire
     */
    public void loadProject(File directory) {
        clearGraph();
        clearFiles();
        this.projectPath = directory.getAbsolutePath();
        addLogEntry("Loading project from: " + directory.getAbsolutePath());

        // Essayer de charger le contexte d'abord
        try {
            File contextFile = new File(directory, "project.context");
            if (contextFile.exists()) {
                semanticGraph.loadContext(contextFile.getAbsolutePath());
                addLogEntry("Project context restored");
            }
        } catch (Exception e) {
            addLogEntry("Could not restore project context: " + e.getMessage());
            // Continuer avec le chargement normal
            loadProjectFiles(directory);
            loadProjectRules(directory);
        }
    }

    private void loadProjectFiles(File directory) {
        File[] ttlFiles = directory.listFiles((dir, name) -> name.toLowerCase().endsWith(".ttl"));
        if (ttlFiles != null) {
            for (File file : ttlFiles) {
                try {
                    loadFile(file);
                    fileListModel.addFile(file.getName());
                } catch (Exception e) {
                    addLogEntry("Error loading file " + file.getName() + ": " + e.getMessage());
                }
            }
        }
    }

    private void loadProjectRules(File directory) {
        File rulesDir = new File(directory, "rules");
        if (rulesDir.exists() && rulesDir.isDirectory()) {
            File[] ruleFiles = rulesDir.listFiles((dir, name) -> name.toLowerCase().endsWith(".rul"));
            if (ruleFiles != null) {
                for (File ruleFile : ruleFiles) {
                    try {
                        loadRuleFile(ruleFile);
                        addLogEntry("Loaded rule file: " + ruleFile.getName());
                    } catch (Exception e) {
                        addLogEntry("Error loading rule file " + ruleFile.getName() + ": " + e.getMessage());
                    }
                }
            }
        }
    }

    /**
     * Sauvegarde le projet actuel
     */
    public void saveProject(File targetFile) {
        try {
            Path projectDir = Paths.get(targetFile.getParent());
            Files.createDirectories(projectDir);

            // Sauvegarder le contexte
            semanticGraph.saveContext();

            saveRulesConfiguration(projectDir);
            saveProjectConfiguration(targetFile);

            this.projectPath = projectDir.toString();
            addLogEntry("Project and context saved successfully to: " + projectDir.toString());
        } catch (Exception e) {
            addLogEntry("Error saving project: " + e.getMessage());
        }
    }

    private void saveRulesConfiguration(Path projectDir) throws IOException {
        Path rulesDir = projectDir.resolve("rules");
        Files.createDirectories(rulesDir);

        StringBuilder configContent = createRulesConfigContent();
        File configFile = new File(projectDir.toFile(), "rules.config");
        try (FileWriter writer = new FileWriter(configFile)) {
            writer.write(configContent.toString());
        }
        addLogEntry("Rules configuration saved to: " + configFile.getAbsolutePath());
    }

    private StringBuilder createRulesConfigContent() {
        StringBuilder configContent = new StringBuilder();
        configContent.append("# Rules configuration\n");
        configContent.append("RDFS_SUBSET=").append(isRDFSSubsetEnabled).append("\n");
        configContent.append("RDFS_RL=").append(isRDFSRLEnabled).append("\n");
        configContent.append("OWL_RL=").append(isOWLRLEnabled).append("\n");
        configContent.append("OWL_RL_EXTENDED=").append(isOWLRLExtendedEnabled).append("\n");
        configContent.append("OWL_RL_TEST=").append(isOWLRLTestEnabled).append("\n");
        configContent.append("OWL_CLEAN=").append(isOWLCleanEnabled).append("\n");
        return configContent;
    }

    private void saveProjectConfiguration(File targetFile) throws IOException {
        String baseName = targetFile.getName();
        if (!baseName.endsWith(".ttl")) {
            baseName += ".ttl";
        }
        File graphFile = new File(targetFile.getParentFile(), baseName);
        try (FileWriter writer = new FileWriter(graphFile)) {
            writer.write(((CoreseGraph)semanticGraph).getCoreseGraph().toString());
        }
        addLogEntry("Graph saved to: " + graphFile.getAbsolutePath());
    }

    // Delegated methods for semantic graph
    public int getSemanticElementsCount() {
        return semanticGraph.getSemanticElementsCount();
    }

    public int getTripletCount() {
        return semanticGraph.getTripletCount();
    }

    public int getGraphCount() {
        return semanticGraph.getGraphCount();
    }

    public void addLogEntry(String entry) {
        semanticGraph.addLogEntry(entry);
    }

    public List<String> getLogEntries() {
        return semanticGraph.getLogEntries();
    }

    // Getters for model components
    public FileListModel getFileListModel() {
        return fileListModel;
    }

    public List<File> getLoadedFiles() {
        return semanticGraph.getLoadedFiles();
    }

    public List<File> getLoadedRules() {
        return semanticGraph.getLoadedRules();
    }

    // MÉTHODES DE GESTION DES RÈGLES (intégrées directement au lieu d'utiliser RuleModel)

    /**
     * Vérifie si différents types de règles sont activés.
     */
    public boolean isRDFSSubsetEnabled() { return isRDFSSubsetEnabled; }
    public boolean isRDFSRLEnabled() { return isRDFSRLEnabled; }
    public boolean isOWLRLEnabled() { return isOWLRLEnabled; }
    public boolean isOWLRLExtendedEnabled() { return isOWLRLExtendedEnabled; }
    public boolean isOWLRLTestEnabled() { return isOWLRLTestEnabled; }
    public boolean isOWLCleanEnabled() { return isOWLCleanEnabled; }

    /**
     * Charge et applique les règles spécifiques RDFS Subnet
     */
    public void loadRDFSSubset() {
        //TODO (Implémenter RDFS Subset)
    }

    /**
     * Charge et applique les règles spécifiques RDFS RL
     */
    public void loadRDFSRL() {
        if (isRDFSRLEnabled) {
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

    /**
     * Charge et applique les règles spécifiques OWL RL
     */
    public void loadOWLRL() {
        if (isOWLRLEnabled) {
            try {
                ruleEngine.setProfile(RuleEngine.OWL_RL);
                ruleEngine.process();

                if (!loadedRules.contains("OWL RL")) {
                    loadedRules.add("OWL RL");
                }
            } catch (Exception e) {
                logger.warning("Error loading OWL RL: " + e.getMessage());
            }
        } else {
            loadedRules.remove("OWL RL");
            reloadRules();
        }
    }

    /**
     * Charge et applique les règles spécifiques OWL RL Extended
     */
    public void loadOWLRLExtended() {
        if (isOWLRLExtendedEnabled) {
            try {
                ruleEngine.setProfile(RuleEngine.OWL_RL_EXT);
                ruleEngine.process();

                if (!loadedRules.contains("OWL RL Extended")) {
                    loadedRules.add("OWL RL Extended");
                }
            } catch (Exception e) {
                logger.warning("Error loading OWL RL Extended: " + e.getMessage());
            }
        } else {
            loadedRules.remove("OWL RL Extended");
            reloadRules();
        }
    }

    /**
     * Charge et applique les règles spécifiques OWL RL Test
     */
    public void loadOWLRLTest() {
        if (isOWLRLTestEnabled) {
            try {
                ruleEngine.setProfile(RuleEngine.OWL_RL_TEST);
                ruleEngine.process();

                if (!loadedRules.contains("OWL RL Test")) {
                    loadedRules.add("OWL RL Test");
                }
            } catch (Exception e) {
                logger.warning("Error loading OWL RL Test: " + e.getMessage());
            }
        } else {
            loadedRules.remove("OWL RL Test");
            reloadRules();
        }
    }

    /**
     * Charge et applique les règles spécifiques OWL Clean
     */
    public void loadOWLClean() {
        //TODO (Implémenter OWL Clean)
    }

    /**
     * Recharge toutes les règles actives.
     */
    public void reloadRules() {
        // Sauvegarde des règles personnalisées
        Set<String> customRules = new HashSet<>(loadedRules);
        customRules.removeAll(List.of("RDFS Subset", "RDFS RL", "OWL RL", "OWL RL Extended", "OWL RL Test", "OWL Clean"));

        // Réinitialiser la liste des règles
        loadedRules.clear();

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
                logger.warning("Error reloading custom rule " + customRule + ": " + e.getMessage());
            }
        }

        // Appliquer toutes les règles
        try {
            ruleEngine.process();
        } catch (Exception e) {
            logger.warning("Error processing rules: " + e.getMessage());
        }
    }

    /**
     * Active ou désactive les règles RDFS Subset.
     */
    public void setRDFSSubsetEnabled(boolean enabled) {
        this.isRDFSSubsetEnabled = enabled;
        loadRDFSSubset();
    }

    /**
     * Active ou désactive les règles RDFS RL.
     */
    public void setRDFSRLEnabled(boolean enabled) {
        this.isRDFSRLEnabled = enabled;
        loadRDFSRL();
    }

    /**
     * Active ou désactive les règles OWL RL.
     */
    public void setOWLRLEnabled(boolean enabled) {
        this.isOWLRLEnabled = enabled;
        loadOWLRL();
    }

    /**
     * Active ou désactive les règles OWL RL Extended.
     */
    public void setOWLRLExtendedEnabled(boolean enabled) {
        this.isOWLRLExtendedEnabled = enabled;
        loadOWLRLExtended();
    }

    /**
     * Active ou désactive les règles OWL RL Test.
     */
    public void setOWLRLTestEnabled(boolean enabled) {
        this.isOWLRLTestEnabled = enabled;
        loadOWLRLTest();
    }

    /**
     * Active ou désactive les règles OWL Clean.
     */
    public void setOWLCleanEnabled(boolean enabled) {
        this.isOWLCleanEnabled = enabled;
        loadOWLClean();
    }

    /**
     * Charge un fichier de règles.
     */
    public void loadRuleFile(File file) {
        try {
            RuleLoad ruleLoader = RuleLoad.create(ruleEngine);
            ruleLoader.parse(file.getAbsolutePath());

            String ruleName = file.getName();
            if (!loadedRules.contains(ruleName)) {
                loadedRules.add(ruleName);
            }

            // Appliquer les règles sur le graphe
            ruleEngine.process();
        } catch (Exception e) {
            logger.warning("Error loading rule file: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to load rule file: " + e.getMessage());
        }
    }


    /**
     * Retourne le nombre total de règles actuellement chargées.
     */
    public int getLoadedRulesCount() {
        return loadedRules.size();
    }

    /**
     * Supprime une règle spécifique.
     */
    public void removeRule(String ruleName) {
        loadedRules.remove(ruleName);
        reloadRules();
    }

    /**
     * Exécute le moteur de règles sur le graphe actuel.
     */
    public void processRules() {
        try {
            if (ruleEngine != null) {
                addLogEntry("Starting rule processing with profile: " +
                        (isOWLRLEnabled ? "OWL RL" :
                                (isOWLRLExtendedEnabled ? "OWL RL Extended" : "None")));

                int tripletsBefore = graph.size();

                // Activer le mode debug pour voir plus de détails
                ruleEngine.setDebug(true);
                ruleEngine.process();

                int tripletsAfter = graph.size();
                addLogEntry("Rules processing completed. Added " +
                        (tripletsAfter - tripletsBefore) + " new triples.");

                // Afficher les règles utilisées
                List<Rule> rules = ruleEngine.getRules();
                addLogEntry("Rules used: " + rules.size());
                for (int i = 0; i < Math.min(5, rules.size()); i++) {
                    addLogEntry("Rule[" + i + "]: " + rules.get(i).getName());
                }
            }
        } catch (Exception e) {
            addLogEntry("Error processing rules: " + e.getMessage());
        }
    }

    public void saveCurrentState() {
        try {
            // Sauvegarder le contexte du graphe
            SemanticGraph semanticGraph = getSemanticGraph();
            semanticGraph.saveContext();

            // Log de débogage
            System.out.println("Saving current state:");
            System.out.println("  Loaded Files: " + semanticGraph.getLoadedFiles());
            System.out.println("  Loaded Rules: " + semanticGraph.getLoadedRules());
            System.out.println("  Triplet Count: " + semanticGraph.getTripletCount());
        } catch (Exception e) {
            System.err.println("Error saving current state: " + e.getMessage());
        }
    }

    public void restoreState() {
        try {
            // Charger le contexte du graphe
            SemanticGraph semanticGraph = getSemanticGraph();
            File contextFile = new File("graph-context.properties");

            if (this.isOWLRLEnabled) {
                System.out.println("Re-applying OWL RL rules after state restoration");
                // Force l'initialisation d'un nouveau moteur de règles
                this.ruleEngine = RuleEngine.create(graph);
                // Définir explicitement le profil
                this.ruleEngine.setProfile(RuleEngine.OWL_RL);
                // Exécuter immédiatement les règles
                this.ruleEngine.process();
                System.out.println("OWL RL rules applied, graph now has " + graph.size() + " triples");
            }

            if (contextFile.exists()) {
                semanticGraph.loadContext(contextFile.getAbsolutePath());

                // Log de débogage
                System.out.println("Restoring state:");
                System.out.println("  Loaded Files: " + semanticGraph.getLoadedFiles());
                System.out.println("  Loaded Rules: " + semanticGraph.getLoadedRules());
                System.out.println("  Triplet Count: " + semanticGraph.getTripletCount());
            }
        } catch (Exception e) {
            System.err.println("Error restoring state: " + e.getMessage());
        }
    }

    public SemanticGraph getSemanticGraph() {
        return semanticGraph;
    }
}