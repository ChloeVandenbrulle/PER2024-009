package fr.inria.corese.demo.controller;

import fr.inria.corese.demo.model.ProjectDataModel;
import fr.inria.corese.demo.model.RuleModel;
import fr.inria.corese.demo.view.RuleItem;
import fr.inria.corese.demo.view.RuleView;
import fr.inria.corese.demo.view.popup.IPopup;
import fr.inria.corese.demo.view.popup.PopupFactory;
import fr.inria.corese.demo.view.popup.RuleInfoPopup;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import java.io.File;
import java.util.Map;

public class RuleViewController {
    private RuleModel ruleModel;
    private ProjectDataModel projectDataModel;
    private RuleView view;
    private Map<String, RuleItem> ruleItems;
    private PopupFactory popupFactory;

    @FXML
    private VBox rdfsRulesContainer;
    @FXML
    private VBox owlRulesContainer;
    @FXML
    private VBox rulesContainer;

    // Constructeur par défaut utilisé par FXML
    public RuleViewController() {
        this.ruleModel = new RuleModel();
        this.view = new RuleView();
        this.ruleItems = new HashMap<>();
    }

    // Méthode pour injecter les dépendances après la construction
    public void injectDependencies(ProjectDataModel projectDataModel, RuleModel ruleModel) {
        this.projectDataModel = projectDataModel;
        this.ruleModel = ruleModel;
        this.popupFactory = PopupFactory.getInstance(projectDataModel);
    }

    @FXML
    public void initialize() {
        if (rulesContainer != null) {
            rulesContainer.getChildren().add(view);
        }
    }

    public void initializeRules() {
        if (rdfsRulesContainer != null && owlRulesContainer != null) {
            rdfsRulesContainer.getChildren().clear();
            owlRulesContainer.getChildren().clear();

            // RDFS rules
            addRuleItem(rdfsRulesContainer, "RDFS Subset", true);
            addRuleItem(rdfsRulesContainer, "RDFS RL", true);

            // OWL rules
            addRuleItem(owlRulesContainer, "OWL RL", true);
            addRuleItem(owlRulesContainer, "OWL RL Extended", true);
            addRuleItem(owlRulesContainer, "OWL RL Test", true);
            addRuleItem(owlRulesContainer, "OWL Clean", true);
        }
    }

    private void addRuleItem(VBox container, String ruleName, boolean predefined) {
        RuleItem ruleItem = new RuleItem(ruleName);
        ruleItems.put(ruleName, ruleItem);

        ruleItem.getDuplicateButton().setOnAction(e -> handleDuplicateRule(ruleName));
        ruleItem.getDocumentationButton().setOnAction(e -> handleShowDocumentation(ruleName));
        ruleItem.getCheckBox().setOnAction(e -> handleRuleToggle(ruleName, ruleItem.getCheckBox().isSelected()));

        container.getChildren().add(ruleItem);
    }

    private void handleShowDocumentation(String ruleName) {
        if (popupFactory == null || projectDataModel == null) {
            System.err.println("PopupFactory or ProjectDataModel not initialized");
            return;
        }

        IPopup popup = popupFactory.createPopup(PopupFactory.RULE_INFO_POPUP);
        if (popup instanceof RuleInfoPopup) {
            popup.displayPopup();
        }
    }

    void updateView() {
        // Update predefined rules states
        for (Map.Entry<String, RuleItem> entry : ruleItems.entrySet()) {
            // Update checkbox states based on model
            // You might need to add methods in your model to get the state of each rule
        }

        // Update loaded rules
        if (view != null) {
            view.updateLoadedRules(ruleModel.getLoadedRules());
        }
    }

    private void handleRuleToggle(String ruleName, boolean selected) {
        switch (ruleName) {
            case "RDFS Subset":
                ruleModel.setRDFSSubsetEnabled(selected);
                break;
            case "RDFS RL":
                ruleModel.setRDFSRLEnabled(selected);
                break;
            case "OWL RL":
                ruleModel.setOWLRLEnabled(selected);
                break;
            case "OWL RL Extended":
                ruleModel.setOWLRLExtendedEnabled(selected);
                break;
            case "OWL RL Test":
                ruleModel.setOWLRLTestEnabled(selected);
                break;
            case "OWL Clean":
                ruleModel.setOWLCleanEnabled(selected);
                break;
        }
        updateView();
    }

    private void handleDuplicateRule(String ruleName) {
        System.out.println("Duplicating rule: " + ruleName);
        ruleModel.duplicateRule(ruleName);
        updateView();
    }
}