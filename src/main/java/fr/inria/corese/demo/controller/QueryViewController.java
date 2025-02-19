package fr.inria.corese.demo.controller;

import fr.inria.corese.demo.enums.icon.IconButtonBarType;
import fr.inria.corese.demo.model.TabEditorModel;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.layout.StackPane;

import java.io.File;
import java.nio.file.Files;

public class QueryViewController {

    @FXML private StackPane editorContainer;

    private TabEditorController tabEditorController;

    public QueryViewController() {
        System.out.println("QueryViewController initialized");
    }

    @FXML
    public void initialize() {
        // Créer le TabEditorController avec le type spécifique pour les requêtes
        tabEditorController = new TabEditorController(IconButtonBarType.QUERY);

        // Ajouter un onglet de requête initial avec une requête par défaut
        String initialQuery =
                "PREFIX ex: <http://www.un.url.example>.\n\n" +
                        "SELECT * WHERE {\n" +
                        "  ?x ?p ?y\n" +
                        "}\n";

        addNewQueryTab("Query 1", initialQuery);

        // Injecter le TabEditor dans le conteneur
        if (editorContainer != null) {
            editorContainer.getChildren().add(tabEditorController.getView());
        }
    }

    /**
     * Ajoute un nouvel onglet avec le contenu spécifié
     *
     * @param title Titre de l'onglet
     * @param content Contenu initial de l'éditeur
     * @return L'onglet créé
     */
    public Tab addNewQueryTab(String title, String content) {
        CodeEditorController codeEditorController = new CodeEditorController(IconButtonBarType.QUERY, content);

        // Ajouter un bouton d'exécution (optionnel, pour l'interface future)
        Button runButton = new Button("Run");
        runButton.getStyleClass().add("run-button");
        runButton.setStyle("""
            -fx-background-color: #2196F3;
            -fx-text-fill: white;
            -fx-font-weight: bold;
            -fx-padding: 5 15;
            -fx-background-radius: 3;
        """);
        codeEditorController.getIconButtonBarController().addCustomButton(runButton);

        // Créer l'onglet et l'ajouter au TabEditorView
        Tab tab = tabEditorController.getView().addNewEditorTab(title, codeEditorController.getView());
        tabEditorController.getModel().addTabModel(tab, codeEditorController);

        return tab;
    }

    /**
     * Ouvre un fichier de requête dans un nouvel onglet
     *
     * @param file Le fichier à ouvrir
     */
    public void openQueryFile(File file) {
        try {
            // Vérifier si le fichier est déjà ouvert
            for (Tab tab : tabEditorController.getView().getTabs()) {
                if (tab != tabEditorController.getView().getAddTab()) {
                    CodeEditorController controller = tabEditorController.getModel().getControllerForTab(tab);
                    if (controller != null && file.getPath().equals(controller.getModel().getCurrentFile())) {
                        // Fichier déjà ouvert, sélectionner son onglet
                        tabEditorController.getView().getSelectionModel().select(tab);
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
     * Accède au contrôleur TabEditor
     */
    public TabEditorController getTabEditorController() {
        return tabEditorController;
    }

    /**
     * Accède au modèle TabEditor
     */
    public TabEditorModel getTabEditorModel() {
        return tabEditorController.getModel();
    }
}