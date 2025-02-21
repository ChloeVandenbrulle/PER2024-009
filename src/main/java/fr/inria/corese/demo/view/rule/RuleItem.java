package fr.inria.corese.demo.view.rule;

import atlantafx.base.controls.Spacer;
import fr.inria.corese.demo.enums.icon.IconButtonType;
import fr.inria.corese.demo.view.icon.IconButtonView;
import javafx.scene.layout.HBox;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Button;

/**
 * Représente un élément de règle dans l'interface utilisateur.
 *
 * Cette classe définit la structure visuelle d'un élément de règle,
 * comprenant une case à cocher et un bouton de documentation.
 *
 * Caractéristiques principales :
 * - Disposition horizontale (HBox)
 * - Case à cocher avec le nom de la règle
 * - Bouton de documentation
 *
 * @author Clervie Causer
 * @version 1.0
 * @since 2025
 */
public class RuleItem extends HBox {
    private final CheckBox checkBox;
    private final IconButtonView documentationButton;

    /**
     * Constructeur d'un élément de règle.
     *
     * Crée un élément de règle avec :
     * - Une case à cocher portant le nom de la règle
     * - Un espace flexible
     * - Un bouton de documentation
     *
     * @param ruleName Le nom de la règle à afficher
     */
    public RuleItem(String ruleName) {
        super(5); // spacing between elements

        // Create checkbox with rule name
        checkBox = new CheckBox(ruleName);

        // Create flexible space between checkbox and buttons
        Spacer spacer = new Spacer();

        // Create documentation button using IconButtonView
        documentationButton = new IconButtonView(IconButtonType.DOCUMENTATION);

        // Add all elements to the HBox
        getChildren().addAll(checkBox, spacer, documentationButton);
    }

    /**
     * Récupère la case à cocher de l'élément de règle.
     *
     * @return La case à cocher associée à la règle
     */
    public CheckBox getCheckBox() {
        return checkBox;
    }

    /**
     * Récupère le bouton de documentation de l'élément de règle.
     *
     * @return Le bouton de documentation
     */
    public Button getDocumentationButton() {
        return documentationButton;
    }
}