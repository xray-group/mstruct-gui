package cz.kfkl.mstruct.gui.ui;

import java.util.List;
import java.util.stream.Collectors;

import cz.kfkl.mstruct.gui.model.CrystalModel;
import cz.kfkl.mstruct.gui.model.ScattererModel;
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

	@FXML
	private TableView<ScattererModel> scatterersTableView;
	@FXML
	private TableColumn<ScattererModel, String> scatterersTableNameColumn;
//	@FXML
//	private TableColumn<ScattererModel, String> scatterersTableScatteringPowerColumn;
	@FXML
	private TableColumn<ScattererModel, String> scatterersTableXColumn;
	@FXML
	private TableColumn<ScattererModel, String> scatterersTableYColumn;
	@FXML
	private TableColumn<ScattererModel, String> scatterersTableZColumn;
	@FXML
	private TableColumn<ScattererModel, String> scatterersTableOccupColumn;
	@FXML
	private StackPane scatterresDetailsStackPane;

	@FXML
	private TableView<DynamicMatrixRow> antiBumpDistanceTableView;

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

		scatterersTableView.getItems().addAll(crystalModel.scatterers);
		scatterersTableView.setEditable(true);
		BindingUtils.setupSelectionToChildrenListener(this, scatterersTableView.getSelectionModel().selectedItemProperty(),
				scatterresDetailsStackPane.getChildren(), getAppContext());
		BindingUtils.autoHeight(scatterersTableView);

		scatterersTableNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
		scatterersTableNameColumn.setEditable(true);
//		scatterersTableScatteringPowerColumn.setCellValueFactory(new PropertyValueFactory<>("scattPow"));
//		scatterersTableScatteringPowerColumn.setEditable(true);
		scatterersTableXColumn.setCellValueFactory(new PropertyValueFactory<>("x"));
		scatterersTableYColumn.setCellValueFactory(new PropertyValueFactory<>("y"));
		scatterersTableZColumn.setCellValueFactory(new PropertyValueFactory<>("z"));

		scatterersTableOccupColumn.setCellValueFactory(new PropertyValueFactory<>("occup"));

		List<String> keys = crystalModel.scatterintPowers.stream().map((i) -> i.getName()).collect(Collectors.toList());

	}

}
