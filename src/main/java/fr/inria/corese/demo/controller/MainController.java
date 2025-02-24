package fr.inria.corese.demo.controller;

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


    @FXML
    public void initialize() {
        // Initialiser le modèle de données du projet
        projectDataModel = new ProjectDataModel();

        // Initialiser le contrôleur de navigation avec le conteneur principal
        navigationBarController = new NavigationBarController(contentArea, projectDataModel);

        // Ajouter la vue de navigation
        navigationContainer.getChildren().clear();
        navigationContainer.getChildren().add(navigationBarController.getView());

        // Charger la vue validation par défaut
        navigationBarController.selectView("data-view");
    }

    public ProjectDataModel getProjectDataModel() {
        return projectDataModel;
    }
}