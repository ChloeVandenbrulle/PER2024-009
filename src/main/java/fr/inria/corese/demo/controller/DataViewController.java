package fr.inria.corese.demo.controller;

import fr.inria.corese.demo.model.ButtonType;
import fr.inria.corese.demo.view.DataView;
import fr.inria.corese.demo.model.ProjectDataModel;
import javafx.fxml.FXML;
import javafx.scene.layout.HBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import java.io.File;

public class DataViewController {
    private DataView view;
    private ProjectDataModel model;
    private ButtonManager buttonManager;

    @FXML
    private HBox topButtonBox;
    @FXML
    private HBox fileActionBox;

    public DataViewController() {
        this.view = new DataView();
        this.model = new ProjectDataModel();
        initializeEventHandlers();
    }

    @FXML
    public void initialize() {
        buttonManager = new ButtonManager(new ProjectDataModel());

        // Ajout des boutons à la vue
        topButtonBox.getChildren().addAll(
                buttonManager.getButton(ButtonType.OPEN_PROJECT),
                buttonManager.getButton(ButtonType.SAVE_AS),
                buttonManager.getButton(ButtonType.SHOW_LOGS)
        );

        fileActionBox.getChildren().addAll(
                buttonManager.getButton(ButtonType.CLEAR_GRAPH),
                buttonManager.getButton(ButtonType.RELOAD_FILES),
                buttonManager.getButton(ButtonType.LOAD_FILES)
        );

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

        // File list handlers
        view.getFileListView().getClearGraphButton().setOnAction(e -> handleClearGraph());
        view.getFileListView().getReloadFilesButton().setOnAction(e -> handleReloadFiles());
        view.getFileListView().getLoadFilesButton().setOnAction(e -> handleLoadFiles());

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

    private void handleShowLogs() {
        // Implémenter l'affichage des logs
    }

    private void handleClearGraph() {
        model.clearGraph();
        updateView();
    }

    private void handleReloadFiles() {
        model.reloadFiles();
        updateView();
    }

    private void handleLoadFiles() {
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            model.loadFile(file);
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

        // Mettre à jour la liste des fichiers
        view.getFileListView().getFileList().setItems(model.getFileList());
    }

    public DataView getView() {
        return view;
    }
}