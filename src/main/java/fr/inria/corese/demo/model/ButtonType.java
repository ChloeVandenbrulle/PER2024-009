// model/ButtonType.java
package fr.inria.corese.demo.model;

public enum ButtonType {
    // Project Buttons
    OPEN_PROJECT("Open project", "project-button"),
    SAVE_AS("Save as", "project-button"),
    SHOW_LOGS("Show logs", "log-button"),

    // File Buttons
    CLEAR_GRAPH("", "file-button"),
    RELOAD_FILES("", "file-button"),
    LOAD_FILES("", "file-button"),
    LOAD_RULE_FILE( "Load rule file", "file-button");

    private final String label;
    private final String styleClass;

    ButtonType(String label, String styleClass) {
        this.label = label;
        this.styleClass = styleClass;
    }

    public String getLabel() { return label; }
    public String getStyleClass() { return styleClass; }
}