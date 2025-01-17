package fr.inria.corese.demo.view;

import javafx.scene.layout.StackPane;
import javafx.scene.web.WebView;
import javafx.scene.web.WebEngine;
import javafx.beans.property.StringProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.application.Platform;
import netscape.javascript.JSObject;

public class CodeMirrorView extends StackPane {
    private final WebView webView;
    private final WebEngine webEngine;
    private final StringProperty contentProperty = new SimpleStringProperty("");
    private boolean initialized = false;
    private boolean isInternalUpdate = false;

    public CodeMirrorView() {
        webView = new WebView();
        webEngine = webView.getEngine();

        webView.setContextMenuEnabled(false);
        webView.prefWidthProperty().bind(widthProperty());
        webView.prefHeightProperty().bind(heightProperty());

        getChildren().add(webView);

        Platform.runLater(this::initializeEditor);

        contentProperty.addListener((obs, old, newValue) -> {
            if (initialized && newValue != null && !isInternalUpdate) {
                updateEditorContent(newValue);
            }
        });
    }

    private void initializeEditor() {
        String html = """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="utf-8">
                <style>
                    body, html {
                        height: 100%;
                        margin: 0;
                        padding: 0;
                        overflow: hidden;
                    }
                </style>
                <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/codemirror/5.65.2/codemirror.min.css">
                <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/codemirror/5.65.2/theme/eclipse.min.css">
                <script src="https://cdnjs.cloudflare.com/ajax/libs/codemirror/5.65.2/codemirror.min.js"></script>
                <script src="https://cdnjs.cloudflare.com/ajax/libs/codemirror/5.65.2/mode/turtle/turtle.min.js"></script>
                <script src="https://cdnjs.cloudflare.com/ajax/libs/codemirror/5.65.2/addon/edit/closebrackets.min.js"></script>
                <script src="https://cdnjs.cloudflare.com/ajax/libs/codemirror/5.65.2/addon/edit/matchbrackets.min.js"></script>
                <style>
                    .CodeMirror {
                        height: 100vh;
                        font-family: monospace;
                        font-size: 14px;
                    }
                </style>
            </head>
            <body>
                <textarea id="editor"></textarea>
                <script>
                    var editor = CodeMirror.fromTextArea(document.getElementById('editor'), {
                        mode: 'turtle',
                        theme: 'eclipse',
                        lineNumbers: true,
                        matchBrackets: true,
                        autoCloseBrackets: true,
                        lineWrapping: true,
                        tabSize: 2,
                        autofocus: true,
                        preserveScrollPosition: true
                    });
                    
                    editor.on('change', function(cm, change) {
                        // Ne pas envoyer les changements si c'est une mise à jour programmatique
                        if (!change.origin || change.origin !== 'setValue') {
                            if (window.bridge) {
                                window.bridge.onContentChanged(editor.getValue());
                            }
                        }
                    });
                    
                    window.setContent = function(content) {
                        // Sauvegarder la position du curseur et le scroll
                        var cursor = editor.getCursor();
                        var scrollInfo = editor.getScrollInfo();
                        
                        editor.setValue(content || '');
                        
                        // Restaurer la position du curseur et le scroll
                        editor.setCursor(cursor);
                        editor.scrollTo(scrollInfo.left, scrollInfo.top);
                        
                        editor.refresh();
                    };
                    
                    window.getContent = function() {
                        return editor.getValue();
                    };
                </script>
            </body>
            </html>
            """;

        webEngine.loadContent(html);

        webEngine.getLoadWorker().stateProperty().addListener((obs, old, newState) -> {
            switch (newState) {
                case SUCCEEDED:
                    try {
                        JSObject window = (JSObject) webEngine.executeScript("window");
                        window.setMember("bridge", new JavaBridge());
                        initialized = true;

                        String content = contentProperty.get();
                        if (content != null && !content.isEmpty()) {
                            updateEditorContent(content);
                        }
                    } catch (Exception e) {
                        System.err.println("Error during CodeMirror initialization: " + e.getMessage());
                        e.printStackTrace();
                    }
                    break;
                case FAILED:
                    System.err.println("Failed to load CodeMirror editor");
                    break;
            }
        });
    }

    public class JavaBridge {
        public void onContentChanged(String newContent) {
            Platform.runLater(() -> {
                isInternalUpdate = true;
                contentProperty.set(newContent);
                isInternalUpdate = false;
            });
        }
    }

    private void updateEditorContent(String content) {
        if (initialized) {
            try {
                String escapedContent = content.replace("\\", "\\\\")
                        .replace("'", "\\'")
                        .replace("\n", "\\n")
                        .replace("\r", "\\r");
                webEngine.executeScript(String.format("window.setContent('%s');", escapedContent));
            } catch (Exception e) {
                System.err.println("Error updating editor content: " + e.getMessage());
            }
        }
    }

    public String getContent() {
        if (!initialized) return contentProperty.get();
        try {
            return (String) webEngine.executeScript("window.getContent()");
        } catch (Exception e) {
            System.err.println("Error getting editor content: " + e.getMessage());
            return "";
        }
    }

    public void setContent(String content) {
        contentProperty.set(content);
    }

    public StringProperty contentProperty() {
        return contentProperty;
    }
}