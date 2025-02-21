package fr.inria.corese.demo.view.rule;

import javafx.scene.layout.VBox;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.util.HashMap;
import java.util.Map;

/**
 * Vue représentant une liste de règles dans l'interface utilisateur.
 *
 * Cette classe gère l'affichage et l'organisation des règles,
 * incluant les règles prédéfinies et personnalisées.
 *
 * Caractéristiques principales :
 * - Conteneur vertical (VBox) pour les éléments de règles
 * - Gestion d'une liste observable d'éléments de règles
 * - Initialisation des règles prédéfinies
 *
 * @author Clervie Causer
 * @version 1.0
 * @since 2025
 */
public class RuleView extends VBox {
    private final ObservableList<RuleItem> ruleItems;
    private final Map<String, RuleItem> predefinedRuleItems;

    /**
     * Constructeur par défaut.
     *
     * Initialise :
     * - Une liste observable des éléments de règles
     * - Un map des règles prédéfinies
     * - Ajout des règles prédéfinies
     */
    public RuleView() {
        this.predefinedRuleItems = new HashMap<>();

        ruleItems = FXCollections.observableArrayList();

        // Ajouter les règles prédéfinies
        addPredefinedRules();
    }

    /**
     * Ajoute les règles prédéfinies à la vue.
     *
     * Crée des éléments de règle pour un ensemble prédéfini de règles et les ajoute à la map des règles prédéfinies.
     */
    private void addPredefinedRules() {
        String[] predefinedRules = {"Trace", "Load Named", "Graph index"};

        for (String ruleName : predefinedRules) {
            RuleItem ruleItem = new RuleItem(ruleName);
            predefinedRuleItems.put(ruleName, ruleItem);
        }
    }
}