package fr.inria.corese.demo.view;

import fr.inria.corese.demo.model.FileItem;
import fr.inria.corese.demo.model.FileListModel;
import fr.inria.corese.demo.view.popup.IPopup;
import fr.inria.corese.demo.view.popup.PopupFactory;
import fr.inria.corese.demo.view.popup.WarningPopup;
import fr.inria.corese.demo.model.ProjectDataModel;
import fr.inria.corese.demo.factory.IconButtonBarFactory;
import fr.inria.corese.demo.enums.IconButtonType;
import fr.inria.corese.demo.controller.IconButtonBarController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import java.io.IOException;

public class FileListView extends VBox {
    private FileListModel model;
    private ProjectDataModel projectDataModel;
    private PopupFactory popupFactory;

    @FXML
    private ListView<FileItem> fileList;
    private Button clearButton;
    private Button reloadButton;
    private Button loadButton;

    @FXML
    private HBox buttonContainer;

    public FileListView() {
        loadFxml();
        setupIconButtons();
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

    private void setupIconButtons() {
        // Créer les boutons avec IconButtonView
        clearButton = new IconButtonView(IconButtonType.DELETE);
        reloadButton = new IconButtonView(IconButtonType.RELOAD);
        loadButton = new IconButtonView(IconButtonType.IMPORT);

        // Ajouter les boutons au conteneur
        buttonContainer.getChildren().clear();
        buttonContainer.getChildren().addAll(clearButton, reloadButton, loadButton);
        buttonContainer.setAlignment(Pos.CENTER_LEFT);
    }

    @FXML
    private void initialize() {
        setupListView();
    }

    private void setupListView() {
        if (fileList != null) {
            fileList.setCellFactory(lv -> new FileListCell(this));
        }
    }

    public void setModel(FileListModel model) {
        this.model = model;
        if (model != null && fileList != null) {
            fileList.setItems(model.getFiles());
        }
    }

    public void setProjectDataModel(ProjectDataModel projectDataModel) {
        this.projectDataModel = projectDataModel;
        this.popupFactory = PopupFactory.getInstance(projectDataModel);
    }

    public Button getClearButton() {
        return clearButton;
    }

    public Button getReloadButton() {
        return reloadButton;
    }

    public Button getLoadButton() {
        return loadButton;
    }

    public ListView<FileItem> getFileList() {
        return fileList;
    }

    private boolean showWarningPopup(String message) {
        if (popupFactory != null) {
            IPopup warningPopup = popupFactory.createPopup(PopupFactory.WARNING_POPUP);
            warningPopup.setMessage(message);
            return ((WarningPopup) warningPopup).getResult();
        }
        return true;
    }

    public boolean confirmReload() {
        return showWarningPopup("Reloading files will reset the current graph. Do you want to continue?");
    }

    public boolean confirmDelete(FileItem item) {
        return showWarningPopup("Removing this file will reset the current graph. Do you want to continue?");
    }

    private static class FileListCell extends ListCell<FileItem> {
        private final FileListView parentView;

        public FileListCell(FileListView parentView) {
            this.parentView = parentView;
        }

        @Override
        protected void updateItem(FileItem item, boolean empty) {
            super.updateItem(item, empty);

            if (empty || item == null) {
                setText(null);
                setGraphic(null);
            } else {
                HBox cell = new HBox();
                cell.setAlignment(Pos.CENTER_LEFT);
                cell.setSpacing(5);

                Label nameLabel = new Label(item.getName());
                Region spacer = new Region();
                HBox.setHgrow(spacer, Priority.ALWAYS);

                IconButtonView deleteButton = new IconButtonView(IconButtonType.DELETE);
                deleteButton.setOnAction(e -> {
                    if (parentView.confirmDelete(item)) {
                        ListView<FileItem> listView = getListView();
                        listView.getItems().remove(item);
                    }
                });

                if (item.isLoading()) {
                    ProgressIndicator progress = new ProgressIndicator();
                    progress.setMaxSize(16, 16);
                    cell.getChildren().addAll(nameLabel, spacer, progress);
                } else {
                    cell.getChildren().addAll(nameLabel, spacer, deleteButton);
                }

                setGraphic(cell);
            }
        }
    }


}