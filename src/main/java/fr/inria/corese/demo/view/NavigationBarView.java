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

    private static final String BUTTON_STYLE_NORMAL = """
            -fx-background-color: white;
            -fx-text-fill: #2196F3;
            -fx-border-color: #2196F3;
            -fx-border-width: 0 0 1 0;
            -fx-min-height: 35;
            -fx-font-size: 14;
            -fx-min-width: 160;
            """;

    private static final String BUTTON_STYLE_SELECTED = """
            -fx-background-color: #E3F2FD;
            -fx-text-fill: #2196F3;
            -fx-border-color: #2196F3;
            -fx-border-width: 0 0 1 3;
            -fx-min-height: 35;
            -fx-font-size: 14;
            -fx-min-width: 160;
            """;


    private static final String BUTTON_STYLE_HOVER = """
            -fx-background-color: #E3F2FD;
            -fx-text-fill: #2196F3;
            -fx-border-color: #2196F3;
            -fx-border-width: 0 0 1 0;
            -fx-min-height: 35;
            -fx-font-size: 14;
            -fx-min-width: 160;
            """;

    public NavigationBarView() {

        dataButton = createNavigationButton("Data");
        rdfEditorButton = createNavigationButton("RDF Editor");
        validationButton = createNavigationButton("Validation");
        queryButton = createNavigationButton("Query");
        settingsButton = createNavigationButton("Settings");

        getChildren().addAll(dataButton, rdfEditorButton, validationButton, queryButton, settingsButton);
    }

    private Button createNavigationButton(String text) {
        Button button = new Button(text);
        button.setStyle(BUTTON_STYLE_NORMAL);

        button.setOnMouseEntered(e -> {
            if (!button.getStyle().equals(BUTTON_STYLE_SELECTED)) {
                button.setStyle(BUTTON_STYLE_HOVER);
            }
        });

        button.setOnMouseExited(e -> {
            if (!button.getStyle().equals(BUTTON_STYLE_SELECTED)) {
                button.setStyle(BUTTON_STYLE_NORMAL);
            }
        });

        button.setPrefWidth(160);
        return button;
    }

    public void setButtonSelected(Button selectedButton) {
        // Réinitialiser tous les boutons
        for (Button button : new Button[]{dataButton, rdfEditorButton, validationButton, queryButton, settingsButton}) {
            button.setStyle(BUTTON_STYLE_NORMAL);
        }
        // Définir le style sélectionné pour le bouton actif
        selectedButton.setStyle(BUTTON_STYLE_SELECTED);
    }

    // Getters
    public Button getDataButton() { return dataButton; }
    public Button getRdfEditorButton() { return rdfEditorButton; }
    public Button getValidationButton() { return validationButton; }
    public Button getQueryButton() { return queryButton; }
    public Button getSettingsButton() { return settingsButton; }
}