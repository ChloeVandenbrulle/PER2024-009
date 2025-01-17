package fr.inria.corese.demo.view;

import org.kordamp.ikonli.javafx.FontIcon;
import javafx.scene.control.Button;
import atlantafx.base.theme.Styles;

public class ToolbarButton extends Button {
    public ToolbarButton(FontIcon icon) {
        super();
        icon.setIconSize(20);
        setGraphic(icon);
        getStyleClass().addAll("toolbar-button", Styles.BUTTON_OUTLINED);
        setPrefSize(40, 40);
    }
}
