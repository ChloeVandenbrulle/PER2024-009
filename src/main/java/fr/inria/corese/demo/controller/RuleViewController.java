package fr.inria.corese.demo.controller;

import fr.inria.corese.demo.enums.icon.IconButtonType;
import fr.inria.corese.demo.manager.ApplicationStateManager;
import fr.inria.corese.demo.model.ProjectDataModel;
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
 * Contrôleur pour la gestion des règles dans l'application.
 *
 * Responsable de l'affichage et de la gestion des règles prédéfinies et personnalisées,
 * ainsi que de leur interaction avec le modèle de données et le gestionnaire d'état global.
 */
public class RuleViewController {
    private ProjectDataModel projectDataModel;
    private ApplicationStateManager stateManager;
    private RuleView view;
    private Map<String, RuleItem> ruleItems;
    private PopupFactory popupFactory;
    private Runnable owlRLAction;
    private Runnable owlRLExtendedAction;

    @FXML
    private VBox rdfsRulesContainer;
    @FXML
    private VBox owlRulesContainer;
    @FXML
    private VBox customRulesContainer;
    @FXML
    private Button loadRuleButton;

    /**
     * Constructeur par défaut.
     * Initialise les composants de base du contrôleur.
     */
    public RuleViewController() {
        this.view = new RuleView();
        this.ruleItems = new HashMap<>();
        this.stateManager = ApplicationStateManager.getInstance();
    }

    /**
     * Injecte les dépendances nécessaires après la construction.
     * Maintenant uniquement ProjectDataModel car toutes les fonctions
     * de règles sont intégrées dedans.
     *
     * @param projectDataModel Le modèle de données du projet
     */
    public void injectDependencies(ProjectDataModel projectDataModel) {
        this.projectDataModel = projectDataModel;
        this.popupFactory = PopupFactory.getInstance(projectDataModel);
    }

    /**
     * Initialise les composants du contrôleur après le chargement FXML.
     */
    @FXML
    public void initialize() {
        view = new RuleView();
        stateManager = ApplicationStateManager.getInstance();
        initializeRules();

        if (customRulesContainer != null) {
            Label noRulesLabel = new Label("No custom rules loaded");
            noRulesLabel.setStyle("-fx-font-style: italic; -fx-text-fill: #888888;");
            customRulesContainer.getChildren().clear();
            customRulesContainer.getChildren().add(noRulesLabel);
        }

        if (loadRuleButton != null) {
            loadRuleButton.setOnAction(e -> handleLoadRuleFile());
        }
    }

    /**
     * Gère le chargement d'un fichier de règles.
     * Affiche un dialogue de sélection de fichier et charge la règle sélectionnée.
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
                // Ajouter un log pour suivre le chargement
                if (projectDataModel != null) {
                    projectDataModel.addLogEntry("Starting to load rule file: " + selectedFile.getName());
                }

                // Chargement de la règle via ApplicationStateManager pour assurer la cohérence
                stateManager.processLoadedRuleFile(selectedFile);

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
     * Initialise les items de règles prédéfinies.
     * Crée et configure les items pour les règles RDFS et OWL.
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

        // Charger l'état à partir du gestionnaire d'état
        updateView();
    }

    /**
     * Met à jour l'affichage des règles en fonction de l'état actuel.
     * Synchronise l'interface utilisateur avec l'état des règles dans le gestionnaire d'état.
     */
    public void updateView() {
        // Mettre à jour les états des règles RDFS et OWL
        updatePredefinedRuleStates();

        if (customRulesContainer != null) {
            // Récupérer les règles personnalisées via ApplicationStateManager
            ArrayList<File> customRules = new ArrayList<>(stateManager.getLoadedRuleFiles());

            // Filtrer pour ne garder que les règles personnalisées
            customRules.removeIf(rule ->
                    rule.getName().equals("RDFS Subset") ||
                            rule.getName().equals("RDFS RL") ||
                            rule.getName().equals("OWL RL") ||
                            rule.getName().equals("OWL RL Extended") ||
                            rule.getName().equals("OWL RL Test") ||
                            rule.getName().equals("OWL Clean"));

            // Afficher les règles personnalisées
            displayCustomRules(customRules);
        }
    }

    /**
     * Met à jour l'état visuel des règles prédéfinies.
     * Synchronise l'état des cases à cocher avec l'état du gestionnaire d'état.
     */
    private void updatePredefinedRuleStates() {
        for (Map.Entry<String, RuleItem> entry : ruleItems.entrySet()) {
            String ruleName = entry.getKey();
            RuleItem item = entry.getValue();

            // Utiliser ApplicationStateManager pour récupérer l'état des règles
            boolean isEnabled = switch (ruleName) {
                case "RDFS Subset" -> stateManager.isRDFSSubsetEnabled();
                case "RDFS RL" -> stateManager.isRDFSRLEnabled();
                case "OWL RL" -> stateManager.isOWLRLEnabled();
                case "OWL RL Extended" -> stateManager.isOWLRLExtendedEnabled();
                case "OWL RL Test" -> stateManager.isOWLRLTestEnabled();
                case "OWL Clean" -> stateManager.isOWLCleanEnabled();
                default -> stateManager.isCustomRuleEnabled(ruleName);
            };

            item.getCheckBox().setSelected(isEnabled);
        }
    }

    /**
     * Ajoute un item de règle au conteneur spécifié.
     *
     * @param container Le conteneur auquel ajouter l'item
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
     * Gère l'affichage de la documentation pour une règle spécifique.
     *
     * @param ruleName Le nom de la règle
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
        // Utiliser le gestionnaire d'état pour maintenir la cohérence entre les vues
        ApplicationStateManager stateManager = ApplicationStateManager.getInstance();

        switch (ruleName) {
            case "RDFS Subset":
                stateManager.setRDFSSubsetEnabled(selected);
                break;
            case "RDFS RL":
                stateManager.setRDFSRLEnabled(selected);
                break;
            case "OWL RL":
                if (selected && owlRLAction != null) {
                    owlRLAction.run();
                }
                stateManager.setOWLRLEnabled(selected);
                // Sauvegarder l'état actuel pour le partager entre les vues
                stateManager.saveCurrentState();
                break;
            case "OWL RL Extended":
                if (selected && owlRLExtendedAction != null) {
                    owlRLExtendedAction.run();
                }
                stateManager.setOWLRLExtendedEnabled(selected);
                // Sauvegarder l'état actuel pour le partager entre les vues
                stateManager.saveCurrentState();
                break;
            case "OWL RL Test":
                stateManager.setOWLRLTestEnabled(selected);
                break;
            case "OWL Clean":
                stateManager.setOWLCleanEnabled(selected);
                break;
            default:
                // Gestion des règles personnalisées
                stateManager.setCustomRuleEnabled(ruleName, selected);
                break;
        }

        // Mettre à jour l'interface après le changement d'état
        updateView();
    }

    /**
     * Affiche les règles personnalisées dans le conteneur approprié.
     *
     * @param customRules Liste des fichiers de règles personnalisées
     */
    private void displayCustomRules(ArrayList<File> customRules) {
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
            for (File ruleFile : customRules) {
                RuleItem ruleItem = new RuleItem(ruleFile);
                String ruleName = ruleFile.getName();

                // Ajouter à la map pour pouvoir retrouver l'item plus tard
                ruleItems.put(ruleName, ruleItem);

                // Configurer le checkbox en fonction de l'état dans le gestionnaire d'état
                boolean isEnabled = stateManager.isCustomRuleEnabled(ruleName);
                ruleItem.getCheckBox().setSelected(isEnabled);

                // Configurer le checkbox pour mettre à jour l'état quand il change
                ruleItem.getCheckBox().setOnAction(e ->
                        handleRuleToggle(ruleName, ruleItem.getCheckBox().isSelected()));

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
                final String finalRuleName = ruleName;
                deleteButton.setOnAction(e -> handleDeleteCustomRule(finalRuleName));

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
                // Supprimer la règle via ApplicationStateManager pour assurer la cohérence
                stateManager.setCustomRuleEnabled(ruleName, false); // Désactive d'abord la règle

                // Puis supprimer la règle du modèle de projet
                projectDataModel.removeRule(ruleName);

                // Mettre à jour l'état interne
                stateManager.saveCurrentState();

                // Mettre à jour l'affichage
                updateView();

                // Notification de succès via PopupFactory si disponible
                if (popupFactory != null) {
                    IPopup successPopup = popupFactory.createPopup(PopupFactory.TOAST_NOTIFICATION);
                    successPopup.setMessage("Rule '" + ruleName + "' has been deleted successfully!");
                    successPopup.displayPopup();
                } else {
                    // Fallback vers Alert si PopupFactory n'est pas disponible
                    Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                    successAlert.setTitle("Rule Deleted");
                    successAlert.setHeaderText(null);
                    successAlert.setContentText("Rule '" + ruleName + "' has been deleted successfully.");
                    successAlert.showAndWait();
                }
            } catch (Exception ex) {
                // Notification d'erreur en cas de problème
                if (popupFactory != null) {
                    IPopup errorPopup = popupFactory.createPopup(PopupFactory.WARNING_POPUP);
                    errorPopup.setMessage("Failed to delete rule: " + ex.getMessage());
                    ((WarningPopup) errorPopup).getResult();
                } else {
                    Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                    errorAlert.setTitle("Error");
                    errorAlert.setHeaderText("Error Deleting Rule");
                    errorAlert.setContentText("Failed to delete rule: " + ex.getMessage());
                    errorAlert.showAndWait();
                }
            }
        }
    }

    /**
     * Définit l'action à exécuter lors de l'activation des règles OWL RL.
     *
     * @param action L'action à exécuter
     */
    public void setOWLRLAction(Runnable action) {
        this.owlRLAction = action;
    }

    /**
     * Définit l'action à exécuter lors de l'activation des règles OWL RL Extended.
     *
     * @param action L'action à exécuter
     */
    public void setOWLRLExtendedAction(Runnable action) {
        this.owlRLExtendedAction = action;
    }
}