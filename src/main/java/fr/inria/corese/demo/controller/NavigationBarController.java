package fr.inria.corese.demo.controller;

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
    private final NavigationBarView view;
    private final BorderPane mainContent;

    /**
     * Construit un contrôleur de barre de navigation.
     *
     * Actions réalisées :
     * - Validation du contenu principal
     * - Création de la vue de barre de navigation
     * - Initialisation des boutons de navigation
     *
     * @param mainContent Le conteneur principal de l'application
     * @throws IllegalArgumentException si le contenu principal est null
     */
    public NavigationBarController(BorderPane mainContent) {
        if (mainContent == null) {
            throw new IllegalArgumentException("mainContent cannot be null");
        }
        this.mainContent = mainContent;
        this.view = new NavigationBarView();
        System.out.println("NavigationBarController initialized with mainContent: " + mainContent);
        initializeButtons();
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
     * Sélectionne et charge une vue spécifique dans le contenu principal.
     *
     * Workflow :
     * 1. Charger le fichier FXML correspondant à la vue
     * 2. Définir le contenu chargé comme centre du conteneur principal
     * 3. Mettre à jour le bouton sélectionné dans la barre de navigation
     *
     * @param viewName Le nom de la vue à charger (par exemple "data-view")
     * @throws IOException en cas d'erreur de chargement du fichier FXML
     */
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