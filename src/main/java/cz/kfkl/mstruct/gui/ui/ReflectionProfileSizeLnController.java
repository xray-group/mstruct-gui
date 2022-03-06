package cz.kfkl.mstruct.gui.ui;

import static cz.kfkl.mstruct.gui.utils.BindingUtils.bindAndBuildParFieldsNoName;
import static cz.kfkl.mstruct.gui.utils.BindingUtils.doWhenFocuseLost;

import cz.kfkl.mstruct.gui.model.ReflectionProfileSizeLnElement;
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
		doWhenFocuseLost(componentNameTextField, () -> getParentController().componentNameChanged());

		bindAndBuildParFieldsNoName(mParContainer, model.mPar);
		bindAndBuildParFieldsNoName(sigmaParContainer, model.sigmaPar);
	}

}
