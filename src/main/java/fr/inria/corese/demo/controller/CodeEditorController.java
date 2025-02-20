package fr.inria.corese.demo.controller;

import fr.inria.corese.demo.enums.icon.IconButtonBarType;
import fr.inria.corese.demo.enums.icon.IconButtonType;
import fr.inria.corese.demo.factory.icon.IconButtonBarFactory;
import fr.inria.corese.demo.model.codeEditor.CodeEditorModel;
import fr.inria.corese.demo.view.codeEditor.CodeEditorView;
import fr.inria.corese.demo.view.codeEditor.CodeMirrorView;
import fr.inria.corese.demo.factory.popup.IPopup;
import fr.inria.corese.demo.factory.popup.PopupFactory;
import javafx.application.Platform;
import java.io.FileWriter;
import java.io.IOException;

public class CodeEditorController {
    private final CodeEditorView view;
    private final CodeEditorModel model;
    private boolean isUpdatingContent = false;
    private final IconButtonBarController iconButtonBarController;
    private CodeMirrorView editorContainer;
    private String content = "";
    private IconButtonBarType type;

    public CodeEditorController(IconButtonBarType type) {
        this.view = new CodeEditorView();
        this.model = new CodeEditorModel();
        this.iconButtonBarController = IconButtonBarFactory.create(type);
        this.type = type;
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
        this.type = type;

        this.editorContainer = view.getCodeMirrorView();

        initializeComponents();
    }

    private void initializeComponents() {
        // Initialise the button bar
        view.getIconButtonBarView().getChildren().add(iconButtonBarController.getView());
        iconButtonBarController.getModel().setCodeEditorModel(model);

        if (this.type.equals(IconButtonBarType.VALIDATION) || this.type.equals(IconButtonBarType.QUERY)){
            view.displayRunButton();
        }

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
                    model.setCurrentSavedContent(content);

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
            try (FileWriter writer = new FileWriter(currentFile)) {
                writer.write(model.getContent());
                IPopup successPopup = PopupFactory.getInstance(null).createPopup(PopupFactory.TOAST_NOTIFICATION);
                successPopup.setMessage("File has been saved successfully!");
                successPopup.displayPopup();
                model.setModified(false);
                model.setCurrentSavedContent(model.getContent());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
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
