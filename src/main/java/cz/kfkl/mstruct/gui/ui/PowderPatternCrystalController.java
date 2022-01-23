package cz.kfkl.mstruct.gui.ui;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;

import cz.kfkl.mstruct.gui.model.CrystalModel;
import cz.kfkl.mstruct.gui.model.PowderPatternCrystalsModel;
import cz.kfkl.mstruct.gui.model.ReflectionProfileModel;
import cz.kfkl.mstruct.gui.model.ReflectionProfileType;
import cz.kfkl.mstruct.gui.model.utils.XmlLinkedModelElement;
import cz.kfkl.mstruct.gui.utils.BindingUtils;
import cz.kfkl.mstruct.gui.utils.JvStringUtils;
import cz.kfkl.mstruct.gui.utils.validation.UnexpectedException;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;

public class PowderPatternCrystalController extends BaseController<PowderPatternCrystalsModel> {

	@FXML
	private ComboBox<CrystalModel> powderPatternCrystalNameComboBox;
	@FXML
	private TextField powderPatternCrystalUserNameTextField;
	@FXML
	private TextField powderPatternCrystalInternalNameTextField;

	@FXML
	private CheckBox ignoreImagScattFactCheckBox;

	@FXML
	private HBox globalBisoParContainer;

	@FXML
	private TextField powderPatternComponentScaleTextField;

	@FXML
	private TextField absoptionCorrectionNameTextField;
	@FXML
	private TextField absoptionDepthTextField;
	@FXML
	private TextField absoptionThicknessTextField;
	@FXML
	private TextField absoptionFactorTextField;

	@FXML
	private ListView<ReflectionProfileModel> reflectionProfileListView;

	private static List<ReflectionProfileType> allowedReflectionProfiles = Arrays.asList(ReflectionProfileType.values());

	@FXML
	private Button reflectionProfileAddButton;
	@FXML
	private Button reflectionProfileRemoveButton;

	@FXML
	private StackPane reflectionProfileStackPane;

	@Override
	public void bindToInstance() {
		PowderPatternCrystalsModel model = getModelInstance();

		powderPatternCrystalInternalNameTextField.textProperty().bind(model.nameProperty);
		powderPatternCrystalUserNameTextField.textProperty().bindBidirectional(model.userNameProperty);

		BindingUtils.doWhenNodeFocusedLost(powderPatternCrystalUserNameTextField,
				() -> getAppContext().getMainController().phaseNameUpdated());

		XmlLinkedModelElement parentModelElement = model.getParentModelElement().getParentModelElement();
		if (parentModelElement instanceof ObjCrystModel) {
			ObjCrystModel ocm = (ObjCrystModel) parentModelElement;
			powderPatternCrystalNameComboBox.setItems(ocm.crystals);
			String crystalName = model.crystalProperty.get();
			if (JvStringUtils.isNotBlank(crystalName)) {
				powderPatternCrystalNameComboBox.getSelectionModel().select(ocm.getCrystal(crystalName));
			}
			powderPatternCrystalNameComboBox.getSelectionModel().selectedItemProperty()
					.addListener((observable, oldValue, newValue) -> {
						if (newValue != null) {
							model.crystalProperty.set(newValue.getName());
							getAppContext().getMainController().phaseNameUpdated();
						}
					});
		}
		BindingUtils.bindlBooleanPropertyToInteger(ignoreImagScattFactCheckBox.selectedProperty(),
				model.ignoreImagScattFactProperty);
//		BindingUtils.bindAndBuildParFieldsFullLarge(globalBisoParContainer, model.globalBisoPar);
		BindingUtils.bindAndBuildParFieldsNoName(globalBisoParContainer, model.globalBisoPar);

		powderPatternComponentScaleTextField.textProperty().bindBidirectional(model.powderPatternComponent.scalePoperty);

		absoptionCorrectionNameTextField.textProperty().bindBidirectional(model.absorptionCorrElement.nameProperty);
		BindingUtils.doubleTextField(absoptionDepthTextField);
		absoptionDepthTextField.textProperty().bindBidirectional(model.absorptionCorrElement.depthPoperty);
		BindingUtils.doubleTextField(absoptionThicknessTextField);
		absoptionThicknessTextField.textProperty().bindBidirectional(model.absorptionCorrElement.thicknessPoperty);
		BindingUtils.doubleTextField(absoptionFactorTextField);
		absoptionFactorTextField.textProperty().bindBidirectional(model.absorptionCorrElement.absorptionFactorPoperty);

		reflectionProfileListView.setItems(model.reflectionProfile.reflectionProfilesList);
//		BindingUtils.setupListViewListener(reflectionProfileListView, reflectionProfileStackPane, getAppContext());
		BindingUtils.setupSelectionToChildrenListener(reflectionProfileListView.getSelectionModel().selectedItemProperty(),
				reflectionProfileStackPane.getChildren(), getAppContext());
		BindingUtils.autoHeight(reflectionProfileListView);

		reflectionProfileRemoveButton.disableProperty()
				.bind(reflectionProfileListView.getSelectionModel().selectedItemProperty().isNull());

//		BindingUtils.bindAndBuildRadioButtonsOption(xFuncTypeOptionContainer, model.xFuncTypeOption);
	}

	@FXML
	public void onReflectionProfileAddButton() {
		ChoiceDialog<ReflectionProfileType> choiceDialog = new ChoiceDialog<ReflectionProfileType>(null,
				allowedReflectionProfiles);
		choiceDialog.setGraphic(null);
		choiceDialog.setTitle("Select Type");
		choiceDialog.setHeaderText("Choose new reflection profile type:");
		choiceDialog.showAndWait();
		ReflectionProfileType selectedType = choiceDialog.getResult();

		if (selectedType != null) {

			try {
				Constructor<? extends ReflectionProfileModel<?>> constructor = selectedType.getModelClass().getConstructor();
				ReflectionProfileModel<?> newInstance = constructor.newInstance();

				reflectionProfileListView.getItems().add(newInstance);
				reflectionProfileListView.getSelectionModel().select(newInstance);
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
					| NoSuchMethodException | SecurityException e) {

				new UnexpectedException(e, "Failed to create an instance for type [%s] with class [%s]", selectedType,
						selectedType.getModelClass());
			}

		}
	}

	@FXML
	public void onReflectionProfileRemoveButton() {
		ReflectionProfileModel selectedItem = reflectionProfileListView.getSelectionModel().getSelectedItem();
		if (selectedItem != null) {
			Alert alert = new Alert(AlertType.CONFIRMATION, null);
			alert.setHeaderText("Are you sure you want to remove the \"" + selectedItem.toString() + "\" reflection profile?");
			alert.showAndWait();
			ButtonType result = alert.getResult();
			if (result == ButtonType.OK) {
				reflectionProfileListView.getItems().remove(selectedItem);
			}

		}
	}

}
