package fr.inria.corese.demo.controller;

import fr.inria.corese.demo.model.CodeEditorModel;
import fr.inria.corese.demo.view.ValidationPageView;

public class ValidationPageController {
    private ValidationPageView view;
    private CodeEditorModel model;

    public ValidationPageController() {
        this.view = new ValidationPageView();
    }

    public void setModel(CodeEditorModel model) {
        this.model = model;
    }

    public ValidationPageView getView() {
        return view;
    }
}