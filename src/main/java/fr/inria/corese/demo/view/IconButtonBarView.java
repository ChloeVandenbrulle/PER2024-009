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
            Button button = createButton(type);
            buttons.put(type, button);
        });
        getChildren().addAll(buttons.values());
    }

    private Button createButton(IconButtonType type) {
        return switch (type) {
            case SAVE -> createIconButton(MaterialDesignC.CONTENT_SAVE_OUTLINE, "Save");
            case OPEN_FILE -> createIconButton(MaterialDesignF.FOLDER_OPEN, "Open file");
            case EXPORT -> createIconButton(MaterialDesignE.EXPORT, "Export");
            case IMPORT -> createIconButton(MaterialDesignI.IMPORT, "Import");
            case CLEAR -> createIconButton(MaterialDesignB.BROOM, "Clear");
            case UNDO -> createIconButton(MaterialDesignU.UNDO, "Undo");
            case REDO -> createIconButton(MaterialDesignR.REDO, "Redo");
            case DOCUMENTATION -> createIconButton(MaterialDesignB.BOOK_OPEN_VARIANT, "Documentation");
            case ZOOM_IN -> createIconButton(MaterialDesignM.MAGNIFY_PLUS_OUTLINE, "Zoom in");
            case ZOOM_OUT -> createIconButton(MaterialDesignM.MAGNIFY_MINUS_OUTLINE, "Zoom out");
            case FULL_SCREEN -> createIconButton(MaterialDesignF.FULLSCREEN, "Full screen");
        };
    }

    public Button getButton(IconButtonType type) {
        return buttons.get(type);
    }

    private Button createIconButton(Ikon icon, String tooltipText) {
        FontIcon fontIcon = new FontIcon(icon);
        fontIcon.setIconSize(22);  // Taille de l'icône en pixels
        Button button = new Button();
        button.setGraphic(fontIcon);
        button.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-pref-width: 6;" +
            "-fx-pref-height: 6;"
        );

        Tooltip tooltip = new Tooltip(tooltipText);

        Tooltip.install(button, tooltip);

        button.setOnMouseEntered(e -> {
            button.setStyle(
                    "-fx-background-color: #2196F3;"+
                    "-fx-pref-width: 6;" +
                    "-fx-pref-height: 6;"
            );
            fontIcon.setIconColor(Color.WHITE);
            tooltip.setStyle(
                    "-fx-font-size: 14;"
            );
        });

        button.setOnMouseExited(e -> {
            button.setStyle(
                    "-fx-background-color: transparent;" +
                    "-fx-pref-width: 6;" +
                    "-fx-pref-height: 6;"
            );
            fontIcon.setIconColor(Color.BLACK);
        });
        return button;
    }

}