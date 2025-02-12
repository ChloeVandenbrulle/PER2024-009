package fr.inria.corese.demo.controller;

import fr.inria.corese.demo.model.FileExplorerModel;
import fr.inria.corese.demo.model.FileItem;
import fr.inria.corese.demo.view.FileExplorerView;
import fr.inria.corese.demo.view.popup.NewFilePopup;
import fr.inria.corese.demo.view.popup.PopupFactory;
import javafx.scene.control.TreeItem;
import javafx.stage.DirectoryChooser;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.materialdesign2.MaterialDesignF;

import java.io.File;
import java.io.IOException;
import java.util.function.Consumer;

public class FileExplorerController {
    private FileExplorerModel model;
    private FileExplorerView view;
    private Consumer<File> onFileOpenRequest;
    private ContextMenuController contextMenuController;

    public FileExplorerController() {
        this.model = new FileExplorerModel();
        this.view = new FileExplorerView();
        this.contextMenuController = new ContextMenuController();

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
        view.getNewFileButton().setOnAction(e -> handleAddFile());
        view.getNewFolderButton().setOnAction(e -> handleAddFolder());
        view.getOpenFolderButton().setOnAction(e -> openProject());

        System.out.println("All button handlers initialized");
    }

    private void initializeTreeViewEvents() {
        view.getTreeView().setOnMouseClicked(event -> {
            TreeItem<String> selectedItem = view.getTreeView().getSelectionModel().getSelectedItem();
            if (selectedItem != null) {
                System.out.println("Selected: " + selectedItem.getValue());
            }
            if (event.getClickCount() == 2) {
                System.out.println("Double click detected");
                if (selectedItem != null && selectedItem.getChildren().isEmpty()) {
                    String path = buildPath(selectedItem);
                    File file = new File(path);
                    if (file.isFile() && onFileOpenRequest != null) {
                        onFileOpenRequest.accept(file);
                    }
                }
            }
        });

        view.getTreeView().setOnContextMenuRequested(event -> {
            TreeItem<String> selectedItem = view.getTreeView().getSelectionModel().getSelectedItem();
            if (selectedItem != null) {
                String path = buildPath(selectedItem);
                File file = new File(path);

                // Display the ContextMenu
                contextMenuController.show(event.getScreenX(), event.getScreenY(), file, path, view.getTreeView());

                contextMenuController.setOnItemDeleted(item -> {
                    selectedItem.getParent().getChildren().remove(selectedItem);
                });

                contextMenuController.setOnItemRenamed(newItem -> {
                    selectedItem.setValue(newItem.getValue());
                });
            }
        });
    }

    private String buildPath(TreeItem<String> item) {
        StringBuilder path = new StringBuilder(item.getValue());
        TreeItem<String> parent = item.getParent();
        while (parent != null && parent.getParent() != null && parent.getValue() != null) {
            path.insert(0, File.separator).insert(0, parent.getValue());
            parent = parent.getParent();
        }
        return model.getRootPath()+"\\"+path;
    }

    public void setOnFileOpenRequest(Consumer<File> handler) {
        this.onFileOpenRequest = handler;
    }


    public FileExplorerModel getModel() {
        return model;
    }

    public FileExplorerView getView() {
        return view;
    }

    private void handleAddFile() {
        System.out.println("Add file button clicked");
        TreeItem<String> selectedItem = view.getTreeView().getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            selectedItem = view.getTreeView().getRoot();
        }


        NewFilePopup newFilePopup = (NewFilePopup) PopupFactory.getInstance(null).createPopup("newFile");
        TreeItem<String> finalSelectedItem = selectedItem;
        newFilePopup.setOnConfirm(() -> {
            String fileName = newFilePopup.getFileName();
            if (fileName != null && !fileName.isEmpty()) {
                model.addFile(finalSelectedItem, new FileItem(fileName));
            }
        });
        newFilePopup.displayPopup();
    }

    private void handleAddFolder() {
        System.out.println("Add folder button clicked");
        TreeItem<String> selectedItem = view.getTreeView().getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            selectedItem = view.getTreeView().getRoot();
        }

        NewFilePopup newFolderPopup = (NewFilePopup) PopupFactory.getInstance(null).createPopup("newFile");
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
            model.setRootPath(selectedDirectory.getPath());
        }
    }

    private void loadProjectStructure(File directory) {
        TreeItem<String> root = new TreeItem<>(directory.getName());
        root.setExpanded(true);

        FontIcon rootFolderIcon = new FontIcon(MaterialDesignF.FOLDER_OUTLINE);
        rootFolderIcon.setIconSize(20);
        root.setGraphic(rootFolderIcon);

        try {
            populateTreeItems(root, directory);
        } catch (IOException e) {
            e.printStackTrace();
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
                icon.setIconSize(5);
                item.setGraphic(icon);
                populateTreeItems(item, file);
            } else {
                icon = new FontIcon(MaterialDesignF.FILE_OUTLINE);
                icon.setIconSize(5);
                item.setGraphic(icon);
            }

            parentItem.getChildren().add(item);
        }
    }
}