package fr.inria.corese.demo.controller;

import fr.inria.corese.demo.manager.ApplicationStateManager;
import javafx.fxml.FXML;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

/**
 * Main controller for the application.
 * Manages the main container, navigation, and content area.
 */
public class MainController {
    @FXML private BorderPane mainContainer;
    @FXML private VBox navigationContainer;
    @FXML private BorderPane contentArea;

    private NavigationBarController navigationBarController;
    private ApplicationStateManager stateManager;

    /**
     * Initializes the controller.
     * Sets up the state manager and navigation.
     */
    @FXML
    public void initialize() {
        // Get the global state manager
        stateManager = ApplicationStateManager.getInstance();

        // Initialize the navigation controller with the content area
        navigationBarController = new NavigationBarController(contentArea);

        // Set up the navigation view
        navigationContainer.getChildren().clear();
        navigationContainer.getChildren().add(navigationBarController.getView());

        // Load the default view
        navigationBarController.selectView("data-view");

        stateManager.addLogEntry("MainController initialization complete");
    }
}