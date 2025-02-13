package fr.inria.corese.demo.view;

import javafx.scene.layout.BorderPane;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.geometry.Insets;

public class DataView extends BorderPane {
    private Button openProjectButton;
    private Button saveAsButton;
    private Button showLogsButton;
    private FileListView fileListView;
    private ProjectStatisticsView projectStatisticsView;

    public DataView() {
        initializeComponents();
        setupLayout();
    }

    private void initializeComponents() {
        openProjectButton = new Button("Open project");
        saveAsButton = new Button("Save as");
        showLogsButton = new Button("Show logs");

        fileListView = new FileListView();
        projectStatisticsView = new ProjectStatisticsView();
    }

    private void setupLayout() {
        // Top toolbar
        HBox toolbar = new HBox(10);
        toolbar.setPadding(new Insets(10));
        toolbar.getChildren().addAll(openProjectButton, saveAsButton);

        // Right area for logs button
        VBox rightArea = new VBox(showLogsButton);
        rightArea.setPadding(new Insets(10));

        // Center content with files and rules
        BorderPane centerContent = new BorderPane();
        centerContent.setLeft(fileListView);
        centerContent.setBottom(projectStatisticsView);

        setTop(toolbar);
        setRight(rightArea);
        setCenter(centerContent);
    }

    // Getters for all components
    public Button getOpenProjectButton() { return openProjectButton; }
    public Button getSaveAsButton() { return saveAsButton; }
    public Button getShowLogsButton() { return showLogsButton; }
    public FileListView getFileListView() { return fileListView; }
    public ProjectStatisticsView getProjectStatisticsView() { return projectStatisticsView; }
}