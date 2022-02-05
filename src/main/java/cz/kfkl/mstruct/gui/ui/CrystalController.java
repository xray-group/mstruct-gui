package cz.kfkl.mstruct.gui.ui;

import cz.kfkl.mstruct.gui.model.AtomElement;
import cz.kfkl.mstruct.gui.model.CrystalModel;
import cz.kfkl.mstruct.gui.model.ScatteringPowerModel;
import cz.kfkl.mstruct.gui.utils.BindingUtils;
import cz.kfkl.mstruct.gui.utils.ColorTableCell;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

public class CrystalController extends BaseController<CrystalModel, MStructGuiController> {

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
	private HBox alphaParContainer;
	@FXML
	private HBox betaParContainer;
	@FXML
	private HBox gammaParContainer;

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
	private TableView<ScatteringPowerModel> scatPowersTableView;
	@FXML
	private TableColumn<ScatteringPowerModel, String> scatPowersTableNameColumn;
	@FXML
	private TableColumn<ScatteringPowerModel, String> scatPowersTableSymbolColumn;
	@FXML
	private TableColumn<ScatteringPowerModel, Color> scatPowersTableColourColumn;
	@FXML
	private TableColumn<ScatteringPowerModel, String> scatPowersTableBisoValueColumn;
	@FXML
	private StackPane scatPowersDetailsStackPane;

	@Override
	public void init() {
		CrystalModel crystalModel = getModelInstance();
		crystalName.textProperty().bindBidirectional(crystalModel.getNameProperty());
		BindingUtils.doWhenFocuseLost(crystalName, () -> getAppContext().getMainController().getCrystalsListView().refresh());
		spaceGroup.textProperty().bindBidirectional(crystalModel.getSpaceGroupProperty());

		BindingUtils.bindToggleGroupToPropertyByText(constrainLatticeToggleGroup, crystalModel.constrainLatticeOption);
		BindingUtils.bindToggleGroupToPropertyByText(useOccupancyCorrectionToggleGroup,
				crystalModel.useOccupancyCorrectionOption);
		BindingUtils.bindToggleGroupToPropertyByText(displayEnantiomerToggleGroup, crystalModel.constrainLatticeOption);

		BindingUtils.bindAndBuildParFieldsNoName(aParContainer, crystalModel.aPar);
		BindingUtils.bindAndBuildParFieldsNoName(bParContainer, crystalModel.bPar);
		BindingUtils.bindAndBuildParFieldsNoName(cParContainer, crystalModel.cPar);

		BindingUtils.bindAndBuildParFieldsNoName(alphaParContainer, crystalModel.alphaPar);
		BindingUtils.bindAndBuildParFieldsNoName(betaParContainer, crystalModel.betaPar);
		BindingUtils.bindAndBuildParFieldsNoName(gammaParContainer, crystalModel.gammaPar);

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
		BindingUtils.setupSelectionToChildrenListener(this, scatPowersTableView.getSelectionModel().selectedItemProperty(),
				scatPowersDetailsStackPane.getChildren(), getAppContext());
		BindingUtils.autoHeight(scatPowersTableView, 33);

		scatPowersTableNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
		scatPowersTableNameColumn.setEditable(true);
		scatPowersTableSymbolColumn.setCellValueFactory(new PropertyValueFactory<>("symbol"));
		scatPowersTableSymbolColumn.setEditable(true);

		scatPowersTableColourColumn.setCellValueFactory(new PropertyValueFactory<ScatteringPowerModel, Color>("color"));
		scatPowersTableColourColumn.setCellFactory(column -> new ColorTableCell<>(column));
		scatPowersTableColourColumn
				.setOnEditCommit(t -> t.getTableView().getItems().get(t.getTablePosition().getRow()).setColor(t.getNewValue()));

		scatPowersTableBisoValueColumn.setCellValueFactory(new PropertyValueFactory<>("biso"));
	}

}
