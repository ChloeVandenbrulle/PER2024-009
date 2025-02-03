package fr.inria.corese.demo.view;

import javafx.geometry.Insets;
import javafx.scene.layout.VBox;
import org.kordamp.ikonli.feather.Feather;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.materialdesign2.MaterialDesignE;
import org.kordamp.ikonli.materialdesign2.MaterialDesignF;
import org.kordamp.ikonli.materialdesign2.MaterialDesignI;
import org.kordamp.ikonli.materialdesign2.MaterialDesignU;
import org.kordamp.ikonli.materialdesign2.MaterialDesignR;
import org.kordamp.ikonli.materialdesign2.MaterialDesignB;

import javafx.scene.control.Button;

public class IconButtonBarView extends VBox {
    private Button saveButton;
    private Button openFilesButton;
    private Button exportButton;
    private Button importButton;
    private Button previousStepButton;
    private Button nextStepButton;
    private Button documentationButton;
    private Button clearButton;

    public IconButtonBarView() {
        setSpacing(10); // Ajoute un espacement entre les boutons
        setMargin(this, new Insets(20, 0,0 ,0));

        FontIcon saveIcon = new FontIcon(Feather.SAVE);
        saveIcon.setIconSize(20);  // Taille de l'icône en pixels
        saveButton = new Button();
        saveButton.setGraphic(saveIcon);
        saveButton.setStyle("""
            -fx-background-color: white;
            -fx-border-radius: 20;
            -fx-background-radius: 20;
            -fx-min-height: 10;
            -fx-max-width: 10;
            """);
        getChildren().add(saveButton);

        FontIcon openFilesIcon = new FontIcon(MaterialDesignF.FOLDER_OPEN);
        openFilesIcon.setIconSize(20);  // Taille de l'icône en pixels
        openFilesButton = new Button();
        openFilesButton.setGraphic(openFilesIcon);
        openFilesButton.setStyle("""
            -fx-background-color: white;
            -fx-border-radius: 20;
            -fx-background-radius: 20;
            -fx-min-height: 10;
            -fx-max-width: 10;
            """);
        getChildren().add(openFilesButton);

        FontIcon exportIcon = new FontIcon(MaterialDesignE.EXPORT);
        exportIcon.setIconSize(20);  // Taille de l'icône en pixels
        exportButton = new Button();
        exportButton.setGraphic(exportIcon);
        exportButton.setStyle("""
            -fx-background-color: white;
            -fx-border-radius: 20;
            -fx-background-radius: 20;
            -fx-min-height: 10;
            -fx-max-width: 10;
            """);
        getChildren().add(exportButton);

        FontIcon importIcon = new FontIcon(MaterialDesignI.IMPORT);
        importIcon.setIconSize(20);  // Taille de l'icône en pixels
        importButton = new Button();
        importButton.setGraphic(importIcon);
        importButton.setStyle("""
            -fx-background-color: white;
            -fx-border-radius: 20;
            -fx-background-radius: 20;
            -fx-min-height: 10;
            -fx-max-width: 10;
            """);
        getChildren().add(importButton);

        FontIcon previousStepIcon = new FontIcon(MaterialDesignU.UNDO);
        previousStepIcon.setIconSize(20);  // Taille de l'icône en pixels
        previousStepButton = new Button();
        previousStepButton.setGraphic(previousStepIcon);
        previousStepButton.setStyle("""
            -fx-background-color: white;
            -fx-border-radius: 20;
            -fx-background-radius: 20;
            -fx-min-height: 10;
            -fx-max-width: 10;
            """);
        getChildren().add(previousStepButton);

        FontIcon nextStepIcon = new FontIcon(MaterialDesignR.REDO);
        nextStepIcon.setIconSize(20);  // Taille de l'icône en pixels
        nextStepButton = new Button();
        nextStepButton.setGraphic(nextStepIcon);
        nextStepButton.setStyle("""
            -fx-background-color: white;
            -fx-border-radius: 20;
            -fx-background-radius: 20;
            -fx-min-height: 10;
            -fx-max-width: 10;
            """);
        getChildren().add(nextStepButton);

        FontIcon documentationIcon = new FontIcon(MaterialDesignB.BOOK_OPEN_VARIANT);
        documentationIcon.setIconSize(20);  // Taille de l'icône en pixels
        documentationButton = new Button();
        documentationButton.setGraphic(documentationIcon);
        documentationButton.setStyle("""
            -fx-background-color: white;
            -fx-border-radius: 20;
            -fx-background-radius: 20;
            -fx-min-height: 10;
            -fx-max-width: 10;
            """);
        getChildren().add(documentationButton);

        FontIcon clearIcon = new FontIcon(MaterialDesignB.BROOM);
        clearIcon.setIconSize(20);  // Taille de l'icône en pixels
        clearButton = new Button();
        clearButton.setGraphic(clearIcon);
        clearButton.setStyle("""
            -fx-background-color: white;
            -fx-border-radius: 20;
            -fx-background-radius: 20;
            -fx-min-height: 10;
            -fx-max-width: 10;
            """);
        getChildren().add(clearButton);
    }

    // note: créer une méthode qui crée la bar en fonction des boutons passés en paramètre
}