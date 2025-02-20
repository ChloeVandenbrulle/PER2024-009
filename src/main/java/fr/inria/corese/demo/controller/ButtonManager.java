package fr.inria.corese.demo.controller;

import fr.inria.corese.core.load.LoadException;
import fr.inria.corese.demo.enums.button.ButtonType;
import fr.inria.corese.demo.factory.popup.IPopup;
import fr.inria.corese.demo.factory.popup.LoadingPopup;
import fr.inria.corese.demo.factory.popup.PopupFactory;
import fr.inria.corese.demo.factory.popup.WarningPopup;
import fr.inria.corese.demo.model.ProjectDataModel;
import fr.inria.corese.demo.view.CustomButton;
import java.util.HashMap;
import java.util.Map;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.stage.FileChooser;
import javafx.stage.DirectoryChooser;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ButtonManager {
    private static final Logger logger = Logger.getLogger(ButtonManager.class.getName());
    private final ProjectDataModel model;
    private final Map<ButtonType, CustomButton> buttons = new HashMap<>();
    private PopupFactory popupFactory;

    public ButtonManager(ProjectDataModel model) {
        this.model = model;
        this.popupFactory = PopupFactory.getInstance(model);
        initializeButtons();
    }

    private void initializeButtons() {
        createProjectButton(ButtonType.OPEN_PROJECT);
        createProjectButton(ButtonType.SAVE_AS);
        createLogButton(ButtonType.SHOW_LOGS);
        createFileButton(ButtonType.CLEAR_GRAPH);
        createFileButton(ButtonType.RELOAD_FILES);
        createFileButton(ButtonType.LOAD_FILES);
        createFileButton(ButtonType.LOAD_RULE_FILE);

        Button showLogsButton = new Button("Show Logs");
        showLogsButton.setOnAction(event -> {
            System.out.println("Show logs button clicked");
            showLogsButton.fireEvent(new ActionEvent());
        });
    }

    private void createProjectButton(ButtonType type) {
        CustomButton button = new CustomButton.Builder(type)
                .withTooltip("Click to " + type.getLabel().toLowerCase())
                .withOnClick(this::handleProjectAction)
                .build();
        buttons.put(type, button);
    }

    private void createLogButton(ButtonType type) {
        CustomButton button = new CustomButton.Builder(type)
                .withStyle("log-button-special")
                .withTooltip("View application logs")
                .withOnClick(this::handleLogAction)
                .build();
        buttons.put(type, button);
    }

    private void createFileButton(ButtonType type) {
        CustomButton button = new CustomButton.Builder(type)
                .withTooltip("File operation: " + type.getLabel())
                .withOnClick(this::handleFileAction)
                .build();
        buttons.put(type, button);
    }

    private void handleProjectAction(CustomButton button) {
        switch (button.getType()) {
            case OPEN_PROJECT -> handleOpenProject(button);
            case SAVE_AS -> handleSaveAs(button);
            default -> { /* Aucune action */ }
        }
    }

    private void handleOpenProject(CustomButton button) {
        DirectoryChooser chooser = new DirectoryChooser();
        File dir = chooser.showDialog(button.getScene().getWindow());
        if (dir != null) {
            try {
                // Afficher un popup de chargement
                IPopup loadingPopup = popupFactory.createPopup(PopupFactory.LOADING_POPUP);
                loadingPopup.setMessage("Loading project from " + dir.getName() + "...");
                loadingPopup.displayPopup();

                // Chargement du projet dans un thread séparé pour éviter de bloquer l'UI
                new Thread(() -> {
                    try {
                        model.loadProject(dir);

                        // Mettre à jour l'UI dans le thread JavaFX
                        Platform.runLater(() -> {
                            ((LoadingPopup)loadingPopup).close();

                            // Notification de succès
                            IPopup successPopup = popupFactory.createPopup(PopupFactory.TOAST_NOTIFICATION);
                            successPopup.setMessage("Project loaded successfully!");
                            successPopup.displayPopup();
                        });

                    } catch (Exception e) {
                        logger.log(Level.SEVERE, "Error loading project", e);
                        // Gérer l'erreur dans le thread JavaFX
                        Platform.runLater(() -> {
                            ((LoadingPopup)loadingPopup).close();

                            String errorMessage = "Error loading project: " + e.getMessage();
                            model.addLogEntry("ERROR: " + errorMessage);

                            IPopup errorPopup = popupFactory.createPopup(PopupFactory.WARNING_POPUP);
                            errorPopup.setMessage(errorMessage);
                            ((WarningPopup) errorPopup).getResult();
                        });
                    }
                }).start();

            } catch (Exception e) {
                logger.log(Level.SEVERE, "Error initializing project load", e);
                // Attraper toute exception qui pourrait se produire lors de l'initialisation
                String errorMessage = "Error initializing project load: " + e.getMessage();
                model.addLogEntry("ERROR: " + errorMessage);

                IPopup errorPopup = popupFactory.createPopup(PopupFactory.WARNING_POPUP);
                errorPopup.setMessage(errorMessage);
                ((WarningPopup) errorPopup).getResult();
            }
        }
    }

    private void handleSaveAs(CustomButton button) {
        FileChooser chooser = new FileChooser();
        File file = chooser.showSaveDialog(button.getScene().getWindow());
        if (file != null) {
            try {
                model.saveProject(file);

                // Notification de succès
                IPopup successPopup = popupFactory.createPopup(PopupFactory.TOAST_NOTIFICATION);
                successPopup.setMessage("Project saved successfully!");
                successPopup.displayPopup();

            } catch (Exception e) {
                logger.log(Level.SEVERE, "Error saving project", e);
                String errorMessage = "Error saving project: " + e.getMessage();
                model.addLogEntry("ERROR: " + errorMessage);

                IPopup errorPopup = popupFactory.createPopup(PopupFactory.WARNING_POPUP);
                errorPopup.setMessage(errorMessage);
                ((WarningPopup) errorPopup).getResult();
            }
        }
    }

    private void handleLogAction(CustomButton button) {
        // Afficher la boîte de dialogue des logs
        try {
            IPopup logDialog = new fr.inria.corese.demo.factory.popup.LogDialog(model);
            logDialog.displayPopup();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error displaying logs", e);
            String errorMessage = "Error displaying logs: " + e.getMessage();
            model.addLogEntry("ERROR: " + errorMessage);

            IPopup errorPopup = popupFactory.createPopup(PopupFactory.WARNING_POPUP);
            errorPopup.setMessage(errorMessage);
            ((WarningPopup) errorPopup).getResult();
        }
    }

    private void handleFileAction(CustomButton button) {
        try {
            switch (button.getType()) {
                case CLEAR_GRAPH -> handleClearGraph();
                case RELOAD_FILES -> handleReloadFiles();
                case LOAD_FILES -> handleLoadFiles(button);
                case LOAD_RULE_FILE -> handleLoadRuleFile(button);
                default -> { /* Aucune action */ }
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error in file action", e);
            String errorMessage = "Error: " + e.getMessage();
            model.addLogEntry("ERROR: " + errorMessage);

            IPopup errorPopup = popupFactory.createPopup(PopupFactory.WARNING_POPUP);
            errorPopup.setMessage(errorMessage);
            ((WarningPopup) errorPopup).getResult();
        }
    }

    private void handleClearGraph() {
        try {
            // Demander confirmation
            IPopup confirmPopup = popupFactory.createPopup(PopupFactory.CLEAR_GRAPH_CONFIRMATION);
            confirmPopup.setMessage("Are you sure you want to clear the graph? This action cannot be undone.");
            confirmPopup.displayPopup();

            if (((fr.inria.corese.demo.factory.popup.ClearGraphConfirmationPopup) confirmPopup).getResult()) {
                model.clearGraph();
                model.clearFiles();

                // Notification de succès
                IPopup successPopup = popupFactory.createPopup(PopupFactory.TOAST_NOTIFICATION);
                successPopup.setMessage("Graph has been cleared successfully!");
                successPopup.displayPopup();
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error clearing graph", e);
            String errorMessage = "Error clearing graph: " + e.getMessage();
            model.addLogEntry("ERROR: " + errorMessage);

            IPopup errorPopup = popupFactory.createPopup(PopupFactory.WARNING_POPUP);
            errorPopup.setMessage(errorMessage);
            ((WarningPopup) errorPopup).getResult();
        }
    }

    private void handleReloadFiles() {
        try {
            // Demander confirmation
            IPopup warningPopup = popupFactory.createPopup(PopupFactory.WARNING_POPUP);
            warningPopup.setMessage("Reloading files will reset the current graph. Do you want to continue?");
            boolean result = ((WarningPopup) warningPopup).getResult();

            if (result) {
                model.reloadFiles();

                // Notification de succès
                IPopup successPopup = popupFactory.createPopup(PopupFactory.TOAST_NOTIFICATION);
                successPopup.setMessage("Files reloaded successfully!");
                successPopup.displayPopup();
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error reloading files", e);
            String errorMessage = "Error reloading files: " + e.getMessage();
            model.addLogEntry("ERROR: " + errorMessage);

            IPopup errorPopup = popupFactory.createPopup(PopupFactory.WARNING_POPUP);
            errorPopup.setMessage(errorMessage);
            ((WarningPopup) errorPopup).getResult();
        }
    }

    private void handleLoadFiles(CustomButton button) {
        FileChooser chooser = new FileChooser();
        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("TTL files", "*.ttl")
        );

        File file = chooser.showOpenDialog(button.getScene().getWindow());
        if (file != null) {
            try {
                model.addLogEntry("Starting to load file: " + file.getName());

                // Check if there are already files loaded
                if (!model.getFileListModel().getFiles().isEmpty()) {
                    // Show warning popup before loading
                    IPopup warningPopup = popupFactory.createPopup(PopupFactory.WARNING_POPUP);
                    warningPopup.setMessage("Loading this file will reset the current graph. Do you want to continue?");
                    boolean result = ((WarningPopup) warningPopup).getResult();

                    if (!result) {
                        model.addLogEntry("File loading cancelled by user: " + file.getName());
                        return;
                    }
                }

                try {
                    // Charger le fichier
                    model.loadFile(file);
                    model.addFile(file.getName());
                    model.addLogEntry("File loaded successfully: " + file.getName());

                    // Notification de succès
                    IPopup successPopup = popupFactory.createPopup(PopupFactory.TOAST_NOTIFICATION);
                    successPopup.setMessage("File '" + file.getName() + "' has been successfully loaded!");
                    successPopup.displayPopup();

                } catch (LoadException e) {
                    logger.log(Level.SEVERE, "LoadException while loading file", e);
                    String errorMessage = "Error loading file: " + e.getMessage();
                    model.addLogEntry("ERROR: " + errorMessage);

                    IPopup errorPopup = popupFactory.createPopup(PopupFactory.WARNING_POPUP);
                    errorPopup.setMessage(errorMessage);
                    ((WarningPopup) errorPopup).getResult();
                }

            } catch (Exception e) {
                logger.log(Level.SEVERE, "Unexpected error while handling file load", e);
                String errorMessage = "Unexpected error: " + e.getMessage();
                model.addLogEntry("CRITICAL ERROR: " + errorMessage);

                IPopup errorPopup = popupFactory.createPopup(PopupFactory.WARNING_POPUP);
                errorPopup.setMessage(errorMessage);
                ((WarningPopup) errorPopup).getResult();
            }
        }
    }

    private void handleLoadRuleFile(CustomButton button) {
        FileChooser chooser = new FileChooser();
        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Rule files", "*.rul")
        );

        File file = chooser.showOpenDialog(button.getScene().getWindow());
        if (file != null) {
            try {
                model.addLogEntry("Starting to load rule file: " + file.getName());
                model.getRuleModel().loadRuleFile(file);
                model.addLogEntry("Rule file loaded successfully: " + file.getName());

                // Notification de succès
                IPopup successPopup = popupFactory.createPopup(PopupFactory.TOAST_NOTIFICATION);
                successPopup.setMessage("Rule file '" + file.getName() + "' has been successfully loaded!");
                successPopup.displayPopup();

            } catch (Exception e) {
                logger.log(Level.SEVERE, "Error loading rule file", e);
                String errorMessage = "Error loading rule file: " + e.getMessage();
                model.addLogEntry("ERROR: " + errorMessage);

                IPopup errorPopup = popupFactory.createPopup(PopupFactory.WARNING_POPUP);
                errorPopup.setMessage(errorMessage);
                ((WarningPopup) errorPopup).getResult();
            }
        }
    }

    public CustomButton getButton(ButtonType type) {
        return buttons.get(type);
    }
}