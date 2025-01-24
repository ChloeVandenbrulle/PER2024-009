package fr.inria.corese.demo.view;

import atlantafx.base.theme.Styles;
import fr.inria.corese.demo.controller.NavigationBarController;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import lombok.Getter;

public class NavigationBarView extends VBox {
    public final Button dataButton;
    public final Button rdfEditorButton;
    public final Button validationButton;
    public final Button queryButton;
    public final Button settingsButton;

    public NavigationBarView() {
        dataButton = new Button("Data");
        rdfEditorButton = new Button("RDF Editor");
        validationButton = new Button("Validation");
        queryButton = new Button("Query");
        settingsButton = new Button("Settings");
        initializeButtonsStyles();

        getChildren().addAll(dataButton, rdfEditorButton, validationButton, queryButton, settingsButton);
    }

    private void initializeButtonsStyles() {
        // Styles spéciaux pour les boutons du menu latéral
        dataButton.getStyleClass().addAll("rounded-button", Styles.BUTTON_OUTLINED, Styles.ACCENT);
        rdfEditorButton.getStyleClass().addAll("rounded-button", Styles.BUTTON_OUTLINED, Styles.ACCENT);
        validationButton.getStyleClass().addAll("rounded-button", Styles.BUTTON_OUTLINED, Styles.ACCENT);
        queryButton.getStyleClass().addAll("rounded-button", Styles.BUTTON_OUTLINED, Styles.ACCENT);
        settingsButton.getStyleClass().addAll("rounded-button", Styles.BUTTON_OUTLINED, Styles.ACCENT);

        // Configuration de la largeur maximale pour les boutons du menu
        dataButton.setMaxWidth(Double.MAX_VALUE);
        rdfEditorButton.setMaxWidth(Double.MAX_VALUE);
        validationButton.setMaxWidth(Double.MAX_VALUE);
        queryButton.setMaxWidth(Double.MAX_VALUE);
        settingsButton.setMaxWidth(Double.MAX_VALUE);

        dataButton.setOnAction(e -> switchToDataView());
        rdfEditorButton.setOnAction(e -> switchToRDFEditor());
        validationButton.setOnAction(e -> switchToValidation());
        queryButton.setOnAction(e -> switchToQuery());
        settingsButton.setOnAction(e -> switchToSettings());
    }

    private void switchToDataView() {
        System.out.println("Switching to Data view");
    }

    private void switchToRDFEditor() {
        System.out.println("Switching to RDF Editor view");
    }

    private void switchToValidation() {
        System.out.println("Switching to Validation view");
    }

    private void switchToQuery() {
        System.out.println("Switching to Query view");
    }

    private void switchToSettings() {
        System.out.println("Switching to Settings view");
    }
}