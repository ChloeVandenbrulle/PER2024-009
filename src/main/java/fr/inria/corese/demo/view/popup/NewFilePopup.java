package fr.inria.corese.demo.view.popup;

import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

public class NewFilePopup extends BasePopup{
    private Button confirmButton;
    private Button cancelButton;
    private TextField fileNameField;
    public NewFilePopup() {
        setTitle("New File");

        BorderPane contentPane = new BorderPane();
        Label messageLabel = new Label();
        messageLabel.textProperty().bind(new SimpleStringProperty(message));

        confirmButton = new Button("Confirm");
        cancelButton = new Button("Cancel");

        confirmButton.setOnAction(e -> { confirmFileName(); });

        fileNameField = new TextField();

        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);
        buttonBox.getChildren().addAll(cancelButton, confirmButton);

        contentPane.setTop(messageLabel);
        contentPane.setCenter(fileNameField);
        contentPane.setBottom(buttonBox);

        getDialogPane().setContent(contentPane);
    }

    private void confirmFileName() {
        if (fileNameField.getText().isEmpty()) {
            setMessage("Please enter a file name");
            return;
        }
        closePopup();
    }
}
