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
                .editor-container {
                    display: flex;
                    flex-direction: column;
                    height: 100vh;
                }
                .CodeMirror {
                    flex-grow: 1;
                    font-family: 'JetBrains Mono', monospace;
                    font-size: 14px;
                }
                .status-bar {
                    height: 25px;
                    background-color: #f3f3f3;
                    border-top: 1px solid #ddd;
                    display: flex;
                    align-items: center;
                    padding: 0 10px;
                    font-size: 12px;
                    font-family: sans-serif;
                    color: #666;
                }
                .status-bar-right {
                    margin-left: auto;
                    display: flex;
                    gap: 15px;
                }
                .status-item {
                    margin-left: 15px;
                }

                .CodeMirror-linenumbers {
                    padding: 0;
                    min-width: 2px;
                    max-width: 2px;
                }
                
                .syntax-error {
                    background-color: rgba(255, 0, 0, 0.1);
                    border-bottom: 2px dotted #f00;
                }
                
                .CodeMirror-lint-tooltip {
                    background-color: #ffd;
                    border: 1px solid #886;
                    border-radius: 4px;
                    color: #333;
                    font-family: monospace;
                    font-size: 10pt;
                    max-width: 600px;
                    opacity: 0;
                    padding: 2px 5px;
                    position: fixed;
                    transition: opacity .4s;
                    white-space: pre-wrap;
                    z-index: 100;
                    box-shadow: 2px 3px 5px rgba(0,0,0,.2);
                }
                        
                .CodeMirror-lint-mark {
                    background-position: left bottom;
                    background-repeat: repeat-x;
                }
                        
                .CodeMirror-lint-mark-error {
                    background-image: url("data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAQAAAADCAYAAAC09K7GAAAAAXNSR0IArs4c6QAAAAZiS0dEAP8A/wD/oL2nkwAAAAlwSFlzAAALEwAACxMBAJqcGAAAAAd0SU1FB9sJDw4cOCW1/KIAAAAZdEVYdENvbW1lbnQAQ3JlYXRlZCB3aXRoIEdJTVBXgQ4XAAAAHElEQVQI12NggIL/DAz/GdA5/xkY/qPKMDAwAADLZwf5rvm+LQAAAABJRU5ErkJggg==");
                }
            </style>
            <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/codemirror/5.65.2/codemirror.min.css">
            <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/codemirror/5.65.2/theme/eclipse.min.css">
            <script src="https://cdnjs.cloudflare.com/ajax/libs/codemirror/5.65.2/codemirror.min.js"></script>
            <script src="https://cdnjs.cloudflare.com/ajax/libs/codemirror/5.65.2/mode/turtle/turtle.min.js"></script>
            <script src="https://cdnjs.cloudflare.com/ajax/libs/codemirror/5.65.2/addon/edit/closebrackets.min.js"></script>
            <script src="https://cdnjs.cloudflare.com/ajax/libs/codemirror/5.65.2/addon/edit/matchbrackets.min.js"></script>
            <script src="https://cdnjs.cloudflare.com/ajax/libs/codemirror/5.65.2/addon/scroll/simplescrollbars.min.js"></script>
            <script src="https://cdnjs.cloudflare.com/ajax/libs/codemirror/5.65.2/addon/selection/active-line.min.js"></script>
            <script src="https://cdnjs.cloudflare.com/ajax/libs/codemirror/5.65.2/addon/fold/foldcode.min.js"></script>
            <script src="https://cdnjs.cloudflare.com/ajax/libs/codemirror/5.65.2/addon/fold/foldgutter.min.js"></script>
            <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/codemirror/5.65.2/addon/fold/foldgutter.min.css">
            <script src="https://cdnjs.cloudflare.com/ajax/libs/codemirror/5.65.2/addon/lint/lint.js"></script>
            <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/codemirror/5.65.2/addon/lint/lint.css">
            <script src="https://cdn.jsdelivr.net/npm/n3@1.16.2/browser/n3.min.js"></script>
            <script src="https://cdn.jsdelivr.net/npm/@frogcat/rdf-validate@1.0.0/rdf-validate.min.js"></script>
            <script src="https://cdnjs.cloudflare.com/ajax/libs/codemirror/5.65.2/addon/selection/undo.js"></script>
        </head>
        <body>
            <div class="editor-container">
                <textarea id="editor"></textarea>
                <div class="status-bar">
                    <div class="status-bar-right">
                        <span id="cursor-position">Ln 1, Col 1</span>
                        <span class="status-item" id="selection-count"></span>
                        <span class="status-item" id="file-info">UTF-8</span>
                        <span class="status-item" id="editor-mode">Turtle</span>
                    </div>
                </div>
            </div>
            <script>
                window.onerror = function(message, source, lineno, colno, error) {
                    if (window.bridge) {
                        window.bridge.log("JS Error: " + message + " at " + source + ":" + lineno + ":" + colno);
                    }
                };
                
                console.log = function(message) {
                    if (window.bridge) {
                        window.bridge.log(message);
                    }
                };
                        
                console.error = function(message) {
                    if (window.bridge) {
                        window.bridge.log("Error: " + message);
                    }
                };
                
                function getRealPosition(text, globalColumn) {
                    let lines = text.split("\\n");
                    let charCount = 0;
                        
                    for (let i = 0; i < lines.length; i++) {
                        if (charCount + lines[i].length >= globalColumn) {
                            return { realLine: i, realColumn: globalColumn - charCount };
                        }
                        charCount += lines[i].length + 1; // +1 pour le "\\n"
                    }
                        
                    // Si on dépasse la longueur du texte, on retourne la dernière position connue
                    return { realLine: lines.length - 1, realColumn: lines[lines.length - 1].length };
                }

                function turtleLinter(text, callback) {
                    console.log("Hello from turtleLinter!");
                    
                    console.log("Before N3.Parser initialization");     
                    const parser = new N3.Parser({ format: 'Turtle' });
                    console.log("After N3.Parser initialization");
                         
                    var issues = [];
                         
                    new Promise((resolve) => {
                        parser.parse(text, (error, quad, prefixes) => {
                            if (error) {
                                console.error("Parsing error detected:");
                                console.error(`Message: ${error.message}`);
                                console.error(`Line: ${error.line}, Column: ${error.column}`);
                                
                                let { realLine, realColumn } = getRealPosition(text, error.column);
                                console.log(`Recalculated position -> Line: ${realLine}, Column: ${realColumn}`);
                                    
                                issues.push({
                                    message: error.message,
                                    severity: 'error',
                                    from: CodeMirror.Pos(realLine, realColumn),
                                    to: CodeMirror.Pos(realLine, realColumn + 1)
                                });
                            }
                         
                            // Quand le parsing est terminé, on résout la promesse
                            resolve();
                        });
                    }).then(() => {
                        callback(issues);
                    }).catch((e) => {
                        console.error("Catch block error:", e);
                        issues.push({
                            message: e.message,
                            severity: 'error',
                            from: CodeMirror.Pos(0, 0),
                            to: CodeMirror.Pos(0, 1)
                        });
                         
                         callback(issues);
                    });
                }
                
                CodeMirror.registerHelper("lint", "turtle", turtleLinter);
                
                var editor = CodeMirror.fromTextArea(document.getElementById('editor'), {
                    mode: 'turtle',
                    theme: 'eclipse',
                    lineNumbers: true,
                    matchBrackets: true,
                    autoCloseBrackets: true,
                    lineWrapping: true,
                    tabSize: 2,
                    autofocus: true,
                    styleActiveLine: true,
                    scrollbarStyle: 'overlay',
                    foldGutter: true,
                    historyEventDelay: 350,
                    undoDepth: 200,
                    lint: {
                        getAnnotations: turtleLinter,
                        async: true
                    },
                    gutters: ["CodeMirror-linenumbers", "CodeMirror-foldgutter", "CodeMirror-lint-markers"],
                    extraKeys: {
                        "Ctrl-S": function(cm) {
                            if (window.bridge) {
                                window.bridge.saveFile();
                            }
                        },
                        "Ctrl-F": "findPersistent",
                        "Ctrl-/": "toggleComment",
                        "Ctrl-Space": "autocomplete",
                        "Ctrl-Z": function(cm) { cm.undo(); },
                        "Ctrl-Y": function(cm) { cm.redo(); },
                    }
                });

                // Mise à jour de la position du curseur
                editor.on('cursorActivity', function() {
                    var pos = editor.getCursor();
                    var sel = editor.getSelection();
                    document.getElementById('cursor-position').textContent = 
                        'Ln ' + (pos.line + 1) + ', Col ' + (pos.ch + 1);
                    
                    // Afficher le nombre de caractères sélectionnés
                    if (sel && sel.length > 0) {
                        document.getElementById('selection-count').textContent = 
                            sel.length + ' selected';
                    } else {
                        document.getElementById('selection-count').textContent = '';
                    }
                });

                editor.on('change', function(cm, change) {
                    if (!change.origin || change.origin !== 'setValue') {
                        if (window.bridge) {
                            window.bridge.onContentChanged(editor.getValue());
                        }
                    }
                });

                window.setContent = function(content) {
                    var cursor = editor.getCursor();
                    var scrollInfo = editor.getScrollInfo();
                    editor.setValue(content || '');
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

        public void log(String message) {
            System.out.println("JS Log: " + message);
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