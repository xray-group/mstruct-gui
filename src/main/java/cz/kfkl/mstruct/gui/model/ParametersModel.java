package cz.kfkl.mstruct.gui.model;

import cz.kfkl.mstruct.gui.ui.ParametersController;

public class ParametersModel implements FxmlFileNameProvider<ParametersController> {

	private static final String FXML_FILE_NAME = "parameters.fxml";

	@Override
	public String getFxmlFileName() {
		return FXML_FILE_NAME;
	}

}
