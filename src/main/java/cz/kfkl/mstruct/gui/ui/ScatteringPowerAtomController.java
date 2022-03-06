package cz.kfkl.mstruct.gui.ui;

import static cz.kfkl.mstruct.gui.utils.BindingUtils.bindAndBuildParFieldsNoName;
import static cz.kfkl.mstruct.gui.utils.BindingUtils.doWhenFocuseLost;

import cz.kfkl.mstruct.gui.model.ScatteringPowerAtomElement;
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
		doWhenFocuseLost(scatteringPowerSymbolTextField, () -> getParentController().scatteringPowerRecordChanged());

		bindAndBuildParFieldsNoName(mlErrorParContainer, model.mlErrorPar);
		bindAndBuildParFieldsNoName(mlNbGhostParContainer, model.mlNbGhostPar);
		bindAndBuildParFieldsNoName(formalChargeParContainer, model.formalChargePar);
	}

}
