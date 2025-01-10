package com.example.test_librairies;

import atlantafx.base.theme.NordLight;
import atlantafx.base.controls.Popover;
import atlantafx.base.controls.Notification;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;
import javafx.fxml.Initializable;

import java.lang.invoke.SwitchPoint;
import java.net.URL;
import java.util.ResourceBundle;

public class HelloController implements Initializable {
    @FXML
    private Label welcomeText;

    @FXML
    private Button helloButton;

    @FXML
    private WebView codeEditor;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeCodeMirror();
    }

    private void initializeCodeMirror() {
        String html = """
            <!DOCTYPE html>
            <html>
            <head>
                <link href="https://cdnjs.cloudflare.com/ajax/libs/codemirror/5.65.2/codemirror.min.css" rel="stylesheet">
                <link href="https://cdnjs.cloudflare.com/ajax/libs/codemirror/5.65.2/theme/monokai.min.css" rel="stylesheet">
                <script src="https://cdnjs.cloudflare.com/ajax/libs/codemirror/5.65.2/codemirror.min.js"></script>
                <script src="https://cdnjs.cloudflare.com/ajax/libs/codemirror/5.65.2/mode/javascript/javascript.min.js"></script>
                <style>
                    html, body { height: 100%; margin: 0; }
                    .CodeMirror { height: 100%; }
                </style>
            </head>
            <body>
                <textarea id="code"></textarea>
                <script>
                    var editor = CodeMirror.fromTextArea(document.getElementById("code"), {
                        lineNumbers: true,
                        mode: "javascript",
                        theme: "monokai",
                        autoCloseBrackets: true,
                        matchBrackets: true,
                        indentUnit: 4
                    });
                    
                    // Fonction pour obtenir le contenu
                    function getContent() {
                        return editor.getValue();
                    }
                    
                    // Fonction pour définir le contenu
                    function setContent(text) {
                        editor.setValue(text);
                    }
                </script>
            </body>
            </html>
            """;

        codeEditor.getEngine().loadContent(html);
    }

    // Méthode pour obtenir le contenu de l'éditeur
    public String getEditorContent() {
        return (String) codeEditor.getEngine().executeScript("getContent()");
    }

    // Méthode pour définir le contenu de l'éditeur
    public void setEditorContent(String content) {
        codeEditor.getEngine().executeScript("setContent('" + content.replace("'", "\\'") + "')");
    }

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");

        // Créer le contenu du Popover
        VBox content = new VBox();
        content.getChildren().add(new Label("Ceci est un popover !"));

        // Créer et configurer le Popover
        Popover popover = new Popover(content);
        popover.setTitle("Mon Popover");

        // Afficher le Popover
        popover.show(helloButton);

        // Votre code existant pour le bouton
        // Vous pouvez ajouter ici un exemple pour interagir avec l'éditeur :
        setEditorContent("// Écrivez votre code ici\nfunction hello() {\n    console.log('Hello World!');\n}");

    }
}