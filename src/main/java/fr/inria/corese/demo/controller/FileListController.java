package fr.inria.corese.demo.controller;

import fr.inria.corese.demo.model.FileListModel;
import fr.inria.corese.demo.view.FileListView;
import javafx.stage.FileChooser;
import java.io.File;

public class FileListController {
    private final FileListModel model;
    private final FileListView view;

    public FileListController() {
        this.model = new FileListModel();
        this.view = new FileListView();

        initializeListeners();
        bindModelToView();
    }

    private void initializeListeners() {
        view.getClearButton().setOnAction(e -> handleClearGraph());
        view.getReloadButton().setOnAction(e -> handleReloadFiles());
        view.getLoadButton().setOnAction(e -> handleLoadFiles());
    }

    private void bindModelToView() {
        view.getFileList().setItems(model.getFiles());
    }

    private void handleClearGraph() {
        model.clearFiles();
    }

    private void handleReloadFiles() {
        // Implement reload logic
    }

    private void handleLoadFiles() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("TTL files", "*.ttl")
        );

        File file = fileChooser.showOpenDialog(view.getScene().getWindow());
        if (file != null) {
            model.addFile(file.getName());
        }
    }

    public FileListView getView() {
        return view;
    }
}