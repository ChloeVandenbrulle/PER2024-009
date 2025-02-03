package fr.inria.corese.demo.view;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.layout.VBox;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.Ikon;
import org.kordamp.ikonli.materialdesign2.*;

import javafx.scene.control.Button;

public class IconButtonBarView extends VBox {
    private Button saveButton;
    private Button openFilesButton;
    private Button exportButton;
    private Button importButton;
    private Button undoButton;
    private Button redoButton;
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
        undoButton = createIconButton(MaterialDesignU.UNDO);
        redoButton = createIconButton(MaterialDesignR.REDO);
        documentationButton = createIconButton(MaterialDesignB.BOOK_OPEN_VARIANT);
        clearButton = createIconButton(MaterialDesignB.BROOM);
        zoomInButton = createIconButton(MaterialDesignM.MAGNIFY_PLUS_OUTLINE);
        zoomOutButton = createIconButton(MaterialDesignM.MAGNIFY_MINUS_OUTLINE);
        fullScreenButton = createIconButton(MaterialDesignF.FULLSCREEN);

        if (page=="editor") {
            getChildren().addAll(
                    saveButton,
                    exportButton,
                    undoButton,
                    redoButton,
                    documentationButton
            );
        } else if (page=="validation") {
            getChildren().addAll(
                    saveButton,
                    openFilesButton,
                    importButton,
                    exportButton,
                    clearButton,
                    undoButton,
                    redoButton,
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

    public Button getSaveButton() {
        return saveButton;
    }

    public Button getOpenFilesButton() {
        return openFilesButton;
    }

    public Button getExportButton() {
        return exportButton;
    }

    public Button getImportButton() {
        return importButton;
    }

    public Button getUndoButton() {
        return undoButton;
    }

    public Button getRedoButton() {
        return redoButton;
    }

    public Button getDocumentationButton() {
        return documentationButton;
    }

    public Button getClearButton() {
        return clearButton;
    }

    public Button getZoomInButton() {
        return zoomInButton;
    }

    public Button getZoomOutButton() {
        return zoomOutButton;
    }

    public Button getFullScreenButton() {
        return fullScreenButton;
    }
}