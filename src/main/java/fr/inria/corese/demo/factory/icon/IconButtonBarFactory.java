package fr.inria.corese.demo.factory.icon;

import fr.inria.corese.demo.controller.IconButtonBarController;
import fr.inria.corese.demo.enums.icon.IconButtonBarType;
import fr.inria.corese.demo.enums.icon.IconButtonType;
import fr.inria.corese.demo.model.IconButtonBarModel;
import fr.inria.corese.demo.view.icon.IconButtonBarView;
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
            case QUERY -> List.of(
                    IconButtonType.IMPORT,
                    IconButtonType.EXPORT
            );
        };
    }

}
