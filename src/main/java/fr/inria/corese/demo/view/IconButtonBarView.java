package fr.inria.corese.demo.view;

import fr.inria.corese.demo.enums.IconButtonType;
import javafx.geometry.Pos;
import javafx.scene.layout.VBox;
import javafx.scene.control.Button;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class IconButtonBarView extends VBox {
    private final Map<IconButtonType, Button> buttons = new LinkedHashMap<>();

    public IconButtonBarView() {
        setSpacing(10);
        setAlignment(Pos.TOP_CENTER);
        setStyle("-fx-background-color: transparent;");
    }

    public void initializeButtons(List<IconButtonType> buttonTypes) {
        getChildren().clear();
        buttonTypes.forEach(type -> {
            Button button = new IconButtonView(type);
            buttons.put(type, button);
        });
        getChildren().addAll(buttons.values());
        setHeight(50);
    }


    public Button getButton(IconButtonType type) {
        return buttons.get(type);
    }

    public void addCustomButton(Button runButton) {
        getChildren().add(runButton);
    }
}