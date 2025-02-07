package fr.inria.corese.demo.controller;

import fr.inria.corese.demo.model.ButtonType;
import fr.inria.corese.demo.model.RuleModel;
import fr.inria.corese.demo.view.DataView;
import fr.inria.corese.demo.model.ProjectDataModel;
import fr.inria.corese.demo.view.FileListView;
import fr.inria.corese.demo.view.popup.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import java.io.File;
import java.io.IOException;
import java.util.Optional;

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
    private HBox topButtonBox;
    @FXML
    private HBox fileActionBox;
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


    public DataViewController() {
        this.model = new ProjectDataModel();
        this.logDialog = new LogDialog(model);
        this.popupFactory = PopupFactory.getInstance(model);
    }

    @FXML
    public void initialize() {
        System.out.println("Initializing DataViewController");
        buttonManager = new ButtonManager(model);

        // Créer et configurer FileListView
        fileListView = new FileListView();
        fileListView.setModel(model.getFileListModel());

        // Ajouter FileListView au conteneur
        if (fileListContainer != null) {
            fileListContainer.getChildren().add(fileListView);
            VBox.setVgrow(fileListView, Priority.ALWAYS);

            fileListView.getClearButton().setOnAction(e -> handleClearGraph());
            fileListView.getReloadButton().setOnAction(e -> handleReloadFiles());
            fileListView.getLoadButton().setOnAction(e -> handleLoadFiles());
        }

        Button showLogsButton = buttonManager.getButton(ButtonType.SHOW_LOGS);
        showLogsButton.setOnAction(e -> {
            System.out.println("Log button clicked directly");
            handleShowLogs();
        });

        // Créer une Region pour l'espacement
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Créer un HBox pour les boutons de gauche avec un petit espacement
        HBox leftButtons = new HBox(5); // 5 pixels d'espacement
        leftButtons.getChildren().addAll(
                buttonManager.getButton(ButtonType.OPEN_PROJECT),
                buttonManager.getButton(ButtonType.SAVE_AS)
        );

        // Ajouter tous les éléments dans l'ordre
        topButtonBox.getChildren().clear(); // Pour s'assurer qu'il n'y a pas de boutons existants
        topButtonBox.getChildren().addAll(
                leftButtons,  // Les boutons de gauche groupés
                spacer,       // Le spacer qui pousse le dernier bouton à droite
                showLogsButton
        );

        configActionBox.getChildren().addAll(
                buttonManager.getButton(ButtonType.LOAD_RULE_FILE)
        );

        // Initialisation des styles
        topButtonBox.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                String cssPath = getClass().getResource("/styles/buttons.css").toExternalForm();
                newScene.getStylesheets().add(cssPath);
            }
        });

        ruleModel = new RuleModel();
        ruleViewController = new RuleViewController(ruleModel);
        ruleViewController.initialize();

    }

    private void initializeEventHandlers() {
        view.getOpenProjectButton().setOnAction(e -> handleOpenProject());
        view.getSaveAsButton().setOnAction(e -> handleSaveAs());
        view.getShowLogsButton().setOnAction(e -> handleShowLogs());

        // File action handlers
        view.getFileListView().getClearButton().setOnAction(e -> handleClearGraph());
        view.getFileListView().getReloadButton().setOnAction(e -> handleReloadFiles());
        view.getFileListView().getLoadButton().setOnAction(e -> handleLoadFiles());

        // Rule config handlers
        view.getRuleConfigView().getLoadRuleFileButton().setOnAction(e -> handleLoadRuleFile());
        view.getRuleConfigView().getMyRuleFileCheckbox().setOnAction(e -> handleMyRuleFileToggle());
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
        model.clearGraph();
        model.clearFiles();
        updateView();
    }

    private void handleReloadFiles() {
        model.reloadFiles();
        updateView();
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

                // Warn user about graph reset
                IPopup warningPopup = popupFactory.createPopup(PopupFactory.WARNING_POPUP);
                warningPopup.setMessage("Loading this file will reset the current graph. Do you want to continue?");
                boolean result = ((WarningPopup) warningPopup).getResult();

                if (result) {
                    model.loadFile(file);
                    model.addFile(file.getName());
                    model.addLogEntry("File loaded successfully: " + file.getName());
                }
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
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            model.loadRuleFile(file);
            updateView();
        }
    }

    private void handleMyRuleFileToggle() {
        boolean selected = view.getRuleConfigView().getMyRuleFileCheckbox().isSelected();
        model.setMyRuleFileEnabled(selected);
        updateView();
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
            rulesLoadedLabel.setText("Number of rules loaded: " + model.getRulesLoadedCount());
        }
    }

    public DataView getView() {
        return view;
    }
}