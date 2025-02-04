package fr.inria.corese.demo.view;

import javafx.scene.layout.VBox;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Button;
import javafx.scene.control.TreeView;
import javafx.scene.control.TreeItem;

public class RuleConfigView extends VBox {
    private TreeView<String> rulesTree;
    private Button loadRuleFileButton;
    private CheckBox myRuleFileCheckbox;

    public RuleConfigView() {
        initializeComponents();
        setupLayout();
    }

    private void initializeComponents() {
        rulesTree = new TreeView<>();
        setupRulesTree();

        loadRuleFileButton = new Button("Load rule file");
        myRuleFileCheckbox = new CheckBox("My rule file");
    }

    private void setupRulesTree() {
        TreeItem<String> root = new TreeItem<>("Rules");
        TreeItem<String> rdfs = new TreeItem<>("RDFS");
        rdfs.getChildren().addAll(
                new TreeItem<>("RDFS Subset"),
                new TreeItem<>("RDFS RL")
        );

        TreeItem<String> owl = new TreeItem<>("OWL");
        owl.getChildren().addAll(
                new TreeItem<>("OWL RL"),
                new TreeItem<>("OWL RL Extended"),
                new TreeItem<>("OWL RL Test"),
                new TreeItem<>("OWL Clean")
        );

        root.getChildren().addAll(
                new TreeItem<>("Trace"),
                new TreeItem<>("Load Named"),
                rdfs,
                owl,
                new TreeItem<>("Graph index")
        );

        rulesTree.setRoot(root);
        root.setExpanded(true);
    }

    private void setupLayout() {
        getChildren().addAll(rulesTree, myRuleFileCheckbox, loadRuleFileButton);
    }

    // Getters
    public TreeView<String> getRulesTree() { return rulesTree; }
    public Button getLoadRuleFileButton() { return loadRuleFileButton; }
    public CheckBox getMyRuleFileCheckbox() { return myRuleFileCheckbox; }
}