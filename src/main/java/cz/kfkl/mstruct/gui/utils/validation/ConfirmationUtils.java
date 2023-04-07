package cz.kfkl.mstruct.gui.utils.validation;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;

public class ConfirmationUtils {
	public static void confirmAction(String message, Runnable action) {
		Alert alert = new Alert(AlertType.CONFIRMATION, null);
		alert.setHeaderText(message);
		alert.showAndWait();
		ButtonType result = alert.getResult();
		if (result == ButtonType.OK) {
			action.run();
		}
	}
}
