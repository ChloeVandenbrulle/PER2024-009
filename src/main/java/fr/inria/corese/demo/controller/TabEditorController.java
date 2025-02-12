package fr.inria.corese.demo.controller;

import fr.inria.corese.demo.enums.IconButtonBarType;
import fr.inria.corese.demo.model.TabEditorModel;
import fr.inria.corese.demo.view.TabEditorView;
import javafx.collections.ListChangeListener;
import javafx.scene.control.Tab;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.KeyCode;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class TabEditorController {
    private final TabEditorView view;
    private final TabEditorModel model;
    private final IconButtonBarType type;

    public TabEditorController(IconButtonBarType type) {
        this.view = new TabEditorView();
        this.model = new TabEditorModel();
        this.type = type;

        initializeFirstTab();
        initializeKeyboardShortcuts();
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

    private void initializeKeyboardShortcuts(){
        view.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
                    if (event.isControlDown() && event.getCode() == KeyCode.S) {
                        handleSaveShortcut();
                        event.consume();
                    }
                });
            }
        });
    }

    private void handleSaveShortcut() {
        Tab selectedTab = view.getSelectionModel().getSelectedItem();
        if (selectedTab != null && selectedTab != view.getAddTab()) {
            CodeEditorController activeController = model.getControllerForTab(selectedTab);
            if (activeController != null) {
                activeController.saveFile();
            }
        }
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

            codeEditorController.getModel().setCurrentFile(file.getPath());

            tab.setOnClosed(event -> {
                CodeEditorController controller = model.getControllerForTab(tab);
                if (controller != null && controller.getModel().isModified()) {
                    // TODO: Ajouter une popup pour demander si l'utilisateur veut sauvegarder
                    controller.saveFile();
                }
                model.getTabControllers().remove(tab);
            });

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
