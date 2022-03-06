package cz.kfkl.mstruct.gui.ui;

import static cz.kfkl.mstruct.gui.utils.BindingUtils.bindAndBuildParFieldsNoName;
import static cz.kfkl.mstruct.gui.utils.BindingUtils.doWhenFocuseLost;

import cz.kfkl.mstruct.gui.model.ReflectionProfilePVoigtAElement;
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
		doWhenFocuseLost(componentNameTextField, () -> getParentController().componentNameChanged());

		bindAndBuildParFieldsNoName(uParContainer, model.uPar);
		bindAndBuildParFieldsNoName(vParContainer, model.vPar);
		bindAndBuildParFieldsNoName(wParContainer, model.wPar);
		bindAndBuildParFieldsNoName(eta0ParContainer, model.eta0Par);
		bindAndBuildParFieldsNoName(eta1ParContainer, model.eta1Par);
		bindAndBuildParFieldsNoName(asym0ParContainer, model.asym0Par);
		bindAndBuildParFieldsNoName(asym1ParContainer, model.asym1Par);
		bindAndBuildParFieldsNoName(asym2ParContainer, model.asym2Par);
	}

}
