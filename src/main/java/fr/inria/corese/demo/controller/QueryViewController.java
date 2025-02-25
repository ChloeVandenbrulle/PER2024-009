package fr.inria.corese.demo.controller;

import fr.inria.corese.demo.enums.icon.IconButtonBarType;
import fr.inria.corese.demo.enums.icon.IconButtonType;
import fr.inria.corese.demo.factory.popup.DocumentationPopup;
import fr.inria.corese.demo.manager.ApplicationStateManager;
import fr.inria.corese.demo.model.ProjectDataModel;
import fr.inria.corese.demo.model.TabEditorModel;
import fr.inria.corese.demo.model.graph.SemanticGraph;
import fr.inria.corese.demo.view.CustomButton;
import javafx.collections.ListChangeListener;
import fr.inria.corese.demo.view.TopBar;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.animation.PauseTransition;
import javafx.util.Duration;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import javafx.application.Platform;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;


/**
 * Contrôleur de la vue de requêtes pour une application de gestion de données sémantiques.
 */
public class QueryViewController {
    @FXML private StackPane editorContainer;
    @FXML private BorderPane mainBorderPane;
    @FXML private SplitPane mainSplitPane;
    @FXML private TabPane resultsTabPane;
    @FXML private TextArea resultTextArea;
    @FXML private WebView graphView;
    @FXML private WebView xmlView;
    @FXML private TopBar topBar;

    private TabEditorController tabEditorController;
    private ProjectDataModel projectDataModel;
    private TableView<String[]> resultTable;
    private ApplicationStateManager stateManager;
    private boolean editorInitialized = false;

    public QueryViewController() {
        System.out.println("QueryViewController initialized");
        this.stateManager = ApplicationStateManager.getInstance();
    }

    public void setProjectDataModel(ProjectDataModel projectDataModel) {
        this.projectDataModel = projectDataModel;

        // Restaurer explicitement l'état
        if (projectDataModel != null) {
            projectDataModel.restoreState();
        }

        // Logs détaillés
        System.out.println("=== QueryViewController Model Setup ===");

        if (projectDataModel != null) {
            // Utiliser le graphe sémantique pour obtenir les informations
            SemanticGraph semanticGraph = projectDataModel.getSemanticGraph();

            System.out.println("Loaded Files: " + semanticGraph.getLoadedFiles());
            System.out.println("Loaded Rules: " + semanticGraph.getLoadedRules());
            System.out.println("Triplet Count: " + semanticGraph.getTripletCount());
            System.out.println("Semantic Elements: " + semanticGraph.getSemanticElementsCount());
            System.out.println("OWL RL Enabled: " + projectDataModel.isOWLRLEnabled());
        } else {
            System.out.println("ProjectDataModel is null");
        }
    }

    @FXML
    public void initialize() {
        System.out.println("QueryViewController.initialize() started");
        initializeTopBar();
        // S'assurer que le gestionnaire d'état est disponible
        if (stateManager == null) {
            stateManager = ApplicationStateManager.getInstance();
            System.out.println("StateManager initialized in QueryViewController");
        }

        // Initialiser le TabEditor - IMPORTANT: Ne pas ajouter au conteneur ici!
        tabEditorController = new TabEditorController(IconButtonBarType.QUERY);
        System.out.println("TabEditorController created with type QUERY");

        setupResultsPane();
        setupRunButton(); // Utiliser la méthode de l'ancienne version
        setupLayout();

        // Dans la méthode initialize(), modifiez l'appel à addNewQueryTab comme suit:
        Platform.runLater(() -> {
            if (editorContainer != null) {
                // Ajouter l'éditeur au conteneur seulement maintenant
                editorContainer.getChildren().clear();
                editorContainer.getChildren().add(tabEditorController.getView());
                System.out.println("TabEditorController view added to editorContainer");

                // Créer un onglet par défaut avec un délai pour garantir que le TabEditor est prêt
                PauseTransition delay = new PauseTransition(Duration.millis(200));
                delay.setOnFinished(e -> {
                    // Appeler addNewQueryTab avec un contenu vide
                    System.out.println("Creating empty default query tab");
                    addNewQueryTab("Untitled", "");
                });
                delay.play();
            } else {
                System.err.println("Error: editorContainer is null");
            }
        });
    }

    private void initializeTopBar() {
        List<IconButtonType> buttons = new ArrayList<>();
        buttons.add(IconButtonType.OPEN_FILE);
        buttons.add(IconButtonType.DOCUMENTATION);
        topBar.addRightButtons(buttons);

        topBar.getButton(IconButtonType.OPEN_FILE).setOnAction(e -> onOpenFilesButtonClick());
        topBar.getButton(IconButtonType.DOCUMENTATION).setOnAction(e -> {
            DocumentationPopup documentationPopup = new DocumentationPopup();
            documentationPopup.displayPopup();
        });
    }

    private void onOpenFilesButtonClick() {
        System.out.println("Open files button clicked");
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open File");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("RDF Files", "*.ttl", "*.rdf", "*.n3"),
                new FileChooser.ExtensionFilter("All Files", "*.*")
        );

        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            try {
                tabEditorController.addNewTab(file);
            } catch (Exception e) {
                showError("Error Opening File", "Could not open the file: " + e.getMessage());
            }
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
        System.out.println("=== Setting up Run Button Listener ===");

        tabEditorController.getView().getTabPane().getTabs().addListener((ListChangeListener<Tab>) change -> {
            while (change.next()) {
                if (change.wasAdded()) {
                    for (Tab tab : change.getAddedSubList()) {
                        if (tab != tabEditorController.getView().getAddTab()) {
                            // Ajouter un listener de contenu pour s'assurer que le contenu est chargé
                            tab.contentProperty().addListener(new ChangeListener<Node>() {
                                @Override
                                public void changed(ObservableValue<? extends Node> observable, Node oldValue, Node newValue) {
                                    if (newValue != null) {
                                        // Retirer le listener après son déclenchement
                                        tab.contentProperty().removeListener(this);

                                        Platform.runLater(() -> {
                                            CodeEditorController codeEditorController = tabEditorController.getModel().getControllerForTab(tab);
                                            if (codeEditorController != null) {
                                                System.out.println("Delayed Run Button Configuration for tab: " + tab.getText());
                                                configureEditorRunButton(codeEditorController);
                                            } else {
                                                System.err.println("Still no CodeEditorController for tab: " + tab.getText());
                                            }
                                        });
                                    }
                                }
                            });
                        }
                    }
                }
            }
        });
    }

    private void configureEditorRunButton(CodeEditorController codeEditorController) {
        System.out.println("=== Configuring Editor Run Button ===");

        if (projectDataModel == null) {
            System.err.println("ProjectDataModel is not initialized!");
            return;
        }

        System.out.println("Displaying Run Button");
        codeEditorController.getView().displayRunButton();

        CustomButton runButton = codeEditorController.getView().getRunButton();

        if (runButton == null) {
            System.err.println("Run Button is NULL!");
            return;
        }

        System.out.println("Run Button Retrieved. Setting up event handlers.");

        // Log current event handlers
        System.out.println("Current Action Event Handlers: " +
                (runButton.getOnAction() != null ? "Exists" : "None"));

        // Clear previous event handlers
        runButton.setOnAction(null);

        // Ajouter le nouveau gestionnaire d'événements
        runButton.setOnAction(e -> {
            System.out.println("Run Button Clicked!");
            executeQuery();
        });

        // Configuration des raccourcis clavier
        codeEditorController.getView().setOnKeyPressed(event -> {
            if (event.isControlDown() && event.getCode() == KeyCode.ENTER) {
                System.out.println("Ctrl+Enter Shortcut Triggered!");
                executeQuery();
            }
        });

        System.out.println("Run Button Configuration Complete for: " +
                codeEditorController.getView().getClass().getSimpleName());
    }

    private void setupResultsPane() {
        // Si resultsTabPane est déjà créé dans le FXML, on l'utilise directement
        if (resultsTabPane == null) {
            resultsTabPane = new TabPane();

            // Onglet Table
            resultTable = new TableView<>();
            Tab tableTab = new Tab("Table", resultTable);
            tableTab.setClosable(false);

            // Onglet Graph
            if (graphView == null) {
                graphView = new WebView();
            }
            Tab graphTab = new Tab("Graph", graphView);
            graphTab.setClosable(false);

            // Onglet XML
            if (xmlView == null) {
                xmlView = new WebView();
            }
            Tab xmlTab = new Tab("XML", xmlView);
            xmlTab.setClosable(false);

            resultsTabPane.getTabs().addAll(tableTab, graphTab, xmlTab);
        }
    }

    private void setupLayout() {
        Platform.runLater(() -> {
            if (mainSplitPane != null) {
                mainSplitPane.setDividerPosition(0, 0.6);

                // Gestion des positions du diviseur
                mainSplitPane.getDividers().get(0).positionProperty().addListener((obs, oldPos, newPos) -> {
                    if (newPos.doubleValue() < 0.25) {
                        Platform.runLater(() -> mainSplitPane.setDividerPosition(0, 0.25));
                    } else if (newPos.doubleValue() < 0.4) {
                        Platform.runLater(() -> mainSplitPane.setDividerPosition(0, 0.4));
                    }
                });

                // Gestion du redimensionnement
                mainBorderPane.heightProperty().addListener((obs, oldHeight, newHeight) -> {
                    if (newHeight.doubleValue() > 0) {
                        double currentDividerPos = mainSplitPane.getDividerPositions()[0];
                        double resultsPaneHeight = newHeight.doubleValue() * (1 - currentDividerPos);

                        if (resultsPaneHeight > newHeight.doubleValue() * 0.75) {
                            Platform.runLater(() -> mainSplitPane.setDividerPosition(0, 0.25));
                        }
                    }
                });
            }
        });
    }

    public void executeQuery() {
        ApplicationStateManager stateManager = ApplicationStateManager.getInstance();
        ProjectDataModel projectDataModel = stateManager.getProjectDataModel();

        // Déboguer l'état avant l'exécution
        System.out.println("=== Executing Query ===");
        System.out.println("Loaded Files: " + stateManager.getLoadedFiles().size());
        System.out.println("OWL RL Enabled: " + stateManager.isOWLRLEnabled());

        if (projectDataModel == null) {
            showError("Error", "ProjectDataModel is not initialized!");
            return;
        }

        Tab selectedTab = tabEditorController.getView().getTabPane().getSelectionModel().getSelectedItem();
        if (selectedTab != null && selectedTab != tabEditorController.getView().getAddTab()) {
            CodeEditorController codeEditorController = tabEditorController.getModel().getControllerForTab(selectedTab);
            if (codeEditorController != null) {
                String queryContent = codeEditorController.getView().getCodeMirrorView().getContent();
                System.out.println("\nQuery Content:\n" + queryContent);

                try {
                    // Réinitialiser les vues avant l'exécution
                    clearResultViews();

                    Object[] result = projectDataModel.executeQuery(queryContent);
                    String formattedResult = result[0].toString();
                    String queryType = (String) result[1];

                    System.out.println("\nQuery Execution Result:");
                    System.out.println("Type: " + queryType);
                    System.out.println("Result: " + formattedResult.substring(0,
                            Math.min(100, formattedResult.length())) + "...");

                    Platform.runLater(() -> {
                        switch (queryType) {
                            case "SELECT":
                                updateTableView(formattedResult);
                                resultsTabPane.getSelectionModel().select(0);
                                break;
                            case "CONSTRUCT":
                            case "DESCRIBE":
                                updateGraphView(formattedResult);
                                resultsTabPane.getSelectionModel().select(1);
                                break;
                            case "ASK":
                                updateXMLView(formattedResult);
                                resultsTabPane.getSelectionModel().select(2);
                                break;
                        }
                    });
                } catch (Exception e) {
                    System.err.println("Query Execution Error Details:");
                    e.printStackTrace();
                    showError("Query Execution Error", e.getMessage());
                }
            }
        }
    }

    private void clearResultViews() {
        Platform.runLater(() -> {
            // Réinitialiser la TextArea
            if (resultTextArea != null) {
                resultTextArea.clear();
            }

            // Réinitialiser la WebView pour Graph
            if (graphView != null) {
                graphView.getEngine().loadContent("");
            }

            // Réinitialiser la WebView pour XML
            if (xmlView != null) {
                xmlView.getEngine().loadContent("");
            }

            // Si vous avez une TableView, vous pouvez aussi la vider
            if (resultTable != null) {
                resultTable.getItems().clear();
                resultTable.getColumns().clear();
            }
        });
    }

    public Tab addNewQueryTab(String title, String content) {
        System.out.println("Creating new query tab: " + title);
        CodeEditorController codeEditorController = new CodeEditorController(IconButtonBarType.QUERY, content);
        Tab tab = tabEditorController.getView().addNewEditorTab(title, codeEditorController.getView());
        tabEditorController.getModel().addTabModel(tab, codeEditorController);

        // Ajout d'un délai plus long pour s'assurer de la configuration complète
        PauseTransition delay = new PauseTransition(Duration.millis(300));
        delay.setOnFinished(event -> {
            Platform.runLater(() -> {
                System.out.println("Delayed Run Button Configuration in addNewQueryTab");
                codeEditorController.getView().displayRunButton();
                configureEditorRunButton(codeEditorController);
            });
        });
        delay.play();

        return tab;
    }

    private void updateTableView(String formattedResult) {
        Platform.runLater(() -> {
            if (resultTextArea != null) {
                resultTextArea.setText(formattedResult);
                // Scroll to top
                resultTextArea.positionCaret(0);
            }
        });
    }

    private void updateGraphView(String content) {
        Platform.runLater(() -> {
            if (graphView != null) {
                graphView.getEngine().loadContent(
                        String.format("<html><body><pre>%s</pre></body></html>",
                                content.replace("<", "&lt;").replace(">", "&gt;"))
                );
            }
        });
    }

    private void updateXMLView(String content) {
        Platform.runLater(() -> {
            if (xmlView != null) {
                xmlView.getEngine().loadContent(
                        String.format("<html><body><pre>%s</pre></body></html>",
                                content.replace("<", "&lt;").replace(">", "&gt;"))
                );
            }
        });
    }

    private void showError(String title, String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
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