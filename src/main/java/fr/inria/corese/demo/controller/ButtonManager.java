package fr.inria.corese.demo.controller;

import com.sun.javafx.event.EventUtil;
import fr.inria.corese.demo.model.ButtonType;
import fr.inria.corese.demo.model.ProjectDataModel;
import fr.inria.corese.demo.view.CustomButton;
import java.util.HashMap;
import java.util.Map;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.stage.FileChooser;
import javafx.stage.DirectoryChooser;
import java.io.File;

public class ButtonManager {
    private final ProjectDataModel model;
    private final Map<ButtonType, CustomButton> buttons = new HashMap<>();

    public ButtonManager(ProjectDataModel model) {
        this.model = model;
        initializeButtons();
    }

    private void initializeButtons() {
        // Project buttons
        createProjectButton(ButtonType.OPEN_PROJECT);
        createProjectButton(ButtonType.SAVE_AS);
        createLogButton(ButtonType.SHOW_LOGS);

        // File buttons
        createFileButton(ButtonType.CLEAR_GRAPH);
        createFileButton(ButtonType.RELOAD_FILES);
        createFileButton(ButtonType.LOAD_FILES);

        Button showLogsButton = new Button("Show Logs");
        showLogsButton.setOnAction(event -> {
            System.out.println("Show logs button clicked"); // Debug print
            if (event.getSource() instanceof Button) {
                EventUtil.fireEvent(new ActionEvent());
            }
        });
    }

    private void createProjectButton(ButtonType type) {
        CustomButton button = new CustomButton.Builder(type)
                .withTooltip("Click to " + type.getLabel().toLowerCase())
                .withOnClick(this::handleProjectAction)
                .build();

        buttons.put(type, button);
    }

    private void createLogButton(ButtonType type) {
        CustomButton button = new CustomButton.Builder(type)
                .withStyle("log-button-special")
                .withTooltip("View application logs")
                .withOnClick(this::handleLogAction)
                .build();

        buttons.put(type, button);
    }

    private void createFileButton(ButtonType type) {
        CustomButton button = new CustomButton.Builder(type)
                .withTooltip("File operation: " + type.getLabel())
                .withOnClick(this::handleFileAction)
                .build();

        buttons.put(type, button);
    }

    private void handleProjectAction(CustomButton button) {
        switch (button.getType()) {
            case OPEN_PROJECT -> {
                DirectoryChooser chooser = new DirectoryChooser();
                File dir = chooser.showDialog(button.getScene().getWindow());
                if (dir != null) model.loadProject(dir);
            }
            case SAVE_AS -> {
                FileChooser chooser = new FileChooser();
                File file = chooser.showSaveDialog(button.getScene().getWindow());
                if (file != null) model.saveProject(file);
            }
        }
    }

    private void handleLogAction(CustomButton button) {
        // Logique d'affichage des logs
    }

    private void handleFileAction(CustomButton button) {
        switch (button.getType()) {
            case CLEAR_GRAPH -> model.clearGraph();
            case RELOAD_FILES -> model.reloadFiles();
            case LOAD_FILES -> {
                FileChooser chooser = new FileChooser();
                File file = chooser.showOpenDialog(button.getScene().getWindow());
                if (file != null) model.loadFile(file);
            }
        }
    }

    public CustomButton getButton(ButtonType type) {
        return buttons.get(type);
    }
}