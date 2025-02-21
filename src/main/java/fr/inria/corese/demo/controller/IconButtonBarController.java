package fr.inria.corese.demo.controller;

import fr.inria.corese.demo.enums.icon.IconButtonType;
import fr.inria.corese.demo.model.codeEditor.CodeEditorModel;
import fr.inria.corese.demo.model.IconButtonBarModel;
import fr.inria.corese.demo.view.icon.IconButtonBarView;
import fr.inria.corese.demo.factory.popup.DocumentationPopup;
import fr.inria.corese.demo.factory.popup.IPopup;
import fr.inria.corese.demo.factory.popup.PopupFactory;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.FileChooser;

import java.io.File;
import java.nio.file.Files;

public class IconButtonBarController {
    private IconButtonBarView view;
    private IconButtonBarModel model;
    private PopupFactory popupFactory;

    public IconButtonBarController(IconButtonBarModel model, IconButtonBarView view) {
        this.model = model;
        this.view = view;
        this.popupFactory = new PopupFactory(null);

        view.initializeButtons(model.getAvailableButtons());
        initializeButtonHandlers();
    }

    private void initializeButtonHandlers() {
        model.getAvailableButtons().forEach(this::initializeHandler);
    }

    private void initializeHandler(IconButtonType type) {
        Button button = view.getButton(type);
        switch (type) {
            case SAVE -> button.setOnAction(e -> onSaveButtonClick());
            case OPEN_FILE -> button.setOnAction(e -> onOpenFilesButtonClick());
            case EXPORT -> button.setOnAction(e -> onExportButtonClick());
            case IMPORT -> button.setOnAction(e -> onImportButtonClick());
            case CLEAR -> button.setOnAction(e -> onClearButtonClick());
            case UNDO -> button.setOnAction(e -> onUndoButtonClick());
            case REDO -> button.setOnAction(e -> onRedoButtonClick());
            case DOCUMENTATION -> button.setOnAction(e -> onDocumentationButtonClick());
            case ZOOM_IN -> button.setOnAction(e -> onZoomInButtonClick());
            case ZOOM_OUT -> button.setOnAction(e -> onZoomOutButtonClick());
            case FULL_SCREEN -> button.setOnAction(e -> onFullScreenButtonClick());
        }

    }

    public IconButtonBarView getView() {
        return view;
    }

    public IconButtonBarModel getModel() {
        return model;
    }

    private void onSaveButtonClick() {
        System.out.println("Save button clicked");
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save File");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Turtle Files", "*.ttl"),
                new FileChooser.ExtensionFilter("RDF/XML Files", "*.rdf"),
                new FileChooser.ExtensionFilter("N3 Files", "*.n3")
        );

        File file = fileChooser.showSaveDialog(null);
        if (file != null) {
            try {
                Files.writeString(file.toPath(), model.getCodeEditorModel().getContent());
                IPopup successPopup = PopupFactory.getInstance(null).createPopup(PopupFactory.TOAST_NOTIFICATION);
                successPopup.setMessage("File has been saved successfully!");
                successPopup.displayPopup();
            } catch (Exception e) {
                showError("Error Saving File", "Could not save the file: " + e.getMessage());
            }
        }
    }

    private void onOpenFilesButtonClick() {
        System.out.println("Open files button clicked");
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open File");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("RDF Files", "*.ttl", "*.rdf", "*.n3"),
                new FileChooser.ExtensionFilter("All Files", "*.*")
        );

        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            try {
                String content = Files.readString(file.toPath());
                model.getCodeEditorModel().setContent(content);
            } catch (Exception e) {
                showError("Error Opening File", "Could not open the file: " + e.getMessage());
            }
        }
    }

    private void onImportButtonClick() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Import File");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Text Files", "*.txt")
        );

        File file = fileChooser.showOpenDialog(view.getScene().getWindow());
        if (file != null) {
            try {
                String content = Files.readString(file.toPath());
                model.getCodeEditorModel().setContent(content);
            } catch (Exception e) {
                showError("Error Importing File", "Could not import the file: " + e.getMessage());
            }
        }
    }

    private void onExportButtonClick() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Export File");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Text Files", "*.txt")
        );

        File file = fileChooser.showSaveDialog(view.getScene().getWindow());
        if (file != null) {
            // Ensure the file has the correct extension
            if (!file.getName().endsWith(".txt")) {
                file = new File(file.getAbsolutePath() + ".txt");
            }
            try {
                Files.writeString(file.toPath(), model.getCodeEditorModel().getContent());
            } catch (Exception e) {
                showError("Error Exporting File", "Could not export the file: " + e.getMessage());
            }
        }
    }

    private void onClearButtonClick() {
        System.out.println("Clear button clicked");
        model.getCodeEditorModel().setContent("");
    }

    private void updateUndoRedoButtons() {
        CodeEditorModel editorModel = model.getCodeEditorModel();
        view.getButton(IconButtonType.UNDO).setDisable(!editorModel.canUndo());
        view.getButton(IconButtonType.REDO).setDisable(!editorModel.canRedo());
    }

    private void onUndoButtonClick() {
        System.out.println("Undo button clicked");
        CodeEditorModel editorModel = model.getCodeEditorModel();
        if (editorModel.canUndo()) {
            editorModel.undo();
            updateUndoRedoButtons();
        }

    }

    private void onRedoButtonClick() {
        System.out.println("Redo button clicked");
        CodeEditorModel editorModel = model.getCodeEditorModel();
        if (editorModel.canRedo()) {
            editorModel.redo();
            updateUndoRedoButtons();
        }

    }

    private void onDocumentationButtonClick() {
        System.out.println("Documentation button clicked");
        DocumentationPopup documentationPopup = new DocumentationPopup();
        documentationPopup.displayPopup();
    }

    private void onZoomInButtonClick() {
        System.out.println("Zoom in button clicked");
    }

    private void onZoomOutButtonClick() {
        System.out.println("Zoom out button clicked");
    }

    private void onFullScreenButtonClick() {
        System.out.println("Full screen button clicked");
    }

    private void showError(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
