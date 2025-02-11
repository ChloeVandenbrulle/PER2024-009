package fr.inria.corese.demo.view;

import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

public class TabEditorView extends TabPane {
    private Tab addTab;
    private Button addTabButton;

    public TabEditorView() {
        setTabClosingPolicy(TabClosingPolicy.ALL_TABS);

        addTabButton = new Button("+");
        addTabButton.setStyle("""
            -fx-background-color: transparent;
            -fx-border-radius: 20;
            -fx-background-radius: 20;
            -fx-min-height: 10;
            -fx-max-width: 10;
            """);
        addTab = new Tab();
        addTab.setGraphic(addTabButton);
        addTab.setClosable(false);

        getTabs().add(addTab);
    }

    public Tab addNewEditorTab(String title, CodeEditorView codeEditorView) {
        Tab tab = new Tab(title);
        codeEditorView.setMaxWidth(Double.MAX_VALUE);
        codeEditorView.setMaxHeight(Double.MAX_VALUE);
        tab.setContent(codeEditorView);

        getTabs().add(getTabs().size() - 1, tab);
        getSelectionModel().select(tab);
        return tab;
    }

    public Tab getAddTab() {
        return addTab;
    }

    public Button getAddTabButton() {
        return addTabButton;
    }
}
