package cz.kfkl.mstruct.gui.ui.phases;

import static cz.kfkl.mstruct.gui.utils.BindingUtils.autoHeight;
import static cz.kfkl.mstruct.gui.utils.BindingUtils.bindAndBuildParFieldsNoName;
import static cz.kfkl.mstruct.gui.utils.BindingUtils.bindAndBuildRadioButtonsOption;
import static cz.kfkl.mstruct.gui.utils.BindingUtils.bindBooleanTableColumn;
import static cz.kfkl.mstruct.gui.utils.BindingUtils.bindDoubleTableColumn;
import static cz.kfkl.mstruct.gui.utils.BindingUtils.bindlBooleanPropertyToInteger;
import static cz.kfkl.mstruct.gui.utils.BindingUtils.createUniqueName;
import static cz.kfkl.mstruct.gui.utils.BindingUtils.doWhenFocuseLost;
import static cz.kfkl.mstruct.gui.utils.BindingUtils.doubleTextField;
import static cz.kfkl.mstruct.gui.utils.BindingUtils.setupSelectionToChildrenListener;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;

import cz.kfkl.mstruct.gui.model.crystals.CrystalModel;
import cz.kfkl.mstruct.gui.model.phases.ArbitraryTextureElement;
import cz.kfkl.mstruct.gui.model.phases.IhklParElement;
import cz.kfkl.mstruct.gui.model.phases.PowderPatternCrystalsModel;
import cz.kfkl.mstruct.gui.model.phases.ReflectionProfileModel;
import cz.kfkl.mstruct.gui.model.phases.ReflectionProfileType;
import cz.kfkl.mstruct.gui.model.utils.XmlLinkedModelElement;
import cz.kfkl.mstruct.gui.ui.BaseController;
import cz.kfkl.mstruct.gui.ui.MStructGuiController;
import cz.kfkl.mstruct.gui.ui.ObjCrystModel;
import cz.kfkl.mstruct.gui.utils.JvStringUtils;
import cz.kfkl.mstruct.gui.utils.validation.UnexpectedException;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;

public class PowderPatternCrystalController extends BaseController<PowderPatternCrystalsModel, MStructGuiController> {

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
	private HBox powderPatternComponentScaleParContainer;

	@FXML
	private TextField absorptionCorrectionNameTextField;
	@FXML
	private TextField absorptionDepthTextField;
	@FXML
	private TextField absorptionThicknessTextField;
	@FXML
	private TextField absorptionFactorTextField;

	@FXML
	private ListView<ReflectionProfileModel> reflectionProfileListView;

	private static List<ReflectionProfileType> allowedReflectionProfiles = Arrays.asList(ReflectionProfileType.values());

	@FXML
	private Button reflectionProfileAddButton;
	@FXML
	private Button reflectionProfileRemoveButton;

	@FXML
	private StackPane reflectionProfileStackPane;

	@FXML
	private TextField arbitraryTextureNameTextField;
	@FXML
	private TextField arbitraryTextureTagTextField;
	@FXML
	private HBox arbitraryTextureChoiceOptionContainer;
//	@FXML
//	private RadioButton arbitraryTextureToggleNone;
//	@FXML
//	private RadioButton arbitraryTextureToggleGenerate;
//	@FXML
//	private RadioButton arbitraryTextureToggleFreeAll;
//	@FXML
//	private RadioButton arbitraryTextureToggleRead;
//	@FXML
//	private ToggleGroup arbitraryTextureToggleGroup;

	@FXML
	private TableView<IhklParElement> arbitraryTextureIhklParamsTableView;

	@FXML
	private TableColumn<IhklParElement, String> arbitraryTextureIhklParamsHTableColumn;
	@FXML
	private TableColumn<IhklParElement, String> arbitraryTextureIhklParamsKTableColumn;
	@FXML
	private TableColumn<IhklParElement, String> arbitraryTextureIhklParamsLTableColumn;
	@FXML
	private TableColumn<IhklParElement, String> arbitraryTextureIhklParams2ThetaTableColumn;
	@FXML
	private TableColumn<IhklParElement, String> arbitraryTextureIhklParamsSFactSqMultTableColumn;
	@FXML
	private TableColumn<IhklParElement, String> arbitraryTextureIhklParamsValueTableColumn;
	@FXML
	private TableColumn<IhklParElement, Boolean> arbitraryTextureIhklParamsRefinedTableColumn;
	@FXML
	private TableColumn<IhklParElement, Boolean> arbitraryTextureIhklParamsLimitedTableColumn;
	@FXML
	private TableColumn<IhklParElement, String> arbitraryTextureIhklParamsMinTableColumn;
	@FXML
	private TableColumn<IhklParElement, String> arbitraryTextureIhklParamsMaxTableColumn;

	@FXML
	private Button arbitraryTextureIhklParamAddButton;
	@FXML
	private Button arbitraryTextureIhklParamRemoveButton;

	@Override
	public void init() {
		PowderPatternCrystalsModel model = getModelInstance();

		powderPatternCrystalInternalNameTextField.textProperty().bind(model.nameProperty);
		powderPatternCrystalUserNameTextField.textProperty().bindBidirectional(model.userNameProperty);

		doWhenFocuseLost(powderPatternCrystalUserNameTextField, () -> getAppContext().getMainController().phaseNameUpdated());

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
		bindlBooleanPropertyToInteger(ignoreImagScattFactCheckBox.selectedProperty(), model.ignoreImagScattFactProperty);
//		bindAndBuildParFieldsFullLarge(globalBisoParContainer, model.globalBisoPar);
		bindAndBuildParFieldsNoName(globalBisoParContainer, model.globalBisoPar);
		bindAndBuildParFieldsNoName(powderPatternComponentScaleParContainer, model.powderPatternComponent.scalePar);

		initAbsorptionCorrection(model);

		initReflectionProfile(model);

		initArbitraryTexture(model);
	}

	private void initAbsorptionCorrection(PowderPatternCrystalsModel model) {
		absorptionCorrectionNameTextField.textProperty().bindBidirectional(model.absorptionCorrElement.nameProperty);
		doubleTextField(absorptionDepthTextField);
		absorptionDepthTextField.textProperty().bindBidirectional(model.absorptionCorrElement.depthProperty);
		doubleTextField(absorptionThicknessTextField);
		absorptionThicknessTextField.textProperty().bindBidirectional(model.absorptionCorrElement.thicknessProperty);
		doubleTextField(absorptionFactorTextField);
		absorptionFactorTextField.textProperty().bindBidirectional(model.absorptionCorrElement.absorptionFactorProperty);
	}

	private void initReflectionProfile(PowderPatternCrystalsModel model) {
		reflectionProfileListView.setItems(model.reflectionProfile.reflectionProfilesList);
//		setupListViewListener(reflectionProfileListView, reflectionProfileStackPane, getAppContext());
		setupSelectionToChildrenListener(this, reflectionProfileListView.getSelectionModel().selectedItemProperty(),
				reflectionProfileStackPane.getChildren(), getAppContext());
		autoHeight(reflectionProfileListView);

		reflectionProfileRemoveButton.disableProperty()
				.bind(reflectionProfileListView.getSelectionModel().selectedItemProperty().isNull());
	}

	private void initArbitraryTexture(PowderPatternCrystalsModel model) {
		arbitraryTextureNameTextField.textProperty().bindBidirectional(model.arbitraryTextureElement.nameProperty);
		arbitraryTextureTagTextField.textProperty().bindBidirectional(model.arbitraryTextureElement.tagProperty);

		ChangeListener<? super Toggle> listener = (observable, oldValue, newValue) -> {
			boolean disableParams = true;
			if (newValue instanceof ToggleButton) {
				ToggleButton tb = (ToggleButton) newValue;
				if (tb.getText().equals(ArbitraryTextureElement.CHOICE_READ.getDisplayText())) {
					disableParams = false;
				}
			}
			arbitraryTextureIhklParamsTableView.setDisable(disableParams);
			arbitraryTextureIhklParamsTableView.setEditable(!disableParams);
			arbitraryTextureIhklParamAddButton.setDisable(disableParams);
			arbitraryTextureIhklParamRemoveButton.setDisable(disableParams);

		};

		bindAndBuildRadioButtonsOption(arbitraryTextureChoiceOptionContainer, "Difractions/Parameters/Points ?",
				model.arbitraryTextureElement.choice, listener);

		arbitraryTextureIhklParamsTableView.setEditable(true);
		arbitraryTextureIhklParamsTableView.setItems(model.arbitraryTextureElement.ihklParams);
		arbitraryTextureIhklParamsTableView.setSortPolicy(tw -> {
			FXCollections.sort(tw.getItems(), IhklParElement.IHKL_PAR_COMPARATOR);
			return true;
		});
		autoHeight(9, arbitraryTextureIhklParamsTableView);
		arbitraryTextureIhklParamsTableView.refresh();

		bindDoubleTableColumn(arbitraryTextureIhklParamsHTableColumn, v -> v.h);
		bindDoubleTableColumn(arbitraryTextureIhklParamsKTableColumn, v -> v.k);
		bindDoubleTableColumn(arbitraryTextureIhklParamsLTableColumn, v -> v.l);

		bindDoubleTableColumn(arbitraryTextureIhklParams2ThetaTableColumn, v -> v.twoTheta);
		arbitraryTextureIhklParams2ThetaTableColumn.addEventHandler(TableColumn.editCommitEvent(), t -> {
			Platform.runLater(() -> arbitraryTextureIhklParamsTableView.sort());
		});
		bindDoubleTableColumn(arbitraryTextureIhklParamsSFactSqMultTableColumn, v -> v.sFactSqMult);
		bindDoubleTableColumn(arbitraryTextureIhklParamsValueTableColumn, v -> v.valueProperty);

		bindBooleanTableColumn(arbitraryTextureIhklParamsRefinedTableColumn, v -> v.refinedProperty);
		bindBooleanTableColumn(arbitraryTextureIhklParamsLimitedTableColumn, v -> v.limitedProperty);

		bindDoubleTableColumn(arbitraryTextureIhklParamsMinTableColumn, v -> v.minProperty);
		bindDoubleTableColumn(arbitraryTextureIhklParamsMaxTableColumn, v -> v.maxProperty);
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

				newInstance.setName(newInstance.getType().getNamePrefix() + getModelInstance().getNameSuffix());
				newInstance.setName(createUniqueName(newInstance, reflectionProfileListView.getItems()));

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

	public void componentNameChanged() {
		reflectionProfileListView.refresh();
	}

	@FXML
	public void addArbitraryTextureIhklParam() {
		IhklParElement newItem = new IhklParElement();
		newItem.setPhaseName(getModelInstance().nameProperty.get());
		arbitraryTextureIhklParamsTableView.getItems().add(newItem);
		arbitraryTextureIhklParamsTableView.getSelectionModel().select(newItem);
	}

	@FXML
	public void removeArbitraryTextureIhklParam() {
		IhklParElement selectedItem = arbitraryTextureIhklParamsTableView.getSelectionModel().getSelectedItem();
		if (selectedItem != null) {
			arbitraryTextureIhklParamsTableView.getItems().remove(selectedItem);
		}
	}

}
