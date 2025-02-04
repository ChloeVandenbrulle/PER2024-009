package fr.inria.corese.demo;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) {
        try {
            System.out.println("Starting application...");

            // Charger main-view.fxml comme vue principale
            String mainView = "/fr/inria/corese/demo/main-view.fxml";
            System.out.println("Loading main view: " + mainView);

            FXMLLoader loader = new FXMLLoader(getClass().getResource(mainView));
            if (loader.getLocation() == null) {
                System.err.println("Could not find FXML file: " + mainView);
                return;
            }

            Parent root = loader.load();
            System.out.println("Main view loaded successfully");

            Scene scene = new Scene(root);
            primaryStage.setTitle("RDF Editor");
            primaryStage.setScene(scene);
            primaryStage.show();

            System.out.println("Application started successfully");

        } catch(Exception e) {
            System.err.println("Error starting application:");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}