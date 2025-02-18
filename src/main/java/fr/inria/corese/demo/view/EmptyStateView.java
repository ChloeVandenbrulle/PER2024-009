package fr.inria.corese.demo.view;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.Group;
import javafx.scene.layout.StackPane;

public class EmptyStateView extends StackPane {

    public EmptyStateView() {
        setStyle("-fx-background-color: white;");

        VBox container = new VBox(20);
        container.setAlignment(Pos.CENTER);
        container.setMaxWidth(400);
        container.setMaxHeight(300);

        // Create folder icon with blur effect
        String folderIconPath = "M20 6h-8l-2-2H4c-1.1 0-1.99.9-1.99 2L2 18c0 1.1.9 2 2 2h16c1.1 0 2-.9 2-2V8c0-1.1-.9-2-2-2zm0 12H4V8h16v10z";

        // Blurred background icon
        SVGPath blurredIcon = new SVGPath();
        blurredIcon.setContent(folderIconPath);
        blurredIcon.setFill(Color.web("#757575", 0.5));
        blurredIcon.setScaleX(3);
        blurredIcon.setScaleY(3);
        GaussianBlur blur = new GaussianBlur(3);
        blurredIcon.setEffect(blur);

        // Sharp foreground icon
        SVGPath sharpIcon = new SVGPath();
        sharpIcon.setContent(folderIconPath);
        sharpIcon.setFill(Color.web("#757575", 0.6));
        sharpIcon.setScaleX(3);
        sharpIcon.setScaleY(3);

        Group iconGroup = new Group(blurredIcon, sharpIcon);

        // Labels
        Label titleLabel = new Label("Aucun fichier chargé");
        titleLabel.setFont(Font.font("System", FontWeight.SEMI_BOLD, 18));
        titleLabel.setTextFill(Color.web("#333333"));

        Label messageLabel = new Label("Ouvrez un dossier ou chargez un fichier TTL\npour visualiser les graphes sémantiques");
        messageLabel.setFont(Font.font("System", 14));
        messageLabel.setTextFill(Color.web("#666666"));
        messageLabel.setAlignment(Pos.CENTER);
        messageLabel.setWrapText(true);
        messageLabel.setStyle("-fx-text-alignment: center;");

        container.getChildren().addAll(iconGroup, titleLabel, messageLabel);
        getChildren().add(container);
    }
}