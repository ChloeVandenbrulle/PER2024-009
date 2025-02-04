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
        if (mainContent == null) {
            throw new IllegalArgumentException("mainContent cannot be null");
        }
        this.mainContent = mainContent;
        this.view = new NavigationBarView();
        System.out.println("NavigationBarController initialized with mainContent: " + mainContent);
        initializeButtons();
    }

    private void initializeButtons() {
        view.getDataButton().setOnAction(e -> {
            System.out.println("Data button clicked");
            selectView("data-view");
        });

        view.getRdfEditorButton().setOnAction(e -> {
            System.out.println("RDF Editor button clicked");
            selectView("rdf-editor-view");
        });

        view.getValidationButton().setOnAction(e -> {
            System.out.println("Validation button clicked");
            selectView("validation-view");
        });

        view.getQueryButton().setOnAction(e -> {
            System.out.println("Query button clicked");
            selectView("query-view");
        });

        view.getSettingsButton().setOnAction(e -> {
            System.out.println("Settings button clicked");
            selectView("settings-view");
        });

        System.out.println("All button handlers initialized");
    }

    public void selectView(String viewName) {
        try {
            System.out.println("Attempting to select view: " + viewName);
            String fxmlPath = "/fr/inria/corese/demo/" + viewName + ".fxml";

            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            System.out.println("FXML Loader created for: " + fxmlPath);

            Node content = loader.load();
            System.out.println("Content loaded successfully");

            if (mainContent == null) {
                System.err.println("mainContent is null when trying to set center!");
                return;
            }

            mainContent.setCenter(content);
            System.out.println("Content set to center of mainContent");

            // Update selected button
            Button selectedButton = getButtonForView(viewName);
            if (selectedButton != null) {
                view.setButtonSelected(selectedButton);
                System.out.println("Button selected: " + selectedButton.getText());
            }

        } catch (IOException e) {
            System.err.println("Error loading " + viewName + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    private Button getButtonForView(String viewName) {
        return switch (viewName) {
            case "data-view" -> view.getDataButton();
            case "validation-view" -> view.getValidationButton();
            case "rdf-editor-view" -> view.getRdfEditorButton();
            case "query-view" -> view.getQueryButton();
            case "settings-view" -> view.getSettingsButton();
            default -> null;
        };
    }

    public NavigationBarView getView() {
        return view;
    }
}