package fr.inria.corese.demo.factory;

import fr.inria.corese.demo.controller.IconButtonBarController;
import fr.inria.corese.demo.enums.IconButtonBarType;
import fr.inria.corese.demo.enums.IconButtonType;
import fr.inria.corese.demo.model.IconButtonBarModel;
import fr.inria.corese.demo.view.IconButtonBarView;
import javafx.scene.control.Button;
import org.kordamp.ikonli.feather.Feather;
import org.kordamp.ikonli.javafx.FontIcon;
import atlantafx.base.theme.Styles;
import java.util.List;

public class IconButtonBarFactory {

    public static IconButtonBarController create(IconButtonBarType type) {
        List<IconButtonType> buttons = getButtonsForType(type);

        IconButtonBarModel model = new IconButtonBarModel(buttons);
        IconButtonBarView view = new IconButtonBarView();
        
        return new IconButtonBarController(model, view);
    }

    private static List<IconButtonType> getButtonsForType(IconButtonBarType type) {
        return switch (type) {
            case DATA -> List.of(
                    IconButtonType.CLEAR,
                    IconButtonType.RELOAD,
                    IconButtonType.IMPORT,
                    IconButtonType.CLOSE_FILE_EXPLORER,
                    IconButtonType.DOCUMENTATION
            );
            case RDF_EDITOR -> List.of(
                    IconButtonType.SAVE,
                    IconButtonType.EXPORT,
                    IconButtonType.UNDO,
                    IconButtonType.REDO,
                    IconButtonType.DOCUMENTATION
            );
            case VALIDATION -> List.of(
                    IconButtonType.SAVE,
                    IconButtonType.OPEN_FILE,
                    IconButtonType.IMPORT,
                    IconButtonType.EXPORT,
                    IconButtonType.CLEAR,
                    IconButtonType.UNDO,
                    IconButtonType.REDO,
                    IconButtonType.DOCUMENTATION
            );
            case QUERY_EDITOR -> List.of(
                    IconButtonType.IMPORT,
                    IconButtonType.EXPORT
            );
            case QUERY_TABLE, QUERY_TEXT -> List.of(
                    IconButtonType.EXPORT
            );
            case QUERY_GRAPH -> List.of(
                    IconButtonType.EXPORT,
                    IconButtonType.ZOOM_IN,
                    IconButtonType.ZOOM_OUT,
                    IconButtonType.FULL_SCREEN
            );
        };
    }

    public static Button createSingleButton(IconButtonType type) {
        Button button = new Button();
        FontIcon icon = new FontIcon();
        icon.getStyleClass().add(Styles.SMALL);

        // Configurer le style et l'icône en fonction du type
        switch (type) {
            case CLEAR -> {
                icon.setIconCode(Feather.TRASH_2);
                button.getStyleClass().addAll(Styles.BUTTON_ICON, Styles.DANGER, Styles.SMALL);
            }
            case RELOAD -> {
                icon.setIconCode(Feather.REFRESH_CW);
                button.getStyleClass().addAll(Styles.BUTTON_ICON, Styles.SUCCESS, Styles.SMALL);
            }
            case IMPORT -> {
                icon.setIconCode(Feather.FOLDER_PLUS);
                button.getStyleClass().addAll(Styles.BUTTON_ICON, Styles.SUCCESS, Styles.SMALL);
            }
            case CLOSE_FILE_EXPLORER -> {
                icon.setIconCode(Feather.X);
                button.getStyleClass().addAll(Styles.BUTTON_ICON, Styles.DANGER, Styles.SMALL);
            }
            case DOCUMENTATION -> {
                icon.setIconCode(Feather.EXTERNAL_LINK);
                button.getStyleClass().addAll(Styles.BUTTON_ICON, Styles.SUCCESS, Styles.SMALL);
            }
            default -> {
                icon.setIconCode(Feather.HELP_CIRCLE);
                button.getStyleClass().add(Styles.BUTTON_ICON);
            }
        }

        button.setGraphic(icon);
        button.getStyleClass().add(Styles.FLAT);
        return button;
    }

}
