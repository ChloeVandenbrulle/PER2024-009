package fr.inria.corese.demo.view;

import javafx.scene.layout.VBox;
import javafx.scene.control.ListView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.util.List;

public class RuleView extends VBox {
    private ListView<String> loadedRulesListView;
    private ObservableList<String> loadedRules;

    public RuleView() {
        loadedRulesListView = new ListView<>();
        loadedRules = FXCollections.observableArrayList();
        loadedRulesListView.setItems(loadedRules);

        getChildren().add(loadedRulesListView);
    }

    public void updateLoadedRules(List<String> rules) {
        loadedRules.clear();
        loadedRules.addAll(rules);
    }

    public ListView<String> getLoadedRulesListView() {
        return loadedRulesListView;
    }
}