package cz.kfkl.mstruct.gui.ui;

import cz.kfkl.mstruct.gui.model.MoleculeAtomElement;
import cz.kfkl.mstruct.gui.model.MoleculeElement;
import cz.kfkl.mstruct.gui.utils.BindingUtils;
import javafx.beans.value.WritableValue;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;

public class ScattererMoleculeController extends BaseController<MoleculeElement, CrystalController> {

	@FXML
	private HBox xParContainer;
	@FXML
	private HBox yParContainer;
	@FXML
	private HBox zParContainer;
	@FXML
	private HBox occupParContainer;

	@FXML
	private HBox flexibilityModelOptionContainer;
	@FXML
	private HBox enableFlippingOptionContainer;
	@FXML
	private HBox autoOptimizeStartingConformationOptionContainer;
	@FXML
	private HBox optimizeOrientationOptionContainer;
	@FXML
	private HBox rotationCenterOptionContainer;

	@FXML
	private TextField logLikelihoodScaleTextField;
	@FXML
	private TextField mDMoveFreqTextField;
	@FXML
	private TextField mDMoveEnergyTextField;

	@FXML
	private TextField centerAtomTextField;

	@FXML
	private TableView<MoleculeAtomElement> moleculeAtomsTableView;
	@FXML
	private TableColumn<MoleculeAtomElement, String> moleculeAtomsTableNameColumn;
	@FXML
	private TableColumn<MoleculeAtomElement, String> moleculeAtomsTableScatteringPowerColumn;
	@FXML
	private TableColumn<MoleculeAtomElement, String> moleculeAtomsTableXColumn;
	@FXML
	private TableColumn<MoleculeAtomElement, String> moleculeAtomsTableYColumn;
	@FXML
	private TableColumn<MoleculeAtomElement, String> moleculeAtomsTableZColumn;
	@FXML
	private TableColumn<MoleculeAtomElement, String> moleculeAtomsTableOccupColumn;
	@FXML
	private TableColumn<MoleculeAtomElement, String> moleculeAtomsTableCenterAtomColumn;

	@Override
	public void init() {
		MoleculeElement model = getModelInstance();

		BindingUtils.bindAndBuildParFieldsNoName(xParContainer, model.xPar);
		BindingUtils.bindAndBuildParFieldsNoName(yParContainer, model.yPar);
		BindingUtils.bindAndBuildParFieldsNoName(zParContainer, model.zPar);
		BindingUtils.bindAndBuildParFieldsNoName(occupParContainer, model.occupPar);

		BindingUtils.bindAndBuildComboBoxOption(flexibilityModelOptionContainer, model.flexibilityModelOption);
		BindingUtils.bindAndBuildRadioButtonsOption(enableFlippingOptionContainer, model.enableFlippingOption);
		BindingUtils.bindAndBuildRadioButtonsOption(autoOptimizeStartingConformationOptionContainer,
				model.autoOptimizeStartingConformationOption);
		BindingUtils.bindAndBuildRadioButtonsOption(optimizeOrientationOptionContainer, model.optimizeOrientationOption);
		BindingUtils.bindAndBuildComboBoxOption(rotationCenterOptionContainer, model.rotationCenterOption);

		BindingUtils.bindDoubleTextField(logLikelihoodScaleTextField, model.logLikelihoodScaleProperty);
		BindingUtils.bindDoubleTextField(mDMoveFreqTextField, model.mDMoveFreqProperty);
		BindingUtils.bindDoubleTextField(mDMoveEnergyTextField, model.mDMoveEnergyProperty);

		centerAtomTextField.textProperty().bindBidirectional(model.centerAtomElement.nameProperty);

		moleculeAtomsTableView.getItems().addAll(model.moleculeAtoms);
		moleculeAtomsTableView.setEditable(true);
		BindingUtils.autoHeight(moleculeAtomsTableView);

		moleculeAtomsTableNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
		moleculeAtomsTableNameColumn.setEditable(true);
		moleculeAtomsTableScatteringPowerColumn.setCellValueFactory(new PropertyValueFactory<>("scattPow"));
		moleculeAtomsTableScatteringPowerColumn.setEditable(true);
		moleculeAtomsTableCenterAtomColumn.setCellValueFactory(cdf -> cdf.getValue().nameProperty);

		moleculeAtomsTableCenterAtomColumn
				.setCellFactory(new RadioButtonTableCellFactory<>((WritableValue<String>) model.centerAtomElement.nameProperty));
		moleculeAtomsTableCenterAtomColumn.setStyle("-fx-alignment: CENTER;");

		BindingUtils.bindStringTableColumn(moleculeAtomsTableNameColumn, v -> v.nameProperty);
		BindingUtils.bindStringTableColumn(moleculeAtomsTableScatteringPowerColumn, v -> v.scattPowProperty);

		BindingUtils.bindDoubleTableColumn(moleculeAtomsTableXColumn, v -> v.xProperty);
		BindingUtils.bindDoubleTableColumn(moleculeAtomsTableYColumn, v -> v.yProperty);
		BindingUtils.bindDoubleTableColumn(moleculeAtomsTableZColumn, v -> v.zProperty);
		BindingUtils.bindDoubleTableColumn(moleculeAtomsTableOccupColumn, v -> v.occupProperty);

	}

}
