package fr.inria.corese.demo.view.popup;

import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

public class AlertPopup extends BasePopup {
    private final Button okButton;

    public AlertPopup() {
        setTitle("Alert");

        BorderPane contentPane = new BorderPane();
        Label messageLabel = new Label();
        messageLabel.textProperty().bind(new SimpleStringProperty(message));

        okButton = new Button("OK");
        okButton.setOnAction(e -> hide());

        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);
        buttonBox.getChildren().add(okButton);

        contentPane.setCenter(messageLabel);
        contentPane.setBottom(buttonBox);

        getDialogPane().setContent(contentPane);
    }
}
