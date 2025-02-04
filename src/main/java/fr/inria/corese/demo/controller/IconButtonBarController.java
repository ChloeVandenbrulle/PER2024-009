package fr.inria.corese.demo.controller;

import fr.inria.corese.demo.view.IconButtonBarView;
import fr.inria.corese.demo.view.NavigationBarView;
import javafx.stage.FileChooser;

import java.io.File;
import java.nio.file.Files;

public class IconButtonBarController {
    private IconButtonBarView view;

    public IconButtonBarController() {
        this.view = new IconButtonBarView();
        initializeButtons();
    }

    private void initializeButtons() {
        System.out.println("Initializing icon button bar buttons");
        System.out.println("Save button: " + view.getSaveButton());
        view.getSaveButton().setOnAction(event -> onSaveButtonClick());
        view.getOpenFilesButton().setOnAction(event -> onOpenFilesButtonClick());
        view.getImportButton().setOnAction(event -> onImportButtonClick());
        view.getExportButton().setOnAction(event -> onExportButtonClick());
        view.getClearButton().setOnAction(event -> onClearButtonClick());
        view.getUndoButton().setOnAction(event -> onUndoButtonClick());
        view.getRedoButton().setOnAction(event -> onRedoButtonClick());
        view.getDocumentationButton().setOnAction(event -> onDocumentationButtonClick());
        view.getZoomInButton().setOnAction(event -> onZoomInButtonClick());
        view.getZoomOutButton().setOnAction(event -> onZoomOutButtonClick());
        view.getFullScreenButton().setOnAction(event -> onFullScreenButtonClick());
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
