package cz.kfkl.mstruct.gui.ui;

import cz.kfkl.mstruct.gui.model.ReflectionProfilePVoigtAElement;
import cz.kfkl.mstruct.gui.utils.BindingUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

public class ReflectionProfilePVoigtAController
		extends BaseController<ReflectionProfilePVoigtAElement, PowderPatternCrystalController> {

	@FXML
	private TextField componentNameTextField;
	@FXML
	private Label componentTypeLabel;

	@FXML
	private HBox uParContainer;
	@FXML
	private HBox vParContainer;
	@FXML
	private HBox wParContainer;
	@FXML
	private HBox eta0ParContainer;
	@FXML
	private HBox eta1ParContainer;
	@FXML
	private HBox asym0ParContainer;
	@FXML
	private HBox asym1ParContainer;
	@FXML
	private HBox asym2ParContainer;

	@Override
	public void init() {
		ReflectionProfilePVoigtAElement model = getModelInstance();

		componentNameTextField.textProperty().bindBidirectional(model.getNameProperty());
		componentTypeLabel.textProperty().set(model.getType().toString());
		BindingUtils.doWhenFocuseLost(componentNameTextField, () -> getParentController().componentNameChanged());

		BindingUtils.bindAndBuildParFieldsNoName(uParContainer, model.uPar);
		BindingUtils.bindAndBuildParFieldsNoName(vParContainer, model.vPar);
		BindingUtils.bindAndBuildParFieldsNoName(wParContainer, model.wPar);
		BindingUtils.bindAndBuildParFieldsNoName(eta0ParContainer, model.eta0Par);
		BindingUtils.bindAndBuildParFieldsNoName(eta1ParContainer, model.eta1Par);
		BindingUtils.bindAndBuildParFieldsNoName(asym0ParContainer, model.asym0Par);
		BindingUtils.bindAndBuildParFieldsNoName(asym1ParContainer, model.asym1Par);
		BindingUtils.bindAndBuildParFieldsNoName(asym2ParContainer, model.asym2Par);
	}

}
