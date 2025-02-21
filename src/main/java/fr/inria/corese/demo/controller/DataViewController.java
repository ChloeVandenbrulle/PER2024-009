package fr.inria.corese.demo.controller;

import fr.inria.corese.demo.enums.icon.IconButtonType;
import fr.inria.corese.demo.model.RuleModel;
import fr.inria.corese.demo.view.DataView;
import fr.inria.corese.demo.model.ProjectDataModel;
import fr.inria.corese.demo.view.FileListView;
import fr.inria.corese.demo.view.TopBar;
import fr.inria.corese.demo.factory.popup.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Contrôleur de gestion de la vue de données dans une application web sémantique.
 *
 * Responsabilités principales :
 * - Initialisation et gestion de l'interface utilisateur pour la manipulation de données
 * - Gestion des actions de projet (ouverture, sauvegarde)
 * - Gestion du chargement de fichiers et de règles
 * - Fourniture des interactions utilisateur pour les opérations de graphe et de fichiers
 *
 * Caractéristiques principales :
 * - Configuration de la barre supérieure avec des boutons d'action
 * - Configuration de la liste de fichiers et de la vue des règles
 * - Gestion des notifications popup
 * - Mise à jour des statistiques de la vue
 *
 * Le contrôleur intègre plusieurs composants :
 * - ProjectDataModel pour la gestion des données
 * - FileListView pour l'affichage des fichiers chargés
 * - RuleViewController pour la gestion des règles
 *
 * @author Clervie Causer
 * @version 1.0
 * @since 2025
 */
public class DataViewController {
    private DataView view;
    private ProjectDataModel model;
    private PopupFactory popupFactory;
    private RuleViewController ruleViewController;
    private RuleModel ruleModel;
    private final LogDialog logDialog;
    private FileListView fileListView;

    @FXML
    private HBox configActionBox;
    @FXML
    private VBox fileListContainer;
    @FXML
    private VBox rulesContainer;
    @FXML
    private Label semanticElementsLabel;
    @FXML
    private Label tripletLabel;
    @FXML
    private Label graphLabel;
    @FXML
    private Label rulesLoadedLabel;
    @FXML
    private TopBar topBar;

    /**
     * Construit un nouveau DataViewController.
     *
     * Initialise :
     * - ProjectDataModel
     * - LogDialog
     * - PopupFactory
     */
    public DataViewController() {
        this.model = new ProjectDataModel();
        this.logDialog = new LogDialog(model);
        this.popupFactory = PopupFactory.getInstance(model);
    }

    /**
     * Initialise les composants de la vue après la construction.
     *
     * Réalise les configurations suivantes :
     * - Configuration des boutons de la barre supérieure et de leurs actions
     * - Configuration de la vue de liste de fichiers
     * - Configuration de la vue des règles
     * - Ajout des gestionnaires d'événements pour diverses actions
     */
    @FXML
    public void initialize() {
        // Configuration de la TopBar
        topBar.addLeftButtons(List.of(
                IconButtonType.OPEN_FILE,
                IconButtonType.SAVE
        ));

        topBar.addRightButtons(List.of(
                IconButtonType.LOGS
        ));

        // Configuration des handlers pour les boutons
        topBar.setOnAction(IconButtonType.OPEN_FILE, this::handleOpenProject);
        topBar.setOnAction(IconButtonType.SAVE, this::handleSaveAs);
        topBar.setOnAction(IconButtonType.LOGS, this::handleShowLogs);

        // Initialisation de la liste de fichiers
        if (fileListContainer != null) {
            setupFileList();
        }

        // Initialisation des règles
        if (rulesContainer != null) {
            setupRulesView();
        }
    }

    /**
     * Configurer la vue de liste de fichiers dans l'interface utilisateur.
     *
     * Actions incluant :
     * - Création de FileListView
     * - Définition du modèle de données associé
     * - Ajout de la vue au conteneur
     * - Configuration des boutons d'action (effacer, recharger, charger)
     */
    private void setupFileList() {
        fileListView = new FileListView();
        fileListView.setModel(model.getFileListModel());
        fileListView.setProjectDataModel(model);
        fileListContainer.getChildren().add(fileListView);
        VBox.setVgrow(fileListView, Priority.ALWAYS);

        fileListView.getClearButton().setOnAction(e -> handleClearGraph());
        fileListView.getReloadButton().setOnAction(e -> handleReloadFiles());
        fileListView.getLoadButton().setOnAction(e -> handleLoadFiles());
    }

    /**
     * Configurer la vue des règles dans l'interface utilisateur.
     *
     * Workflow :
     * 1. Créer RuleModel
     * 2. Charger la vue des règles depuis FXML
     * 3. Injecter les dépendances dans RuleViewController
     * 4. Ajouter la vue des règles dans un ScrollPane
     * 5. Initialiser les règles
     *
     * Gère les potentielles IOException lors du chargement de la vue
     */
    private void setupRulesView() {
        try {
            System.out.println("Loading rule view...");

            // Création du modèle de règles
            ruleModel = new RuleModel();

            // Chargement du FXML pour la vue des règles
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fr/inria/corese/demo/rule-view.fxml"));
            VBox ruleView = loader.load();

            // Récupération du contrôleur
            ruleViewController = loader.getController();
            ruleViewController.injectDependencies(model, ruleModel);

            // Placement de la vue dans un ScrollPane
            ScrollPane scrollPane = new ScrollPane(ruleView);
            scrollPane.setFitToWidth(true);
            scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
            scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
            scrollPane.getStyleClass().add("edge-to-edge");

            // Ajout du ScrollPane au conteneur de règles
            rulesContainer.getChildren().add(scrollPane);
            VBox.setVgrow(scrollPane, Priority.ALWAYS);

            // Initialisation des règles
            ruleViewController.initializeRules();

            System.out.println("Rule view loaded successfully");
        } catch (IOException e) {
            System.err.println("Error loading rule view: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Gère l'ouverture d'un projet en :
     * - Affichant un dialogue de sélection de répertoire
     * - Chargeant le projet sélectionné
     * - Mettant à jour la vue
     */
    private void handleOpenProject() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File selectedDirectory = directoryChooser.showDialog(null);
        if (selectedDirectory != null) {
            model.loadProject(selectedDirectory);
            updateView();
        }
    }

    /**
     * Gère la sauvegarde du projet actuel en :
     * - Affichant un dialogue de sauvegarde de fichier
     * - Sauvegardant le projet à l'emplacement sélectionné
     */
    private void handleSaveAs() {
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showSaveDialog(null);
        if (file != null) {
            model.saveProject(file);
        }
    }

    /**
     * Gère la suppression du graphe actuel.
     *
     * Workflow :
     * 1. Afficher un popup de confirmation
     * 2. Si confirmé :
     *    - Effacer le graphe
     *    - Effacer les fichiers chargés
     *    - Mettre à jour la vue
     *    - Afficher une notification de succès
     */
    private void handleClearGraph() {
        IPopup confirmPopup = popupFactory.createPopup(PopupFactory.CLEAR_GRAPH_CONFIRMATION);
        confirmPopup.setMessage("Are you sure you want to clear the graph? This action cannot be undone.");
        confirmPopup.displayPopup();

        if (((ClearGraphConfirmationPopup) confirmPopup).getResult()) {
            model.clearGraph();
            model.clearFiles();
            updateView();

            // Afficher une notification de succès
            IPopup successPopup = popupFactory.createPopup(PopupFactory.TOAST_NOTIFICATION);
            successPopup.setMessage("Graph has been cleared successfully!");
            successPopup.displayPopup();
        }
    }

    /**
     * Gère le rechargement des fichiers du projet actuel.
     *
     * Demande une confirmation avant de recharger
     * Met à jour la vue après un rechargement réussi
     */
    private void handleReloadFiles() {
        if (fileListView.confirmReload()) {
            model.reloadFiles();
            updateView();
        }
    }

    /**
     * Affiche le dialogue des journaux de l'application.
     */
    private void handleShowLogs() {
        logDialog.displayPopup();
    }

    /**
     * Gère le chargement des fichiers dans le projet.
     *
     * Workflow :
     * 1. Ouvrir un sélecteur de fichiers pour les fichiers TTL
     * 2. Vérifier les fichiers existants et demander confirmation
     * 3. Charger le fichier sélectionné
     * 4. Mettre à jour les journaux et la vue
     * 5. Afficher des notifications de succès ou d'erreur
     */
    private void handleLoadFiles() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("TTL files", "*.ttl")
        );

        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            try {
                model.addLogEntry("Starting to load file: " + file.getName());

                // Check if there are already files loaded
                if (!model.getFileListModel().getFiles().isEmpty()) {
                    // Show warning popup before loading
                    IPopup warningPopup = popupFactory.createPopup(PopupFactory.WARNING_POPUP);
                    warningPopup.setMessage("Loading this file will reset the current graph. Do you want to continue?");
                    boolean result = ((WarningPopup) warningPopup).getResult();

                    if (!result) {
                        model.addLogEntry("File loading cancelled by user: " + file.getName());
                        return;
                    }
                }

                model.loadFile(file);
                model.addFile(file.getName());
                model.addLogEntry("File loaded successfully: " + file.getName());

                // Dans handleLoadFiles après le chargement réussi
                IPopup successPopup = popupFactory.createPopup(PopupFactory.TOAST_NOTIFICATION);
                successPopup.setMessage("File '" + file.getName() + "' has been successfully loaded!");
                successPopup.displayPopup();

            } catch (Exception e) {
                String errorMessage = "Error loading file: " + e.getMessage();
                model.addLogEntry("ERROR: " + errorMessage);

                IPopup errorPopup = popupFactory.createPopup(PopupFactory.WARNING_POPUP);
                errorPopup.setMessage(errorMessage);
                ((WarningPopup) errorPopup).getResult();
            }
            updateView();
        }
    }

    /**
     * Met à jour la vue avec les statistiques actuelles du projet.
     *
     * Mises à jour :
     * - Vue des règles
     * - Nombre d'éléments sémantiques
     * - Nombre de triplets
     * - Nombre de graphes
     * - Nombre de règles chargées
     */
    private void updateView() {
        if (ruleViewController != null) {
            ruleViewController.updateView();
        }

        // Update statistics labels
        if (semanticElementsLabel != null) {
            semanticElementsLabel.setText("Number of semantic elements loaded: " + model.getSemanticElementsCount());
            tripletLabel.setText("Number of triplet: " + model.getTripletCount());
            graphLabel.setText("Number of graph: " + model.getGraphCount());

            // Utilisez la méthode getLoadedRulesCount pour obtenir le nombre de règles à jour
            int ruleCount = 0;
            if (ruleModel != null) {
                ruleCount = ruleModel.getLoadedRulesCount();
            }
            rulesLoadedLabel.setText("Number of rules loaded: " + ruleCount);
        }
    }

    /**
     * Récupère la DataView actuelle.
     *
     * @return La DataView associée à ce contrôleur
     */
    public DataView getView() {
        return view;
    }
}