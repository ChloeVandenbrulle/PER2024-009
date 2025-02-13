package fr.inria.corese.demo.view.popup;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import java.util.Optional;

public class ClearGraphConfirmationPopup implements IPopup {
    private Alert alert;
    private boolean result;

    public ClearGraphConfirmationPopup() {
        alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Clear Graph Confirmation");
        alert.setHeaderText(null);
    }

    @Override
    public void closePopup() {
        alert.hide();
    }

    @Override
    public void displayPopup() {
        Optional<ButtonType> response = alert.showAndWait();
        result = response.isPresent() && response.get() == ButtonType.OK;
    }

    @Override
    public String getPopupTitle() {
        return "Clear Graph Confirmation";
    }

    @Override
    public void setMessage(String message) {
        alert.setContentText(message);
    }

    public boolean getResult() {
        return result;
    }
}