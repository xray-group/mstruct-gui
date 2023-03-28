package cz.kfkl.mstruct.gui.model;

import cz.kfkl.mstruct.gui.ui.ParametersController;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class ParametersModel implements FxmlFileNameProvider<ParametersController> {

	private static final String FXML_FILE_NAME = "parameters.fxml";

	public IntegerProperty selectedParametersCount = new SimpleIntegerProperty(0);

	@Override
	public String getFxmlFileName() {
		return FXML_FILE_NAME;
	}

}
