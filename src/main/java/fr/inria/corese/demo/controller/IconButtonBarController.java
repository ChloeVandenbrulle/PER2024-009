package fr.inria.corese.demo.controller;

import fr.inria.corese.demo.enums.IconButtonType;
import fr.inria.corese.demo.model.CodeEditorModel;
import fr.inria.corese.demo.model.IconButtonBarModel;
import fr.inria.corese.demo.view.IconButtonBarView;
import fr.inria.corese.demo.view.NavigationBarView;
import javafx.scene.control.Button;
import javafx.stage.FileChooser;

import java.io.File;
import java.nio.file.Files;

public class IconButtonBarController {
    private IconButtonBarView view;
    private IconButtonBarModel model;

    public IconButtonBarController(IconButtonBarModel model, IconButtonBarView view) {
        this.model = model;
        this.view = view;

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
    }

    private void onOpenFilesButtonClick() {
        System.out.println("Open files button clicked");
    }

    private void onImportButtonClick() {
        System.out.println("Import button clicked");
    }

    private void onExportButtonClick() {
        System.out.println("Export button clicked");
    }

    private void onClearButtonClick() {
        System.out.println("Clear button clicked");
    }

    private void onUndoButtonClick() {
        System.out.println("Undo button clicked");
    }

    private void onRedoButtonClick() {
        System.out.println("Redo button clicked");
    }

    private void onDocumentationButtonClick() {
        System.out.println("Documentation button clicked");
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
}
