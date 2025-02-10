package fr.inria.corese.demo.view;

import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

public class CodeEditorView extends HBox {
    private final CodeMirrorView codeMirrorView;
    private final IconButtonBarView iconButtonBarView;

    public CodeEditorView() {
        this.codeMirrorView = new CodeMirrorView();
        this.iconButtonBarView = new IconButtonBarView();

        // Définir HBox.hgrow pour que CodeMirror prenne l'espace disponible
        HBox.setHgrow(codeMirrorView, Priority.ALWAYS);

        // S'assurer que CodeMirror utilise tout l'espace disponible
        codeMirrorView.setMaxWidth(Double.MAX_VALUE);
        codeMirrorView.setMaxHeight(Double.MAX_VALUE);

        iconButtonBarView.setAlignment(Pos.TOP_RIGHT);
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
