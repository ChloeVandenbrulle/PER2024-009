package fr.inria.corese.demo.view;

import fr.inria.corese.demo.enums.IconButtonType;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.Ikon;
import org.kordamp.ikonli.materialdesign2.*;
import javafx.scene.control.Button;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class IconButtonBarView extends VBox {
    private final Map<IconButtonType, Button> buttons = new LinkedHashMap<>();

    public IconButtonBarView() {
        setSpacing(10);
        setAlignment(Pos.TOP_CENTER);
    }

    public void initializeButtons(List<IconButtonType> buttonTypes) {
        getChildren().clear();
        buttonTypes.forEach(type -> {
            Button button = new IconButtonView(type);
            buttons.put(type, button);
        });
        getChildren().addAll(buttons.values());
    }


    public Button getButton(IconButtonType type) {
        return buttons.get(type);
    }

}