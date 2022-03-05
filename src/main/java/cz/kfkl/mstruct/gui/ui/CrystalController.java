package cz.kfkl.mstruct.gui.ui;

import static cz.kfkl.mstruct.gui.utils.BindingUtils.autoHeight;
import static cz.kfkl.mstruct.gui.utils.BindingUtils.bindAndBuildParFieldsNoName;
import static cz.kfkl.mstruct.gui.utils.BindingUtils.bindDoubleTextField;
import static cz.kfkl.mstruct.gui.utils.BindingUtils.bindToggleGroupToPropertyByText;
import static cz.kfkl.mstruct.gui.utils.BindingUtils.doWhenFocuseLost;
import static cz.kfkl.mstruct.gui.utils.BindingUtils.setupSelectionToChildrenListener;

import java.util.List;
import java.util.stream.Collectors;

import cz.kfkl.mstruct.gui.model.AntiBumpDistanceElement;
import cz.kfkl.mstruct.gui.model.BondValenceRoElement;
import cz.kfkl.mstruct.gui.model.CrystalModel;
import cz.kfkl.mstruct.gui.model.ScattererModel;
import cz.kfkl.mstruct.gui.model.ScatteringPowerAtomElement;
import cz.kfkl.mstruct.gui.model.ScatteringPowerModel;
import cz.kfkl.mstruct.gui.ui.matrix.DynamicMatrix;
import cz.kfkl.mstruct.gui.ui.matrix.DynamicMatrixRow;
import cz.kfkl.mstruct.gui.utils.ImageWithBackgroud;
import cz.kfkl.mstruct.gui.utils.ImageWithBackgroudTableCell;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;

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
	private TableColumn<ScatteringPowerModel, ImageWithBackgroud> scatPowersTableIconColumn;
	@FXML
	private TableColumn<ScatteringPowerModel, String> scatPowersTableNameColumn;
	@FXML
	private TableColumn<ScatteringPowerModel, String> scatPowersTableSymbolColumn;
//	@FXML
//	private TableColumn<ScatteringPowerModel, Color> scatPowersTableColourColumn;
//	@FXML
//	private TableColumn<ScatteringPowerModel, String> scatPowersTableBisoValueColumn;
	@FXML
	private StackPane scatPowersDetailsStackPane;

	@FXML
	private TextField antiBumpScaleTextField;
	@FXML
	private TableView<DynamicMatrixRow<AntiBumpDistanceElement>> antiBumpTableView;

	@FXML
	private TextField bondValenceScaleTextField;
	@FXML
	private TableView<DynamicMatrixRow<BondValenceRoElement>> bondValenceTableView;

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
		doWhenFocuseLost(crystalName, () -> getAppContext().getMainController().getCrystalsListView().refresh());
		spaceGroup.textProperty().bindBidirectional(crystalModel.getSpaceGroupProperty());

		bindToggleGroupToPropertyByText(constrainLatticeToggleGroup, crystalModel.constrainLatticeOption);
		bindToggleGroupToPropertyByText(useOccupancyCorrectionToggleGroup, crystalModel.useOccupancyCorrectionOption);
		bindToggleGroupToPropertyByText(displayEnantiomerToggleGroup, crystalModel.constrainLatticeOption);

		bindAndBuildParFieldsNoName(aParContainer, crystalModel.aPar);
		bindAndBuildParFieldsNoName(bParContainer, crystalModel.bPar);
		bindAndBuildParFieldsNoName(cParContainer, crystalModel.cPar);

		bindAndBuildParFieldsNoName(alphaParContainer, crystalModel.alphaPar);
		bindAndBuildParFieldsNoName(betaParContainer, crystalModel.betaPar);
		bindAndBuildParFieldsNoName(gammaParContainer, crystalModel.gammaPar);

		scatPowersTableView.getItems().addAll(crystalModel.scatterintPowers);
		scatPowersTableView.setEditable(true);
		setupSelectionToChildrenListener(this, scatPowersTableView.getSelectionModel().selectedItemProperty(),
				scatPowersDetailsStackPane.getChildren(), getAppContext());
		autoHeight(scatPowersTableView, 28);

		scatPowersTableIconColumn.setCellValueFactory(new PropertyValueFactory<>("typeGraphics"));
		scatPowersTableIconColumn.setCellFactory(column -> new ImageWithBackgroudTableCell<>(column));

		scatPowersTableNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
		scatPowersTableNameColumn.setEditable(true);
		scatPowersTableSymbolColumn.setCellValueFactory(new PropertyValueFactory<>("symbol"));
		scatPowersTableSymbolColumn.setEditable(true);

//		scatPowersTableColourColumn.setCellValueFactory(new PropertyValueFactory<ScatteringPowerModel, Color>("color"));
//		scatPowersTableColourColumn.setCellFactory(column -> new ColorTableCell<>(column));
//		scatPowersTableColourColumn.setOnEditCommit(t -> {
//			t.getTableView().getItems().get(t.getTablePosition().getRow()).setColor(t.getNewValue());
//			scatPowersTableView.refresh();
//		});

//		scatPowersTableBisoValueColumn.setCellValueFactory(new PropertyValueFactory<>("biso"));

		List<String> scatPowNames = crystalModel.scatterintPowers.stream().filter(i -> i instanceof ScatteringPowerAtomElement)
				.map((i) -> i.getName()).collect(Collectors.toList());

		bindDoubleTextField(antiBumpScaleTextField, crystalModel.antiBumpScale.valueProperty);
		antiBumpTableView.setEditable(true);
		DynamicMatrix<AntiBumpDistanceElement> antiBumpDistanceDynamicMatrix = new DynamicMatrix<>(antiBumpTableView,
				scatPowNames, v -> v.valueProperty);
		antiBumpDistanceDynamicMatrix.registerTuples(crystalModel.antiBumpDistances, AntiBumpDistanceElement::new);

		bindDoubleTextField(bondValenceScaleTextField, crystalModel.bondValenceCostScale.valueProperty);
		bondValenceTableView.setEditable(true);
		DynamicMatrix<BondValenceRoElement> bondValenceDynamicMatrix = new DynamicMatrix<>(bondValenceTableView, scatPowNames,
				v -> v.valueProperty);
		bondValenceDynamicMatrix.registerTuples(crystalModel.bondValences, BondValenceRoElement::new);

		scatterersTableView.getItems().addAll(crystalModel.scatterers);
		scatterersTableView.setEditable(true);
		setupSelectionToChildrenListener(this, scatterersTableView.getSelectionModel().selectedItemProperty(),
				scatterresDetailsStackPane.getChildren(), getAppContext());
		autoHeight(scatterersTableView);

		scatterersTableNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
		scatterersTableNameColumn.setEditable(true);
//		scatterersTableScatteringPowerColumn.setCellValueFactory(new PropertyValueFactory<>("scattPow"));
//		scatterersTableScatteringPowerColumn.setEditable(true);
		scatterersTableXColumn.setCellValueFactory(new PropertyValueFactory<>("x"));
		scatterersTableYColumn.setCellValueFactory(new PropertyValueFactory<>("y"));
		scatterersTableZColumn.setCellValueFactory(new PropertyValueFactory<>("z"));

		scatterersTableOccupColumn.setCellValueFactory(new PropertyValueFactory<>("occup"));

	}

	public void scatteringPowerRecordChanged() {
		scatPowersTableView.refresh();
	}

}
