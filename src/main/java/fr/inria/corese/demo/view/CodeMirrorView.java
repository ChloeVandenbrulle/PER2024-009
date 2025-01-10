package fr.inria.corese.demo.view;

import javafx.scene.layout.StackPane;
import javafx.scene.web.WebView;
import javafx.application.Platform;
import javafx.concurrent.Worker.State;

public class CodeMirrorView extends StackPane {
    private final WebView webView;

    public CodeMirrorView() {
        webView = new WebView();
        getChildren().add(webView);
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
                        theme: "dracula",
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
                    
                    window.getEditorContent = function() {
                        return editor.getValue();
                    };
                    
                    window.setEditorContent = function(content) {
                        editor.setValue(content);
                    };
                    
                    editor.setValue("// Votre code ici\\n");
                    
                    setTimeout(function() {
                        editor.refresh();
                    }, 100);
                </script>
            </body>
            </html>
            """;

        webView.getEngine().loadContent(html);

        webView.getEngine().getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
            if (newState == State.SUCCEEDED) {
                Platform.runLater(() -> {
                    webView.getEngine().executeScript("editor.refresh();");
                });
            }
        });
    }

    public String getContent() {
        return (String) webView.getEngine().executeScript("window.getEditorContent()");
    }

    public void setContent(String content) {
        String escapedContent = content.replace("\\", "\\\\").replace("'", "\\'").replace("\n", "\\n");
        webView.getEngine().executeScript("window.setEditorContent('" + escapedContent + "')");
    }

    public WebView getWebView() {
        return webView;
    }
}