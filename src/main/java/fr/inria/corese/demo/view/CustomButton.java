package fr.inria.corese.demo.view;

import fr.inria.corese.demo.model.ButtonType;
import javafx.scene.control.Button;
import java.util.function.Consumer;

public class CustomButton extends Button {
    private ButtonType type;
    private Consumer<CustomButton> onClick;

    public static class Builder {
        private final ButtonType type;
        private String customStyle = null;
        private Consumer<CustomButton> onClick = null;
        private boolean disabled = false;
        private String tooltip = null;

        public Builder(ButtonType type) {
            this.type = type;
        }

        public Builder withStyle(String style) {
            this.customStyle = style;
            return this;
        }

        public Builder withOnClick(Consumer<CustomButton> onClick) {
            this.onClick = onClick;
            return this;
        }

        public Builder withDisabled(boolean disabled) {
            this.disabled = disabled;
            return this;
        }

        public Builder withTooltip(String tooltip) {
            this.tooltip = tooltip;
            return this;
        }

        public CustomButton build() {
            CustomButton button = new CustomButton(type);
            if (customStyle != null) button.getStyleClass().add(customStyle);
            if (onClick != null) button.setOnClick(onClick);
            button.setDisable(disabled);
            if (tooltip != null) button.setTooltip(tooltip);
            return button;
        }
    }

    private CustomButton(ButtonType type) {
        super(type.getLabel());
        this.type = type;
        setupStyle();
    }

    private void setupStyle() {
        getStyleClass().addAll("custom-button", type.getStyleClass());
    }

    public void setOnClick(Consumer<CustomButton> onClick) {
        this.onClick = onClick;
        setOnAction(e -> onClick.accept(this));
    }

    public ButtonType getType() {
        return type;
    }

    public void setTooltip(String text) {
        setTooltip(new javafx.scene.control.Tooltip(text));
    }
}