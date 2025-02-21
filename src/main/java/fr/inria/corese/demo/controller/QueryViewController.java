package fr.inria.corese.demo.controller;

import fr.inria.corese.core.Graph;
import fr.inria.corese.core.load.Load;
import fr.inria.corese.core.query.QueryProcess;
import fr.inria.corese.demo.enums.icon.IconButtonBarType;
import fr.inria.corese.demo.model.TabEditorModel;
import fr.inria.corese.demo.view.CustomButton;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import javafx.application.Platform;


/**
 * Contrôleur de la vue de requêtes pour une application de gestion de données sémantiques.
 *
 * Responsabilités principales :
 * - Gestion de l'interface utilisateur pour l'édition et l'exécution de requêtes SPARQL
 * - Initialisation du moteur de requêtage Corese
 * - Gestion du chargement et de l'exécution des requêtes
 *
 * Fonctionnalités clés :
 * - Chargement et gestion de graphes RDF
 * - Édition de requêtes avec support de plusieurs onglets
 * - Exécution de requêtes SPARQL
 * - Gestion de l'interface utilisateur dynamique
 *
 * Caractéristiques principales :
 * - Utilisation de Corese comme moteur de requêtage
 * - Chargement de données de test
 * - Configuration dynamique des éditeurs de requêtes
 * - Gestion des interactions utilisateur
 *
 * @author Généré automatiquement
 * @version 1.0
 * @since 2024
 */
public class QueryViewController {
    @FXML private StackPane editorContainer;
    @FXML private TextArea resultTextArea;
    @FXML private BorderPane mainBorderPane;
    @FXML private SplitPane mainSplitPane;

    private TabEditorController tabEditorController;
    private Graph graph;
    private QueryProcess exec;
    public TabPane resultsTabPane;

    /**
     * Constructeur par défaut du contrôleur de vue de requêtes.
     *
     * Initialise les composants de base du contrôleur.
     */
    public QueryViewController() {
        System.out.println("QueryViewController initialized");
    }

    /**
     * Initialise les composants de la vue de requêtes.
     *
     * Actions principales :
     * - Initialisation du moteur Corese
     * - Création d'un fichier temporaire avec des données de test
     * - Configuration du contrôleur d'éditeur de requêtes
     * - Configuration des écouteurs d'événements
     * - Gestion de la disposition de l'interface utilisateur
     */
    @FXML
    public void initialize() {
        // Initialiser Corese
        initializeCorese();

        // Créer un fichier temporaire avec les données de test
        try {
            // Créer fichier temporaire
            File tempFile = File.createTempFile("test", ".ttl");
            tempFile.deleteOnExit();

            // Données de test
            String testData = """
            @prefix ex: <http://example.org/> .
            @prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .
            
            ex:Alice rdf:type ex:Person ;
                ex:name "Alice" ;
                ex:age "25" ;
                ex:knows ex:Bob .
                
            ex:Bob rdf:type ex:Person ;
                ex:name "Bob" ;
                ex:age "30" .
            """;

            // Écrire les données dans le fichier temporaire
            try (FileWriter writer = new FileWriter(tempFile)) {
                writer.write(testData);
            }

            // Charger les données de test
            Load ld = Load.create(graph);
            ld.load(tempFile.getAbsolutePath());
            System.out.println("Données de test chargées avec succès");

            // Créer le TabEditorController avec le type spécifique pour les requêtes
            tabEditorController = new TabEditorController(IconButtonBarType.QUERY);

            // Ajouter un onglet de requête avec une requête de test
            String testQuery = """
            PREFIX ex: <http://example.org/>
            SELECT ?person ?name ?age
            WHERE {
                ?person ex:name ?name .
                ?person ex:age ?age .
            }
            """;

            addNewQueryTab("Test Query", testQuery);

            // Injecter le TabEditor dans le conteneur
            if (editorContainer != null) {
                editorContainer.getChildren().add(tabEditorController.getView());
            }

            // Configurer le gestionnaire d'événements pour le bouton Run et Ctrl+Enter
            setupRunButton();

        } catch (IOException e) {
            showError("Error Loading Test Data", e.getMessage());
        }

        // Définir la position initiale du diviseur à 0.6 (60% pour l'éditeur, 40% pour les résultats)
        Platform.runLater(() -> mainSplitPane.setDividerPosition(0, 0.6));

        // Ajouter un listener pour ajuster la position du diviseur
        mainSplitPane.getDividers().get(0).positionProperty().addListener((obs, oldPos, newPos) -> {
            if (newPos.doubleValue() < 0.25) { // Minimum 25% pour l'éditeur
                Platform.runLater(() -> mainSplitPane.setDividerPosition(0, 0.25));
            } else if (newPos.doubleValue() < 0.4) { // Entre 25% et 40%, on force à 40%
                Platform.runLater(() -> mainSplitPane.setDividerPosition(0, 0.4));
            }
        });

        // Ajouter un listener pour redimensionner en fonction de la hauteur de la fenêtre
        mainBorderPane.heightProperty().addListener((obs, oldHeight, newHeight) -> {
            if (newHeight.doubleValue() > 0) {
                double currentDividerPos = mainSplitPane.getDividerPositions()[0];
                double resultsPaneHeight = newHeight.doubleValue() * (1 - currentDividerPos);

                if (resultsPaneHeight > newHeight.doubleValue() * 0.75) {
                    // Si la partie résultats dépasse 75% de la hauteur totale
                    double newDividerPos = 0.25; // On garde 25% minimum pour l'éditeur
                    Platform.runLater(() -> mainSplitPane.setDividerPosition(0, newDividerPos));
                }
            }
        });
    }

    /**
     * Initialise le moteur de requêtage Corese.
     *
     * Crée un nouveau graphe et un processus de requêtage.
     */
    private void initializeCorese() {
        try {
            graph = Graph.create();
            exec = QueryProcess.create(graph);
        } catch (Exception e) {
            showError("Error initializing Corese", e.getMessage());
        }
    }

    /**
     * Configure le bouton d'exécution de requête.
     *
     * Gère :
     * - L'affichage du bouton Run
     * - Les actions du bouton
     * - Le raccourci clavier Ctrl+Entrée
     */
    private void setupRunButton() {
        // Configuration initiale pour l'onglet actif
        Tab currentTab = tabEditorController.getView().getTabPane().getSelectionModel().getSelectedItem();
        if (currentTab != null && currentTab != tabEditorController.getView().getAddTab()) {
            CodeEditorController codeEditorController = tabEditorController.getModel().getControllerForTab(currentTab);
            if (codeEditorController != null) {
                configureEditorRunButton(codeEditorController);
            }
        }

        // Écouter les changements d'onglets
        tabEditorController.getView().getTabPane().getSelectionModel().selectedItemProperty().addListener((obs, oldTab, newTab) -> {
            if (newTab != null && newTab != tabEditorController.getView().getAddTab()) {
                CodeEditorController codeEditorController = tabEditorController.getModel().getControllerForTab(newTab);
                if (codeEditorController != null) {
                    configureEditorRunButton(codeEditorController);
                }
            }
        });
    }

    /**
     * Configure le bouton d'exécution pour un éditeur de code spécifique.
     *
     * @param codeEditorController Le contrôleur de l'éditeur de code
     */
    private void configureEditorRunButton(CodeEditorController codeEditorController) {
        // S'assurer que le bouton Run est visible
        codeEditorController.getView().displayRunButton();

        // Configurer l'action du bouton Run
        CustomButton runButton = codeEditorController.getView().getRunButton();
        runButton.setOnAction(e -> {
            System.out.println("Run button clicked");
            executeQuery();
        });

        // Configurer le raccourci Ctrl+Enter
        codeEditorController.getView().setOnKeyPressed(event -> {
            if (event.isControlDown() && event.getCode() == KeyCode.ENTER) {
                System.out.println("Ctrl+Enter pressed");
                executeQuery();
            }
        });
    }

    /**
     * Exécute la requête actuellement sélectionnée.
     *
     * Récupère et affiche le contenu de la requête depuis l'onglet actif.
     */
    private void executeQuery() {
        Tab selectedTab = tabEditorController.getView().getTabPane().getSelectionModel().getSelectedItem();
        if (selectedTab != null && selectedTab != tabEditorController.getView().getAddTab()) {
            CodeEditorController codeEditorController = tabEditorController.getModel().getControllerForTab(selectedTab);
            if (codeEditorController != null) {
                // Obtenir le contenu directement depuis le CodeMirrorView
                String queryContent = codeEditorController.getView().getCodeMirrorView().getContent();
                System.out.println("Executing query: " + queryContent);
                // Afficher directement la requête dans le TextArea
                resultTextArea.setText(queryContent);
            }
        }
    }

    /**
     * Affiche un message d'erreur à l'utilisateur.
     *
     * @param title Le titre de l'alerte
     * @param message Le message d'erreur à afficher
     */
    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Ajoute un nouvel onglet avec le contenu spécifié
     *
     * @param title Titre de l'onglet
     * @param content Contenu initial de l'éditeur
     * @return L'onglet créé
     */
    public Tab addNewQueryTab(String title, String content) {
        CodeEditorController codeEditorController = new CodeEditorController(IconButtonBarType.QUERY, content);

        // Créer l'onglet et l'ajouter au TabEditorView
        Tab tab = tabEditorController.getView().addNewEditorTab(title, codeEditorController.getView());
        tabEditorController.getModel().addTabModel(tab, codeEditorController);

        return tab;
    }

    /**
     * Ouvre un fichier de requête dans un nouvel onglet
     *
     * @param file Le fichier à ouvrir
     */
    public void openQueryFile(File file) {
        try {
            // Vérifier si le fichier est déjà ouvert
            for (Tab tab : tabEditorController.getView().getTabPane().getTabs()) {
                if (tab != tabEditorController.getView().getAddTab()) {
                    CodeEditorController controller = tabEditorController.getModel().getControllerForTab(tab);
                    if (controller != null && file.getPath().equals(controller.getModel().getCurrentFile())) {
                        // Fichier déjà ouvert, sélectionner son onglet
                        tabEditorController.getView().getTabPane().getSelectionModel().select(tab);
                        return;
                    }
                }
            }

            // Lire le contenu du fichier
            String content = Files.readString(file.toPath());

            // Créer un nouvel onglet avec le contenu du fichier
            Tab tab = addNewQueryTab(file.getName(), content);

            // Enregistrer le chemin du fichier dans le modèle
            CodeEditorController controller = tabEditorController.getModel().getControllerForTab(tab);
            if (controller != null) {
                controller.getModel().setCurrentFile(file.getPath());
                controller.getModel().setCurrentSavedContent(content);
                controller.getModel().setModified(false);
            }

        } catch (Exception e) {
            System.err.println("Error opening query file: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Récupère le contrôleur d'éditeur d'onglets.
     *
     * @return Le contrôleur d'éditeur d'onglets
     */
    public TabEditorController getTabEditorController() {
        return tabEditorController;
    }

    /**
     * Récupère le modèle d'éditeur d'onglets.
     *
     * @return Le modèle d'éditeur d'onglets
     */
    public TabEditorModel getTabEditorModel() {
        return tabEditorController.getModel();
    }
}