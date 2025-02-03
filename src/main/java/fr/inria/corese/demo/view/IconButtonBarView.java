package fr.inria.corese.demo.view;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.layout.VBox;
import org.kordamp.ikonli.feather.Feather;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.materialdesign.MaterialDesign;
import org.kordamp.ikonli.Ikon;
import org.kordamp.ikonli.materialdesign2.MaterialDesignE;
import org.kordamp.ikonli.materialdesign2.MaterialDesignF;
import org.kordamp.ikonli.materialdesign2.MaterialDesignI;
import org.kordamp.ikonli.materialdesign2.MaterialDesignU;
import org.kordamp.ikonli.materialdesign2.MaterialDesignR;
import org.kordamp.ikonli.materialdesign2.MaterialDesignB;
import org.kordamp.ikonli.materialdesign2.MaterialDesignC;
import org.kordamp.ikonli.materialdesign2.MaterialDesignM;

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
    private Button zoomInButton;
    private Button zoomOutButton;
    private Button fullScreenButton;
    @FXML
    private StringProperty page = new SimpleStringProperty();

    public IconButtonBarView() {
        this("editor"); // Valeur par défaut
    }

    public String getPage() {
        return page.get();
    }

    public void setPage(String page) {
        this.page.set(page);
    }

    public StringProperty pageProperty() {
        return page;
    }

    public IconButtonBarView(String page) {
        setSpacing(10); // Ajoute un espacement entre les boutons
        setPadding(new Insets(15, 0, 0, 0));

        saveButton = createIconButton(MaterialDesignC.CONTENT_SAVE_OUTLINE);
        openFilesButton = createIconButton(MaterialDesignF.FOLDER_OPEN);
        exportButton = createIconButton(MaterialDesignE.EXPORT);
        importButton = createIconButton(MaterialDesignI.IMPORT);
        previousStepButton = createIconButton(MaterialDesignU.UNDO);
        nextStepButton = createIconButton(MaterialDesignR.REDO);
        documentationButton = createIconButton(MaterialDesignB.BOOK_OPEN_VARIANT);
        clearButton = createIconButton(MaterialDesignB.BROOM);
        zoomInButton = createIconButton(MaterialDesignM.MAGNIFY_PLUS_OUTLINE);
        zoomOutButton = createIconButton(MaterialDesignM.MAGNIFY_MINUS_OUTLINE);
        fullScreenButton = createIconButton(MaterialDesignF.FULLSCREEN);

        if (page=="editor") {
            getChildren().addAll(
                    saveButton,
                    exportButton,
                    previousStepButton,
                    nextStepButton,
                    documentationButton
            );
        } else if (page=="validation") {
            getChildren().addAll(
                    saveButton,
                    openFilesButton,
                    importButton,
                    exportButton,
                    clearButton,
                    previousStepButton,
                    nextStepButton,
                    documentationButton
            );
        } else if (page=="query") {
            getChildren().addAll(
                    exportButton,
                    importButton
            );
        } else if (page=="query-table") {
            getChildren().addAll(
                    exportButton
            );
        } else if (page == "query-") {
            getChildren().addAll(
                    exportButton,
                    zoomInButton,
                    zoomOutButton,
                    fullScreenButton
            );
        } else if (page=="query-file") {
            getChildren().addAll(
                    exportButton
            );
        }
    }

    private Button createIconButton(Ikon icon) {
        FontIcon fontIcon = new FontIcon(icon);
        fontIcon.setIconSize(20);  // Taille de l'icône en pixels
        Button button = new Button();
        button.setGraphic(fontIcon);
        button.setStyle(
            "-fx-background-color: white;" +
            "-fx-border-radius: 20;" +
            "-fx-background-radius: 20;" +
            "-fx-min-height: 10;" +
            "-fx-max-width: 10;"
        );

        button.setOnMouseEntered(e -> button.setStyle(
            "-fx-background-color: #E3F2FD;" +
            "-fx-border-radius: 20;" +
            "-fx-background-radius: 20;" +
            "-fx-min-height: 10;" +
            "-fx-max-width: 10;"
        ));

        button.setOnMouseExited(e -> button.setStyle(
            "-fx-background-color: white;" +
            "-fx-border-radius: 20;" +
            "-fx-background-radius: 20;" +
            "-fx-min-height: 10;" +
            "-fx-max-width: 10;"
        ));
        return button;
    }
}