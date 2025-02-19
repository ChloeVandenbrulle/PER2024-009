package fr.inria.corese.demo.view;

import fr.inria.corese.demo.model.fileList.FileItem;
import fr.inria.corese.demo.model.fileList.FileListModel;
import fr.inria.corese.demo.factory.popup.IPopup;
import fr.inria.corese.demo.factory.popup.PopupFactory;
import fr.inria.corese.demo.factory.popup.WarningPopup;
import fr.inria.corese.demo.model.ProjectDataModel;
import fr.inria.corese.demo.enums.icon.IconButtonType;
import fr.inria.corese.demo.view.icon.IconButtonView;
import javafx.beans.binding.Bindings;
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
    private EmptyStateView emptyStateView;
    private Button clearButton;
    private Button reloadButton;
    private Button loadButton;

    @FXML
    private ListView<FileItem> fileList;
    @FXML
    private FlowPane buttonContainer;

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
            setupEmptyState();

        } catch (IOException e) {
            throw new RuntimeException("Impossible de charger FileListView.fxml", e);
        }
    }

    private void setupIconButtons() {
        // Créer les boutons avec IconButtonView
        loadButton = new IconButtonView(IconButtonType.IMPORT);
        reloadButton = new IconButtonView(IconButtonType.RELOAD);
        clearButton = new IconButtonView(IconButtonType.DELETE);

        // Ajouter les boutons au conteneur vertical dans l'ordre souhaité
        buttonContainer.getChildren().clear();
        buttonContainer.getChildren().addAll(loadButton, reloadButton, clearButton);

        // S'assurer que les boutons sont bien alignés en haut à gauche
        buttonContainer.setAlignment(Pos.TOP_LEFT);
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

            // Bind empty state visibility to model's file list size
            emptyStateView.visibleProperty().bind(Bindings.isEmpty(model.getFiles()));

            // Ensure that when empty state is visible, fileList is not visible
            fileList.visibleProperty().bind(Bindings.isNotEmpty(model.getFiles()));
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

    private void setupEmptyState() {
        // Create empty state view
        emptyStateView = new EmptyStateView();

        // Create a StackPane to hold both the ListView and empty state
        StackPane contentContainer = new StackPane();

        // Get the current fileList from the FXML
        if (fileList != null) {
            // Remove the ListView from its current parent (maintenant un HBox)
            HBox parent = (HBox)fileList.getParent();
            int index = parent.getChildren().indexOf(fileList);
            parent.getChildren().remove(fileList);

            // Add both to the stack pane
            contentContainer.getChildren().addAll(emptyStateView, fileList);

            // Add the stack pane to the parent at the same position
            parent.getChildren().add(index, contentContainer);
            HBox.setHgrow(contentContainer, Priority.ALWAYS);
        } else {
            // Si fileList est toujours null
            getChildren().add(0, contentContainer);
            VBox.setVgrow(contentContainer, Priority.ALWAYS);
        }

        // Initially hide the empty state until model is set
        emptyStateView.setVisible(false);
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