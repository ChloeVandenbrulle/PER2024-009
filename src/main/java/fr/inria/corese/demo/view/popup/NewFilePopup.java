package fr.inria.corese.demo.view.popup;

import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

public class NewFilePopup extends BasePopup {
    private Button confirmButton;
    private Button cancelButton;
    private TextField fileNameField;
    private Runnable onConfirm;

    public NewFilePopup() {
        setTitle("New File");

        BorderPane contentPane = new BorderPane();
        Label messageLabel = new Label();
        messageLabel.textProperty().bind(new SimpleStringProperty(message));

        confirmButton = new Button("Confirm");
        confirmButton.setOnAction(e -> {
            if (onConfirm != null) {
                onConfirm.run();
            }
            closePopup();
        });

        cancelButton = new Button("Cancel");
        cancelButton.setOnAction(e -> closePopup());

        fileNameField = new TextField();

        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);
        buttonBox.getChildren().addAll(cancelButton, confirmButton);

        contentPane.setTop(messageLabel);
        contentPane.setCenter(fileNameField);
        contentPane.setBottom(buttonBox);

        getDialogPane().setContent(contentPane);
        getDialogPane().getButtonTypes().addAll(ButtonType.CLOSE);
        setOnCloseRequest(event -> closePopup());
    }

    public Button getConfirmButton() {
        return confirmButton;
    }

    public Button getCancelButton() {
        return cancelButton;
    }

    public String getFileName() {
        return fileNameField.getText();
    }

    public void setOnConfirm(Runnable callback) {
        this.onConfirm = callback;
    }

}
