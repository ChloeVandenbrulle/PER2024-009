package fr.inria.corese.demo;

import atlantafx.base.theme.NordLight;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Application du thème AtlantaFX
        Application.setUserAgentStylesheet(new NordLight().getUserAgentStylesheet());

        // Chargement de la vue principale
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("/fr/inria/corese/demo/main-view.fxml"));
        if (loader.getLocation() == null) {
            throw new IllegalStateException("Cannot find FXML file at: /fr/inria/corese/demo/main-view.fxml");
        }
        Scene scene = new Scene(loader.load());

        primaryStage.setTitle("RDF Editor");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}