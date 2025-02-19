package fr.inria.corese.demo.view;

import fr.inria.corese.demo.controller.CodeEditorController;
import fr.inria.corese.demo.enums.icon.IconButtonBarType;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;

public class QueryView extends BorderPane {

    private CodeEditorController editorController;
    private Button runButton;
    private TabPane resultsTabPane;
    private StackPane tableContainer;
    private StackPane graphContainer;
    private StackPane xmlContainer;


    public QueryView() {
        System.out.println("QueryView initialized");
        initialize();
    }

    private void initialize() {
        // Initialize the CodeMirror editor for SPARQL
        String initialSparqlQuery =
                "PREFIX ex: <http://www.un.url.example>.\n\n" +
                        "SELECT * WHERE {\n" +
                        "  ?x ?p ?y\n" +
                        "}\n";

        editorController = new CodeEditorController(IconButtonBarType.QUERY, initialSparqlQuery);


        // Set up the UI layout
        setTop(editorController.getView());

        // Create results TabPane
        resultsTabPane = new TabPane();

        // Table tab
        tableContainer = new StackPane();
        Tab tableTab = new Tab("Table", tableContainer);
        tableTab.setClosable(false);

        // Graph tab
        graphContainer = new StackPane();
        Tab graphTab = new Tab("Graph", graphContainer);
        graphTab.setClosable(false);

        // XML tab
        xmlContainer = new StackPane();
        Tab xmlTab = new Tab("XML", xmlContainer);
        xmlTab.setClosable(false);

        resultsTabPane.getTabs().addAll(tableTab, graphTab, xmlTab);
        setCenter(resultsTabPane);

        // Setup run button with action
        runButton = new Button("Run");
        runButton.setOnAction(event -> executeQuery());
        editorController.getIconButtonBarController().addCustomButton(runButton);
    }

    private void executeQuery() {
        String queryContent = editorController.getModel().getContent();
        System.out.println("Executing query: " + queryContent);

    }

    private Object getSampleData() {
        // This would be replaced with actual query results
        return """
            {
              "head": {
                "vars": ["x", "p", "y"]
              },
              "results": {
                "bindings": [
                  {
                    "x": {"type": "uri", "value": "http://stardog.com/tutorial/imagine"},
                    "p": {"type": "uri", "value": "http://stardog.com/tutorial/artist"},
                    "y": {"type": "uri", "value": "http://stardog.com/tutorial/John_Lennon"}
                  },
                  {
                    "x": {"type": "uri", "value": "http://stardog.com/tutorial/imagine"},
                    "p": {"type": "uri", "value": "http://stardog.com/tutorial/date"},
                    "y": {"type": "literal", "value": "1971-10-11", "datatype": "http://www.w3.org/2001/XMLSchema#date"}
                  },
                  {
                    "x": {"type": "uri", "value": "http://stardog.com/tutorial/Please_Please_Me"},
                    "p": {"type": "uri", "value": "http://stardog.com/tutorial/name"},
                    "y": {"type": "literal", "value": "Please Please Me"}
                  }
                ]
              }
            }
            """;
    }

    public CodeEditorController getEditorController() {
        return editorController;
    }
}