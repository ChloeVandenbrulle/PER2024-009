package fr.inria.corese.demo.view;

import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.geometry.Insets;

public class NavigationBarView extends VBox {
    private final Button dataButton;
    private final Button rdfEditorButton;
    private final Button validationButton;
    private final Button queryButton;
    private final Button settingsButton;

    public NavigationBarView() {
        setSpacing(8);
        setPadding(new Insets(10));

        // Création des boutons
        dataButton = createNavigationButton("Data");
        rdfEditorButton = createNavigationButton("RDF Editor");
        validationButton = createNavigationButton("Validation");
        queryButton = createNavigationButton("Query");
        settingsButton = createNavigationButton("Settings");

        getChildren().addAll(dataButton, rdfEditorButton, validationButton, queryButton, settingsButton);
    }

    private Button createNavigationButton(String text) {
        Button button = new Button(text);

        // Style de base : bleu clair avec bordures arrondies
        button.setStyle("""
            -fx-background-color: white;
            -fx-text-fill: #2196F3;
            -fx-border-color: #2196F3;
            -fx-border-radius: 20;
            -fx-background-radius: 20;
            -fx-min-height: 35;
            -fx-max-width: infinity;
            -fx-font-size: 14;
            """);

        // Effet de survol : légèrement plus foncé
        button.setOnMouseEntered(e -> button.setStyle("""
            -fx-background-color: #E3F2FD;
            -fx-text-fill: #2196F3;
            -fx-border-color: #2196F3;
            -fx-border-radius: 20;
            -fx-background-radius: 20;
            -fx-min-height: 35;
            -fx-max-width: infinity;
            -fx-font-size: 14;
            """));

        // Retour à l'état normal
        button.setOnMouseExited(e -> button.setStyle("""
            -fx-background-color: white;
            -fx-text-fill: #2196F3;
            -fx-border-color: #2196F3;
            -fx-border-radius: 20;
            -fx-background-radius: 20;
            -fx-min-height: 35;
            -fx-max-width: infinity;
            -fx-font-size: 14;
            """));

        // Effet de clic
        button.setOnMousePressed(e -> button.setStyle("""
            -fx-background-color: #BBDEFB;
            -fx-text-fill: #2196F3;
            -fx-border-color: #2196F3;
            -fx-border-radius: 20;
            -fx-background-radius: 20;
            -fx-min-height: 35;
            -fx-max-width: infinity;
            -fx-font-size: 14;
            """));

        button.setPrefWidth(150);
        return button;
    }

    // Getters
    public Button getDataButton() { return dataButton; }
    public Button getRdfEditorButton() { return rdfEditorButton; }
    public Button getValidationButton() { return validationButton; }
    public Button getQueryButton() { return queryButton; }
    public Button getSettingsButton() { return settingsButton; }
}