package fr.inria.corese.demo.controller;

import fr.inria.corese.demo.manager.ApplicationStateManager;
import fr.inria.corese.demo.model.ProjectDataModel;
import fr.inria.corese.demo.view.NavigationBarView;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import javafx.fxml.FXMLLoader;
import java.io.IOException;
import javafx.scene.control.Button;

/**
 * Contrôleur de la barre de navigation pour une application de gestion de données sémantiques.
 *
 * Responsabilités principales :
 * - Gestion de la navigation entre différentes vues de l'application
 * - Configuration des boutons de navigation
 * - Chargement dynamique des vues
 *
 * Fonctionnalités clés :
 * - Initialisation des gestionnaires d'événements pour les boutons de navigation
 * - Sélection et chargement des vues (Données, Éditeur RDF, Validation, Requête, Paramètres)
 * - Mise à jour de l'interface principale
 *
 * Le contrôleur gère :
 * - NavigationBarView pour l'affichage de la barre de navigation
 * - BorderPane principal pour le contenu de l'application
 *
 * @author Clervie Causer
 * @version 1.0
 * @since 2025
 */
public class NavigationBarController {
    private final BorderPane mainContent;
    private final ApplicationStateManager stateManager;
    private final NavigationBarView view;
    private String currentViewName;

    public NavigationBarController(BorderPane mainContent, ProjectDataModel projectDataModel) {
        if (mainContent == null) {
            throw new IllegalArgumentException("mainContent cannot be null");
        }
        this.mainContent = mainContent;
        this.stateManager = ApplicationStateManager.getInstance();
        this.view = new NavigationBarView();

        initializeButtons();
    }

    public void selectView(String viewName) {
        try {
            // Sauvegarde de l'état actuel avant de changer de vue
            if (currentViewName != null) {
                stateManager.saveCurrentState();
                System.out.println("State saved before navigating from " + currentViewName + " to " + viewName);
            }

            // Mémoriser le nom de la vue courante pour les opérations futures
            currentViewName = viewName;

            String fxmlPath = "/fr/inria/corese/demo/" + viewName + ".fxml";
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));

            Node content = loader.load();
            Object controller = loader.getController();

            ProjectDataModel projectDataModel = stateManager.getProjectDataModel();

            // Injection du ProjectDataModel et restauration de l'état
            if (controller instanceof DataViewController) {
                ((DataViewController) controller).setProjectDataModel(projectDataModel);
                stateManager.restoreState();  // Restaurer l'état global pour cette vue
                System.out.println("Restored state for DataViewController");
            } else if (controller instanceof QueryViewController) {
                QueryViewController queryController = (QueryViewController) controller;
                queryController.setProjectDataModel(projectDataModel);
                stateManager.restoreState();  // Restaurer explicitement l'état pour la vue Query
                System.out.println("Restored state for QueryViewController");
            } else if (controller instanceof RDFEditorViewController) {
                // Assurer que l'état global est restauré aussi pour l'éditeur RDF
                stateManager.restoreState();
                System.out.println("Restored state for RDFEditorViewController");
            } else if (controller instanceof ValidationViewController) {
                // Assurer que l'état global est restauré aussi pour la vue de validation
                stateManager.restoreState();
                System.out.println("Restored state for ValidationViewController");
            }

            // Sélection visuelle du bouton correspondant
            Button selectedButton = getButtonForView(viewName);
            if (selectedButton != null) {
                view.setButtonSelected(selectedButton);
            }

            mainContent.setCenter(content);

            // Log de debug
            System.out.println("View changed to " + viewName);
        } catch (IOException e) {
            System.err.println("Error loading view: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Initialise les gestionnaires d'événements pour les boutons de navigation.
     *
     * Configure les actions pour les boutons :
     * - Données
     * - Éditeur RDF
     * - Validation
     * - Requête
     * - Paramètres
     *
     * Chaque bouton déclenche le chargement de sa vue correspondante.
     */
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

    /**
     * Récupère le bouton correspondant à une vue spécifique.
     *
     * @param viewName Le nom de la vue
     * @return Le bouton associé à la vue, ou null si aucun bouton correspondant n'est trouvé
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
     * Récupère la vue de la barre de navigation.
     *
     * @return La NavigationBarView associée à ce contrôleur
     */
    public NavigationBarView getView() {
        return view;
    }
}