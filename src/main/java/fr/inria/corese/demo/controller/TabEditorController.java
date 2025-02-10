package fr.inria.corese.demo.controller;

import fr.inria.corese.demo.enums.IconButtonBarType;
import fr.inria.corese.demo.model.TabEditorModel;
import fr.inria.corese.demo.view.TabEditorView;
import fr.inria.corese.demo.view.CodeEditorView;
import javafx.collections.ListChangeListener;
import javafx.scene.control.Tab;

public class TabEditorController {
    private final TabEditorView view;
    private final TabEditorModel model;
    private IconButtonBarType type;

    public TabEditorController(IconButtonBarType type) {
        this.view = new TabEditorView();
        this.model = new TabEditorModel();
        this.type = type;
        initializeFirstTab();
    }

    private void initializeFirstTab() {
        addNewTab("Untitled");

        // Empêcher la sélection du faux onglet
        view.getTabs().addListener((ListChangeListener<Tab>) change -> {
            if (view.getTabs().isEmpty()) {
                view.getTabs().add(view.getAddTab());
            } else if (view.getTabs().get(view.getTabs().size() - 1) != view.getAddTab()) {
                view.getTabs().add(view.getAddTab());
            }
        });

        view.getAddTabButton().setOnAction(e -> {
            addNewTab("Untitled ");
        });
    }

    public void addNewTab(String title) {
        CodeEditorController codeEditorController = new CodeEditorController(type);
        Tab tab = view.addNewEditorTab(title, codeEditorController.getView());
        model.addTabModel(tab, codeEditorController);
    }

    public TabEditorView getView() {
        return view;
    }
}
