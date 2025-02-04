package fr.inria.corese.demo.controller;

import fr.inria.corese.demo.model.CodeEditorModel;
import fr.inria.corese.demo.view.CodeMirrorView;
import fr.inria.corese.demo.view.NavigationBarView;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import atlantafx.base.theme.Styles;
import java.io.File;
import java.nio.file.Files;

public class ValidationViewController {

    @FXML private Button newFileButton;
    @FXML private Button openFileButton;
    @FXML private Button saveButton;
    @FXML private TreeView<String> fileTreeView;
    @FXML private CodeMirrorView editorContainer;
    @FXML private BorderPane mainContainer;
    @FXML private NavigationBarView navigationBarView;
    private ValidationResultController validationResultController;
    private ValidationPageController validationPageController;
    private CodeEditorModel codeEditorModel;
    private IconButtonBarController iconButtonBarController;
    private NavigationBarController navigationBarController;

    @FXML
    public void initialize() {
        navigationBarController = new NavigationBarController(mainContainer);
        codeEditorModel = new CodeEditorModel();
        validationPageController = new ValidationPageController();
        validationResultController = new ValidationResultController();

        setupFileTree();
        setupButtons();
        initializeEditor();

        validationPageController.setModel(codeEditorModel);
        validationResultController.setModel(codeEditorModel);

        navigationBarController.getView().getValidationButton().setOnAction(e -> {
            navigationBarController.selectView("validation-view");
            navigationBarController.getView().setButtonSelected(
                    navigationBarController.getView().getValidationButton()
            );
        });
    }

    private void initializeEditor() {
        Platform.runLater(() -> {
            // Contenu initial
            String initialContent = """
                @prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .
                @prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> .
                @prefix ex: <http://example.org/> .
                
                # Exemple de triplet RDF
                ex:resource1 rdf:type rdfs:Resource .
                """;

            editorContainer.setContent(initialContent);
            codeEditorModel.setContent(initialContent);

            // Écouter les changements
            editorContainer.contentProperty().addListener((obs, oldVal, newVal) -> {
                System.out.println("Contenu modifié");
                codeEditorModel.setContent(newVal);
            });
        });
    }

    private void initializeIconButtonBar() {
        iconButtonBarController = new IconButtonBarController();
    }

    private void setupFileTree() {
        TreeItem<String> root = new TreeItem<>("Project");
        root.setExpanded(true);

        TreeItem<String> src = new TreeItem<>("src");
        TreeItem<String> resources = new TreeItem<>("resources");

        root.getChildren().addAll(src, resources);
        fileTreeView.setRoot(root);
    }

    private void setupButtons() {
        // Configuration des actions des boutons
        newFileButton.setOnAction(e -> createNewFile());
        openFileButton.setOnAction(e -> openFile());
        saveButton.setOnAction(e -> saveFile());

        // Application des styles AtlantaFX
        newFileButton.getStyleClass().addAll(Styles.BUTTON_OUTLINED);
        openFileButton.getStyleClass().addAll(Styles.BUTTON_OUTLINED);
        saveButton.getStyleClass().addAll(Styles.BUTTON_OUTLINED);
    }

    private void createNewFile() {
        editorContainer.setContent("");
    }

    private void openFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("RDF Files", "*.ttl", "*.rdf", "*.n3"),
                new FileChooser.ExtensionFilter("All Files", "*.*")
        );

        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            try {
                String content = Files.readString(file.toPath());
                editorContainer.setContent(content);
            } catch (Exception e) {
                showError("Error Opening File", "Could not open the file: " + e.getMessage());
            }
        }
    }

    private void saveFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Turtle Files", "*.ttl"),
                new FileChooser.ExtensionFilter("RDF/XML Files", "*.rdf"),
                new FileChooser.ExtensionFilter("N3 Files", "*.n3")
        );

        File file = fileChooser.showSaveDialog(null);
        if (file != null) {
            try {
                Files.writeString(file.toPath(), editorContainer.getContent());
            } catch (Exception e) {
                showError("Error Saving File", "Could not save the file: " + e.getMessage());
            }
        }
    }

    private void showError(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}