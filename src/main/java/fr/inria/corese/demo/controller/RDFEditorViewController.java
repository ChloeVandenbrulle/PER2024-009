package fr.inria.corese.demo.controller;

import fr.inria.corese.demo.enums.IconButtonBarType;
import fr.inria.corese.demo.factory.IconButtonBarFactory;
import fr.inria.corese.demo.model.CodeEditorChange;
import fr.inria.corese.demo.model.CodeEditorModel;
import fr.inria.corese.demo.view.CodeMirrorView;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class RDFEditorViewController {
    @FXML private BorderPane mainContainer;
    @FXML private BorderPane contentContainer;
    @FXML private VBox fileExplorerContainer;
    @FXML private StackPane editorContainer;
    @FXML private SplitPane splitPane;
//    @FXML private CodeMirrorView editorContainer;
//    @FXML private VBox iconButtonBarContainer;

    private IconButtonBarController iconButtonBarController;
    private FileExplorerController fileExplorerController;
    private TabEditorController tabEditorController;

    private boolean isUpdatingContent = false;

    @FXML
    public void initialize() {
        checkFXMLInjections();

        try {
            System.out.println("Initializing RDFEditorViewController");

            // Initialize components
            setupFileTree();
            initializeTabEditor();


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


    private void setupFileTree() {
        fileExplorerController = new FileExplorerController();
        fileExplorerContainer.getChildren().add(fileExplorerController.getView());
    }

    private void checkFXMLInjections() {
        StringBuilder missingInjections = new StringBuilder();
        if (mainContainer == null) missingInjections.append("mainContainer, ");
        if (contentContainer == null) missingInjections.append("contentContainer, ");
        if (fileExplorerContainer == null) missingInjections.append("fileTreeView, ");
//        if (editorContainer == null) missingInjections.append("editorContainer, ");
//        if (iconButtonBarContainer == null) missingInjections.append("iconButtonContainer, ");

        if (missingInjections.length() > 0) {
            System.err.println("Missing FXML injections: " + missingInjections);
        }
    }

}