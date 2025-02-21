package fr.inria.corese.demo.controller;

import fr.inria.corese.demo.enums.icon.IconButtonBarType;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.SplitPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;

public class ValidationViewController {
    @FXML private BorderPane contentContainer;
    @FXML private StackPane editorContainer;
    @FXML private SplitPane splitPane;
    @FXML private StackPane consoleContainer;

    private TabEditorController tabEditorController;
    private ConsoleController consoleController;

    @FXML
    public void initialize() {
        checkFXMLInjections();

        try {
            // Initialize components
            initializeTabEditor();
            initializeConsole();
            initializeSplitPane();
            System.out.println("RDFEditorViewController initialization complete");
        } catch (Exception e) {
            System.err.println("Error during initialization:");
            e.printStackTrace();
        }
    }

    private void initializeTabEditor(){
        tabEditorController = new TabEditorController(IconButtonBarType.VALIDATION);
        tabEditorController.getView().setMaxWidth(Double.MAX_VALUE);
        tabEditorController.getView().setMaxHeight(Double.MAX_VALUE);
        editorContainer.getChildren().add(tabEditorController.getView());
    }

    private void initializeConsole() {
        consoleController = new ConsoleController();
        consoleController.getView().setMaxWidth(Double.MAX_VALUE);
        consoleContainer.getChildren().add(consoleController.getView());
        appendToConsole("Message de test");
    }

    private void initializeSplitPane() {
        splitPane.sceneProperty().addListener((observable, oldScene, newScene) -> {
            if (newScene != null) {
                Platform.runLater(() -> {
                    splitPane.lookupAll(".split-pane-divider").forEach(div -> {
                        div.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
                            if (event.getClickCount() == 2) {
                                toggleConsolePane();
                            }
                        });
                    });
                });
            }
        });
    }

    private void toggleConsolePane() {
        if (splitPane.getDividerPositions()[0] > 0.9) {
            splitPane.setDividerPositions(0.7);
        } else {
            splitPane.setDividerPositions(1.0);
        }
    }

    public void appendToConsole(String message) {
        consoleController.appendMessage(message);
    }

    private void checkFXMLInjections() {
        StringBuilder missingInjections = new StringBuilder();
        if (contentContainer == null) missingInjections.append("contentContainer, ");
        if (editorContainer == null) missingInjections.append("editorContainer, ");
        if (consoleContainer == null) missingInjections.append("consoleContainer, ");

        if (missingInjections.length() > 0) {
            System.err.println("Missing FXML injections: " + missingInjections);
        }
    }

}