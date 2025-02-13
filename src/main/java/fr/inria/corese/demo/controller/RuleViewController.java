package fr.inria.corese.demo.controller;

import fr.inria.corese.demo.model.ProjectDataModel;
import fr.inria.corese.demo.model.RuleModel;
import fr.inria.corese.demo.view.RuleItem;
import fr.inria.corese.demo.view.RuleView;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.VBox;
import java.util.HashMap;
import java.util.Map;

public class RuleViewController {
    private RuleModel ruleModel;
    private ProjectDataModel projectDataModel;
    private RuleView view;
    private Map<String, RuleItem> ruleItems;

    @FXML
    private VBox rdfsRulesContainer;
    @FXML
    private VBox owlRulesContainer;
    @FXML
    private VBox globalOptionsContainer;
    @FXML
    private VBox activeRulesContainer;

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
    }

    @FXML
    public void initialize() {
        view = new RuleView();
        initializeRules();
    }

    public void initializeRules() {
        if (rdfsRulesContainer != null && owlRulesContainer != null) {
            rdfsRulesContainer.getChildren().clear();
            owlRulesContainer.getChildren().clear();

            if (globalOptionsContainer != null) {
                globalOptionsContainer.getChildren().clear();
                // Ajouter les options globales
                addRuleItem(globalOptionsContainer, "Trace", true);
                addRuleItem(globalOptionsContainer, "Load Named", true);
                addRuleItem(globalOptionsContainer, "Graph index", true);
            }

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

    private void setupPredefinedRuleHandlers() {
        RuleView ruleView = (RuleView) view;
        ruleView.setRuleCheckboxHandler("Trace", e ->
                handleRuleToggle("Trace", ((CheckBox)e.getSource()).isSelected()));
        ruleView.setRuleCheckboxHandler("Load Named", e ->
                handleRuleToggle("Load Named", ((CheckBox)e.getSource()).isSelected()));
        ruleView.setRuleCheckboxHandler("Graph index", e ->
                handleRuleToggle("Graph index", ((CheckBox)e.getSource()).isSelected()));
    }

    void updateView() {
        RuleView ruleView = (RuleView) view;
        if (ruleView != null) {
            // Mettre à jour l'état des règles prédéfinies
            ruleView.setPredefinedRuleState("Trace", ruleModel.isTraceEnabled());
            ruleView.setPredefinedRuleState("Load Named", ruleModel.isLoadNamedEnabled());
            ruleView.setPredefinedRuleState("Graph index", ruleModel.isGraphIndexEnabled());

            // Mettre à jour les règles actives
            ruleView.updateActiveRules(ruleModel.getActiveRules());
        }

        // Mettre à jour les états des règles RDFS et OWL
        updatePredefinedRuleStates();
    }

    private void updatePredefinedRuleStates() {
        for (Map.Entry<String, RuleItem> entry : ruleItems.entrySet()) {
            String ruleName = entry.getKey();
            RuleItem item = entry.getValue();

            boolean isEnabled = switch (ruleName) {
                case "RDFS Subset" -> ruleModel.isRDFSSubsetEnabled();
                case "RDFS RL" -> ruleModel.isRDFSRLEnabled();
                case "OWL RL" -> ruleModel.isOWLRLEnabled();
                case "OWL RL Extended" -> ruleModel.isOWLRLExtendedEnabled();
                case "OWL RL Test" -> ruleModel.isOWLRLTestEnabled();
                case "OWL Clean" -> ruleModel.isOWLCleanEnabled();
                default -> false;
            };

            item.getCheckBox().setSelected(isEnabled);
        }
    }

    private void addRuleItem(VBox container, String ruleName, boolean predefined) {
        RuleItem ruleItem = new RuleItem(ruleName);
        ruleItems.put(ruleName, ruleItem);

        ruleItem.getDocumentationButton().setOnAction(e -> handleShowDocumentation(ruleName));
        ruleItem.getCheckBox().setOnAction(e -> handleRuleToggle(ruleName, ruleItem.getCheckBox().isSelected()));

        container.getChildren().add(ruleItem);
    }

    private void handleShowDocumentation(String ruleName) {
        //external link to documentation
    }

    private void handleRuleToggle(String ruleName, boolean selected) {
        switch (ruleName) {
            case "Trace":
                ruleModel.setTraceEnabled(selected);
                break;
            case "Load Named":
                ruleModel.setLoadNamedEnabled(selected);
                break;
            case "Graph index":
                ruleModel.setGraphIndexEnabled(selected);
                break;
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