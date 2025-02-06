package fr.inria.corese.demo.view;

import fr.inria.corese.demo.model.FileItem;
import fr.inria.corese.demo.model.FileListModel;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import java.io.IOException;

public class FileListView extends VBox {
    private FileListModel model;

    @FXML
    private ListView<FileItem> fileList;
    @FXML
    private Button clearButton;
    @FXML
    private Button reloadButton;
    @FXML
    private Button loadButton;

    public FileListView() {
        loadFxml();
    }

    private void loadFxml() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fr/inria/corese/demo/fileList-view.fxml"));
            loader.setRoot(this);
            loader.setController(this);
            loader.load();
            setupListView();
        } catch (IOException e) {
            throw new RuntimeException("Impossible de charger FileListView.fxml", e);
        }
    }

    @FXML
    private void initialize() {
        setupListView();
    }

    private void setupListView() {
        if (fileList != null) {
            fileList.setCellFactory(lv -> new FileListCell());
        }
    }

    public void setModel(FileListModel model) {
        this.model = model;
        if (model != null && fileList != null) {
            fileList.setItems(model.getFiles());
        }
    }

    // Getters pour le controller
    public Button getClearButton() { return clearButton; }
    public Button getReloadButton() { return reloadButton; }
    public Button getLoadButton() { return loadButton; }
    public ListView<FileItem> getFileList() { return fileList; }

    // Custom cell pour les fichiers
    private static class FileListCell extends ListCell<FileItem> {
        @Override
        protected void updateItem(FileItem item, boolean empty) {
            super.updateItem(item, empty);

            if (empty || item == null) {
                setText(null);
                setGraphic(null);
            } else {
                HBox cell = new HBox(10);
                Label nameLabel = new Label(item.getName());

                Button deleteButton = new Button();
                Region deleteIcon = new Region();
                deleteIcon.getStyleClass().add("delete-icon");
                deleteButton.setGraphic(deleteIcon);
                deleteButton.getStyleClass().add("delete-button");

                if (item.isLoading()) {
                    ProgressIndicator progress = new ProgressIndicator();
                    progress.setMaxSize(16, 16);
                    cell.getChildren().addAll(nameLabel, progress);
                } else {
                    cell.getChildren().addAll(nameLabel, deleteButton);
                }

                setGraphic(cell);
            }
        }
    }
}