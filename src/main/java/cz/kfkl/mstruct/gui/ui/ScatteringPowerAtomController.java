package cz.kfkl.mstruct.gui.ui;

import cz.kfkl.mstruct.gui.model.ScatteringPowerAtomElement;
import cz.kfkl.mstruct.gui.utils.BindingUtils;
import javafx.fxml.FXML;
import javafx.scene.layout.HBox;

public class ScatteringPowerAtomController extends BaseController<ScatteringPowerAtomElement, CrystalController> {

	@FXML
	private HBox bisoParContainer;
	@FXML
	private HBox mlErrorParContainer;
	@FXML
	private HBox mlNbGhostParContainer;
	@FXML
	private HBox formalChargeParContainer;

	@Override
	public void init() {
		ScatteringPowerAtomElement model = getModelInstance();

		BindingUtils.bindAndBuildParFieldsNoName(bisoParContainer, model.bisoPar);
		BindingUtils.bindAndBuildParFieldsNoName(mlErrorParContainer, model.mlErrorPar);
		BindingUtils.bindAndBuildParFieldsNoName(mlNbGhostParContainer, model.mlNbGhostPar);
		BindingUtils.bindAndBuildParFieldsNoName(formalChargeParContainer, model.formalChargePar);
	}

}
