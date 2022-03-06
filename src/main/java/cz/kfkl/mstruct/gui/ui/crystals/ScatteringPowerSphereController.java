package cz.kfkl.mstruct.gui.ui.crystals;

import static cz.kfkl.mstruct.gui.utils.BindingUtils.bindAndBuildParFieldsNoName;

import cz.kfkl.mstruct.gui.model.crystals.ScatteringPowerSphereElement;
import javafx.fxml.FXML;
import javafx.scene.layout.HBox;

public class ScatteringPowerSphereController extends ScatteringPowerCommonController<ScatteringPowerSphereElement> {

	@FXML
	private HBox radiusParContainer;

	@Override
	public void init() {
		super.init();
		ScatteringPowerSphereElement model = getModelInstance();

		bindAndBuildParFieldsNoName(radiusParContainer, model.radiusPar);
	}

}
