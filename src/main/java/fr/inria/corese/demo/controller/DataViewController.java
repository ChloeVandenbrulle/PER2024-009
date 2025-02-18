package fr.inria.corese.demo.controller;

import fr.inria.corese.demo.enums.IconButtonType;
import fr.inria.corese.demo.model.ButtonType;
import fr.inria.corese.demo.model.RuleModel;
import fr.inria.corese.demo.view.DataView;
import fr.inria.corese.demo.model.ProjectDataModel;
import fr.inria.corese.demo.view.FileListView;
import fr.inria.corese.demo.view.TopBar;
import fr.inria.corese.demo.view.popup.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
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
    private ButtonManager buttonManager;
    private PopupFactory popupFactory;
    private RuleViewController ruleViewController;
    private RuleModel ruleModel;
    private final LogDialog logDialog;
    private FileListView fileListView;

    @FXML
    private HBox configActionBox;
    @FXML
    private VBox fileListContainer;
    @FXML
    private Label semanticElementsLabel;
    @FXML
    private Label tripletLabel;
    @FXML
    private Label graphLabel;
    @FXML
    private Label rulesLoadedLabel;
    @FXML
    private TopBar topBar;

    public DataViewController() {
        this.model = new ProjectDataModel();
        this.logDialog = new LogDialog(model);
        this.popupFactory = PopupFactory.getInstance(model);
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

        // Initialisation du ButtonManager
        buttonManager = new ButtonManager(model);

        if (fileListContainer != null) {
            fileListView = new FileListView();
            fileListView.setModel(model.getFileListModel());
            fileListView.setProjectDataModel(model);
            fileListContainer.getChildren().add(fileListView);
            VBox.setVgrow(fileListView, Priority.ALWAYS);

            fileListView.getClearButton().setOnAction(e -> handleClearGraph());
            fileListView.getReloadButton().setOnAction(e -> handleReloadFiles());
            fileListView.getLoadButton().setOnAction(e -> handleLoadFiles());
        }

        setupConfigButtons();

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fr/inria/corese/demo/rule-view.fxml"));
            VBox ruleView = loader.load();
            ruleViewController = loader.getController();
            ruleModel = new RuleModel();
            ruleViewController.injectDependencies(model, ruleModel);
            ruleViewController.initializeRules();

            if (configActionBox != null && configActionBox.getParent() instanceof VBox) {
                VBox parent = (VBox) configActionBox.getParent();

                // Create a scroll pane and set its properties
                ScrollPane scrollPane = new ScrollPane();
                scrollPane.setFitToWidth(true);
                scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
                scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
                scrollPane.getStyleClass().add("edge-to-edge");

                scrollPane.setMaxHeight(parent.getMaxHeight());
                scrollPane.setContent(ruleView);

                // Add the scroll pane to the parent
                parent.getChildren().add(0, scrollPane);
                VBox.setVgrow(scrollPane, Priority.ALWAYS);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setupConfigButtons() {
        Button loadRuleFileButton = buttonManager.getButton(ButtonType.LOAD_RULE_FILE);
        loadRuleFileButton.setOnAction(event -> handleLoadRuleFile());

        configActionBox.getChildren().addAll(loadRuleFileButton);
    }

    private void handleOpenProject() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File selectedDirectory = directoryChooser.showDialog(null);
        if (selectedDirectory != null) {
            model.loadProject(selectedDirectory);
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

    private void handleReloadFiles() {
        if (fileListView.confirmReload()) {
            model.reloadFiles();
            updateView();
        }
    }

    private void handleShowLogs() {
        logDialog.displayPopup();
    }

    private void handleLoadFiles() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("TTL files", "*.ttl")
        );

        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            try {
                model.addLogEntry("Starting to load file: " + file.getName());

                // Check if there are already files loaded
                if (!model.getFileListModel().getFiles().isEmpty()) {
                    // Show warning popup before loading
                    IPopup warningPopup = popupFactory.createPopup(PopupFactory.WARNING_POPUP);
                    warningPopup.setMessage("Loading this file will reset the current graph. Do you want to continue?");
                    boolean result = ((WarningPopup) warningPopup).getResult();

                    if (!result) {
                        model.addLogEntry("File loading cancelled by user: " + file.getName());
                        return;
                    }
                }

                model.loadFile(file);
                model.addFile(file.getName());
                model.addLogEntry("File loaded successfully: " + file.getName());

                // Dans handleLoadFiles après le chargement réussi
                IPopup successPopup = popupFactory.createPopup(PopupFactory.TOAST_NOTIFICATION);
                successPopup.setMessage("File '" + file.getName() + "' has been successfully loaded!");
                successPopup.displayPopup();

            } catch (Exception e) {
                String errorMessage = "Error loading file: " + e.getMessage();
                model.addLogEntry("ERROR: " + errorMessage);

                IPopup errorPopup = popupFactory.createPopup(PopupFactory.WARNING_POPUP);
                errorPopup.setMessage(errorMessage);
                ((WarningPopup) errorPopup).getResult();
            }
            updateView();
        }
    }

    private void handleLoadRuleFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Rule files (*.rul)", "*.rul")
        );

        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            try {
                // Ajout d'un log pour suivre le chargement
                model.addLogEntry("Starting to load rule file: " + selectedFile.getName());

                // Chargement de la règle via le RuleModel
                ruleModel.loadRuleFile(selectedFile);

                // Message de succès dans les logs
                model.addLogEntry("Rule file loaded successfully: " + selectedFile.getName());

                // Afficher une notification de succès
                IPopup successPopup = popupFactory.createPopup(PopupFactory.TOAST_NOTIFICATION);
                successPopup.setMessage("Rule file '" + selectedFile.getName() + "' has been successfully loaded!");
                successPopup.displayPopup();

                // Mettre à jour l'affichage
                updateView();

            } catch (Exception e) {
                String errorMessage = "Error loading rule file: " + e.getMessage();
                model.addLogEntry("ERROR: " + errorMessage);

                // Afficher une popup d'erreur
                IPopup errorPopup = popupFactory.createPopup(PopupFactory.WARNING_POPUP);
                errorPopup.setMessage(errorMessage);
                ((WarningPopup) errorPopup).getResult();
            }
        }
    }

    private void updateView() {
        if (ruleViewController != null) {
            ruleViewController.updateView();
        }

        // Update statistics labels
        if (semanticElementsLabel != null) {
            semanticElementsLabel.setText("Number of semantic elements loaded: " + model.getSemanticElementsCount());
            tripletLabel.setText("Number of triplet: " + model.getTripletCount());
            graphLabel.setText("Number of graph: " + model.getGraphCount());

            // Utilisez la méthode getLoadedRulesCount pour obtenir le nombre de règles à jour
            int ruleCount = 0;
            if (ruleModel != null) {
                ruleCount = ruleModel.getLoadedRulesCount();
            }
            rulesLoadedLabel.setText("Number of rules loaded: " + ruleCount);
        }
    }

    public DataView getView() {
        return view;
    }
}