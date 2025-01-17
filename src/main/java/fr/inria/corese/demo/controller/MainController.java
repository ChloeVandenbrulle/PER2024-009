package fr.inria.corese.demo.controller;

import fr.inria.corese.demo.view.CodeMirrorView;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import atlantafx.base.theme.Styles;
import java.io.File;
import java.nio.file.Files;

public class MainController {

    @FXML private Button dataButton;
    @FXML private Button rdfEditorButton;
    @FXML private Button validationButton;
    @FXML private Button queryButton;
    @FXML private Button settingsButton;
    @FXML private Button newFileButton;
    @FXML private Button openFileButton;
    @FXML private Button saveButton;
    @FXML private TreeView<String> fileTreeView;
    @FXML private CodeMirrorView editorContainer;

    @FXML
    public void initialize() {
        setupFileTree();
        setupButtons();
        initializeEditor();
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

            // Écouter les changements
            editorContainer.contentProperty().addListener((obs, oldVal, newVal) -> {
                System.out.println("Contenu modifié");
            });
        });
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

        dataButton.setOnAction(e -> switchToDataView());
        rdfEditorButton.setOnAction(e -> switchToRDFEditor());
        validationButton.setOnAction(e -> switchToValidation());
        queryButton.setOnAction(e -> switchToQuery());
        settingsButton.setOnAction(e -> switchToSettings());

        // Application des styles AtlantaFX
        newFileButton.getStyleClass().addAll(Styles.BUTTON_OUTLINED);
        openFileButton.getStyleClass().addAll(Styles.BUTTON_OUTLINED);
        saveButton.getStyleClass().addAll(Styles.BUTTON_OUTLINED);

        // Styles spéciaux pour les boutons du menu latéral
        dataButton.getStyleClass().addAll("rounded-button", Styles.BUTTON_OUTLINED, Styles.ACCENT);
        rdfEditorButton.getStyleClass().addAll("rounded-button", Styles.BUTTON_OUTLINED, Styles.ACCENT);
        validationButton.getStyleClass().addAll("rounded-button", Styles.BUTTON_OUTLINED, Styles.ACCENT);
        queryButton.getStyleClass().addAll("rounded-button", Styles.BUTTON_OUTLINED, Styles.ACCENT);
        settingsButton.getStyleClass().addAll("rounded-button", Styles.BUTTON_OUTLINED, Styles.ACCENT);

        // Configuration de la largeur maximale pour les boutons du menu
        dataButton.setMaxWidth(Double.MAX_VALUE);
        rdfEditorButton.setMaxWidth(Double.MAX_VALUE);
        validationButton.setMaxWidth(Double.MAX_VALUE);
        queryButton.setMaxWidth(Double.MAX_VALUE);
        settingsButton.setMaxWidth(Double.MAX_VALUE);
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

    private void switchToDataView() {
        System.out.println("Switching to Data view");
    }

    private void switchToRDFEditor() {
        System.out.println("Switching to RDF Editor view");
    }

    private void switchToValidation() {
        System.out.println("Switching to Validation view");
    }

    private void switchToQuery() {
        System.out.println("Switching to Query view");
    }

    private void switchToSettings() {
        System.out.println("Switching to Settings view");
    }
}