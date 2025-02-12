package fr.inria.corese.demo.factory;

import fr.inria.corese.demo.controller.IconButtonBarController;
import fr.inria.corese.demo.enums.IconButtonBarType;
import fr.inria.corese.demo.enums.IconButtonType;
import fr.inria.corese.demo.model.IconButtonBarModel;
import fr.inria.corese.demo.view.IconButtonBarView;
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

}
