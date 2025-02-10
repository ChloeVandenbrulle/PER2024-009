package fr.inria.corese.demo.controller;

import fr.inria.corese.demo.enums.IconButtonBarType;
import fr.inria.corese.demo.enums.IconButtonType;
import fr.inria.corese.demo.factory.IconButtonBarFactory;
import fr.inria.corese.demo.model.CodeEditorModel;
import fr.inria.corese.demo.view.CodeEditorView;
import fr.inria.corese.demo.view.CodeMirrorView;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.layout.VBox;

public class CodeEditorController {
    private final CodeEditorView view;
    private final CodeEditorModel model;
    private boolean isUpdatingContent = false;
    private final IconButtonBarController iconButtonBarController;

    private CodeMirrorView editorContainer;

    public CodeEditorController(IconButtonBarType type) {
        this.view = new CodeEditorView();
        this.model = new CodeEditorModel();
        this.iconButtonBarController = IconButtonBarFactory.create(type);
        System.out.println("Initializing CodeEditorController");

        this.editorContainer = view.getCodeMirrorView();

        initializeComponents();
//        setupBindings();
    }

    private void initializeComponents() {
        // Initialise la barre de boutons
        view.getIconButtonBarView().getChildren().add(iconButtonBarController.getView());
        iconButtonBarController.getModel().setCodeEditorModel(model);

        Platform.runLater(this::initializeEditor);
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
                view.setCodeMirrorViewContent(initialContent);
                System.out.println("CodeMirrorView content : "+ view.getCodeMirrorView().getContent());

                if (model != null) {
                    model.setContent(initialContent);

                    // Écouter les changements de la vue
                    editorContainer.contentProperty().addListener((obs, oldVal, newVal) -> {
                        System.out.println("Contenu modifié");
                        model.recordCurrentChange(newVal);
                    });
                } else {
                    System.err.println("CodeEditorModel is null!");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            // Écouter les changements du modèle
            model.contentProperty().addListener((obs, oldVal, newVal) -> {
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

    public CodeEditorModel getModel() {
        return model;
    }

    public CodeEditorView getView() {
        return view;
    }

    public IconButtonBarController getIconButtonBarController() {
        return iconButtonBarController;
    }



}
