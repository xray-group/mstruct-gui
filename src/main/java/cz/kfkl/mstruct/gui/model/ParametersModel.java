package cz.kfkl.mstruct.gui.model;

import java.util.Map;
import java.util.Set;

import cz.kfkl.mstruct.gui.ui.ObjCrystModel;
import cz.kfkl.mstruct.gui.ui.ParametersController;
import javafx.beans.property.ObjectProperty;

public class ParametersModel implements FxmlFileNameProvider<ParametersController> {

	private static final String FXML_FILE_NAME = "parameters.fxml";
	private ObjCrystModel rootModel;

	// all parameters as they were parsed from the optimalization output file
	public ObjectProperty<Map<String, ParUniqueElement>> fittedParamsProperty;
	public Set<String> refinedParams;

	public ParametersModel(ObjCrystModel rootModel) {
		this.rootModel = rootModel;
	}

	public ObjCrystModel getRootModel() {
		return rootModel;
	}

	@Override
	public String getFxmlFileName() {
		return FXML_FILE_NAME;
	}

}
