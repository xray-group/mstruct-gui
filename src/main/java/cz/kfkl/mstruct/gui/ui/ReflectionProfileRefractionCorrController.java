package cz.kfkl.mstruct.gui.ui;

import cz.kfkl.mstruct.gui.model.ReflectionProfileRefractionCorrElement;
import cz.kfkl.mstruct.gui.utils.BindingUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

public class ReflectionProfileRefractionCorrController extends BaseController<ReflectionProfileRefractionCorrElement> {

	@FXML
	private TextField componentNameTextField;
	@FXML
	private Label componentTypeLabel;

	@FXML
	private HBox chi0SourceOptionContainer;
	@FXML
	private HBox relDensityParContainer;

	@Override
	public void bindToInstance() {
		ReflectionProfileRefractionCorrElement model = getModelInstance();

		componentNameTextField.textProperty().bindBidirectional(model.getNameProperty());
		componentTypeLabel.textProperty().set(model.getType().toString());

		BindingUtils.bindAndBuildRadioButtonsOption(chi0SourceOptionContainer, model.chi0SourceOption);
		BindingUtils.bindAndBuildParFieldsNoName(relDensityParContainer, model.relDensityPar);
	}

}
