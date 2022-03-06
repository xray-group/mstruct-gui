package cz.kfkl.mstruct.gui.ui.crystals;

import static cz.kfkl.mstruct.gui.utils.BindingUtils.autoHeight;
import static cz.kfkl.mstruct.gui.utils.BindingUtils.bindAndBuildComboBoxOption;
import static cz.kfkl.mstruct.gui.utils.BindingUtils.bindAndBuildParFieldsNoName;
import static cz.kfkl.mstruct.gui.utils.BindingUtils.bindAndBuildRadioButtonsOption;
import static cz.kfkl.mstruct.gui.utils.BindingUtils.bindDoubleTableColumn;
import static cz.kfkl.mstruct.gui.utils.BindingUtils.bindDoubleTextField;
import static cz.kfkl.mstruct.gui.utils.BindingUtils.bindStringTableColumn;

import cz.kfkl.mstruct.gui.model.MoleculeAtomBondAngleElement;
import cz.kfkl.mstruct.gui.model.MoleculeAtomBondElement;
import cz.kfkl.mstruct.gui.model.MoleculeAtomDihedralAngleElement;
import cz.kfkl.mstruct.gui.model.MoleculeAtomElement;
import cz.kfkl.mstruct.gui.model.MoleculeElement;
import cz.kfkl.mstruct.gui.ui.BaseController;
import cz.kfkl.mstruct.gui.ui.RadioButtonTableCellFactory;
import javafx.beans.value.WritableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

public class ScattererMoleculeController extends BaseController<MoleculeElement, CrystalController> {

	@FXML
	private TextField scattererNameTextField;
	@FXML
	private Label scattererTypeLabel;

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

	@FXML
	private TableView<MoleculeAtomBondAngleElement> bondAngleTableView;
	@FXML
	private TableColumn<MoleculeAtomBondAngleElement, String> bondAngleAtom1TableNameColumn;
	@FXML
	private TableColumn<MoleculeAtomBondAngleElement, String> bondAngleAtom2TableNameColumn;
	@FXML
	private TableColumn<MoleculeAtomBondAngleElement, String> bondAngleAtom3TableNameColumn;
	@FXML
	private TableColumn<MoleculeAtomBondAngleElement, String> bondAngleAngleTableNameColumn;
	@FXML
	private TableColumn<MoleculeAtomBondAngleElement, String> bondAngleDeltaTableNameColumn;
	@FXML
	private TableColumn<MoleculeAtomBondAngleElement, String> bondAngleSigmaTableNameColumn;

	@FXML
	private TableView<MoleculeAtomDihedralAngleElement> dihedralAngleTableView;
	@FXML
	private TableColumn<MoleculeAtomDihedralAngleElement, String> dihedralAngleAtom1TableNameColumn;
	@FXML
	private TableColumn<MoleculeAtomDihedralAngleElement, String> dihedralAngleAtom2TableNameColumn;
	@FXML
	private TableColumn<MoleculeAtomDihedralAngleElement, String> dihedralAngleAtom3TableNameColumn;
	@FXML
	private TableColumn<MoleculeAtomDihedralAngleElement, String> dihedralAngleAtom4TableNameColumn;
	@FXML
	private TableColumn<MoleculeAtomDihedralAngleElement, String> dihedralAngleAngleTableNameColumn;
	@FXML
	private TableColumn<MoleculeAtomDihedralAngleElement, String> dihedralAngleDeltaTableNameColumn;
	@FXML
	private TableColumn<MoleculeAtomDihedralAngleElement, String> dihedralAngleSigmaTableNameColumn;

	@Override
	public void init() {
		MoleculeElement model = getModelInstance();

		scattererNameTextField.textProperty().bindBidirectional(model.nameProperty);
		scattererTypeLabel.textProperty().set(model.getType());

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

		bondAngleTableView.getItems().addAll(model.moleculeAtomBondAngles);
		autoHeight(bondAngleTableView);

		bindStringTableColumn(bondAngleAtom1TableNameColumn, v -> v.atom1Property);
		bindStringTableColumn(bondAngleAtom2TableNameColumn, v -> v.atom2Property);
		bindStringTableColumn(bondAngleAtom3TableNameColumn, v -> v.atom3Property);
		bindStringTableColumn(bondAngleAngleTableNameColumn, v -> v.angleProperty);
		bindDoubleTableColumn(bondAngleSigmaTableNameColumn, v -> v.sigmaProperty);
		bindDoubleTableColumn(bondAngleDeltaTableNameColumn, v -> v.deltaProperty);

		dihedralAngleTableView.getItems().addAll(model.moleculeAtomDihedralAngles);
		autoHeight(dihedralAngleTableView);

		bindStringTableColumn(dihedralAngleAtom1TableNameColumn, v -> v.atom1Property);
		bindStringTableColumn(dihedralAngleAtom2TableNameColumn, v -> v.atom2Property);
		bindStringTableColumn(dihedralAngleAtom3TableNameColumn, v -> v.atom3Property);
		bindStringTableColumn(dihedralAngleAtom4TableNameColumn, v -> v.atom4Property);
		bindStringTableColumn(dihedralAngleAngleTableNameColumn, v -> v.angleProperty);
		bindDoubleTableColumn(dihedralAngleSigmaTableNameColumn, v -> v.sigmaProperty);
		bindDoubleTableColumn(dihedralAngleDeltaTableNameColumn, v -> v.deltaProperty);
	}

}
