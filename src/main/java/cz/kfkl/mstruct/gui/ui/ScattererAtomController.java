package cz.kfkl.mstruct.gui.ui;

import cz.kfkl.mstruct.gui.model.AtomElement;
import cz.kfkl.mstruct.gui.utils.BindingUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

public class ScattererAtomController extends BaseController<AtomElement, CrystalController> {

	@FXML
	private TextField scattererNameTextField;
	@FXML
	private Label scattererTypeLabel;
	@FXML
	private TextField scaterringPowerTextField;

	@FXML
	private HBox xParContainer;
	@FXML
	private HBox yParContainer;
	@FXML
	private HBox zParContainer;
	@FXML
	private HBox occupParContainer;

	@Override
	public void init() {
		AtomElement model = getModelInstance();

		scattererNameTextField.textProperty().bindBidirectional(model.nameProperty);
		scattererTypeLabel.textProperty().set(model.getType());
		scaterringPowerTextField.textProperty().bindBidirectional(model.scattPowProperty);

		BindingUtils.bindAndBuildParFieldsNoName(xParContainer, model.xPar);
		BindingUtils.bindAndBuildParFieldsNoName(yParContainer, model.yPar);
		BindingUtils.bindAndBuildParFieldsNoName(zParContainer, model.zPar);
		BindingUtils.bindAndBuildParFieldsNoName(occupParContainer, model.occupPar);
	}

}
