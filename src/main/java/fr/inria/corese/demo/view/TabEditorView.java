package fr.inria.corese.demo.view;

import javafx.animation.ScaleTransition;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.materialdesign2.*;

public class TabEditorView extends TabPane {
    private Tab addTab;
    private Button addTabButton;
    private FontIcon modifiedFile;

    public TabEditorView() {
        setTabClosingPolicy(TabClosingPolicy.ALL_TABS);
        setTabMaxWidth(150);

        addTabButton = new Button();
        FontIcon addIcon = new FontIcon(MaterialDesignP.PLUS);
        addIcon.setIconSize(10);

        ScaleTransition scaleIn = new ScaleTransition(Duration.millis(150), addIcon);
        scaleIn.setToX(1.1);
        scaleIn.setToY(1.1);

        ScaleTransition scaleOut = new ScaleTransition(Duration.millis(150), addIcon);
        scaleOut.setToX(1.0);
        scaleOut.setToY(1.0);

        addIcon.setOnMouseEntered(e -> {
            addIcon.setCursor(Cursor.HAND);
            scaleIn.playFromStart();
        });

        addIcon.setOnMouseExited(e -> {
            addIcon.setCursor(Cursor.DEFAULT);
            scaleOut.playFromStart();
        });
        addTabButton.setGraphic(addIcon);
        addTabButton.setStyle("""
        -fx-background-color: transparent;
        -fx-min-height: 30;
        -fx-max-height: 30;
        """);

        addTab = new Tab();
        addTab.setStyle("""
        -fx-background-color: transparent;
        -fx-border-color: #E0E0E0;
        -fx-border-radius: 5 5 0 0;
        -fx-border-width: 1 1 0 1;
        -fx-padding: 0;
        """);
        addTab.setGraphic(addTabButton);
        addTab.setClosable(false);

        getTabs().add(addTab);

        modifiedFile = new FontIcon(MaterialDesignC.CHECKBOX_BLANK_CIRCLE);
        modifiedFile.setIconSize(5);
        modifiedFile.setIconColor(Color.web("#2196F3"));
    }

    public Tab addNewEditorTab(String title, CodeEditorView codeEditorView) {
        Tab tab = new Tab(title);
        tab.setStyle("""
            -fx-background-color: transparent;
            -fx-border-color: #E0E0E0;
            -fx-border-width: 0 1 0 1;
            -fx-font-size: 12px;
        """);
        tab.setOnSelectionChanged(e -> {
            if (tab.isSelected()) {
                tab.setStyle("""
                    -fx-border-color: #2196F3;
                    -fx-border-width: 0 0 1 0;
                """);
            } else {
                tab.setStyle("""
                    -fx-border-color: #E0E0E0;
                    -fx-border-width: 0 1 0 1;
                """);
            }
        });
        codeEditorView.setMaxWidth(Double.MAX_VALUE);
        codeEditorView.setMaxHeight(Double.MAX_VALUE);
        tab.setContent(codeEditorView);

        getTabs().add(getTabs().size() - 1, tab);
        getSelectionModel().select(tab);
        return tab;
    }

    public void updateTabIcon(Tab tab, boolean isModified) {
        FontIcon icon = new FontIcon(MaterialDesignR.RECORD);
        icon.setIconSize(2);
        if (isModified) {
            icon.setVisible(true);
        } else {
            icon.setVisible(false);
        }

        tab.setGraphic(icon);
    }

    public Tab getAddTab() {
        return addTab;
    }

    public Button getAddTabButton() {
        return addTabButton;
    }
}
