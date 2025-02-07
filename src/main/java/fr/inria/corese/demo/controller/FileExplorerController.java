package fr.inria.corese.demo.controller;

import fr.inria.corese.demo.model.FileExplorerModel;
import fr.inria.corese.demo.model.FileItem;
import fr.inria.corese.demo.view.FileExplorerView;
import fr.inria.corese.demo.view.popup.LogDialog;
import fr.inria.corese.demo.view.popup.NewFilePopup;
import fr.inria.corese.demo.view.popup.PopupFactory;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TreeItem;
import javafx.stage.Stage;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.materialdesign2.MaterialDesignF;

import java.nio.file.Files;

public class FileExplorerController {
    private FileExplorerModel model;
    private FileExplorerView view;

    public FileExplorerController() {
        this.model = new FileExplorerModel();
        this.view = new FileExplorerView();

        initializeTreeView();
        initializeButtons();
    }

    private void initializeTreeView() {
        if (view.getTreeView() != null) {
            TreeItem<String> root = new TreeItem<>("Project");
            root.setExpanded(true);

            TreeItem<String> src = new TreeItem<>("src");
            root.setExpanded(false);

            TreeItem<String> resources = new TreeItem<>("resources");
            root.setExpanded(true);

            root.getChildren().addAll(src, resources);
            view.getTreeView().setRoot(root);

            System.out.println("File tree initialized");
        } else {
            System.err.println("fileTreeView is null!");
        }
    }

    private void initializeButtons() {
        view.getNewFileButton().setOnAction(e -> addFile());

        view.getNewFolderButton().setOnAction(e -> addFolder());

        view.getCloseFileExplorerButton().setOnAction(e -> toggleView());

        System.out.println("All button handlers initialized");
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
        NewFilePopup newFilePopup = new NewFilePopup();
        newFilePopup.setOnConfirm(() -> {
            String fileName = newFilePopup.getFileName();
            if (fileName != null && !fileName.isEmpty()) {
                model.addFile(view.getTreeView().getRoot(), new FileItem(fileName));
                System.out.println(view.getTreeView().getRoot().getValue());
                System.out.println("children: " + view.getTreeView().getRoot().getChildren());
                System.out.println("first: " + model.getFiles().getFirst().getName());
                System.out.println("last: " + model.getFiles().getLast().getName());
            }
        });
        newFilePopup.displayPopup();
    }

    private void addFolder() {
        System.out.println("Add folder button clicked");
        NewFilePopup newFolderPopup = new NewFilePopup();
        newFolderPopup.setOnConfirm(() -> {
            String folderName = newFolderPopup.getFileName();
            if (folderName != null && !folderName.isEmpty()) {
                model.addFolder(view.getTreeView().getRoot(), new FileItem(folderName));
                System.out.println(view.getTreeView().getRoot().getValue());
                System.out.println("children: " + view.getTreeView().getRoot().getChildren());
                System.out.println("first: " + model.getFiles().getFirst().getName());
                System.out.println("last: " + model.getFiles().getLast().getName());
            }
        });
        newFolderPopup.displayPopup();
    }

}