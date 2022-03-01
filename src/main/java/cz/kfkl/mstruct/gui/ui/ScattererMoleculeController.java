package cz.kfkl.mstruct.gui.ui;

import static cz.kfkl.mstruct.gui.utils.BindingUtils.autoHeight;
import static cz.kfkl.mstruct.gui.utils.BindingUtils.bindAndBuildComboBoxOption;
import static cz.kfkl.mstruct.gui.utils.BindingUtils.bindAndBuildParFieldsNoName;
import static cz.kfkl.mstruct.gui.utils.BindingUtils.bindAndBuildRadioButtonsOption;
import static cz.kfkl.mstruct.gui.utils.BindingUtils.bindDoubleTableColumn;
import static cz.kfkl.mstruct.gui.utils.BindingUtils.bindDoubleTextField;
import static cz.kfkl.mstruct.gui.utils.BindingUtils.bindStringTableColumn;

import cz.kfkl.mstruct.gui.model.MoleculeAtomBondElement;
import cz.kfkl.mstruct.gui.model.MoleculeAtomElement;
import cz.kfkl.mstruct.gui.model.MoleculeElement;
import javafx.beans.value.WritableValue;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
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

	@FXML
	private TableView<MoleculeAtomBondElement> bondLengthsTableView;
	@FXML
	private TableColumn<MoleculeAtomBondElement, String> bondAtom1TableNameColumn;
	@FXML
	private TableColumn<MoleculeAtomBondElement, String> bondAtom2TableNameColumn;
	@FXML
	private TableColumn<MoleculeAtomBondElement, String> bondLengthTableNameColumn;
	@FXML
	private TableColumn<MoleculeAtomBondElement, String> bondRestraintTableNameColumn;
	@FXML
	private TableColumn<MoleculeAtomBondElement, String> bondDeltaTableNameColumn;
	@FXML
	private TableColumn<MoleculeAtomBondElement, String> bondSigmaTableNameColumn;
	@FXML
	private TableColumn<MoleculeAtomBondElement, String> bondOrderTableNameColumn;
	@FXML
	private TableColumn<MoleculeAtomBondElement, String> bondFreeTorsionTableNameColumn;

	@Override
	public void init() {
		MoleculeElement model = getModelInstance();

		bindAndBuildParFieldsNoName(xParContainer, model.xPar);
		bindAndBuildParFieldsNoName(yParContainer, model.yPar);
		bindAndBuildParFieldsNoName(zParContainer, model.zPar);
		bindAndBuildParFieldsNoName(occupParContainer, model.occupPar);

		bindAndBuildComboBoxOption(flexibilityModelOptionContainer, model.flexibilityModelOption);
		bindAndBuildRadioButtonsOption(enableFlippingOptionContainer, model.enableFlippingOption);
		bindAndBuildRadioButtonsOption(autoOptimizeStartingConformationOptionContainer,
				model.autoOptimizeStartingConformationOption);
		bindAndBuildRadioButtonsOption(optimizeOrientationOptionContainer, model.optimizeOrientationOption);
		bindAndBuildComboBoxOption(rotationCenterOptionContainer, model.rotationCenterOption);

		bindDoubleTextField(logLikelihoodScaleTextField, model.logLikelihoodScaleProperty);
		bindDoubleTextField(mDMoveFreqTextField, model.mDMoveFreqProperty);
		bindDoubleTextField(mDMoveEnergyTextField, model.mDMoveEnergyProperty);

		centerAtomTextField.textProperty().bindBidirectional(model.centerAtomElement.nameProperty);

		moleculeAtomsTableView.getItems().addAll(model.moleculeAtoms);
		moleculeAtomsTableView.setEditable(true);
		autoHeight(moleculeAtomsTableView);

		bindStringTableColumn(moleculeAtomsTableNameColumn, v -> v.nameProperty);
		bindStringTableColumn(moleculeAtomsTableScatteringPowerColumn, v -> v.scattPowProperty);
		bindDoubleTableColumn(moleculeAtomsTableXColumn, v -> v.xProperty);
		bindDoubleTableColumn(moleculeAtomsTableYColumn, v -> v.yProperty);
		bindDoubleTableColumn(moleculeAtomsTableZColumn, v -> v.zProperty);
		bindDoubleTableColumn(moleculeAtomsTableOccupColumn, v -> v.occupProperty);
		moleculeAtomsTableCenterAtomColumn.setCellValueFactory(cdf -> cdf.getValue().nameProperty);
		moleculeAtomsTableCenterAtomColumn
				.setCellFactory(new RadioButtonTableCellFactory<>((WritableValue<String>) model.centerAtomElement.nameProperty));
		moleculeAtomsTableCenterAtomColumn.setStyle("-fx-alignment: CENTER;");

		bondLengthsTableView.getItems().addAll(model.moleculeAtomBonds);
		autoHeight(bondLengthsTableView);

		bindStringTableColumn(bondAtom1TableNameColumn, v -> v.atom1Property);
		bindStringTableColumn(bondAtom2TableNameColumn, v -> v.atom2Property);
		// TODO: seems calculated in FOX but how ??
		bindDoubleTableColumn(bondLengthTableNameColumn, v -> v.lengthProperty);
		bindDoubleTableColumn(bondRestraintTableNameColumn, v -> v.lengthProperty);
		bindDoubleTableColumn(bondSigmaTableNameColumn, v -> v.sigmaProperty);
		bindDoubleTableColumn(bondDeltaTableNameColumn, v -> v.deltaProperty);
		bindDoubleTableColumn(bondOrderTableNameColumn, v -> v.bondOrderProperty);
		bindDoubleTableColumn(bondFreeTorsionTableNameColumn, v -> v.freeTorsionProperty);
	}

}
