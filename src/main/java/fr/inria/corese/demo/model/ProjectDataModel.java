package fr.inria.corese.demo.model;

import fr.inria.corese.core.Graph;
import fr.inria.corese.core.kgram.core.Mappings;
import fr.inria.corese.core.load.Load;
import fr.inria.corese.core.print.ResultFormat;
import fr.inria.corese.core.query.QueryProcess;
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
import java.util.List;
import java.util.logging.Logger;

public class ProjectDataModel {
        private static final Logger logger = Logger.getLogger(ProjectDataModel.class.getName());
        private Graph graph;
        private QueryProcess exec;
        private final FileListModel fileListModel;
        private final List<String> logEntries;
        private RuleModel ruleModel;
        private String projectPath;
        private int rulesLoadedCount = 0;
        private final SemanticGraph semanticGraph;

        public ProjectDataModel() {
            this.semanticGraph = new CoreseGraph();
            this.fileListModel = new FileListModel();
            this.logEntries = new ArrayList<>();
            this.graph = Graph.create();
            this.exec = QueryProcess.create(graph);
            this.ruleModel = new RuleModel();
            this.ruleModel.setGraph(this.graph);
        }

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

                // Appliquer les règles après le chargement si nécessaire
                if (ruleModel != null) {
                    ruleModel.process();
                    addLogEntry("Applied " + ruleModel.getLoadedRulesCount() + " rules to the graph");
                }
            } catch (Exception e) {
                addLogEntry("Error loading file: " + e.getMessage());
                throw e;
            }
        }

        private String determineQueryType(String queryString) {
            String upperQuery = queryString.trim().toUpperCase();
            if (upperQuery.startsWith("SELECT")) return "SELECT";
            if (upperQuery.startsWith("CONSTRUCT")) return "CONSTRUCT";
            if (upperQuery.startsWith("ASK")) return "ASK";
            if (upperQuery.startsWith("DESCRIBE")) return "DESCRIBE";
            return "UNKNOWN";
        }

        // Support de différents formats de résultats pour différents types de requêtes
        public String getQueryResultAsFormat(Object result, String format, String queryType) {
            try {
                switch (queryType) {
                    case "SELECT" -> {
                        Mappings mappings = (Mappings) result;
                        return switch (format.toUpperCase()) {
                            case "XML" -> ResultFormat.create(mappings, ResultFormat.format.XML_FORMAT).toString();
                            case "JSON" -> ResultFormat.create(mappings, ResultFormat.format.JSON_FORMAT).toString();
                            case "CSV" -> ResultFormat.create(mappings, ResultFormat.format.CSV_FORMAT).toString();
                            case "TSV" -> ResultFormat.create(mappings, ResultFormat.format.TSV_FORMAT).toString();
                            default -> ResultFormat.create(mappings, ResultFormat.format.MARKDOWN_FORMAT).toString();
                        };
                    }
                    case "CONSTRUCT", "DESCRIBE" -> {
                        Graph resultGraph = (Graph) result;
                        return switch (format.toUpperCase()) {
                            case "TURTLE" -> ResultFormat.create(resultGraph, ResultFormat.format.TURTLE_FORMAT).toString();
                            case "RDF/XML" -> ResultFormat.create(resultGraph, ResultFormat.format.RDF_XML_FORMAT).toString();
                            case "JSONLD" -> ResultFormat.create(resultGraph, ResultFormat.format.JSONLD_FORMAT).toString();
                            case "NTRIPLES" -> ResultFormat.create(resultGraph, ResultFormat.format.NTRIPLES_FORMAT).toString();
                            case "NQUADS" -> ResultFormat.create(resultGraph, ResultFormat.format.NQUADS_FORMAT).toString();
                            default -> ResultFormat.create(resultGraph, ResultFormat.format.TRIG_FORMAT).toString();
                        };
                    }
                    default -> throw new IllegalArgumentException("Unsupported query type for format conversion: " + queryType);
                }
            } catch (Exception e) {
                addLogEntry("Error formatting result: " + e.getMessage());
                return "Error formatting result: " + e.getMessage();
            }
        }

        // Getters et autres méthodes existantes...

        public void clearGraph() {
            this.graph = Graph.create();
            this.exec = QueryProcess.create(graph);
            if (ruleModel != null) {
                ruleModel.setGraph(this.graph);
            }
            addLogEntry("Graph cleared");
        }


    // Constructor for dependency injection and testing
    public ProjectDataModel(List<String> logEntries, SemanticGraph semanticGraph) {
        this.logEntries = logEntries;
        this.semanticGraph = semanticGraph;
        this.fileListModel = new FileListModel();
        this.ruleModel = new RuleModel();
    }

    public void addFile(String fileName) {
        fileListModel.addFile(fileName);
    }

    public void clearFiles() {
        fileListModel.clearFiles();
    }

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
                        ruleModel.loadRuleFile(ruleFile);
                        addLogEntry("Loaded rule file: " + ruleFile.getName());
                    } catch (Exception e) {
                        addLogEntry("Error loading rule file " + ruleFile.getName() + ": " + e.getMessage());
                    }
                }
            }
        }
    }

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
        configContent.append("RDFS_SUBSET=").append(ruleModel.isRDFSSubsetEnabled()).append("\n");
        configContent.append("RDFS_RL=").append(ruleModel.isRDFSRLEnabled()).append("\n");
        configContent.append("OWL_RL=").append(ruleModel.isOWLRLEnabled()).append("\n");
        configContent.append("OWL_RL_EXTENDED=").append(ruleModel.isOWLRLExtendedEnabled()).append("\n");
        configContent.append("OWL_RL_TEST=").append(ruleModel.isOWLRLTestEnabled()).append("\n");
        configContent.append("OWL_CLEAN=").append(ruleModel.isOWLCleanEnabled()).append("\n");
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

    // Delegated methods
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

    // Getters
    public FileListModel getFileListModel() {
        return fileListModel;
    }

    public RuleModel getRuleModel() {
        return ruleModel;
    }

    public int getRulesLoadedCount() {
        return rulesLoadedCount;
    }

    public void setRulesLoadedCount(int count) {
        this.rulesLoadedCount = count;
    }

    public List<File> getLoadedFiles() {
        return semanticGraph.getLoadedFiles();
    }

    public List<File> getLoadedRules() {
        return semanticGraph.getLoadedRules();
    }
}