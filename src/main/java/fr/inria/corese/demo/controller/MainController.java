package fr.inria.corese.demo.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import lombok.Getter;
import org.fxmisc.richtext.CodeArea;
import atlantafx.base.theme.Styles;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

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
    @FXML private StackPane editorContainer;

    @Getter
    private CodeArea codeArea;
    private WebView webView;

    @FXML
    public void initialize() {
        setupCodeEditor();
        setupFileTree();
        setupButtons();
    }

    private void setupCodeEditor() {
        // Création de l'éditeur de code avec RichTextFX
        codeArea = new CodeArea();
        codeArea.setStyle("-fx-font-family: 'monospace';");

        // Configuration de la coloration syntaxique RDF/Turtle
        // TODO: Implémenter la coloration syntaxique

        editorContainer.getChildren().add(codeArea);
    }

    private void setupFileTree() {
        // Configuration de l'arborescence des fichiers
        TreeItem<String> root = new TreeItem<>("Project");
        root.setExpanded(true);

        // Exemple d'arborescence
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

        // Configuration des boutons du menu latéral
        dataButton.setOnAction(e -> switchToDataView());
        rdfEditorButton.setOnAction(e -> switchToRDFEditor());
        validationButton.setOnAction(e -> switchToValidation());
        queryButton.setOnAction(e -> switchToQuery());
        settingsButton.setOnAction(e -> switchToSettings());

        // Application du thème AtlantaFX
        applyAtlantaFXStyles();
    }

    private void applyAtlantaFXStyles() {
        newFileButton.getStyleClass().add(Styles.BUTTON_OUTLINED);
        openFileButton.getStyleClass().add(Styles.BUTTON_OUTLINED);
        saveButton.getStyleClass().add(Styles.BUTTON_OUTLINED);

        dataButton.getStyleClass().addAll("rounded-button", Styles.BUTTON_OUTLINED, Styles.ACCENT);
        rdfEditorButton.getStyleClass().addAll("rounded-button", Styles.BUTTON_OUTLINED, Styles.ACCENT);
        validationButton.getStyleClass().addAll("rounded-button", Styles.BUTTON_OUTLINED, Styles.ACCENT);
        queryButton.getStyleClass().addAll("rounded-button", Styles.BUTTON_OUTLINED, Styles.ACCENT);
        settingsButton.getStyleClass().addAll("rounded-button", Styles.BUTTON_OUTLINED, Styles.ACCENT);
    }

    private void createNewFile() {
        // TODO: Implémenter la création de nouveau fichier
    }

    private void openFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("RDF Files", "*.ttl", "*.rdf", "*.n3"),
                new FileChooser.ExtensionFilter("All Files", "*.*")
        );

        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            // TODO: Charger le fichier dans l'éditeur
        }
    }

    private void saveFile() {
        // TODO: Implémenter la sauvegarde de fichier
    }

    private void switchToDataView() {
        // TODO: Implémenter le changement de vue
    }

    private void switchToRDFEditor() {
        // TODO: Implémenter le changement de vue
    }

    private void switchToValidation() {
        // TODO: Implémenter le changement de vue
    }

    private void switchToQuery() {
        // TODO: Implémenter le changement de vue
    }

    private void switchToSettings() {
        // TODO: Implémenter le changement de vue
    }
}