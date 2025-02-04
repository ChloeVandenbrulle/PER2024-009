package fr.inria.corese.demo.view;

import fr.inria.corese.demo.enums.IconButtonType;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.VBox;
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
        setPadding(new Insets(15, 0, 0, 0));
        setAlignment(Pos.TOP_CENTER);

    }

    public void initializeButtons(List<IconButtonType> buttonTypes) {
        getChildren().clear();
        buttonTypes.forEach(type -> {
            Button button = createButton(type);
            buttons.put(type, button);
        });
        getChildren().addAll(buttons.values());
    }

    private Button createButton(IconButtonType type) {
        return switch (type) {
            case SAVE -> createIconButton(MaterialDesignC.CONTENT_SAVE_OUTLINE);
            case OPEN_FILE -> createIconButton(MaterialDesignF.FOLDER_OPEN);
            case EXPORT -> createIconButton(MaterialDesignE.EXPORT);
            case IMPORT -> createIconButton(MaterialDesignI.IMPORT);
            case CLEAR -> createIconButton(MaterialDesignB.BROOM);
            case UNDO -> createIconButton(MaterialDesignU.UNDO);
            case REDO -> createIconButton(MaterialDesignR.REDO);
            case DOCUMENTATION -> createIconButton(MaterialDesignB.BOOK_OPEN_VARIANT);
            case ZOOM_IN -> createIconButton(MaterialDesignM.MAGNIFY_PLUS_OUTLINE);
            case ZOOM_OUT -> createIconButton(MaterialDesignM.MAGNIFY_MINUS_OUTLINE);
            case FULL_SCREEN -> createIconButton(MaterialDesignF.FULLSCREEN);
        };
    }

    public Button getButton(IconButtonType type) {
        return buttons.get(type);
    }

    private Button createIconButton(Ikon icon) {
        FontIcon fontIcon = new FontIcon(icon);
        fontIcon.setIconSize(20);  // Taille de l'icône en pixels
        Button button = new Button();
        button.setGraphic(fontIcon);
        button.setStyle(
            "-fx-background-color: white;" +
            "-fx-border-radius: 20;" +
            "-fx-background-radius: 20;" +
            "-fx-min-height: 10;" +
            "-fx-max-width: 10;"
        );

        button.setOnMouseEntered(e -> button.setStyle(
            "-fx-background-color: #E3F2FD;" +
            "-fx-border-radius: 20;" +
            "-fx-background-radius: 20;" +
            "-fx-min-height: 10;" +
            "-fx-max-width: 10;"
        ));

        button.setOnMouseExited(e -> button.setStyle(
            "-fx-background-color: white;" +
            "-fx-border-radius: 20;" +
            "-fx-background-radius: 20;" +
            "-fx-min-height: 10;" +
            "-fx-max-width: 10;"
        ));
        return button;
    }

}