package fr.inria.corese.demo.controller;


import fr.inria.corese.demo.enums.icon.IconButtonBarType;
import fr.inria.corese.demo.model.ProjectDataModel;
import fr.inria.corese.demo.model.TabEditorModel;
import fr.inria.corese.demo.view.CustomButton;
import javafx.collections.ListChangeListener;
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
import javafx.application.Platform;
import javafx.scene.web.WebView;


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
    @FXML private BorderPane mainBorderPane;
    @FXML private SplitPane mainSplitPane;
    @FXML private TabPane resultsTabPane;
    @FXML private TextArea resultTextArea;
    @FXML private WebView graphView;
    @FXML private WebView xmlView;

    private TabEditorController tabEditorController;
    private ProjectDataModel projectDataModel;
    private TableView<String[]> resultTable;

    public QueryViewController() {
        System.out.println("QueryViewController initialized");
    }

    public void setProjectDataModel(ProjectDataModel projectDataModel) {
        this.projectDataModel = projectDataModel;
    }

    @FXML
    public void initialize() {
        // Initialiser juste le TabEditor sans onglet par défaut
        tabEditorController = new TabEditorController(IconButtonBarType.QUERY);

        // Injecter le TabEditor dans le conteneur
        if (editorContainer != null) {
            editorContainer.getChildren().add(tabEditorController.getView());
        }

        setupResultsPane();
        setupRunButton();
        setupLayout();

        // Ajouter un listener global pour configurer le bouton Run
        Platform.runLater(() -> {
            tabEditorController.getView().getTabPane().getTabs().addListener((ListChangeListener<Tab>) change -> {
                while (change.next()) {
                    if (change.wasAdded()) {
                        for (Tab tab : change.getAddedSubList()) {
                            if (tab != tabEditorController.getView().getAddTab()) {
                                Platform.runLater(() -> {
                                    CodeEditorController codeEditorController = tabEditorController.getModel().getControllerForTab(tab);
                                    if (codeEditorController != null) {
                                        System.out.println("Configuring Run Button for new tab: " + tab.getText());
                                        codeEditorController.getView().displayRunButton();
                                        configureEditorRunButton(codeEditorController);
                                    } else {
                                        System.err.println("No CodeEditorController found for tab: " + tab.getText());
                                    }
                                });
                            }
                        }
                    }
                }
            });
        });
    }

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

    private void executeQuery() {
        if (projectDataModel == null) {
            showError("Error", "ProjectDataModel is not initialized!");
            return;
        }

        Tab selectedTab = tabEditorController.getView().getTabPane().getSelectionModel().getSelectedItem();
        if (selectedTab != null && selectedTab != tabEditorController.getView().getAddTab()) {
            CodeEditorController codeEditorController = tabEditorController.getModel().getControllerForTab(selectedTab);
            if (codeEditorController != null) {
                String queryContent = codeEditorController.getView().getCodeMirrorView().getContent();
                System.out.println("Executing query: " + queryContent);

                try {
                    // Réinitialiser les vues avant l'exécution
                    clearResultViews();

                    Object[] result = projectDataModel.executeQuery(queryContent);
                    String formattedResult = result[0].toString();
                    String queryType = (String) result[1];

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

    private void setupResultsPane() {
        resultsTabPane = new TabPane();

        // Onglet Table
        resultTable = new TableView<>();
        Tab tableTab = new Tab("Table", resultTable);
        tableTab.setClosable(false);

        // Onglet Graph
        graphView = new WebView();
        Tab graphTab = new Tab("Graph", graphView);
        graphTab.setClosable(false);

        // Onglet XML
        xmlView = new WebView();
        Tab xmlTab = new Tab("XML", xmlView);
        xmlTab.setClosable(false);

        resultsTabPane.getTabs().addAll(tableTab, graphTab, xmlTab);
    }

    public Tab addNewQueryTab(String title, String content) {
        CodeEditorController codeEditorController = new CodeEditorController(IconButtonBarType.QUERY, content);
        Tab tab = tabEditorController.getView().addNewEditorTab(title, codeEditorController.getView());
        tabEditorController.getModel().addTabModel(tab, codeEditorController);

        // Ajout d'un délai supplémentaire pour s'assurer de la configuration
        PauseTransition delay = new PauseTransition(Duration.millis(100));
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

    private void setupLayout() {
        Platform.runLater(() -> {
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
        });
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