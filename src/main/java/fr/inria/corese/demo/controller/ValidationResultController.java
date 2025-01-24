package fr.inria.corese.demo.controller;

import fr.inria.corese.demo.model.CodeEditorModel;
import fr.inria.corese.demo.view.ValidationResultView;
import fr.inria.corese.demo.view.ValidationPageView;

public class ValidationResultController {
    private ValidationResultView view;
    private CodeEditorModel model;

    public ValidationResultController() {
        this.view = new ValidationResultView();
    }

    public void setModel(CodeEditorModel model) {
        this.model = model;
    }

    public ValidationResultView getView() {
        return view;
    }
}