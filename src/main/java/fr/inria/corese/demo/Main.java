package fr.inria.corese.demo;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import fr.inria.corese.demo.controller.NavigationBarController;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) {
        try {
            // Créer le contrôleur de navigation
            NavigationBarController navigationController = new NavigationBarController();

            // Créer la scène avec la vue de navigation
            Scene scene = new Scene(navigationController.getView(), 200, 400);

            // Configurer la fenêtre principale
            primaryStage.setTitle("Navigation");
            primaryStage.setScene(scene);
            primaryStage.show();

        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}