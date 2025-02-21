package fr.inria.corese.demo.view;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.layout.StackPane;

public class EmptyStateView extends StackPane {
    private Label titleLabel;
    private Label messageLabel;
    private String image;

    public EmptyStateView(Label title, Label message, String img) {
        titleLabel = title;
        messageLabel = message;
        image = img;

        setupUI();
    }

    private void setupUI(){
        setStyle("-fx-background-color: white;");
        getStyleClass().add("empty-state-view");

        VBox container = new VBox(15);
        container.setAlignment(Pos.CENTER);
        container.setMaxWidth(400);
        container.setMaxHeight(300);

        SVGPath folderOpenIcon = new SVGPath();
        folderOpenIcon.setContent(image);
        folderOpenIcon.setFill(Color.web("#2196F3"));  // Couleur bleue primaire
        folderOpenIcon.setScaleX(2.5);
        folderOpenIcon.setScaleY(2.5);

        titleLabel.setFont(Font.font("System", FontWeight.SEMI_BOLD, 16));
        titleLabel.setTextFill(Color.web("#333333"));

        messageLabel.setFont(Font.font("System", 14));
        messageLabel.setTextFill(Color.web("#666666"));
        messageLabel.setAlignment(Pos.CENTER);
        messageLabel.setWrapText(true);
        messageLabel.setStyle("-fx-text-alignment: center;");

        container.getChildren().addAll(folderOpenIcon, titleLabel, messageLabel);
        getChildren().add(container);
    }

    public void setTitleLabel(Label newLabel) {
        titleLabel = newLabel;
    }

    public void setMessageLabel(Label newLabel) {
        messageLabel = newLabel;
    }

    public void setImage(String newimage) {
        image = newimage;
    }

    public Label getTitleLabel() {
        return titleLabel;
    }

    public Label getMessageLabel() {
        return messageLabel;
    }

    public String getImage() {
        return image;
    }
}