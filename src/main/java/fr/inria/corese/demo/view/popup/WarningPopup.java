package fr.inria.corese.demo.view.popup;

import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

public class WarningPopup extends BasePopup {
    private final Button confirmButton;
    private final Button cancelButton;

    public WarningPopup() {
        setTitle("Warning");

        BorderPane contentPane = new BorderPane();
        Label messageLabel = new Label();
        messageLabel.textProperty().bind(new SimpleStringProperty(message));

        confirmButton = new Button("Confirm");
        cancelButton = new Button("Cancel");

        confirmButton.setOnAction(e -> onConfirmClick());
        cancelButton.setOnAction(e -> onCancelClick());

        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);
        buttonBox.getChildren().addAll(cancelButton, confirmButton);

        contentPane.setCenter(messageLabel);
        contentPane.setBottom(buttonBox);

        getDialogPane().setContent(contentPane);
    }

    protected void onConfirmClick() {
        // À implémenter dans les sous-classes si nécessaire
        hide();
    }

    protected void onCancelClick() {
        hide();
    }
}