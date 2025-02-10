package fr.inria.corese.demo.view.popup;

import fr.inria.corese.demo.model.ProjectDataModel;

import java.util.HashMap;
import java.util.Map;

public class PopupFactory {
    public static final String ALERT_POPUP = "alert";
    public static final String WARNING_POPUP = "warning";
    public static final String FILE_INFO_POPUP = "fileInfo";
    public static final String LOG_POPUP = "log";
    public static final String NEW_FILE_POPUP = "newFile";
    public static final String RULE_INFO_POPUP = "ruleInfo";

    private final Map<String, IPopup> popupCache = new HashMap<>();
    private final ProjectDataModel model;

    public PopupFactory(ProjectDataModel model) {
        this.model = model;
    }

    public static PopupFactory getInstance(ProjectDataModel model) {
        return new PopupFactory(model);
    }

    public IPopup createPopup(String type) {
        if (popupCache.containsKey(type)) {
            return popupCache.get(type);
        }

        IPopup popup = switch (type) {
            case ALERT_POPUP -> new AlertPopup();
            case WARNING_POPUP -> new WarningPopup();
            case FILE_INFO_POPUP -> new FileInfoPopup();
            case LOG_POPUP -> new LogDialog(model);
            case NEW_FILE_POPUP -> new NewFilePopup();
            case RULE_INFO_POPUP -> new RuleInfoPopup();
            default -> throw new IllegalArgumentException("Unknown popup type: " + type);
        };

        popupCache.put(type, popup);
        return popup;
    }
}