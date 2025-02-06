package fr.inria.corese.demo.controller;

import fr.inria.corese.demo.model.ButtonType;
import fr.inria.corese.demo.view.DataView;
import fr.inria.corese.demo.model.ProjectDataModel;
import fr.inria.corese.demo.view.FileListView;
import fr.inria.corese.demo.view.popup.*;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import java.io.File;

public class DataViewController {
    private DataView view;
    private ProjectDataModel model;
    private ButtonManager buttonManager;
    private PopupFactory popupFactory;

    @FXML
    private HBox topButtonBox;
    @FXML
    private HBox fileActionBox;
    @FXML
    private HBox configActionBox;
    @FXML
    private VBox fileListContainer;  // Au lieu de FileListView

    private FileListView fileListView;  // Ajoutez ce champ

    public DataViewController() {
        this.model = new ProjectDataModel();
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
        System.out.println("handleShowLogs called"); // Debug print
        try {
            LogDialog logDialog = (LogDialog) popupFactory.createPopup(PopupFactory.LOG_POPUP);
            System.out.println("LogDialog created"); // Debug print
            if (logDialog != null) {
                logDialog.displayPopup();
                System.out.println("LogDialog displayed"); // Debug print
            } else {
                System.out.println("LogDialog is null"); // Debug print
            }
        } catch (Exception e) {
            System.err.println("Error showing logs: " + e.getMessage());
            e.printStackTrace();
        }
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
                model.loadFile(file);
                model.addFile(file.getName());  // Ajoute le fichier à la liste
                model.addLogEntry("File loaded successfully: " + file.getName());

                // Afficher les informations du fichier
                IPopup fileInfoPopup = popupFactory.createPopup(PopupFactory.FILE_INFO_POPUP);
                ((FileInfoPopup) fileInfoPopup).show(file);

            } catch (Exception e) {
                String errorMessage = "Error loading file: " + e.getMessage();
                model.addLogEntry("ERROR: " + errorMessage);

                IPopup warningPopup = popupFactory.createPopup(PopupFactory.WARNING_POPUP);
                warningPopup.setMessage(errorMessage);
                ((WarningPopup) warningPopup).showAndWait();
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
        // Mettre à jour les statistiques
        view.getProjectStatisticsView().updateStatistics(
                model.getSemanticElementsCount(),
                model.getTripletCount(),
                model.getGraphCount(),
                model.getRulesLoadedCount()
        );
    }

    public DataView getView() {
        return view;
    }
}