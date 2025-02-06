package fr.inria.corese.demo.model;

import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class FileExplorerModel {
    private List<FileItem> files;
    private List<FileItem> folders;

    public FileExplorerModel() {
        this.files = new ArrayList<>();
        this.folders = new ArrayList<>();
    }

    public FileExplorerModel(List<FileItem> files) {
        this.files = files;
    }

    public List<FileItem> getFiles() {
        return files;
    }

    public void addFile(TreeView treeView, FileItem file) {
        files.add(file);
        TreeItem<String> root = new TreeItem<>(file.getName());
        treeView.setRoot(root);
    }

    public void addFolder(TreeView treeView, FileItem folder) {
        folders.add(folder);
        TreeItem<String> root = new TreeItem<>(folder.getName());
        treeView.setRoot(root);
    }
}