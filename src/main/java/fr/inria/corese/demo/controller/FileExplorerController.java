package fr.inria.corese.demo.controller;

import fr.inria.corese.demo.model.FileExplorerModel;
import fr.inria.corese.demo.model.FileItem;
import fr.inria.corese.demo.view.FileExplorerView;
import fr.inria.corese.demo.view.popup.NewFilePopup;
import javafx.scene.control.Alert;
import javafx.scene.control.TreeItem;
import javafx.stage.DirectoryChooser;
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

        view.getOpenFolderButton().setOnAction(e -> openProject());

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
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Open Project Directory");
        File selectedDirectory = directoryChooser.showDialog(view.getScene().getWindow());

        if (selectedDirectory != null && selectedDirectory.isDirectory()) {
            loadProjectStructure(selectedDirectory);
        }
    }

    private void loadProjectStructure(File directory) {
        TreeItem<String> root = new TreeItem<>(directory.getName());
        root.setExpanded(true);

        // Ajouter l'icône de dossier à la racine
        FontIcon rootFolderIcon = new FontIcon(MaterialDesignF.FOLDER_OUTLINE);
        rootFolderIcon.setIconSize(20);
        root.setGraphic(rootFolderIcon);

        try {
            populateTreeItems(root, directory);
        } catch (IOException e) {
            e.printStackTrace();
            // Optionnel : Ajouter une alerte pour informer l'utilisateur
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Error Loading Project");
            alert.setContentText("Failed to load project structure: " + e.getMessage());
            alert.showAndWait();
        }

        view.getTreeView().setRoot(root);
    }

    private void populateTreeItems(TreeItem<String> parentItem, File parent) throws IOException {
        if (!parent.isDirectory()) {
            return;
        }

        File[] files = parent.listFiles();
        if (files == null) {
            return;
        }

        for (File file : files) {
            TreeItem<String> item = new TreeItem<>(file.getName());

            FontIcon icon;
            if (file.isDirectory()) {
                icon = new FontIcon(MaterialDesignF.FOLDER_OUTLINE);
                icon.setIconSize(20);
                item.setGraphic(icon);
                populateTreeItems(item, file);
            } else {
                icon = new FontIcon(MaterialDesignF.FILE_OUTLINE);
                icon.setIconSize(20);
                item.setGraphic(icon);
            }

            parentItem.getChildren().add(item);
        }
    }
}