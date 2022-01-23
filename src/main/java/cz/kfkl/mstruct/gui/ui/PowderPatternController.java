package cz.kfkl.mstruct.gui.ui;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.kfkl.mstruct.gui.model.PowderPatternBackgroundModel;
import cz.kfkl.mstruct.gui.model.PowderPatternBackgroundType;
import cz.kfkl.mstruct.gui.model.PowderPatternElement;
import cz.kfkl.mstruct.gui.utils.BindingUtils;
import cz.kfkl.mstruct.gui.utils.validation.UnexpectedException;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;

public class PowderPatternController extends BaseController<PowderPatternElement> {
	private static final Logger LOG = LoggerFactory.getLogger(PowderPatternController.class);

	@FXML
	private TextField powderPatternName;

	@FXML
	private HBox zeroParContainer;
	@FXML
	private HBox thetaDisplacementParContainer;
	@FXML
	private HBox thetaTransparencyParContainer;

	@FXML
	private HBox useIntegratedProfilesOptionContainer;

	// Radiation START

	@FXML
	private HBox radiationOptionContainer;
	@FXML
	private HBox spectrumOptionContainer;
	@FXML
	private TextField linearPolarRateElementTextField;

	@FXML
	private HBox wavelengthParContainer;
	@FXML
	private HBox xRayTubeDeltaLambdaParContainer;
	@FXML
	private HBox xRayTubeAlpha2Alpha1RatioParContainer;

	// Radiation END

	@FXML
	private TextField maxSinThetaOvLambdaTextField;

	@FXML
	private TextField geometryOmegaTextField;

	@FXML
	private ListView<PowderPatternBackgroundModel> powderPatternComponentsListView;

	private static List<PowderPatternBackgroundType> allowedPowderPatternBackgrounds = Arrays
			.asList(PowderPatternBackgroundType.values());

	@FXML
	private Button powderPatternComponentsAddButton;
	@FXML
	private Button powderPatternComponentsRemoveButton;
	@FXML
	private ScrollPane powderPatternComponentsCenterPane;
	@FXML
	private StackPane powderPatternComponentsStackPane;

	@FXML
	private StackPane powderPatternCrystalStackPane;

	@Override
	public void init() {
		PowderPatternElement model = getModelInstance();

		powderPatternName.textProperty().bindBidirectional(model.nameProperty);
		BindingUtils.doWhenNodeFocusedLost(powderPatternName, () -> getAppContext().getMainController().instrumentNameUpdated());

		BindingUtils.bindAndBuildParFieldsNoName(zeroParContainer, model.zeroPar);
		BindingUtils.bindAndBuildParFieldsNoName(thetaDisplacementParContainer, model.thetaDisplacementPar);
		BindingUtils.bindAndBuildParFieldsNoName(thetaTransparencyParContainer, model.thetaTransparencyPar);

		BindingUtils.bindAndBuildRadioButtonsOption(useIntegratedProfilesOptionContainer, model.useIntegratedProfilesOption);

		// Radiation
		BindingUtils.bindAndBuildRadioButtonsOption(radiationOptionContainer, model.radiationElement.radiationOption);
		BindingUtils.bindAndBuildComboBoxOption(spectrumOptionContainer, model.radiationElement.spectrumOption);
		BindingUtils.doubleTextField(linearPolarRateElementTextField);
		linearPolarRateElementTextField.textProperty()
				.bindBidirectional(model.radiationElement.linearPolarRateElement.valueProperty);

		BindingUtils.bindAndBuildParFieldsNoName(wavelengthParContainer, model.radiationElement.wavelengthPar);
		BindingUtils.bindAndBuildParFieldsNoName(xRayTubeDeltaLambdaParContainer, model.radiationElement.xRayTubeDeltaLambdaPar);
		BindingUtils.bindAndBuildParFieldsNoName(xRayTubeAlpha2Alpha1RatioParContainer,
				model.radiationElement.xRayTubeAlpha2Alpha1RatioPar);

		BindingUtils.doubleTextField(maxSinThetaOvLambdaTextField);
		maxSinThetaOvLambdaTextField.textProperty().bindBidirectional(model.maxSinThetaOvLambdaElement.valueProperty);

		BindingUtils.doubleTextField(geometryOmegaTextField);
		geometryOmegaTextField.textProperty().bindBidirectional(model.geometryElement.omegaProperty);

		powderPatternComponentsListView.setItems(model.powderPatternComponents);
//		BindingUtils.setupListViewListener(powderPatternComponentsListView, powderPatternComponentsCenterPane, getAppContext());

		BindingUtils.setupSelectionToChildrenListener(powderPatternComponentsListView.getSelectionModel().selectedItemProperty(),
				powderPatternComponentsStackPane.getChildren(), getAppContext());
		BindingUtils.autoHeight(powderPatternComponentsListView);

		powderPatternComponentsRemoveButton.disableProperty()
				.bind(powderPatternComponentsListView.getSelectionModel().selectedItemProperty().isNull());

	}

	@FXML
	public void onPowderPatternComponentsAddButton() {
		ChoiceDialog<PowderPatternBackgroundType> choiceDialog = new ChoiceDialog<PowderPatternBackgroundType>(
				PowderPatternBackgroundType.Chebyshev, allowedPowderPatternBackgrounds);
		choiceDialog.setGraphic(null);
		choiceDialog.setTitle("Select Type");
		choiceDialog.setHeaderText("Choose new powder pattern background type:");
		choiceDialog.showAndWait();
		PowderPatternBackgroundType selectedType = choiceDialog.getResult();
		LOG.debug("Adding new PowderPatternComponent, selected type [{}]", selectedType);

		if (selectedType != null) {

			try {
				Constructor<? extends PowderPatternBackgroundModel<?>> constructor = selectedType.getModelClass()
						.getConstructor();
				PowderPatternBackgroundModel<?> newInstance = constructor.newInstance();

				powderPatternComponentsListView.getItems().add(newInstance);
				powderPatternComponentsListView.getSelectionModel().select(newInstance);
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
					| NoSuchMethodException | SecurityException e) {

				new UnexpectedException(e, "Failed to create an instance for type [%s] with class [%s]", selectedType,
						selectedType.getModelClass());
			}

		}
	}

	@FXML
	public void onPowderPatternComponentsRemoveButton() {
		PowderPatternBackgroundModel selectedItem = powderPatternComponentsListView.getSelectionModel().getSelectedItem();
		if (selectedItem != null) {
			Alert alert = new Alert(AlertType.CONFIRMATION, null);
			alert.setHeaderText("Are you sure you want to remove the \"" + selectedItem.toString() + "\" background?");
			alert.showAndWait();
			ButtonType result = alert.getResult();
			if (result == ButtonType.OK) {
				powderPatternComponentsListView.getItems().remove(selectedItem);
			}

		}
	}

}
