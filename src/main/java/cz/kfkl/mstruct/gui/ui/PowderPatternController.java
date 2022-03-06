package cz.kfkl.mstruct.gui.ui;

import static cz.kfkl.mstruct.gui.utils.BindingUtils.autoHeight;
import static cz.kfkl.mstruct.gui.utils.BindingUtils.bindAndBuildComboBoxOption;
import static cz.kfkl.mstruct.gui.utils.BindingUtils.bindAndBuildParFieldsNoName;
import static cz.kfkl.mstruct.gui.utils.BindingUtils.bindAndBuildRadioButtonsOption;
import static cz.kfkl.mstruct.gui.utils.BindingUtils.bindDoubleTableColumn;
import static cz.kfkl.mstruct.gui.utils.BindingUtils.createUniqueName;
import static cz.kfkl.mstruct.gui.utils.BindingUtils.doWhenFocuseLost;
import static cz.kfkl.mstruct.gui.utils.BindingUtils.doubleTextField;
import static cz.kfkl.mstruct.gui.utils.BindingUtils.setupSelectionToChildrenListener;
import static java.util.Comparator.naturalOrder;
import static java.util.Comparator.nullsFirst;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.kfkl.mstruct.gui.model.ExcludeXElement;
import cz.kfkl.mstruct.gui.model.GeometryElement;
import cz.kfkl.mstruct.gui.model.PowderPatternBackgroundModel;
import cz.kfkl.mstruct.gui.model.PowderPatternBackgroundType;
import cz.kfkl.mstruct.gui.model.PowderPatternElement;
import cz.kfkl.mstruct.gui.utils.validation.UnexpectedException;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;

public class PowderPatternController extends BaseController<PowderPatternElement, MStructGuiController> {
	private static final Comparator<ExcludeXElement> EXCLUDE_REGIONS_TABLE_COMPARATOR = Comparator
			.comparing(ExcludeXElement::from, nullsFirst(naturalOrder()))
			.thenComparing(ExcludeXElement::to, nullsFirst(naturalOrder()));

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
	private RadioButton geometryToggleBB;
	@FXML
	private RadioButton geometryToggleBBVS;
	@FXML
	private RadioButton geometryTogglePB;
	@FXML
	private ToggleGroup geometryToggleGroup;

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

	@FXML
	private TableView<ExcludeXElement> excludedRegionsTableView;
	@FXML
	private TableColumn<ExcludeXElement, String> excludedRegionsFromTableColumn;
	@FXML
	private TableColumn<ExcludeXElement, String> excludedRegionsToTableColumn;

	@FXML
	private Button excludedRegionAddButton;
	@FXML
	private Button excludedRegionRemoveButton;

	@Override
	public void init() {
		PowderPatternElement model = getModelInstance();

		powderPatternName.textProperty().bindBidirectional(model.nameProperty);
		doWhenFocuseLost(powderPatternName, () -> getAppContext().getMainController().instrumentNameUpdated());

		bindAndBuildParFieldsNoName(zeroParContainer, model.zeroPar);
		bindAndBuildParFieldsNoName(thetaDisplacementParContainer, model.thetaDisplacementPar);
		bindAndBuildParFieldsNoName(thetaTransparencyParContainer, model.thetaTransparencyPar);

		bindAndBuildRadioButtonsOption(useIntegratedProfilesOptionContainer, model.useIntegratedProfilesOption);

		// Radiation
		bindAndBuildRadioButtonsOption(radiationOptionContainer, model.radiationElement.radiationOption);
		bindAndBuildComboBoxOption(spectrumOptionContainer, model.radiationElement.spectrumOption);
		doubleTextField(linearPolarRateElementTextField);
		linearPolarRateElementTextField.textProperty()
				.bindBidirectional(model.radiationElement.linearPolarRateElement.valueProperty);

		bindAndBuildParFieldsNoName(wavelengthParContainer, model.radiationElement.wavelengthPar);
		bindAndBuildParFieldsNoName(xRayTubeDeltaLambdaParContainer, model.radiationElement.xRayTubeDeltaLambdaPar);
		bindAndBuildParFieldsNoName(xRayTubeAlpha2Alpha1RatioParContainer, model.radiationElement.xRayTubeAlpha2Alpha1RatioPar);

		doubleTextField(maxSinThetaOvLambdaTextField);
		maxSinThetaOvLambdaTextField.textProperty().bindBidirectional(model.maxSinThetaOvLambdaElement.valueProperty);

		initGeometryTogles(model.geometryElement);

		powderPatternComponentsListView.setItems(model.powderPatternComponents);
//		setupListViewListener(powderPatternComponentsListView, powderPatternComponentsCenterPane, getAppContext());

		setupSelectionToChildrenListener(this, powderPatternComponentsListView.getSelectionModel().selectedItemProperty(),
				powderPatternComponentsStackPane.getChildren(), getAppContext());
		autoHeight(powderPatternComponentsListView);

		powderPatternComponentsRemoveButton.disableProperty()
				.bind(powderPatternComponentsListView.getSelectionModel().selectedItemProperty().isNull());

		excludedRegionsTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		excludedRegionsTableView.setEditable(true);
		excludedRegionsTableView.setItems(model.excludeRegions);
		autoHeight(excludedRegionsTableView);
		excludedRegionsTableView.setSortPolicy(tw -> {
			FXCollections.sort(tw.getItems(), EXCLUDE_REGIONS_TABLE_COMPARATOR);
			return true;
		});

		bindDoubleTableColumn(excludedRegionsFromTableColumn, v -> v.fromProperty);
		excludedRegionsFromTableColumn.addEventHandler(TableColumn.editCommitEvent(), t -> {
			Platform.runLater(() -> excludedRegionsTableView.sort());
		});

		bindDoubleTableColumn(excludedRegionsToTableColumn, v -> v.toProperty);
		excludedRegionsToTableColumn.addEventHandler(TableColumn.editCommitEvent(), t -> {
			Platform.runLater(() -> excludedRegionsTableView.sort());
		});

		excludedRegionRemoveButton.disableProperty()
				.bind(excludedRegionsTableView.getSelectionModel().selectedItemProperty().isNull());
	}

	private void initGeometryTogles(GeometryElement geometryElement) {
		Double omegaCode = geometryElement.decodeOmega();
		if (omegaCode == GeometryElement.OMEGA_BBVS_CONSTANT) {
			geometryToggleGroup.selectToggle(geometryToggleBBVS);
		} else if (omegaCode == GeometryElement.OMEGA_BB_CONSTANT) {
			geometryToggleGroup.selectToggle(geometryToggleBB);
		} else {
			geometryToggleGroup.selectToggle(geometryTogglePB);
		}

		doubleTextField(geometryOmegaTextField);
		geometryOmegaTextField.textProperty().bindBidirectional(geometryElement.omegaProperty);
		geometryOmegaTextField.disableProperty().bind(Bindings.not(geometryTogglePB.selectedProperty()));
		geometryToggleGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
			if (oldValue == geometryTogglePB) {
				geometryElement.lastPbOmega = geometryOmegaTextField.textProperty().get();
			}

			if (newValue == geometryToggleBB) {
				geometryOmegaTextField.textProperty().set(GeometryElement.OMEGA_BB_CONSTANT_STR);
			} else if (newValue == geometryToggleBBVS) {
				geometryOmegaTextField.textProperty().set(GeometryElement.OMEGA_BBVS_CONSTANT_STR);
			} else if (newValue == geometryTogglePB) {
				Double omegaD = geometryElement.decodeOmega();
				if (omegaD != null && omegaD < 0) {
					geometryOmegaTextField.textProperty().set(geometryElement.lastPbOmega);
				}
			}
		});
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

				newInstance.setName(createUniqueName(newInstance, getModelInstance().powderPatternComponents));

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

	public void powderPatternComponentNameChanged() {
		powderPatternComponentsListView.refresh();
	}

	@FXML
	public void addExcludedRegion() {
		ExcludeXElement newRow = new ExcludeXElement();
		excludedRegionsTableView.getItems().add(newRow);
		excludedRegionsTableView.getSelectionModel().select(newRow);
	}

	@FXML
	public void removeExcludedRegion() {
		excludedRegionsTableView.getItems().removeAll(excludedRegionsTableView.getSelectionModel().getSelectedItems());
	}

}
