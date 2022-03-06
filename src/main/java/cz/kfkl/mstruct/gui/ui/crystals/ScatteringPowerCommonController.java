package cz.kfkl.mstruct.gui.ui.crystals;

import static cz.kfkl.mstruct.gui.utils.BindingUtils.bindAndBuildParFieldsNoName;

import cz.kfkl.mstruct.gui.model.crystals.ScatteringPowerModel;
import cz.kfkl.mstruct.gui.ui.BaseController;
import javafx.fxml.FXML;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

public class ScatteringPowerCommonController<M extends ScatteringPowerModel<?>> extends BaseController<M, CrystalController> {

	@FXML
	private TextField scatteringPowerNameTextField;
	@FXML
	private Label scatteringPowerTypeLabel;
	@FXML
	private ColorPicker scatteringPowerColorPicker;

	@FXML
	private HBox bisoParContainer;

	@Override
	public void init() {
		M model = getModelInstance();

		scatteringPowerNameTextField.textProperty().bindBidirectional(model.nameProperty);

		scatteringPowerTypeLabel.textProperty().set(model.getType().toString());
		scatteringPowerTypeLabel.setGraphic(new ImageView(model.getIcon()));

		scatteringPowerColorPicker.valueProperty().bindBidirectional(model.colorProperty);

		bindAndBuildParFieldsNoName(bisoParContainer, model.bisoPar);
	}

}
