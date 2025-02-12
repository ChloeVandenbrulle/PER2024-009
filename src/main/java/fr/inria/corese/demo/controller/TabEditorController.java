package fr.inria.corese.demo.controller;

import fr.inria.corese.demo.enums.IconButtonBarType;
import fr.inria.corese.demo.model.TabEditorModel;
import fr.inria.corese.demo.view.TabEditorView;
import fr.inria.corese.demo.view.CodeEditorView;
import javafx.collections.ListChangeListener;
import javafx.scene.control.Tab;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

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
            addNewTab("Untitled");
        });
    }

    public void addNewTab(String title) {
        CodeEditorController codeEditorController = new CodeEditorController(type);
        Tab tab = view.addNewEditorTab(title, codeEditorController.getView());
        model.addTabModel(tab, codeEditorController);
    }

    private void addNewTab(File file) {
        try {
            String content = Files.readString(file.toPath());
            CodeEditorController codeEditorController = new CodeEditorController(type, content);
            Tab tab = view.addNewEditorTab(file.getName(), codeEditorController.getView());
            model.addTabModel(tab, codeEditorController);

            System.out.println("content : "+ content);
            // Définir le contenu de l'éditeur
//            codeEditorController.getModel().setContent(content);
            System.out.println("content dans model "+codeEditorController.getModel().getContent());
            codeEditorController.getModel().setCurrentFile(file.getAbsolutePath());
//            codeEditorController.getView().getCodeMirrorView().setContent(codeEditorController.getModel().getContent());
            System.out.println("content dans view "+codeEditorController.getView().getCodeMirrorView().getContent());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void openFile(File file) {
        for (Tab tab : view.getTabs()) {
            if (tab != view.getAddTab() && file.getName().equals(tab.getText())) {
                view.getSelectionModel().select(tab);
                return;
            }
        }
        addNewTab(file);
    }

    public TabEditorView getView() {
        return view;
    }

    public TabEditorModel getModel() {
        return model;
    }
}
