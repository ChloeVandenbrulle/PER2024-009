package fr.inria.corese.demo.view;

import javafx.scene.layout.VBox;
import javafx.scene.control.Label;

public class ProjectStatisticsView extends VBox {
    private Label semanticElementsLabel;
    private Label tripletLabel;
    private Label graphLabel;
    private Label rulesLoadedLabel;

    public ProjectStatisticsView() {
        initializeComponents();
        setupLayout();
    }

    private void initializeComponents() {
        semanticElementsLabel = new Label("Number of semantic elements loaded: 0");
        tripletLabel = new Label("Number of triplet: 0");
        graphLabel = new Label("Number of graph: 0");
        rulesLoadedLabel = new Label("Number of rules loaded: 0");
    }

    private void setupLayout() {
        getChildren().addAll(
                semanticElementsLabel,
                tripletLabel,
                graphLabel,
                rulesLoadedLabel
        );
    }

    // Getters and update methods
    public void updateStatistics(int semanticElements, int triplets, int graphs, int rules) {
        semanticElementsLabel.setText("Number of semantic elements loaded: " + semanticElements);
        tripletLabel.setText("Number of triplet: " + triplets);
        graphLabel.setText("Number of graph: " + graphs);
        rulesLoadedLabel.setText("Number of rules loaded: " + rules);
    }
}