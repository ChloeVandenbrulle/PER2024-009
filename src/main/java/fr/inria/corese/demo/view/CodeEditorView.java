package fr.inria.corese.demo.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;

public class CodeEditorView extends AnchorPane {
    private final CodeMirrorView codeMirrorView;
    private final IconButtonBarView iconButtonBarView;

    public CodeEditorView() {
        this.codeMirrorView = new CodeMirrorView();
        this.iconButtonBarView = new IconButtonBarView();

        // CodeMirror prend tout l'espace
        setTopAnchor(codeMirrorView, 0.0);
        setRightAnchor(codeMirrorView, 0.0);
        setBottomAnchor(codeMirrorView, 0.0);
        setLeftAnchor(codeMirrorView, 0.0);

        // IconButtonBar en haut à droite
        setTopAnchor(iconButtonBarView, 5.0);
        setRightAnchor(iconButtonBarView, 5.0);

        getChildren().addAll(codeMirrorView, iconButtonBarView);
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
}
