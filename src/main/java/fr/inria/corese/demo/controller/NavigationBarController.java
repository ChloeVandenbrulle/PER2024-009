package fr.inria.corese.demo.controller;

import fr.inria.corese.demo.view.NavigationBarView;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import javafx.fxml.FXMLLoader;
import java.io.IOException;
import javafx.scene.control.Button;

public class NavigationBarController {
    private final NavigationBarView view;
    private final BorderPane mainContent;

    public NavigationBarController(BorderPane mainContent) {
        this.mainContent = mainContent;
        this.view = new NavigationBarView();
        initializeButtons();
    }

    private void initializeButtons() {
        view.getDataButton().setOnAction(e -> selectView("data-view"));
        view.getRdfEditorButton().setOnAction(e -> selectView("rdf-editor-view"));
        view.getValidationButton().setOnAction(e -> selectView("validation-view"));
        view.getQueryButton().setOnAction(e -> selectView("query-view"));
        view.getSettingsButton().setOnAction(e -> selectView("settings-view"));
    }

    public void selectView(String viewName) {
        // Déterminer quel bouton sélectionner
        Button buttonToSelect = switch (viewName) {
            case "data-view" -> view.getDataButton();
            case "rdf-editor-view" -> view.getRdfEditorButton();
            case "validation-view" -> view.getValidationButton();
            case "query-view" -> view.getQueryButton();
            case "settings-view" -> view.getSettingsButton();
            default -> view.getDataButton();
        };

        // Sélectionner le bouton
        view.setButtonSelected(buttonToSelect);

        // Charger la vue
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fr/inria/corese/demo/" + viewName + ".fxml"));
            Node viewNode = loader.load();
            mainContent.setCenter(viewNode);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error loading view: " + viewName);
        }
    }

    public NavigationBarView getView() {
        return view;
    }
}