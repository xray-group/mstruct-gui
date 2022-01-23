package cz.kfkl.mstruct.gui.ui;

import cz.kfkl.mstruct.gui.model.AtomElement;
import cz.kfkl.mstruct.gui.model.CrystalModel;
import cz.kfkl.mstruct.gui.model.ScatteringPowerCommon;
import cz.kfkl.mstruct.gui.utils.BindingUtils;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;

public class CrystalController extends BaseController<CrystalModel> {

	@FXML
	private TextField crystalName;
	@FXML
	private TextField spaceGroup;

	@FXML
	private ToggleGroup constrainLatticeToggleGroup;
	@FXML
	private ToggleGroup useOccupancyCorrectionToggleGroup;
	@FXML
	private ToggleGroup displayEnantiomerToggleGroup;

	@FXML
	private HBox aParContainer;
	@FXML
	private HBox bParContainer;
	@FXML
	private HBox cParContainer;

	@FXML
	private TableView<AtomElement> atomsTableView;
	@FXML
	private TableColumn<AtomElement, String> atomsTableNameColumn;
	@FXML
	private TableColumn<AtomElement, String> atomsTableScatteringPowerColumn;
	@FXML
	private TableColumn<AtomElement, String> atomsTableXColumn;
	@FXML
	private TableColumn<AtomElement, String> atomsTableYColumn;
	@FXML
	private TableColumn<AtomElement, String> atomsTableZColumn;
	@FXML
	private TableColumn<AtomElement, String> atomsTableOccupColumn;

	@FXML
	private TableView<ScatteringPowerCommon> scatPowersTableView;
	@FXML
	private TableColumn<ScatteringPowerCommon, String> scatPowersTableNameColumn;
	@FXML
	private TableColumn<ScatteringPowerCommon, String> scatPowersTableSymbolColumn;
	@FXML
	private TableColumn<ScatteringPowerCommon, String> scatPowersTableColourColumn;

	@Override
	public void init() {
		CrystalModel crystalModel = getModelInstance();
		crystalName.textProperty().bindBidirectional(crystalModel.getNameProperty());
		BindingUtils.doWhenNodeFocusedLost(crystalName,
				() -> getAppContext().getMainController().getCrystalsListView().refresh());
		spaceGroup.textProperty().bindBidirectional(crystalModel.getSpaceGroupProperty());

		BindingUtils.bindToggleGroupToPropertyByText(constrainLatticeToggleGroup, crystalModel.constrainLatticeOption);
		BindingUtils.bindToggleGroupToPropertyByText(useOccupancyCorrectionToggleGroup,
				crystalModel.useOccupancyCorrectionOption);
		BindingUtils.bindToggleGroupToPropertyByText(displayEnantiomerToggleGroup, crystalModel.constrainLatticeOption);

		BindingUtils.bindAndBuildParFieldsNoName(aParContainer, crystalModel.aPar);
		BindingUtils.bindAndBuildParFieldsNoName(bParContainer, crystalModel.bPar);
		BindingUtils.bindAndBuildParFieldsNoName(cParContainer, crystalModel.cPar);

		atomsTableView.getItems().addAll(crystalModel.atoms);
		atomsTableView.setEditable(true);
		BindingUtils.autoHeight(atomsTableView);

		atomsTableNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
		atomsTableNameColumn.setEditable(true);
		atomsTableScatteringPowerColumn.setCellValueFactory(new PropertyValueFactory<>("scattPow"));
		atomsTableScatteringPowerColumn.setEditable(true);
		atomsTableXColumn.setCellValueFactory(new PropertyValueFactory<>("x"));
		atomsTableYColumn.setCellValueFactory(new PropertyValueFactory<>("y"));
		atomsTableZColumn.setCellValueFactory(new PropertyValueFactory<>("z"));
		atomsTableOccupColumn.setCellValueFactory(new PropertyValueFactory<>("occup"));

		scatPowersTableView.getItems().addAll(crystalModel.scatterintPowers);
		scatPowersTableView.setEditable(true);
		BindingUtils.autoHeight(scatPowersTableView);

		scatPowersTableNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
		scatPowersTableNameColumn.setEditable(true);
		scatPowersTableSymbolColumn.setCellValueFactory(new PropertyValueFactory<>("symbol"));
		scatPowersTableSymbolColumn.setEditable(true);
		scatPowersTableColourColumn.setCellValueFactory(new PropertyValueFactory<>("colour"));
	}

}
