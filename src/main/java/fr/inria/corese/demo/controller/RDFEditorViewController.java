package fr.inria.corese.demo.controller;

import fr.inria.corese.demo.enums.icon.IconButtonBarType;
import fr.inria.corese.demo.enums.icon.IconButtonType;
import fr.inria.corese.demo.factory.popup.DocumentationPopup;
import fr.inria.corese.demo.view.TopBar;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.SplitPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;

public class RDFEditorViewController {
    @FXML private BorderPane mainContainer;
    @FXML private BorderPane contentContainer;
    @FXML private VBox fileExplorerContainer;
    @FXML private StackPane editorContainer;
    @FXML private SplitPane splitPane;
    @FXML private TopBar topbar;

    private FileExplorerController fileExplorerController;
    private TabEditorController tabEditorController;

    @FXML
    public void initialize() {
        checkFXMLInjections();

        try {
            System.out.println("Initializing RDFEditorViewController");

            // Initialize components
            setupFileTree();
            initializeTopBar();
            initializeTabEditor();
            initializeSplitPane();
            setupComponentInteractions();

            System.out.println("RDFEditorViewController initialization complete");
        } catch (Exception e) {
            System.err.println("Error during initialization:");
            e.printStackTrace();
        }
    }

    private void initializeTabEditor(){
        tabEditorController = new TabEditorController(IconButtonBarType.RDF_EDITOR);
        tabEditorController.getView().setMaxWidth(Double.MAX_VALUE);
        tabEditorController.getView().setMaxHeight(Double.MAX_VALUE);
        editorContainer.getChildren().add(tabEditorController.getView());
    }

    private void initializeTopBar() {
        List<IconButtonType> buttons = new ArrayList<>();
        buttons.add(IconButtonType.DOCUMENTATION);
        topbar.addRightButtons(buttons);
        topbar.getButton(IconButtonType.DOCUMENTATION).setOnAction(e -> {
            DocumentationPopup documentationPopup = new DocumentationPopup();
            documentationPopup.displayPopup();
        });

    }

    private void setupFileTree() {
        fileExplorerController = new FileExplorerController();
        fileExplorerContainer.getChildren().add(fileExplorerController.getView());
    }

    private void setupComponentInteractions() {
        fileExplorerController.setOnFileOpenRequest(file -> {
            tabEditorController.openFile(file);
        });
    }

    private void initializeSplitPane() {
        splitPane.sceneProperty().addListener((observable, oldScene, newScene) -> {
            if (newScene != null) {
                Platform.runLater(() -> {
                    splitPane.lookupAll(".split-pane-divider").forEach(div -> {
                        div.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
                            if (event.getClickCount() == 2) {
                                toggleLeftPane();
                            }
                        });
                    });
                });
            }
        });
    }

    private void toggleLeftPane() {
        System.out.println("Toggling left pane");
        System.out.println(splitPane.getDividerPositions()[0]);
        if (splitPane.getDividerPositions()[0] > 0.01) {
            splitPane.setDividerPositions(0.00);
        } else {
            splitPane.setDividerPositions(0.2);
        }
    }

    private void checkFXMLInjections() {
        StringBuilder missingInjections = new StringBuilder();
        if (mainContainer == null) missingInjections.append("mainContainer, ");
        if (contentContainer == null) missingInjections.append("contentContainer, ");
        if (fileExplorerContainer == null) missingInjections.append("fileTreeView, ");
        if (editorContainer == null) missingInjections.append("editorContainer, ");

        if (missingInjections.length() > 0) {
            System.err.println("Missing FXML injections: " + missingInjections);
        }
    }
}