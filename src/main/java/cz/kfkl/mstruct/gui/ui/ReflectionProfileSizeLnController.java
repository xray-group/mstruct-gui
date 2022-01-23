package cz.kfkl.mstruct.gui.ui;

import cz.kfkl.mstruct.gui.model.ReflectionProfileSizeLnElement;
import cz.kfkl.mstruct.gui.utils.BindingUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

public class ReflectionProfileSizeLnController
		extends BaseController<ReflectionProfileSizeLnElement, PowderPatternCrystalController> {

	@FXML
	private TextField componentNameTextField;
	@FXML
	private Label componentTypeLabel;

	@FXML
	private HBox mParContainer;
	@FXML
	private HBox sigmaParContainer;

	@Override
	public void init() {
		ReflectionProfileSizeLnElement model = getModelInstance();

		componentNameTextField.textProperty().bindBidirectional(model.getNameProperty());
		componentTypeLabel.textProperty().set(model.getType().toString());
		BindingUtils.doWhenFocuseLost(componentNameTextField, () -> getParentController().componentNameChanged());

		BindingUtils.bindAndBuildParFieldsNoName(mParContainer, model.mPar);
		BindingUtils.bindAndBuildParFieldsNoName(sigmaParContainer, model.sigmaPar);
	}

}
