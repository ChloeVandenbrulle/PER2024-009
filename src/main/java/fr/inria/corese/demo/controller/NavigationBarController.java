package fr.inria.corese.demo.controller;

import fr.inria.corese.demo.manager.ApplicationStateManager;
import fr.inria.corese.demo.view.NavigationBarView;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import javafx.fxml.FXMLLoader;
import java.io.IOException;
import javafx.scene.control.Button;

/**
 * Controller for the navigation bar.
 * Manages navigation between different views of the application.
 */
public class NavigationBarController {
    private final BorderPane mainContent;
    private final ApplicationStateManager stateManager;
    private final NavigationBarView view;
    private String currentViewName;

    /**
     * Constructor for the navigation bar controller.
     *
     * @param mainContent The main content pane
     */
    public NavigationBarController(BorderPane mainContent) {
        if (mainContent == null) {
            throw new IllegalArgumentException("mainContent cannot be null");
        }
        this.mainContent = mainContent;
        this.stateManager = ApplicationStateManager.getInstance();
        this.view = new NavigationBarView();

        initializeButtons();
    }

    /**
     * Selects and loads a view.
     *
     * @param viewName The name of the view to select
     */
    public void selectView(String viewName) {
        try {
            // Save current state before changing views
            if (currentViewName != null) {
                stateManager.saveCurrentState();
                stateManager.addLogEntry("State saved before navigating from " + currentViewName + " to " + viewName);
            }

            // Remember the current view name for future operations
            currentViewName = viewName;

            String fxmlPath = "/fr/inria/corese/demo/" + viewName + ".fxml";
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));

            Node content = loader.load();
            Object controller = loader.getController();

            // Inject dependencies and restore state based on controller type
            if (controller instanceof DataViewController) {
                stateManager.restoreState();
                stateManager.addLogEntry("Restored state for DataViewController");
            } else if (controller instanceof QueryViewController) {
                stateManager.restoreState();
                stateManager.addLogEntry("Restored state for QueryViewController");
            } else if (controller instanceof RDFEditorViewController) {
                stateManager.restoreState();
                stateManager.addLogEntry("Restored state for RDFEditorViewController");
            } else if (controller instanceof ValidationViewController) {
                stateManager.restoreState();
                stateManager.addLogEntry("Restored state for ValidationViewController");
            }

            // Update button selection
            Button selectedButton = getButtonForView(viewName);
            if (selectedButton != null) {
                view.setButtonSelected(selectedButton);
            }

            mainContent.setCenter(content);
            stateManager.addLogEntry("View changed to " + viewName);

        } catch (IOException e) {
            stateManager.addLogEntry("Error loading view: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Initializes the button event handlers.
     */
    private void initializeButtons() {
        view.getDataButton().setOnAction(e -> {
            stateManager.addLogEntry("Data button clicked");
            selectView("data-view");
        });

        view.getRdfEditorButton().setOnAction(e -> {
            stateManager.addLogEntry("RDF Editor button clicked");
            selectView("rdf-editor-view");
        });

        view.getValidationButton().setOnAction(e -> {
            stateManager.addLogEntry("Validation button clicked");
            selectView("validation-view");
        });

        view.getQueryButton().setOnAction(e -> {
            stateManager.addLogEntry("Query button clicked");
            selectView("query-view");
        });

        view.getSettingsButton().setOnAction(e -> {
            stateManager.addLogEntry("Settings button clicked");
            selectView("settings-view");
        });
    }

    /**
     * Gets the button for a specific view.
     *
     * @param viewName The name of the view
     * @return The button for the view
     */
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

    /**
     * Gets the navigation bar view.
     *
     * @return The navigation bar view
     */
    public NavigationBarView getView() {
        return view;
    }
}