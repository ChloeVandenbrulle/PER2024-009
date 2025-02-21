package fr.inria.corese.demo.controller;

import fr.inria.corese.demo.model.ConsoleModel;
import fr.inria.corese.demo.view.ConsoleView;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.stage.FileChooser;

import java.io.File;
import java.nio.file.Files;

public class ConsoleController {
    private final ConsoleModel model;
    private final ConsoleView view;

    public ConsoleController() {
        this.model = new ConsoleModel();
        this.view = new ConsoleView();

        initializeBindings();
        view.getExportButton().setOnAction(e -> exportContent());
    }

    private void initializeBindings() {
        view.getConsoleOutput().textProperty().bind(model.contentProperty());

        model.contentProperty().addListener((observable, oldValue, newValue) -> {
            Platform.runLater(() ->
                    view.getConsoleOutput().setScrollTop(Double.MAX_VALUE));
        });
    }

    public void appendMessage(String message) {
        if (message != null && !message.trim().isEmpty()) {
            model.appendMessage(message);
        }
    }

    private void exportContent() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Export File");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Text Files", "*.txt")
        );

        File file = fileChooser.showSaveDialog(view.getScene().getWindow());
        if (file != null) {
            if (!file.getName().endsWith(".txt")) {
                file = new File(file.getAbsolutePath() + ".txt");
            }
            try {
                Files.writeString(file.toPath(), model.contentProperty().get());
            } catch (Exception e) {
                showError("Error Exporting File", "Could not export the file: " + e.getMessage());
            }
        }
    }

    private void showError(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public void clear() {
        model.clear();
    }

    public ConsoleView getView() {
        return view;
    }

    public ConsoleModel getModel() {
        return model;
    }
}