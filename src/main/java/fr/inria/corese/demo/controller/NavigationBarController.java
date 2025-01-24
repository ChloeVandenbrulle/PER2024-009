package fr.inria.corese.demo.controller;

import fr.inria.corese.demo.view.NavigationBarView;

public class NavigationBarController {
    private NavigationBarView navigationBarView;

    public NavigationBarController(NavigationBarView view) {
        this.navigationBarView = view;
        view.dataButton.setOnAction(e -> switchToDataView());
        view.rdfEditorButton.setOnAction(e -> switchToRDFEditor());
        view.validationButton.setOnAction(e -> switchToValidation());
        view.queryButton.setOnAction(e -> switchToQuery());
        view.settingsButton.setOnAction(e -> switchToSettings());
    }

    private void switchToDataView() {
        System.out.println("Switching to Data view");
    }

    private void switchToRDFEditor() {
        System.out.println("Switching to RDF Editor view");
    }

    private void switchToValidation() {
        System.out.println("Switching to Validation view");
    }

    private void switchToQuery() {
        System.out.println("Switching to Query view");
    }

    private void switchToSettings() {
        System.out.println("Switching to Settings view");
    }
}
