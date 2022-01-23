package cz.kfkl.mstruct.gui.ui;

import cz.kfkl.mstruct.gui.model.PowderPatternBackgroundInvX;
import cz.kfkl.mstruct.gui.utils.BindingUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

public class PowderPatternBackgroundInvXController extends BaseController<PowderPatternBackgroundInvX, PowderPatternController> {

	@FXML
	private TextField powderPatternComponentName;
	@FXML
	private Label powderPatternComponentTypeLabel;

	@FXML
	private HBox xFuncTypeOptionContainer;

	@FXML
	private TextField powderPatternComponentScaleTextField;

	@Override
	public void init() {
		PowderPatternBackgroundInvX model = getModelInstance();

		powderPatternComponentName.textProperty().bindBidirectional(model.getNameProperty());
		powderPatternComponentTypeLabel.textProperty().set(model.getType().getTypeName());

		BindingUtils.doWhenFocuseLost(powderPatternComponentName,
				() -> getParentController().powderPatternComponentNameChanged());

		BindingUtils.bindAndBuildRadioButtonsOption(xFuncTypeOptionContainer, model.xFuncTypeOption);

		powderPatternComponentScaleTextField.textProperty().bindBidirectional(model.powderPatternComponent.scalePoperty);
	}

}
