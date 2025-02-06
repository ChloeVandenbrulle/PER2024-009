package fr.inria.corese.demo.controller;

import fr.inria.corese.demo.model.FileExplorerModel;
import fr.inria.corese.demo.model.FileItem;
import fr.inria.corese.demo.view.FileExplorerView;
import fr.inria.corese.demo.view.popup.LogDialog;
import fr.inria.corese.demo.view.popup.NewFilePopup;
import fr.inria.corese.demo.view.popup.PopupFactory;
import javafx.stage.Stage;

import java.nio.file.Files;

public class FileExplorerController {
    private FileExplorerModel model;
    private FileExplorerView view;

    public FileExplorerController() {
        this.model = new FileExplorerModel();
        this.view = new FileExplorerView();
    }

    public void initializeButtons() {
        view.getNewFileButton().setOnAction(e -> {
            System.out.println("Add file button clicked");
            // display a dialog to get the file name
            NewFilePopup newFilePopup = new NewFilePopup();
            addFile(new FileItem(""));


        });

        view.getNewFolderButton().setOnAction(e -> {
            System.out.println("Add folder button clicked");
            // display a dialog to get the folder name


        });

        System.out.println("All button handlers initialized");
    }

    public FileExplorerModel getModel() {
        return model;
    }

    public FileExplorerView getView() {
        return view;
    }

    public void addFile(FileItem file) {
        model.addFile(view.getTreeView(), file);
    }

    public void addFolder(FileItem folder) {
        model.addFolder(view.getTreeView(), folder);
    }
}
