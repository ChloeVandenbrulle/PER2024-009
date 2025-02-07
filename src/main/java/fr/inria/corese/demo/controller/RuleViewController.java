package fr.inria.corese.demo.controller;

import fr.inria.corese.demo.model.RuleModel;
import fr.inria.corese.demo.view.RuleView;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.VBox;

public class RuleViewController {
    private RuleModel model;
    private RuleView view;

    @FXML
    private CheckBox rdfsSubsetCheckbox;
    @FXML
    private CheckBox rdfsRLCheckbox;
    @FXML
    private CheckBox owlRLCheckbox;
    @FXML
    private CheckBox owlRLExtendedCheckbox;
    @FXML
    private CheckBox owlRLTestCheckbox;
    @FXML
    private CheckBox owlCleanCheckbox;
    @FXML
    private VBox ruleViewContainer; // Add this

    public RuleViewController() {
        this.model = new RuleModel();
        this.view = new RuleView();
    }

    public RuleViewController(RuleModel ruleModel) {
        this.model = ruleModel;
        this.view = new RuleView();
    }

    @FXML
    public void initialize() {
        if (ruleViewContainer != null) {
            ruleViewContainer.getChildren().add(view);
        }
        initializeEventHandlers();
    }

    void updateView() {
        if (view != null) {
            view.updateLoadedRules(model.getLoadedRules());
        }
    }

    private void initializeEventHandlers() {
        if (rdfsSubsetCheckbox != null) {
            rdfsSubsetCheckbox.setOnAction(e -> handleRDFSSubsetToggle());
            rdfsRLCheckbox.setOnAction(e -> handleRDFSRLToggle());
            owlRLCheckbox.setOnAction(e -> handleOWLRLToggle());
            owlRLExtendedCheckbox.setOnAction(e -> handleOWLRLExtendedToggle());
        }
    }

    private void handleRDFSSubsetToggle() {
        model.setRDFSSubsetEnabled(rdfsSubsetCheckbox.isSelected());
        model.loadRDFSSubset();
        updateView();
    }

    private void handleRDFSRLToggle() {
        model.setRDFSRLChecked(rdfsRLCheckbox.isSelected());
        model.loadRDFSRL();
        updateView();
    }

    private void handleOWLRLToggle() {
        model.setOWLRLChecked(owlRLCheckbox.isSelected());
        model.loadOWLRL();
        updateView();
    }

    private void handleOWLRLExtendedToggle() {
        model.setOWLRLExtendedChecked(owlRLExtendedCheckbox.isSelected());
        model.loadOWLRLExtended();
        updateView();
    }

}