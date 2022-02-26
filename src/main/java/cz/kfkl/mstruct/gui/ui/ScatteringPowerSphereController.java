package cz.kfkl.mstruct.gui.ui;

import cz.kfkl.mstruct.gui.model.ScatteringPowerSphereElement;
import cz.kfkl.mstruct.gui.utils.BindingUtils;
import javafx.fxml.FXML;
import javafx.scene.layout.HBox;

public class ScatteringPowerSphereController extends ScatteringPowerCommonController<ScatteringPowerSphereElement> {

	@FXML
	private HBox radiusParContainer;

	@Override
	public void init() {
		super.init();
		ScatteringPowerSphereElement model = getModelInstance();

		BindingUtils.bindAndBuildParFieldsNoName(radiusParContainer, model.radiusPar);
	}

}
