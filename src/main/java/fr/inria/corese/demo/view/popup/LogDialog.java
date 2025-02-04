package fr.inria.corese.demo.view.popup;

import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import fr.inria.corese.demo.model.ProjectDataModel;

public class LogDialog extends BasePopup {
    private final TextArea logTextArea;
    private final ProjectDataModel model;

    public LogDialog(ProjectDataModel model) {
        this.model = model;

        // Configuration de base du dialog
        setTitle("Logs");

        // Création de la zone de texte pour les logs
        logTextArea = new TextArea();
        logTextArea.setEditable(false);
        logTextArea.setWrapText(true);
        logTextArea.setPrefRowCount(20);
        logTextArea.setPrefColumnCount(50);

        // Création du conteneur principal
        BorderPane contentPane = new BorderPane();
        contentPane.setCenter(logTextArea);

        // Ajout des boutons dans le buttonBox
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);
        buttonBox.setPadding(new Insets(10, 0, 0, 0));

        Button clearButton = new Button("Clear");
        Button refreshButton = new Button("Refresh");

        clearButton.setOnAction(e -> clearLogs());
        refreshButton.setOnAction(e -> updateLogs());

        buttonBox.getChildren().addAll(clearButton, refreshButton);
        contentPane.setBottom(buttonBox);

        // Configuration du contenu
        getDialogPane().setContent(contentPane);

        // Ajout des ButtonTypes au DialogPane
        ButtonType closeButtonType = ButtonType.CLOSE;
        getDialogPane().getButtonTypes().addAll(closeButtonType);

        // Style CSS
        getDialogPane().getStyleClass().add("log-dialog");

        // Rendre la fenêtre redimensionnable
        getDialogPane().setPrefSize(600, 400);

        // Configuration du résultat lors de la fermeture
        setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.CLOSE) {
                closePopup();
            }
            return null;
        });
    }

    public void updateLogs() {
        if (model != null) {
            logTextArea.setText(model.getLogs());
        }
    }

    public void clearLogs() {
        if (model != null) {
            model.clearLogs();
            updateLogs();
        }
    }

    public void appendLog(String logEntry) {
        if (model != null) {
            model.addLogEntry(logEntry);
            updateLogs();
        }
    }

    @Override
    public void displayPopup() {
        updateLogs(); // Met à jour les logs avant d'afficher
        super.displayPopup();
    }
}