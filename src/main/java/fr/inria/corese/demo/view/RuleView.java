package fr.inria.corese.demo.view;

import javafx.event.EventHandler;
import javafx.scene.layout.VBox;
import javafx.scene.control.TitledPane;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RuleView extends VBox {
    private final ObservableList<RuleItem> ruleItems;
    private final Map<String, RuleItem> predefinedRuleItems;

    public RuleView() {
        this.predefinedRuleItems = new HashMap<>();

        ruleItems = FXCollections.observableArrayList();

        // Ajouter les règles prédéfinies
        addPredefinedRules();
    }

    private void addPredefinedRules() {
        String[] predefinedRules = {"Trace", "Load Named", "Graph index"};

        for (String ruleName : predefinedRules) {
            RuleItem ruleItem = new RuleItem(ruleName);
            predefinedRuleItems.put(ruleName, ruleItem);
        }
    }

    public void updateActiveRules(List<String> rules) {
        ruleItems.clear();

        for (String ruleName : rules) {
            if (!predefinedRuleItems.containsKey(ruleName)) {
                RuleItem ruleItem = new RuleItem(ruleName);
                ruleItem.getDocumentationButton().setOnAction(e -> handleShowDocumentation(ruleName));
                ruleItems.add(ruleItem);
            }
        }
    }

    private void handleShowDocumentation(String ruleName) {
        //TODO: Implementation de la documentation
        System.out.println("Showing documentation for: " + ruleName);
    }
}