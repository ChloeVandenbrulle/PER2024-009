package fr.inria.corese.demo.view;

import javafx.scene.layout.VBox;

public class ValidationPageView extends VBox {
    private CodeMirrorView editor;
    private ValidationResultView resultView;

    public ValidationPageView() {
        setupUI();
    }

    private void setupUI() {
        editor = new CodeMirrorView();
        resultView = new ValidationResultView();

        getChildren().addAll(editor, resultView);
    }
}