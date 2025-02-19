package fr.inria.corese.demo.view;

import fr.inria.corese.demo.enums.icon.IconButtonType;
import fr.inria.corese.demo.view.icon.IconButtonView;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import java.io.IOException;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class TopBar extends HBox {

    @FXML
    private HBox leftButtonsContainer;

    @FXML
    private HBox rightButtonsContainer;

    private final Map<IconButtonType, IconButtonView> buttons;

    public TopBar() {
        this.buttons = new HashMap<>();
        loadFxml();
        getStyleClass().add("top-bar");
    }

    private void loadFxml() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fr/inria/corese/demo/topbar.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void addLeftButtons(List<IconButtonType> buttonTypes) {
        leftButtonsContainer.getChildren().clear();
        buttonTypes.forEach(type -> {
            IconButtonView button = new IconButtonView(type);
            buttons.put(type, button);
            leftButtonsContainer.getChildren().add(button);
        });
    }

    public void addRightButtons(List<IconButtonType> buttonTypes) {
        rightButtonsContainer.getChildren().clear();
        buttonTypes.forEach(type -> {
            IconButtonView button = new IconButtonView(type);
            buttons.put(type, button);
            rightButtonsContainer.getChildren().add(button);
        });
    }

    public Button getButton(IconButtonType type) {
        return buttons.get(type);
    }

    public void setOnAction(IconButtonType type, Runnable action) {
        IconButtonView button = buttons.get(type);
        if (button != null) {
            button.setOnAction(e -> action.run());
        }
    }
}