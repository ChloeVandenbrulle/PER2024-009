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

    public EmptyStateView() {
        setStyle("-fx-background-color: white;");
        getStyleClass().add("empty-state-view");

        VBox container = new VBox(15);
        container.setAlignment(Pos.CENTER);
        container.setMaxWidth(400);
        container.setMaxHeight(300);

        String folderOpenPath = "M20 6h-8l-2-2H4c-1.1 0-1.99.9-1.99 2L2 18c0 1.1.9 2 2 2h16c1.1 0 2-.9 2-2V8c0-1.1-.9-2-2-2zm0 12H4V8h16v10z";

        SVGPath folderOpenIcon = new SVGPath();
        folderOpenIcon.setContent(folderOpenPath);
        folderOpenIcon.setFill(Color.web("#2196F3"));  // Couleur bleue primaire
        folderOpenIcon.setScaleX(2.5);
        folderOpenIcon.setScaleY(2.5);

        // Labels
        Label titleLabel = new Label("No files loaded");
        titleLabel.setFont(Font.font("System", FontWeight.SEMI_BOLD, 16));
        titleLabel.setTextFill(Color.web("#333333"));

        Label messageLabel = new Label("Open a folder or load a TTL file\nto visualize semantic graphs");
        messageLabel.setFont(Font.font("System", 14));
        messageLabel.setTextFill(Color.web("#666666"));
        messageLabel.setAlignment(Pos.CENTER);
        messageLabel.setWrapText(true);
        messageLabel.setStyle("-fx-text-alignment: center;");

        container.getChildren().addAll(folderOpenIcon, titleLabel, messageLabel);
        getChildren().add(container);
    }
}