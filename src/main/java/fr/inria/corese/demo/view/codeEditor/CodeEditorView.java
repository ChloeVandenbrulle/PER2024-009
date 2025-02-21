package fr.inria.corese.demo.view.codeEditor;

import fr.inria.corese.demo.enums.button.ButtonType;
import fr.inria.corese.demo.view.CustomButton;
import fr.inria.corese.demo.view.icon.IconButtonBarView;
import javafx.scene.layout.AnchorPane;

public class CodeEditorView extends AnchorPane {
    private final CodeMirrorView codeMirrorView;
    private final IconButtonBarView iconButtonBarView;
    private final CustomButton runButton;
    private boolean runButtonDisplayed = false;

    public CodeEditorView() {
        this.codeMirrorView = new CodeMirrorView();
        this.iconButtonBarView = new IconButtonBarView();

        this.runButton = new CustomButton.Builder(ButtonType.RUN)
                .withTooltip("Run code")
                .build();

        setTopAnchor(codeMirrorView, 0.0);
        setRightAnchor(codeMirrorView, 0.0);
        setBottomAnchor(codeMirrorView, 0.0);
        setLeftAnchor(codeMirrorView, 0.0);

        setTopAnchor(iconButtonBarView, 5.0);
        setRightAnchor(iconButtonBarView, 5.0);

        setRightAnchor(runButton, 20.0);
        setBottomAnchor(runButton, 10.0);

        getChildren().addAll(codeMirrorView, iconButtonBarView);
    }

    public void displayRunButton() {
        if (!runButtonDisplayed) {
            runButton.setStyle("-fx-padding: 3 6; -fx-background-radius: 2; -fx-cursor: hand; -fx-font-size: 14px;" +
                    "-fx-background-color: #2196F3; -fx-text-fill: white; -fx-min-width: 50px");

            runButton.setOnMouseEntered(e -> runButton.setStyle("-fx-padding: 3 6; -fx-background-radius: 2; -fx-cursor: hand; -fx-font-size: 14px;" +
                    "-fx-background-color: #1976D2; -fx-text-fill: white; -fx-min-width: 50px"));
            runButton.setOnMouseExited(e -> runButton.setStyle("-fx-padding: 3 6; -fx-background-radius: 2; -fx-cursor: hand; -fx-font-size: 14px;" +
                    "-fx-background-color: #2196F3; -fx-text-fill: white; -fx-min-width: 50px"));

            getChildren().add(runButton);
            runButtonDisplayed = true;
        }
    }

    public CodeMirrorView getCodeMirrorView() {
        return codeMirrorView;
    }

    public void setCodeMirrorViewContent(String content) {
        codeMirrorView.setContent(content);
    }

    public IconButtonBarView getIconButtonBarView() {
        return iconButtonBarView;
    }

    public CustomButton getRunButton() {
        return runButton;
    }
}