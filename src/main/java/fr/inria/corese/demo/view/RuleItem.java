package fr.inria.corese.demo.view;

import atlantafx.base.controls.Spacer;
import fr.inria.corese.demo.factory.IconButtonBarFactory;
import fr.inria.corese.demo.enums.IconButtonType;
import javafx.scene.layout.HBox;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Button;

public class RuleItem extends HBox {
    private final CheckBox checkBox;
    private final Button documentationButton;

    public RuleItem(String ruleName) {
        // Create checkbox with rule name
        checkBox = new CheckBox(ruleName);

        // Create flexible space between checkbox and buttons
        Spacer spacer = new Spacer();

        // Create documentation button using IconButtonBarFactory
        documentationButton = IconButtonBarFactory.createSingleButton(IconButtonType.DOCUMENTATION);

        // Add all elements to the HBox
        getChildren().addAll(checkBox, spacer, documentationButton);
    }

    public CheckBox getCheckBox() {
        return checkBox;
    }

    public Button getDocumentationButton() {
        return documentationButton;
    }
}