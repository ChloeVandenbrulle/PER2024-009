package fr.inria.corese.demo.view;

import fr.inria.corese.demo.enums.IconButtonType;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.paint.Color;
import org.kordamp.ikonli.Ikon;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.materialdesign2.*;

public class IconButtonView extends Button{
    private IconButtonType iconButtonType;

    public IconButtonView(IconButtonType type) {
        iconButtonType = type;
        createIcon(type);
    }

    private void createIcon(IconButtonType type) {
        switch (type) {
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
            case NEW_FILE -> createIconButton(MaterialDesignF.FILE_PLUS_OUTLINE, "New file");
            case NEW_FOLDER -> createIconButton(MaterialDesignF.FOLDER_PLUS_OUTLINE, "New folder");
            case CLOSE_FILE_EXPLORER -> createIconButton(MaterialDesignL.LESS_THAN, "Close file explorer");
        };
    }

    private void createIconButton(Ikon icon, String tooltipText) {
        FontIcon fontIcon = new FontIcon(icon);
        Tooltip tooltip = new Tooltip(tooltipText);
        Tooltip.install(this, tooltip);

        fontIcon.setIconSize(25);  // Taille de l'icône en pixels
        setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-pref-width: 6;" +
                        "-fx-pref-height: 6;"+
                        "-fx-border-color: transparent;"
        );

        setOnMouseEntered(e -> {
            setStyle(
                    "-fx-background-color: #2196F3;"+
                            "-fx-pref-width: 6;" +
                            "-fx-pref-height: 6;"+
                            "-fx-border-color: transparent;"
            );
            fontIcon.setIconColor(Color.WHITE);
            tooltip.setStyle(
                    "-fx-font-size: 14;"
            );
        });

        setOnMouseExited(e -> {
            setStyle(
                    "-fx-background-color: transparent;" +
                            "-fx-pref-width: 6;" +
                            "-fx-pref-height: 6;"+
                            "-fx-border-color: transparent;"
            );
            fontIcon.setIconColor(Color.BLACK);
        });

        setGraphic(fontIcon);
    }
}
