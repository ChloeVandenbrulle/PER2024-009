package fr.inria.corese.demo.controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import fr.inria.corese.demo.view.NavigationBarView;
import java.io.IOException;
import java.net.URL;

public class NavigationBarController {
    private final NavigationBarView view;
    private Stage validationStage;

    public NavigationBarController() {
        this.view = new NavigationBarView();
        initializeButtons();
    }

    private void initializeButtons() {
        view.getDataButton().setOnAction(e -> onDataButtonClick());
        view.getRdfEditorButton().setOnAction(e -> onRDFEditorButtonClick());
        view.getValidationButton().setOnAction(e -> onValidationButtonClick());
        view.getQueryButton().setOnAction(e -> onQueryButtonClick());
        view.getSettingsButton().setOnAction(e -> onSettingsButtonClick());
    }

    private void onValidationButtonClick() {
        try {
            if (validationStage == null) {
                // Obtenir l'URL de la ressource FXML
                URL fxmlUrl = getClass().getResource("/fr/inria/corese/demo/main-view.fxml");
                if (fxmlUrl == null) {
                    throw new IOException("Cannot find FXML file at: /fr/inria/corese/demo/main-view.fxml");
                }

                FXMLLoader loader = new FXMLLoader(fxmlUrl);
                Parent root = loader.load();

                validationStage = new Stage();
                validationStage.setTitle("Validation");
                validationStage.setScene(new Scene(root));

                validationStage.setOnCloseRequest(event -> {
                    validationStage.hide();
                    event.consume();
                });
            }

            if (!validationStage.isShowing()) {
                validationStage.show();
            } else {
                validationStage.requestFocus();
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error loading validation view: " + e.getMessage());
        }
    }

    private void onDataButtonClick() {
        System.out.println("Data view clicked");
    }

    private void onRDFEditorButtonClick() {
        System.out.println("RDF Editor clicked");
    }

    private void onQueryButtonClick() {
        System.out.println("Query clicked");
    }

    private void onSettingsButtonClick() {
        System.out.println("Settings clicked");
    }

    public NavigationBarView getView() {
        return view;
    }
}