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

        initializeButtons();
        initializeTreeViewEvents();
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
        System.out.println("buildPath");
        if (model.getRootPath() == null) {
            return "";
        }

        StringBuilder path = new StringBuilder();
        TreeItem<String> current = item;

        while (current != null && current.getParent() != null) {
            System.out.println(path);
            path.insert(0, current.getValue());
            path.insert(0, File.separator);
            current = current.getParent();
        }
        System.out.println(model.getRootPath() + path.toString());
        return model.getRootPath() + path.toString();
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
                try {
                    String fullPath = buildPath(finalSelectedItem) + File.separator + fileName;
                    System.out.println("fullPath : "+ fullPath);
                    File newFile = new File(fullPath);

                    if (newFile.createNewFile()) {
                        model.addFile(finalSelectedItem, new FileItem(fileName));
                    } else {
                        System.err.println("Impossible de créer le fichier: " + fullPath);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
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
                String fullPath = buildPath(finalSelectedItem) + File.separator + folderName;
                System.out.println("fullPath : "+ fullPath);

                if (new File(fullPath).mkdir()) {
                    model.addFolder(finalSelectedItem, new FileItem(folderName));
                } else {
                    System.err.println("Impossible de créer le fichier: " + fullPath);
                }
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