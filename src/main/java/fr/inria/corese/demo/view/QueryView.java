package fr.inria.corese.demo.view;

import fr.inria.corese.demo.controller.CodeEditorController;
import fr.inria.corese.demo.enums.icon.IconButtonBarType;
import fr.inria.corese.demo.model.TabEditorModel;
import javafx.fxml.FXML;
import javafx.geometry.Orientation;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.web.WebView;

import java.io.File;
import java.nio.file.Files;

public class QueryView extends BorderPane {
    private CodeEditorController editorController;
    private Button runButton;
    private TabPane resultsTabPane;
    private TableView<String[]> resultTable;
    private WebView graphView;
    private WebView xmlView;
    private SplitPane splitPane;

    public QueryView() {
        initialize();
    }

    private void initialize() {
        // Création du SplitPane vertical
        splitPane = new SplitPane();
        splitPane.setOrientation(Orientation.VERTICAL);

        // Initialize the editor with default query
        String initialSparqlQuery =
                "PREFIX ex: <http://www.un.url.example>.\n\n" +
                        "SELECT * WHERE {\n" +
                        "  ?x ?p ?y\n" +
                        "}\n";

        editorController = new CodeEditorController(IconButtonBarType.QUERY, initialSparqlQuery);

        // Setup du TabPane pour les résultats
        setupResultsPane();

        // Ajout des composants au SplitPane
        splitPane.getItems().addAll(editorController.getView(), resultsTabPane);
        splitPane.setDividerPositions(0.5);

        // Ajout du SplitPane au BorderPane
        setCenter(splitPane);

        // Configuration du bouton Run et du raccourci Ctrl+Enter
        setupRunButton();
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

    private void setupRunButton() {
        // Configurer le raccourci Ctrl+Enter
        KeyCombination ctrlEnter = new KeyCodeCombination(KeyCode.ENTER, KeyCombination.CONTROL_DOWN);
        getScene().getAccelerators().put(ctrlEnter, this::executeQuery);

        // Configurer le bouton Run
        editorController.getView().getRunButton().setOnAction(e -> executeQuery());
    }

    private void executeQuery() {
        String queryContent = editorController.getModel().getContent();
        System.out.println("Executing query: " + queryContent);

        // Mettre à jour les résultats
        updateResults(queryContent);
    }

    private void updateResults(String queryContent) {
        // Mise à jour de l'onglet Table
        updateTableView(queryContent);

        // Mise à jour de l'onglet Graph
        updateGraphView(queryContent);

        // Mise à jour de l'onglet XML
        updateXMLView(queryContent);
    }

    private void updateTableView(String queryContent) {
        // Pour l'exemple, on va créer un tableau simple
        resultTable.getColumns().clear();
        resultTable.getItems().clear();

        // Création des colonnes (à adapter selon vos besoins)
        TableColumn<String[], String> colX = new TableColumn<>("x");
        TableColumn<String[], String> colP = new TableColumn<>("p");
        TableColumn<String[], String> colY = new TableColumn<>("y");

        resultTable.getColumns().addAll(colX, colP, colY);
    }

    private void updateGraphView(String queryContent) {
        // Pour l'exemple, on affiche juste un message
        graphView.getEngine().loadContent(
                "<html><body><h2>Graph View</h2><p>Query content will be visualized here</p></body></html>"
        );
    }

    private void updateXMLView(String queryContent) {
        // Création d'un exemple de sortie XML
        String xmlContent = String.format("""
            <html><body><pre>
            &lt;?xml version="1.0" ?&gt;
            &lt;sparql xmlns="http://www.w3.org/2005/sparql-results#"&gt;
              &lt;head&gt;
                &lt;variable name="x"/&gt;
                &lt;variable name="p"/&gt;
                &lt;variable name="y"/&gt;
              &lt;/head&gt;
              &lt;results&gt;
                &lt;result&gt;
                  &lt;binding name="x"&gt;%s&lt;/binding&gt;
                &lt;/result&gt;
              &lt;/results&gt;
            &lt;/sparql&gt;
            </pre></body></html>
            """, queryContent.replace("<", "&lt;").replace(">", "&gt;"));

        xmlView.getEngine().loadContent(xmlContent);
    }

    public CodeEditorController getEditorController() {
        return editorController;
    }
}