package fr.inria.corese.demo.controller;

import fr.inria.corese.demo.enums.IconButtonBarType;
import fr.inria.corese.demo.factory.IconButtonBarFactory;
import fr.inria.corese.demo.model.CodeEditorModel;
import fr.inria.corese.demo.view.CodeMirrorView;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.control.*;

public class RDFEditorViewController {
    @FXML private BorderPane mainContainer;
    @FXML private BorderPane contentContainer;
    @FXML private Button newFileButton;
    @FXML private Button openFileButton;
    @FXML private Button saveButton;
    @FXML private TreeView<String> fileTreeView;
    @FXML private CodeMirrorView editorContainer;
    @FXML private VBox iconButtonContainer;

    private CodeEditorModel codeEditorModel;
    private IconButtonBarController iconButtonBarController;

    private boolean isUpdatingContent = false;


    @FXML
    public void initialize() {
        checkFXMLInjections();

        try {
            System.out.println("Initializing RDFEditorViewController");

            // Initialize the model first
            codeEditorModel = new CodeEditorModel();

            // Initialize components
            setupFileTree();
            setupButtons();
            initializeEditor();
            initializeIconButtonBar();

            System.out.println("RDFEditorViewController initialization complete");
        } catch (Exception e) {
            System.err.println("Error during initialization:");
            e.printStackTrace();
        }
    }

    private void initializeEditor() {
        if (editorContainer == null) {
            System.err.println("Editor container is null!");
            return;
        }

        Platform.runLater(() -> {
            try {
                // Contenu initial
                String initialContent = """
                    @prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .
                    @prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> .
                    @prefix ex: <http://example.org/> .
                    
                    # Exemple de triplet RDF
                    ex:resource1 rdf:type rdfs:Resource .
                    """;

                editorContainer.setContent(initialContent);

                if (codeEditorModel != null) {
                    codeEditorModel.setContent(initialContent);

                    // Écouter les changements de la vue
                    editorContainer.contentProperty().addListener((obs, oldVal, newVal) -> {
                        System.out.println("Contenu modifié");
                        codeEditorModel.recordCurrentChange(newVal);
                    });
                } else {
                    System.err.println("CodeEditorModel is null!");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            // Écouter les changements du modèle
            codeEditorModel.contentProperty().addListener((obs, oldVal, newVal) -> {
                if (!isUpdatingContent) {
                    isUpdatingContent = true;
                    try {
                        editorContainer.setContent(newVal);
                    } finally {
                        isUpdatingContent = false;
                    }
                }
            });
        });
    }


    private void initializeIconButtonBar() {
        iconButtonBarController = IconButtonBarFactory.create(IconButtonBarType.RDF_EDITOR);
        iconButtonContainer.getChildren().add(iconButtonBarController.getView());
        iconButtonBarController.getModel().setCodeEditorModel(codeEditorModel);
    }

    private void setupFileTree() {
        if (fileTreeView != null) {
            TreeItem<String> root = new TreeItem<>("Project");
            root.setExpanded(true);
            TreeItem<String> src = new TreeItem<>("src");
            TreeItem<String> resources = new TreeItem<>("resources");
            root.getChildren().addAll(src, resources);
            fileTreeView.setRoot(root);
            System.out.println("File tree initialized");
        } else {
            System.err.println("fileTreeView is null!");
        }
    }

    private void setupButtons() {
        if (newFileButton != null) {
            newFileButton.setOnAction(e -> createNewFile());
        }
        if (openFileButton != null) {
            openFileButton.setOnAction(e -> openFile());
        }
        if (saveButton != null) {
            saveButton.setOnAction(e -> saveFile());
        }
        System.out.println("Buttons initialized");
    }

    private void checkFXMLInjections() {
        StringBuilder missingInjections = new StringBuilder();
        if (mainContainer == null) missingInjections.append("mainContainer, ");
        if (contentContainer == null) missingInjections.append("contentContainer, ");
        if (newFileButton == null) missingInjections.append("newFileButton, ");
        if (openFileButton == null) missingInjections.append("openFileButton, ");
        if (saveButton == null) missingInjections.append("saveButton, ");
        if (fileTreeView == null) missingInjections.append("fileTreeView, ");
        if (editorContainer == null) missingInjections.append("editorContainer, ");
        if (iconButtonContainer == null) missingInjections.append("iconButtonContainer, ");

        if (missingInjections.length() > 0) {
            System.err.println("Missing FXML injections: " + missingInjections);
        }
    }



    private void createNewFile() {
        if (editorContainer != null) {
            editorContainer.setContent("");
        }
    }

    private void openFile() {
        // Implémentation de l'ouverture de fichier
    }

    private void saveFile() {
        // Implémentation de la sauvegarde de fichier
    }
}