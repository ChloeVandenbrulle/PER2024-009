package fr.inria.corese.demo.view.rule;

import javafx.scene.layout.VBox;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.File;
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

    /**
     * Constructeur par défaut.
     *
     * Initialise :
     * - Une liste observable des éléments de règles
     * - Un map des règles prédéfinies
     * - Ajout des règles prédéfinies
     */
    public RuleView() {
        ruleItems = FXCollections.observableArrayList();
    }
}