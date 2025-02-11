package fr.inria.corese.demo.controller;

import fr.inria.corese.demo.model.FileExplorerModel;
import fr.inria.corese.demo.model.FileItem;
import fr.inria.corese.demo.view.FileExplorerView;
import fr.inria.corese.demo.view.popup.NewFilePopup;
import javafx.scene.control.TreeItem;
import javafx.stage.FileChooser;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.materialdesign2.MaterialDesignF;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class FileExplorerController {
    private FileExplorerModel model;
    private FileExplorerView view;

    public FileExplorerController() {
        this.model = new FileExplorerModel();
        this.view = new FileExplorerView();

        initializeTreeView();
        initializeButtons();
        initializeTreeViewEvents();

    }

    private void initializeTreeView() {
        if (view.getTreeView() != null) {
            TreeItem<String> root = new TreeItem<>("Project");
            root.setExpanded(true);

            TreeItem<String> src = new TreeItem<>("src");
            TreeItem<String> resources = new TreeItem<>("resources");

            root.getChildren().addAll(src, resources);
            view.getTreeView().setRoot(root);

            // Set initial folder icons
            FontIcon folderIcon = new FontIcon(MaterialDesignF.FOLDER_OUTLINE);
            folderIcon.setIconSize(20);
            src.setGraphic(folderIcon);

            FontIcon resourcesFolderIcon = new FontIcon(MaterialDesignF.FOLDER_OUTLINE);
            resourcesFolderIcon.setIconSize(20);
            resources.setGraphic(resourcesFolderIcon);
        }
    }

    private void initializeButtons() {
        view.getNewFileButton().setOnAction(e -> addFile());

        view.getNewFolderButton().setOnAction(e -> addFolder());

        view.getCloseFileExplorerButton().setOnAction(e -> toggleView());

        System.out.println("All button handlers initialized");
    }

    private void initializeTreeViewEvents() {
        view.getTreeView().setOnMouseClicked(event -> {
            TreeItem<String> selectedItem = view.getTreeView().getSelectionModel().getSelectedItem();
            if (selectedItem != null) {
                System.out.println("Selected: " + selectedItem.getValue());
            }
        });
    }

    public void toggleView() {
        model.setIsOpen(!model.getIsOpen());

        if (model.getIsOpen()) {
            view.openView();
        } else {
            view.closeView();
        }
    }

    public FileExplorerModel getModel() {
        return model;
    }

    public FileExplorerView getView() {
        return view;
    }

    private void addFile() {
        System.out.println("Add file button clicked");
        TreeItem<String> selectedItem = view.getTreeView().getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            selectedItem = view.getTreeView().getRoot();
        }

        NewFilePopup newFilePopup = new NewFilePopup();
        TreeItem<String> finalSelectedItem = selectedItem;
        newFilePopup.setOnConfirm(() -> {
            String fileName = newFilePopup.getFileName();
            if (fileName != null && !fileName.isEmpty()) {
                model.addFile(finalSelectedItem, new FileItem(fileName));
            }
        });
        newFilePopup.displayPopup();
    }

    private void addFolder() {
        System.out.println("Add folder button clicked");
        TreeItem<String> selectedItem = view.getTreeView().getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            selectedItem = view.getTreeView().getRoot();
        }

        NewFilePopup newFolderPopup = new NewFilePopup();
        TreeItem<String> finalSelectedItem = selectedItem;
        newFolderPopup.setOnConfirm(() -> {
            String folderName = newFolderPopup.getFileName();
            if (folderName != null && !folderName.isEmpty()) {
                model.addFolder(finalSelectedItem, new FileItem(folderName));
            }
        });
        newFolderPopup.displayPopup();
    }


    private void openProject() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Project");
        File selectedDirectory = fileChooser.showOpenDialog(view.getScene().getWindow());

        if (selectedDirectory != null) {
            loadProjectStructure(selectedDirectory);
        }
    }

    private void loadProjectStructure(File directory) {
        TreeItem<String> root = new TreeItem<>(directory.getName());
        root.setExpanded(true);

        try {
            populateTreeItems(root, directory);
        } catch (IOException e) {
            e.printStackTrace();
        }

        view.getTreeView().setRoot(root);
    }

    private void populateTreeItems(TreeItem<String> parentItem, File parent) throws IOException {
        Files.list(parent.toPath()).forEach(path -> {
            File file = path.toFile();
            TreeItem<String> item = new TreeItem<>(file.getName());

            FontIcon icon;
            if (file.isDirectory()) {
                icon = new FontIcon(MaterialDesignF.FOLDER_OUTLINE);
                icon.setIconSize(20);
                item.setGraphic(icon);
                try {
                    populateTreeItems(item, file);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                icon = new FontIcon(MaterialDesignF.FILE_OUTLINE);
                icon.setIconSize(20);
                item.setGraphic(icon);
            }

            parentItem.getChildren().add(item);
        });
    }
}