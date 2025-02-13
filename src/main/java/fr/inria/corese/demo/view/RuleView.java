package fr.inria.corese.demo.view;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.layout.VBox;
import javafx.scene.control.TitledPane;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RuleView extends VBox {
    private final VBox predefinedRulesContainer;
    private final VBox activeRulesContainer;
    private final ObservableList<RuleItem> ruleItems;
    private final Map<String, RuleItem> predefinedRuleItems;

    public RuleView() {
        this.predefinedRuleItems = new HashMap<>();

        // Conteneur pour les règles prédéfinies
        predefinedRulesContainer = new VBox(5);
        TitledPane predefinedPane = new TitledPane("Global Options", predefinedRulesContainer);
        predefinedPane.setExpanded(true);

        // Conteneur pour les règles actives
        activeRulesContainer = new VBox(5);
        TitledPane activePane = new TitledPane("Active Rules", activeRulesContainer);
        activePane.setExpanded(true);

        ruleItems = FXCollections.observableArrayList();

        // Ajouter les règles prédéfinies
        addPredefinedRules();

        getChildren().addAll(predefinedPane, activePane);
    }

    private void addPredefinedRules() {
        String[] predefinedRules = {"Trace", "Load Named", "Graph index"};

        for (String ruleName : predefinedRules) {
            RuleItem ruleItem = new RuleItem(ruleName);
            predefinedRuleItems.put(ruleName, ruleItem);
            predefinedRulesContainer.getChildren().add(ruleItem);
        }
    }

    public void setRuleCheckboxHandler(String ruleName, EventHandler handler) {
        RuleItem ruleItem = predefinedRuleItems.get(ruleName);
        if (ruleItem != null) {
            ruleItem.getCheckBox().setOnAction(handler);
        }
    }

    public void updateActiveRules(List<String> rules) {
        activeRulesContainer.getChildren().clear();
        ruleItems.clear();

        for (String ruleName : rules) {
            if (!predefinedRuleItems.containsKey(ruleName)) {
                RuleItem ruleItem = new RuleItem(ruleName);
                ruleItem.getDocumentationButton().setOnAction(e -> handleShowDocumentation(ruleName));
                ruleItems.add(ruleItem);
                activeRulesContainer.getChildren().add(ruleItem);
            }
        }
    }

    public VBox getPredefinedRulesContainer() {
        return predefinedRulesContainer;
    }

    public void setPredefinedRuleState(String ruleName, boolean enabled) {
        RuleItem ruleItem = predefinedRuleItems.get(ruleName);
        if (ruleItem != null) {
            ruleItem.getCheckBox().setSelected(enabled);
        }
    }

    private void handleShowDocumentation(String ruleName) {
        // Implementation de la documentation
        System.out.println("Showing documentation for: " + ruleName);
    }
}