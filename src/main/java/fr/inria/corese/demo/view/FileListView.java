package fr.inria.corese.demo.view;

import javafx.scene.layout.VBox;
import javafx.scene.control.ListView;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;

public class FileListView extends VBox {
    private ListView<String> fileList;
    private Button clearGraphButton;
    private Button reloadFilesButton;
    private Button loadFilesButton;

    public FileListView() {
        initializeComponents();
        setupLayout();
    }

    private void initializeComponents() {
        fileList = new ListView<>();
        clearGraphButton = new Button("Clear graph");
        reloadFilesButton = new Button("Reload files");
        loadFilesButton = new Button("Load files");
    }

    private void setupLayout() {
        HBox buttonBox = new HBox(10);
        buttonBox.getChildren().addAll(clearGraphButton, reloadFilesButton, loadFilesButton);

        getChildren().addAll(fileList, buttonBox);
    }

    // Getters
    public ListView<String> getFileList() { return fileList; }
    public Button getClearGraphButton() { return clearGraphButton; }
    public Button getReloadFilesButton() { return reloadFilesButton; }
    public Button getLoadFilesButton() { return loadFilesButton; }
}