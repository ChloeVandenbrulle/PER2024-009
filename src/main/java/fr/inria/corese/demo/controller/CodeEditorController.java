package fr.inria.corese.demo.controller;

import fr.inria.corese.demo.enums.IconButtonBarType;
import fr.inria.corese.demo.enums.IconButtonType;
import fr.inria.corese.demo.factory.IconButtonBarFactory;
import fr.inria.corese.demo.model.CodeEditorModel;
import fr.inria.corese.demo.view.CodeEditorView;
import fr.inria.corese.demo.view.CodeMirrorView;
import fr.inria.corese.demo.view.popup.DeleteConfirmationPopup;
import fr.inria.corese.demo.view.popup.IPopup;
import fr.inria.corese.demo.view.popup.PopupFactory;
import javafx.application.Platform;
import java.io.FileWriter;
import java.io.IOException;

public class CodeEditorController {
    private final CodeEditorView view;
    private final CodeEditorModel model;
    private boolean isUpdatingContent = false;
    private final IconButtonBarController iconButtonBarController;
    private PopupFactory popupFactory;
    private CodeMirrorView editorContainer;
    private String content = "";

    public CodeEditorController(IconButtonBarType type) {
        this.view = new CodeEditorView();
        this.model = new CodeEditorModel();
        this.iconButtonBarController = IconButtonBarFactory.create(type);
        this.popupFactory = new PopupFactory(null);
        System.out.println("Initializing CodeEditorController");

        this.editorContainer = view.getCodeMirrorView();

        initializeComponents();
    }

    public CodeEditorController(IconButtonBarType type, String content) {
        this.view = new CodeEditorView();
        this.model = new CodeEditorModel();
        this.iconButtonBarController = IconButtonBarFactory.create(type);
        System.out.println("Initializing CodeEditorController");
        this.content = content;

        this.editorContainer = view.getCodeMirrorView();

        initializeComponents();
    }

    private void initializeComponents() {
        // Initialise the button bar
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
                editorContainer.setContent(content);
                view.setCodeMirrorViewContent(content);

                if (model != null) {
                    model.setContent(content);

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

    public void saveFile() {
        String currentFile = model.getCurrentFile();
        System.out.println("Saving file: " + currentFile);

        if (currentFile != null) {
            // Save the file if it already exists
            try (FileWriter writer = new FileWriter(currentFile)) {
                writer.write(model.getContent());
                IPopup successPopup = PopupFactory.getInstance(null).createPopup(PopupFactory.TOAST_NOTIFICATION);
                successPopup.setMessage("File has been saved successfully!");
                successPopup.displayPopup();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            // Show Save As dialog if the file does not exist
            iconButtonBarController.getView().getButton(IconButtonType.SAVE).fire();
        }
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
