package fr.inria.corese.demo.view;

import atlantafx.base.controls.Spacer;
import atlantafx.base.theme.Styles;
import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.scene.layout.HBox;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Tooltip;
import org.kordamp.ikonli.feather.Feather;
import org.kordamp.ikonli.javafx.FontIcon;

public class RuleItem extends HBox {
    private final CheckBox checkBox;
    private final MFXButton duplicateButton;
    private final MFXButton documentationButton;

    public RuleItem(String ruleName) {
        super(5); // spacing between elements

        // Create checkbox with rule name
        checkBox = new CheckBox(ruleName);

        // Create flexible space between checkbox and buttons
        Spacer spacer = new Spacer();

        // Create buttons with Feather icons
        duplicateButton = createIconButton(Feather.COPY, "Duplicate rule");
        documentationButton = createIconButton(Feather.BOOK_OPEN, "View documentation");

        // Style buttons
        duplicateButton.getStyleClass().add(Styles.BUTTON_ICON);
        documentationButton.getStyleClass().add(Styles.BUTTON_ICON);

        // Add all elements to the HBox
        getChildren().addAll(checkBox, spacer, duplicateButton, documentationButton);
    }

    private MFXButton createIconButton(Feather icon, String tooltip) {
        MFXButton button = new MFXButton();
        FontIcon fontIcon = new FontIcon(icon);
        fontIcon.getStyleClass().add(Styles.SMALL);
        button.setGraphic(fontIcon);
        button.setTooltip(new Tooltip(tooltip));
        button.getStyleClass().add(Styles.FLAT);
        return button;
    }

    public CheckBox getCheckBox() {
        return checkBox;
    }

    public MFXButton getDuplicateButton() {
        return duplicateButton;
    }

    public MFXButton getDocumentationButton() {
        return documentationButton;
    }
}