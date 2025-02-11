package fr.inria.corese.demo.view;

import javafx.scene.layout.VBox;
import javafx.scene.control.ListView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.util.List;

public class RuleView extends VBox {
    private final VBox rulesContainer;
    private final ObservableList<RuleItem> ruleItems;

    public RuleView() {
        rulesContainer = new VBox(5); // 5px spacing between rules
        ruleItems = FXCollections.observableArrayList();

        getChildren().add(rulesContainer);
    }

    public void updateLoadedRules(List<String> rules) {
        rulesContainer.getChildren().clear();
        ruleItems.clear();

        for (String ruleName : rules) {
            RuleItem ruleItem = new RuleItem(ruleName);

            // Configure buttons actions
            ruleItem.getDocumentationButton().setOnAction(e -> handleShowDocumentation(ruleName));

            ruleItems.add(ruleItem);
            rulesContainer.getChildren().add(ruleItem);
        }
    }

    private void handleDuplicateRule(String ruleName) {
        // TODO: Implement rule duplication logic
        System.out.println("Duplicating rule: " + ruleName);
    }

    private void handleShowDocumentation(String ruleName) {
        // TODO: Implement documentation display logic
        System.out.println("Showing documentation for: " + ruleName);
    }
}