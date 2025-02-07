package fr.inria.corese.demo.controller;

import fr.inria.corese.demo.enums.IconButtonBarType;
import fr.inria.corese.demo.factory.IconButtonBarFactory;
import fr.inria.corese.demo.model.CodeEditorModel;
import fr.inria.corese.demo.model.FileExplorerModel;
import fr.inria.corese.demo.view.CodeMirrorView;
import fr.inria.corese.demo.view.FileExplorerView;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.control.*;

public class RDFEditorViewController {
    @FXML private BorderPane mainContainer;
    @FXML private BorderPane contentContainer;
    @FXML private VBox fileExplorerContainer;
    @FXML private CodeMirrorView editorContainer;
    @FXML private VBox iconButtonContainer;

    private CodeEditorModel codeEditorModel;
    private IconButtonBarController iconButtonBarController;
    private FileExplorerController fileExplorerController;
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
        fileExplorerController = new FileExplorerController();
        fileExplorerContainer.getChildren().add(fileExplorerController.getView());
    }


    private void checkFXMLInjections() {
        StringBuilder missingInjections = new StringBuilder();
        if (mainContainer == null) missingInjections.append("mainContainer, ");
        if (contentContainer == null) missingInjections.append("contentContainer, ");
        if (fileExplorerContainer == null) missingInjections.append("fileTreeView, ");
        if (editorContainer == null) missingInjections.append("editorContainer, ");
        if (iconButtonContainer == null) missingInjections.append("iconButtonContainer, ");

        if (missingInjections.length() > 0) {
            System.err.println("Missing FXML injections: " + missingInjections);
        }
    }

}