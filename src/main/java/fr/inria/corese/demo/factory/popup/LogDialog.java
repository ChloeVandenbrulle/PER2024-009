package fr.inria.corese.demo.factory.popup;

import fr.inria.corese.core.Graph;
import fr.inria.corese.core.load.Load;
import fr.inria.corese.core.query.QueryProcess;
import fr.inria.corese.demo.manager.ApplicationStateManager;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.geometry.Insets;
import javafx.geometry.Pos;

public class LogDialog extends BasePopup {
    private final TextArea logTextArea;
    private final Graph graph;
    private final QueryProcess exec;
    private final Load ld;
    private final ApplicationStateManager model;

    public LogDialog() {
        this.model = ApplicationStateManager.getInstance();
        this.graph = Graph.create();
        this.exec = QueryProcess.create(graph);
        this.ld = Load.create(graph);

        setTitle("Logs");
        logTextArea = new TextArea();
        logTextArea.setEditable(false);
        logTextArea.setWrapText(true);
        logTextArea.setPrefRowCount(20);
        logTextArea.setPrefColumnCount(50);

        exec.setDebug(true);

        setupUI();
    }

    private void setupUI() {
        BorderPane contentPane = new BorderPane();
        contentPane.setCenter(logTextArea);

        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);
        buttonBox.setPadding(new Insets(10, 0, 0, 0));

        Button clearButton = new Button("Clear");
        Button refreshButton = new Button("Refresh");

        clearButton.setOnAction(e -> clearLogs());
        refreshButton.setOnAction(e -> updateLogs());

        buttonBox.getChildren().addAll(clearButton, refreshButton);
        contentPane.setBottom(buttonBox);

        getDialogPane().setContent(contentPane);
        getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        getDialogPane().getStyleClass().add("log-dialog");
        getDialogPane().setPrefSize(600, 400);

        setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.CLOSE) {
                closePopup();
            }
            return null;
        });
    }

    public void updateLogs() {
        String logs = "=== Application Logs ===\n" +
                model.getLogEntries() +
                "\n=== Corese Logs ===\n" +
                "Graph size: " + graph.size() + " triples\n" +
                "Query engine status: " + exec.toString();

        logTextArea.setText(logs);
    }

    public void clearLogs() {
        model.clearGraph();
        model.clearFiles();
        updateLogs();
    }

    public void appendLog(String logEntry) {
        model.addLogEntry(logEntry);
        updateLogs();
    }

    @Override
    public void displayPopup() {
        updateLogs();
        super.displayPopup();
    }
}