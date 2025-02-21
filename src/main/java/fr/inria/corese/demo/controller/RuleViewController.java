package fr.inria.corese.demo.controller;

import fr.inria.corese.demo.enums.icon.IconButtonType;
import fr.inria.corese.demo.model.ProjectDataModel;
import fr.inria.corese.demo.model.RuleModel;
import fr.inria.corese.demo.view.icon.IconButtonView;
import fr.inria.corese.demo.view.rule.RuleItem;
import fr.inria.corese.demo.view.rule.RuleView;
import fr.inria.corese.demo.factory.popup.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import java.io.File;
import java.util.*;

/**
 * Contrôleur de gestion des règles dans une application web sémantique.
 *
 * Responsabilités principales :
 * - Gestion de l'interface utilisateur pour les règles RDFS et OWL
 * - Chargement et configuration des règles prédéfinies et personnalisées
 * - Interaction avec le modèle de règles
 *
 * Fonctionnalités clés :
 * - Initialisation des règles prédéfinies
 * - Chargement de fichiers de règles
 * - Activation/désactivation des règles
 * - Gestion des règles personnalisées
 *
 * Types de règles gérés :
 * - Règles RDFS (Subset, RL)
 * - Règles OWL (RL, RL Extended, RL Test, Clean)
 * - Règles personnalisées
 *
 * @author Clervie Causer
 * @version 1.0
 * @since 2025
 */
public class RuleViewController {
    private RuleModel ruleModel;
    private ProjectDataModel projectDataModel;
    private RuleView view;
    private Map<String, RuleItem> ruleItems;
    private PopupFactory popupFactory;

    @FXML
    private VBox rdfsRulesContainer;
    @FXML
    private VBox owlRulesContainer;
    @FXML
    private VBox customRulesContainer;

    /**
     * Constructeur par défaut du contrôleur de règles.
     *
     * Initialise :
     * - Le modèle de règles
     * - La vue des règles
     * - La map des éléments de règles
     */
    public RuleViewController() {
        this.ruleModel = new RuleModel();
        this.view = new RuleView();
        this.ruleItems = new HashMap<>();
    }

    /**
     * Injecte les dépendances nécessaires après la construction.
     *
     * @param projectDataModel Le modèle de données du projet
     * @param ruleModel Le modèle de règles
     */
    public void injectDependencies(ProjectDataModel projectDataModel, RuleModel ruleModel) {
        this.projectDataModel = projectDataModel;
        this.ruleModel = ruleModel;
        this.popupFactory = PopupFactory.getInstance(projectDataModel);
    }

    /**
     * Initialise l'interface utilisateur des règles.
     *
     * Actions principales :
     * - Création de la vue des règles
     * - Initialisation des règles prédéfinies
     * - Configuration du conteneur de règles personnalisées
     */
    @FXML
    public void initialize() {
        view = new RuleView();
        initializeRules();

        if (customRulesContainer != null) {
            Label noRulesLabel = new Label("No custom rules loaded");
            noRulesLabel.setStyle("-fx-font-style: italic; -fx-text-fill: #888888;");
            customRulesContainer.getChildren().clear();
            customRulesContainer.getChildren().add(noRulesLabel);
        }
    }

    /**
     * Gère le chargement d'un fichier de règles.
     *
     * Workflow :
     * 1. Ouverture d'un sélecteur de fichiers (.rul)
     * 2. Chargement du fichier de règles
     * 3. Journalisation de l'action
     * 4. Affichage de notifications de succès ou d'erreur
     */
    @FXML
    public void handleLoadRuleFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Rule files (*.rul)", "*.rul")
        );

        File selectedFile = fileChooser.showOpenDialog(rdfsRulesContainer.getScene().getWindow());
        if (selectedFile != null) {
            try {
                // Ajout d'un log pour suivre le chargement
                if (projectDataModel != null) {
                    projectDataModel.addLogEntry("Starting to load rule file: " + selectedFile.getName());
                }

                // Chargement de la règle via le RuleModel
                ruleModel.loadRuleFile(selectedFile);

                // Message de succès dans les logs
                if (projectDataModel != null) {
                    projectDataModel.addLogEntry("Rule file loaded successfully: " + selectedFile.getName());
                }

                // Afficher une notification de succès
                if (popupFactory != null) {
                    IPopup successPopup = popupFactory.createPopup(PopupFactory.TOAST_NOTIFICATION);
                    successPopup.setMessage("Rule file '" + selectedFile.getName() + "' has been successfully loaded!");
                    successPopup.displayPopup();
                } else {
                    // Fallback si popupFactory n'est pas disponible
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Success");
                    alert.setHeaderText(null);
                    alert.setContentText("Rule file '" + selectedFile.getName() + "' has been successfully loaded!");
                    alert.showAndWait();
                }

                // Mettre à jour l'affichage
                updateView();

            } catch (Exception e) {
                String errorMessage = "Error loading rule file: " + e.getMessage();

                if (projectDataModel != null) {
                    projectDataModel.addLogEntry("ERROR: " + errorMessage);
                }

                // Afficher une popup d'erreur
                if (popupFactory != null) {
                    IPopup errorPopup = popupFactory.createPopup(PopupFactory.WARNING_POPUP);
                    errorPopup.setMessage(errorMessage);
                    ((WarningPopup) errorPopup).getResult();
                } else {
                    // Fallback si popupFactory n'est pas disponible
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText("Error Loading Rule");
                    alert.setContentText(errorMessage);
                    alert.showAndWait();
                }
            }
        }
    }

    /**
     * Initialise les règles prédéfinies.
     *
     * Configure :
     * - Les règles RDFS
     * - Les règles OWL
     */
    public void initializeRules() {
        if (rdfsRulesContainer != null && owlRulesContainer != null) {
            rdfsRulesContainer.getChildren().clear();
            owlRulesContainer.getChildren().clear();

            // RDFS rules
            addRuleItem(rdfsRulesContainer, "RDFS Subset", true);
            addRuleItem(rdfsRulesContainer, "RDFS RL", true);

            // OWL rules
            addRuleItem(owlRulesContainer, "OWL RL", true);
            addRuleItem(owlRulesContainer, "OWL RL Extended", true);
            addRuleItem(owlRulesContainer, "OWL RL Test", true);
            addRuleItem(owlRulesContainer, "OWL Clean", true);
        }
    }

    /**
     * Met à jour la vue des règles.
     *
     * Actions :
     * - Mise à jour des états des règles prédéfinies
     * - Affichage des règles personnalisées
     */
    void updateView() {
        // Mettre à jour les états des règles RDFS et OWL
        updatePredefinedRuleStates();

        if (ruleModel != null) {
            List<String> customRules = new ArrayList<>(ruleModel.getLoadedRules());
            // Filtrer pour ne garder que les règles personnalisées
            customRules.removeIf(rule ->
                    rule.equals("RDFS Subset") ||
                            rule.equals("RDFS RL") ||
                            rule.equals("OWL RL") ||
                            rule.equals("OWL RL Extended") ||
                            rule.equals("OWL RL Test") ||
                            rule.equals("OWL Clean"));

            // Afficher les règles personnalisées
            displayCustomRules(customRules);
        }
    }

    /**
     * Met à jour l'état des règles prédéfinies.
     *
     * Synchronise l'état des cases à cocher avec le modèle de règles.
     */
    private void updatePredefinedRuleStates() {
        for (Map.Entry<String, RuleItem> entry : ruleItems.entrySet()) {
            String ruleName = entry.getKey();
            RuleItem item = entry.getValue();

            boolean isEnabled = switch (ruleName) {
                case "RDFS Subset" -> ruleModel.isRDFSSubsetEnabled();
                case "RDFS RL" -> ruleModel.isRDFSRLEnabled();
                case "OWL RL" -> ruleModel.isOWLRLEnabled();
                case "OWL RL Extended" -> ruleModel.isOWLRLExtendedEnabled();
                case "OWL RL Test" -> ruleModel.isOWLRLTestEnabled();
                case "OWL Clean" -> ruleModel.isOWLCleanEnabled();
                default -> false;
            };

            item.getCheckBox().setSelected(isEnabled);
        }
    }

    /**
     * Ajoute un élément de règle à un conteneur.
     *
     * @param container Le conteneur JavaFX pour l'élément de règle
     * @param ruleName Le nom de la règle
     * @param predefined Indique si la règle est prédéfinie
     */
    private void addRuleItem(VBox container, String ruleName, boolean predefined) {
        RuleItem ruleItem = new RuleItem(ruleName);
        ruleItems.put(ruleName, ruleItem);

        ruleItem.getDocumentationButton().setOnAction(e -> handleShowDocumentation(ruleName));
        ruleItem.getCheckBox().setOnAction(e -> handleRuleToggle(ruleName, ruleItem.getCheckBox().isSelected()));

        container.getChildren().add(ruleItem);
    }

    /**
     * Affiche la documentation d'une règle.
     *
     * @param ruleName Le nom de la règle dont la documentation doit être affichée
     */
    private void handleShowDocumentation(String ruleName) {
        //TODO: open documentation with external link
    }

    /**
     * Gère l'activation/désactivation d'une règle.
     *
     * @param ruleName Le nom de la règle
     * @param selected L'état d'activation de la règle
     */
    private void handleRuleToggle(String ruleName, boolean selected) {
        switch (ruleName) {
            case "RDFS Subset":
                ruleModel.setRDFSSubsetEnabled(selected);
                break;
            case "RDFS RL":
                ruleModel.setRDFSRLEnabled(selected);
                break;
            case "OWL RL":
                ruleModel.setOWLRLEnabled(selected);
                break;
            case "OWL RL Extended":
                ruleModel.setOWLRLExtendedEnabled(selected);
                break;
            case "OWL RL Test":
                ruleModel.setOWLRLTestEnabled(selected);
                break;
            case "OWL Clean":
                ruleModel.setOWLCleanEnabled(selected);
                break;
        }
        updateView();
    }

    /**
     * Affiche les règles personnalisées.
     *
     * @param customRules Liste des règles personnalisées à afficher
     */
    private void displayCustomRules(List<String> customRules) {
        // Vérifier que le conteneur existe
        if (customRulesContainer == null) {
            System.err.println("Custom rules container not found in FXML");
            return;
        }

        // Effacer le conteneur
        customRulesContainer.getChildren().clear();

        if (customRules.isEmpty()) {
            // Afficher un message si aucune règle personnalisée n'est chargée
            Label noRulesLabel = new Label("No custom rules loaded");
            noRulesLabel.setStyle("-fx-font-style: italic; -fx-text-fill: #888888;");
            customRulesContainer.getChildren().add(noRulesLabel);
        } else {
            // Ajouter chaque règle personnalisée au conteneur
            for (String ruleName : customRules) {
                RuleItem ruleItem = new RuleItem(ruleName);

                // Configurer le checkbox pour qu'il soit toujours coché
                ruleItem.getCheckBox().setSelected(true);

                // Transformer le bouton de documentation en bouton de suppression
                Button deleteButton = ruleItem.getDocumentationButton();
                // Changer l'icône du bouton pour un icône de suppression
                IconButtonView deleteIconButton = new IconButtonView(IconButtonType.DELETE);

                // Si le bouton original est un IconButtonView, on peut simplement changer son type
                if (deleteButton instanceof IconButtonView) {
                    ((IconButtonView) deleteButton).setType(IconButtonType.DELETE);
                } else {
                    // Sinon, on remplace le bouton existant par notre nouveau bouton
                    HBox parent = (HBox) deleteButton.getParent();
                    int index = parent.getChildren().indexOf(deleteButton);
                    parent.getChildren().remove(deleteButton);
                    parent.getChildren().add(index, deleteIconButton);
                    deleteButton = deleteIconButton;
                }

                // Configurer l'action du bouton de suppression
                deleteButton.setOnAction(e -> handleDeleteCustomRule(ruleName));

                // Ajouter l'élément de règle au conteneur
                customRulesContainer.getChildren().add(ruleItem);
            }
        }
    }

    /**
     * Gère la suppression d'une règle personnalisée.
     *
     * @param ruleName Le nom de la règle à supprimer
     */
    private void handleDeleteCustomRule(String ruleName) {
        // Confirmation avant suppression
        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Delete Rule");
        confirmDialog.setHeaderText("Delete Custom Rule");
        confirmDialog.setContentText("Are you sure you want to delete the rule: " + ruleName + "?");

        Optional<ButtonType> result = confirmDialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                // Supprimer la règle du modèle
                ruleModel.removeRule(ruleName);

                // Mettre à jour le compte de règles dans le modèle de projet si nécessaire
                if (projectDataModel != null) {
                    int ruleCount = ruleModel.getLoadedRulesCount();
                    projectDataModel.setRulesLoadedCount(ruleCount);
                }

                // Mettre à jour l'affichage
                updateView();

                // Notification de succès
                Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                successAlert.setTitle("Rule Deleted");
                successAlert.setHeaderText(null);
                successAlert.setContentText("Rule '" + ruleName + "' has been deleted successfully.");
                successAlert.showAndWait();
            } catch (Exception ex) {
                // Notification d'erreur en cas de problème
                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                errorAlert.setTitle("Error");
                errorAlert.setHeaderText("Error Deleting Rule");
                errorAlert.setContentText("Failed to delete rule: " + ex.getMessage());
                errorAlert.showAndWait();
            }
        }
    }
}