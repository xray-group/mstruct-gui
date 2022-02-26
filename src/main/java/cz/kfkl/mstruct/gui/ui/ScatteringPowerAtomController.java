package cz.kfkl.mstruct.gui.ui;

import cz.kfkl.mstruct.gui.model.ScatteringPowerAtomElement;
import cz.kfkl.mstruct.gui.utils.BindingUtils;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

public class ScatteringPowerAtomController extends ScatteringPowerCommonController<ScatteringPowerAtomElement> {

	@FXML
	private TextField scatteringPowerSymbolTextField;

	@FXML
	private HBox mlErrorParContainer;
	@FXML
	private HBox mlNbGhostParContainer;
	@FXML
	private HBox formalChargeParContainer;

	@Override
	public void init() {
		super.init();

		ScatteringPowerAtomElement model = getModelInstance();

		scatteringPowerSymbolTextField.textProperty().bindBidirectional(model.symbolProperty);
		BindingUtils.doWhenFocuseLost(scatteringPowerSymbolTextField, () -> getParentController().scatteringPowerRecordChanged());

		BindingUtils.bindAndBuildParFieldsNoName(mlErrorParContainer, model.mlErrorPar);
		BindingUtils.bindAndBuildParFieldsNoName(mlNbGhostParContainer, model.mlNbGhostPar);
		BindingUtils.bindAndBuildParFieldsNoName(formalChargeParContainer, model.formalChargePar);
	}

}
