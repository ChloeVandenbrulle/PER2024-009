package fr.inria.corese.demo;

import javafx.fxml.FXML;
import javafx.scene.web.WebView;
import javafx.fxml.Initializable;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.concurrent.Worker.State;

public class HelloController implements Initializable {
    @FXML
    private WebView codeEditor;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Platform.runLater(this::initializeCodeMirror);
    }

    private void initializeCodeMirror() {
        String html = """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="utf-8">
                <script src="https://cdnjs.cloudflare.com/ajax/libs/codemirror/5.65.2/codemirror.min.js"></script>
                <script src="https://cdnjs.cloudflare.com/ajax/libs/codemirror/5.65.2/mode/xml/xml.min.js"></script>
                <script src="https://cdnjs.cloudflare.com/ajax/libs/codemirror/5.65.2/mode/javascript/javascript.min.js"></script>
                <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/codemirror/5.65.2/codemirror.min.css">
                <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/codemirror/5.65.2/theme/dracula.min.css">
                <style>
                    html, body {
                        height: 100%;
                        margin: 0;
                        padding: 0;
                        overflow: hidden;
                    }
                    .CodeMirror {
                        height: 100vh;
                        width: 100%;
                    }
                </style>
            </head>
            <body>
                <textarea id="editor"></textarea>
                <script>
                    var editor = CodeMirror.fromTextArea(document.getElementById("editor"), {
                        lineNumbers: true,
                        theme: "eclipse",
                        mode: "javascript",
                        indentUnit: 2,
                        smartIndent: true,
                        lineWrapping: false,
                        matchBrackets: true,
                        autoCloseBrackets: true,
                        tabSize: 2,
                        keyMap: "default",
                        viewportMargin: Infinity
                    });
                    
                    // Expose methods for JavaFX interaction
                    window.getEditorContent = function() {
                        return editor.getValue();
                    };
                    
                    window.setEditorContent = function(content) {
                        editor.setValue(content);
                    };
                    
                    // Initial content
                    editor.setValue("// Votre code ici\\n");
                    
                    // Force refresh after initialization
                    setTimeout(function() {
                        editor.refresh();
                    }, 100);
                </script>
            </body>
            </html>
            """;

        codeEditor.getEngine().loadContent(html);

        // Attendre que la page soit chargée pour initialiser l'éditeur
        codeEditor.getEngine().getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
            if (newState == State.SUCCEEDED) {
                // Forcer un refresh après le chargement
                Platform.runLater(() -> {
                    codeEditor.getEngine().executeScript("editor.refresh();");
                });
            }
        });
    }

    public String getEditorContent() {
        return (String) codeEditor.getEngine().executeScript("window.getEditorContent()");
    }

    public void setEditorContent(String content) {
        String escapedContent = content.replace("\\", "\\\\").replace("'", "\\'").replace("\n", "\\n");
        codeEditor.getEngine().executeScript("window.setEditorContent('" + escapedContent + "')");
    }
}