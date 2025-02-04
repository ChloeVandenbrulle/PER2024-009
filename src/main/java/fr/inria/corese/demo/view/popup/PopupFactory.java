package fr.inria.corese.demo.view.popup;

import fr.inria.corese.demo.model.ProjectDataModel;

import java.util.HashMap;
import java.util.Map;

public class PopupFactory {
    public static final String ALERT_POPUP = "alert";
    public static final String WARNING_POPUP = "warning";
    public static final String FILE_INFO_POPUP = "fileInfo";
    public static final String LOG_POPUP = "log";

    private static PopupFactory instance;
    private final Map<String, IPopup> popupCache = new HashMap<>();
    private final ProjectDataModel model;

    private PopupFactory(ProjectDataModel model) {
        this.model = model;
    }

    public static PopupFactory getInstance(ProjectDataModel model) {
        if (instance == null) {
            instance = new PopupFactory(model);
        }
        return instance;
    }

    public IPopup createPopup(String type) {
        System.out.println("Creating popup of type: " + type); // Debug print

        // Réutiliser une instance existante si disponible
        if (popupCache.containsKey(type)) {
            System.out.println("Returning cached popup"); // Debug print
            return popupCache.get(type);
        }

        // Créer une nouvelle instance
        System.out.println("Creating new popup instance"); // Debug print
        IPopup popup = switch (type.toLowerCase()) {
            case ALERT_POPUP -> new AlertPopup();
            case WARNING_POPUP -> new WarningPopup();
            case FILE_INFO_POPUP -> new FileInfoPopup();
            case LOG_POPUP -> {
                System.out.println("Creating new LogDialog"); // Debug print
                yield new LogDialog(model);
            }
            default -> throw new IllegalArgumentException("Unknown popup type: " + type);
        };

        // Mettre en cache pour réutilisation
        popupCache.put(type, popup);
        return popup;
    }
}