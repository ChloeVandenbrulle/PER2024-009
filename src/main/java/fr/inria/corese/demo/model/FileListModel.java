package fr.inria.corese.demo.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class FileListModel {
    private final ObservableList<FileItem> files = FXCollections.observableArrayList();

    public ObservableList<FileItem> getFiles() {
        return files;
    }

    public void addFile(String name) {
        files.add(new FileItem(name));
    }

    public void removeFile(FileItem file) {
        files.remove(file);
    }

    public void clearFiles() {
        files.clear();
    }
}