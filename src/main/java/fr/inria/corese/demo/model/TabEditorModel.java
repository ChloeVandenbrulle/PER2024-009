package fr.inria.corese.demo.model;

import javafx.scene.control.Tab;
import fr.inria.corese.demo.controller.CodeEditorController;
import java.util.HashMap;
import java.util.Map;

public class TabEditorModel {
    private final Map<Tab, CodeEditorController> tabControllers;

    public TabEditorModel() {
        this.tabControllers = new HashMap<>();
    }

    public void addTabModel(Tab tab, CodeEditorController controller) {
        tabControllers.put(tab, controller);
    }

    public CodeEditorController getControllerForTab(Tab tab) {
        if (tab == null) {
            System.err.println("Null tab passed to getControllerForTab");
            return null;
        }

        CodeEditorController controller = tabControllers.get(tab);

        if (controller == null) {
            System.err.println("No controller found for tab: " + tab.getText());
            System.err.println("Existing tabs in map:");
            for (Tab existingTab : tabControllers.keySet()) {
                System.err.println("- " + existingTab.getText());
            }
        }

        return controller;
    }

    public Map<Tab, CodeEditorController> getTabControllers() {
        return tabControllers;
    }
}
