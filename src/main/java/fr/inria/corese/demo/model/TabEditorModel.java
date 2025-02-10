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
        return tabControllers.get(tab);
    }

    public Map<Tab, CodeEditorController> getTabControllers() {
        return tabControllers;
    }
}
