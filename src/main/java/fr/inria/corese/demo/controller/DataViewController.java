// Modifications à DataViewController.java

package fr.inria.corese.demo.controller;

import fr.inria.corese.demo.enums.icon.IconButtonType;
import fr.inria.corese.demo.manager.ApplicationStateManager;
import fr.inria.corese.demo.model.fileList.FileItem;
import fr.inria.corese.demo.view.DataView;
import fr.inria.corese.demo.model.ProjectDataModel;
import fr.inria.corese.demo.view.FileListView;
import fr.inria.corese.demo.view.TopBar;
import fr.inria.corese.demo.factory.popup.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class DataViewController {
    private DataView view;
    private ProjectDataModel model;
    private PopupFactory popupFactory;
    private RuleViewController ruleViewController;
    private LogDialog logDialog;
    private FileListView fileListView;
    private ApplicationStateManager stateManager;


    // Injection FXML
    @FXML private HBox configActionBox;
    @FXML private VBox fileListContainer;
    @FXML private VBox rulesContainer;
    @FXML private Label semanticElementsLabel;
    @FXML private Label tripletLabel;
    @FXML private Label graphLabel;
    @FXML private Label rulesLoadedLabel;
    @FXML private TopBar topBar;

    public DataViewController() {
        this.stateManager = ApplicationStateManager.getInstance();
        this.popupFactory = PopupFactory.getInstance(model);

        System.out.println("DataViewController initialized");
    }

    @FXML
    public void initialize() {
        // Configuration de la TopBar
        topBar.addLeftButtons(List.of(
                IconButtonType.OPEN_FILE,
                IconButtonType.SAVE
        ));

        topBar.addRightButtons(List.of(
                IconButtonType.LOGS
        ));

        // Configuration des handlers pour les boutons
        topBar.setOnAction(IconButtonType.OPEN_FILE, this::handleOpenProject);
        topBar.setOnAction(IconButtonType.SAVE, this::handleSaveAs);
        topBar.setOnAction(IconButtonType.LOGS, this::handleShowLogs);
    }

    public void setProjectDataModel(ProjectDataModel model) {
        // Utiliser le modèle passé, mais s'assurer qu'il est synchronisé avec le gestionnaire d'état
        this.model = model;
        this.stateManager = ApplicationStateManager.getInstance();
        this.popupFactory = PopupFactory.getInstance(model);

        // Log de débogage
        System.out.println("=== DataViewController Model Setup ===");
        System.out.println("Received ProjectDataModel: " + model);

        // Initialiser les composants avec le modèle
        initializeComponents();
    }

    private void initializeComponents() {
        // Initialisation de la liste de fichiers
        if (fileListContainer != null) {
            setupFileList();
        }

        // Initialisation des règles
        if (rulesContainer != null) {
            setupRulesView();
        }

        if (ruleViewController != null) {
            ruleViewController.injectDependencies(model);
        }

        // Mise à jour de la vue
        updateView();
    }

    /**
     * Initializes and sets up the file list view component.
     * Configures the file list view and its action buttons.
     */
    private void setupFileList() {
        if (fileListContainer == null) {
            System.err.println("Error: fileListContainer is null in setupFileList");
            return;
        }

        try {
            fileListView = new FileListView();

            if (model != null) {
                fileListView.setModel(model.getFileListModel());
                fileListView.setProjectDataModel(model);
            } else if (stateManager != null) {
                // Fallback to use the state manager's model if direct model injection failed
                ProjectDataModel stateModel = stateManager.getProjectDataModel();
                if (stateModel != null) {
                    fileListView.setModel(stateModel.getFileListModel());
                    fileListView.setProjectDataModel(stateModel);
                    // Update our reference to the model
                    this.model = stateModel;
                } else {
                    System.err.println("Error: Both model and stateManager.getProjectDataModel() are null");
                }
            } else {
                System.err.println("Error: Both model and stateManager are null");
            }

            fileListContainer.getChildren().add(fileListView);
            VBox.setVgrow(fileListView, Priority.ALWAYS);

            // Set up button event handlers only if fileListView was successfully initialized
            if (fileListView != null) {
                Button clearButton = fileListView.getClearButton();
                Button reloadButton = fileListView.getReloadButton();
                Button loadButton = fileListView.getLoadButton();

                if (clearButton != null) {
                    clearButton.setOnAction(e -> handleClearGraph());
                }

                if (reloadButton != null) {
                    reloadButton.setOnAction(e -> handleReloadFiles());
                }

                if (loadButton != null) {
                    loadButton.setOnAction(e -> handleLoadFiles());
                }
            }

            if (model != null && model.getFileListModel() != null && fileListView != null) {
                fileListView.getFileList().setItems(null); // Déconnecter temporairement
                fileListView.getFileList().setItems(model.getFileListModel().getFiles()); // Reconnecter
            }

        } catch (Exception e) {
            System.err.println("Error setting up file list view: " + e.getMessage());
            e.printStackTrace();
        }
    }


    private void handleReloadFiles() {
        model.reloadFiles();
        updateView();
    }

    private void setupRulesView() {
        try {
            System.out.println("Loading rule view...");

            // Chargement du FXML pour la vue des règles
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fr/inria/corese/demo/rule-view.fxml"));
            VBox ruleView = loader.load();

            // Récupération du contrôleur
            ruleViewController = loader.getController();

            // Injecter directement le ProjectDataModel - toutes les fonctions de règles sont maintenant là
            ruleViewController.injectDependencies(model);

            // Placement de la vue dans un ScrollPane
            ScrollPane scrollPane = new ScrollPane(ruleView);
            scrollPane.setFitToWidth(true);
            scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
            scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
            scrollPane.getStyleClass().add("edge-to-edge");

            // Ajout du ScrollPane au conteneur de règles
            rulesContainer.getChildren().add(scrollPane);
            VBox.setVgrow(scrollPane, Priority.ALWAYS);

            // Initialisation des règles
            ruleViewController.initializeRules();

            System.out.println("Rule view loaded successfully");

            if (ruleViewController != null) {
                // Configurer les actions pour activer/désactiver les règles
                ruleViewController.setOWLRLAction(() -> {
                    // Activer les règles OWL RL via le gestionnaire d'état
                    stateManager.setOWLRLEnabled(true);
                    System.out.println("OWL RL Rules Activated via StateManager");
                });

                ruleViewController.setOWLRLExtendedAction(() -> {
                    // Activer les règles OWL RL Extended via le gestionnaire d'état
                    stateManager.setOWLRLExtendedEnabled(true);
                    System.out.println("OWL RL Extended Rules Activated via StateManager");
                });
            }
        } catch (IOException e) {
            System.err.println("Error loading rule view: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void handleOpenProject() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File selectedDirectory = directoryChooser.showDialog(null);
        if (selectedDirectory != null) {
            // Utiliser le gestionnaire d'état pour charger le projet
            ProjectDataModel projectDataModel = stateManager.getProjectDataModel();
            projectDataModel.loadProject(selectedDirectory);

            // Mettre à jour la vue
            updateView();
        }
    }

    private void handleSaveAs() {
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showSaveDialog(null);
        if (file != null) {
            model.saveProject(file);
        }
    }

    private void handleClearGraph() {
        IPopup confirmPopup = popupFactory.createPopup(PopupFactory.CLEAR_GRAPH_CONFIRMATION);
        confirmPopup.setMessage("Are you sure you want to clear the graph? This action cannot be undone.");
        confirmPopup.displayPopup();

        if (((ClearGraphConfirmationPopup) confirmPopup).getResult()) {
            model.clearGraph();
            model.clearFiles();
            updateView();

            // Afficher une notification de succès
            IPopup successPopup = popupFactory.createPopup(PopupFactory.TOAST_NOTIFICATION);
            successPopup.setMessage("Graph has been cleared successfully!");
            successPopup.displayPopup();
        }
    }

    public void loadFile() {
        FileChooser fileChooser = new FileChooser();
        File selectedFile = fileChooser.showOpenDialog(null);

        if (selectedFile != null) {
            // Utilisation du gestionnaire d'état global
            ApplicationStateManager.getInstance().loadFile(selectedFile);

            // Mise à jour de la vue si nécessaire
            updateView();
        }
    }

    public void activateOWLRules() {
        ApplicationStateManager.getInstance().setOWLRLEnabled(true);
        ApplicationStateManager.getInstance().setOWLRLExtendedEnabled(true);
    }

    private void handleShowLogs() {
        logDialog.displayPopup();
    }

    /**
     * Gère le chargement de fichiers.
     * Ouvre un sélecteur de fichiers et charge le fichier sélectionné dans le graphe.
     */
    private void handleLoadFiles() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("TTL files", "*.ttl")
        );

        // Get the correct scene from available components
        javafx.stage.Window window = null;

        // Try to get window from fileListView
        if (fileListView != null && fileListView.getScene() != null) {
            window = fileListView.getScene().getWindow();
        }
        // Try to get window from fileListContainer
        else if (fileListContainer != null && fileListContainer.getScene() != null) {
            window = fileListContainer.getScene().getWindow();
        }
        // Try to get window from topBar
        else if (topBar != null && topBar.getScene() != null) {
            window = topBar.getScene().getWindow();
        }
        // Try to get window from any available FXML component
        else if (semanticElementsLabel != null && semanticElementsLabel.getScene() != null) {
            window = semanticElementsLabel.getScene().getWindow();
        }

        if (window == null) {
            System.err.println("Could not find a valid window to open file dialog. Using null parent window.");
        }

        File file = fileChooser.showOpenDialog(window);
        if (file != null) {
            try {
                stateManager.processLoadedFile(file);

                // Utiliser le gestionnaire d'état pour charger le fichier
                stateManager.loadFile(file);

                // Log de débogage
                System.out.println("File loaded through StateManager: " + file.getName());
                System.out.println("Loaded Files: " + stateManager.getLoadedFiles());

                // Mise à jour de la vue
                updateView();

                // Afficher une notification de succès
                if (popupFactory != null) {
                    IPopup successPopup = popupFactory.createPopup(PopupFactory.TOAST_NOTIFICATION);
                    successPopup.setMessage("File '" + file.getName() + "' has been successfully loaded!");
                    successPopup.displayPopup();
                } else {
                    // Fallback to standard alerts if popupFactory is unavailable
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Success");
                    alert.setHeaderText(null);
                    alert.setContentText("File '" + file.getName() + "' has been successfully loaded!");
                    alert.showAndWait();
                }

            } catch (Exception e) {
                String errorMessage = "Error loading file: " + e.getMessage();

                // Gestion de l'erreur
                if (popupFactory != null) {
                    IPopup errorPopup = popupFactory.createPopup(PopupFactory.WARNING_POPUP);
                    errorPopup.setMessage(errorMessage);
                    ((WarningPopup) errorPopup).getResult();
                } else {
                    // Fallback to standard alerts if popupFactory is unavailable
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText("Error Loading File");
                    alert.setContentText(errorMessage);
                    alert.showAndWait();
                }
            }
        }
    }

    private void updateView() {
        ProjectDataModel projectDataModel = stateManager.getProjectDataModel();

        if (ruleViewController != null) {
            ruleViewController.updateView();
        }

        System.out.println("=== Updating DataView ===");
        if (model != null && model.getFileListModel() != null) {
            System.out.println("Files in model: " + model.getFileListModel().getFiles().size());
            for (FileItem item : model.getFileListModel().getFiles()) {
                System.out.println("  - " + item.getName());
            }
        } else {
            System.out.println("model or fileListModel is null");
        }

        // Update statistics labels
        if (semanticElementsLabel != null) {
            semanticElementsLabel.setText("Number of semantic elements loaded: " + projectDataModel.getSemanticElementsCount());
            tripletLabel.setText("Number of triplet: " + projectDataModel.getTripletCount());
            graphLabel.setText("Number of graph: " + projectDataModel.getGraphCount());

            // Utiliser le nombre de règles chargées
            rulesLoadedLabel.setText("Number of rules loaded: " + projectDataModel.getLoadedRulesCount());
        }

        // Log de débogage
        System.out.println("View Updated:");
        System.out.println("  Semantic Elements: " + projectDataModel.getSemanticElementsCount());
        System.out.println("  Triplets: " + projectDataModel.getTripletCount());
        System.out.println("  Loaded Rules: " + projectDataModel.getLoadedRulesCount());
    }

    public DataView getView() {
        return view;
    }
}