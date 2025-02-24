package fr.inria.corese.demo.controller;

import fr.inria.corese.demo.manager.ApplicationStateManager;
import fr.inria.corese.demo.model.ProjectDataModel;
import javafx.fxml.FXML;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

public class MainController {
    @FXML private BorderPane mainContainer;
    @FXML private VBox navigationContainer;
    @FXML private BorderPane contentArea;

    private NavigationBarController navigationBarController;
    private ProjectDataModel projectDataModel;
    private ApplicationStateManager stateManager;

    @FXML
    public void initialize() {
        // Initialiser le gestionnaire d'état global
        stateManager = ApplicationStateManager.getInstance();

        // Obtenir le modèle de données partagé via le gestionnaire d'état
        projectDataModel = stateManager.getProjectDataModel();

        System.out.println("MainController: initializing with shared ProjectDataModel");

        // Initialiser le contrôleur de navigation avec le conteneur principal et le modèle de données
        navigationBarController = new NavigationBarController(contentArea, projectDataModel);

        // Ajouter la vue de navigation
        navigationContainer.getChildren().clear();
        navigationContainer.getChildren().add(navigationBarController.getView());

        // Charger la vue validation par défaut
        navigationBarController.selectView("data-view");

        System.out.println("MainController initialization complete");
    }

    public ProjectDataModel getProjectDataModel() {
        return projectDataModel;
    }
}