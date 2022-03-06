package cz.kfkl.mstruct.gui.ui;

import static cz.kfkl.mstruct.gui.utils.BindingUtils.bindAndBuildParFieldsNoName;
import static cz.kfkl.mstruct.gui.utils.BindingUtils.bindAndBuildRadioButtonsOption;
import static cz.kfkl.mstruct.gui.utils.BindingUtils.doWhenFocuseLost;

import cz.kfkl.mstruct.gui.model.ReflectionProfileRefractionCorrElement;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

public class ReflectionProfileRefractionCorrController
		extends BaseController<ReflectionProfileRefractionCorrElement, PowderPatternCrystalController> {

	@FXML
	private TextField componentNameTextField;
	@FXML
	private Label componentTypeLabel;

	@FXML
	private HBox chi0SourceOptionContainer;
	@FXML
	private HBox relDensityParContainer;

	@Override
	public void init() {
		ReflectionProfileRefractionCorrElement model = getModelInstance();

		componentNameTextField.textProperty().bindBidirectional(model.getNameProperty());
		componentTypeLabel.textProperty().set(model.getType().toString());
		doWhenFocuseLost(componentNameTextField, () -> getParentController().componentNameChanged());

		bindAndBuildRadioButtonsOption(chi0SourceOptionContainer, model.chi0SourceOption);
		bindAndBuildParFieldsNoName(relDensityParContainer, model.relDensityPar);
	}

}
