package fr.inria.corese.demo.view;


import javafx.scene.layout.VBox;
import javafx.scene.control.TextArea;

public class ValidationResultView extends VBox {
    private TextArea resultArea;

    public ValidationResultView() {
        setupUI();
    }

    private void setupUI() {
        resultArea = new TextArea();
        resultArea.setEditable(false);
        resultArea.setWrapText(true);

        getChildren().add(resultArea);
    }

    public void displayResult(String result) {
        resultArea.setText(result);
    }
}
