package cz.kfkl.mstruct.gui.model;

import cz.kfkl.mstruct.gui.ui.ObjCrystModel;
import cz.kfkl.mstruct.gui.ui.OptimizationController;

public class OptimizaitonModel implements FxmlFileNameProvider<OptimizationController> {

	private static final String FXML_FILE_NAME = "optimization.fxml";

	// may be null if the file is not loaded
	private ObjCrystModel rootModel;

	public OptimizaitonModel() {
	}

	public OptimizaitonModel(ObjCrystModel rootModel) {
		this.rootModel = rootModel;
	}

	public ObjCrystModel getRootModel() {
		return rootModel;
	}

	public void setRootModel(ObjCrystModel rootModel) {
		this.rootModel = rootModel;
	}

	@Override
	public String getFxmlFileName() {
		return FXML_FILE_NAME;
	}

}
