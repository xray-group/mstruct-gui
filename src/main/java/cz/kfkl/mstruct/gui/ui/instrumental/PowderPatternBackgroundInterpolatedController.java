package cz.kfkl.mstruct.gui.ui.instrumental;

import static cz.kfkl.mstruct.gui.utils.BindingUtils.autoHeight;
import static cz.kfkl.mstruct.gui.utils.BindingUtils.bindAndBuildRadioButtonsOption;
import static cz.kfkl.mstruct.gui.utils.BindingUtils.doWhenFocuseLost;

import java.util.Comparator;

import cz.kfkl.mstruct.gui.model.instrumental.PowderPatternBackgroundInterpolated;
import cz.kfkl.mstruct.gui.model.instrumental.XIntensityListItem;
import cz.kfkl.mstruct.gui.ui.BaseController;
import cz.kfkl.mstruct.gui.utils.DoubleTextFieldTableCell;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.layout.HBox;

public class PowderPatternBackgroundInterpolatedController
		extends BaseController<PowderPatternBackgroundInterpolated, PowderPatternController> {

	@FXML
	private TextField powderPatternComponentName;
	@FXML
	private Label powderPatternComponentTypeLabel;

	@FXML
	private HBox interpolationModelOptionContainer;

	@FXML
	private TextField powderPatternComponentScaleTextField;

	@FXML
	private TableView<XIntensityListItem> xIntensityListTableView;

	@FXML
	private TableColumn<XIntensityListItem, Number> xIntensityListXTableColumn;
	@FXML
	private TableColumn<XIntensityListItem, Number> xIntensityListYTableColumn;
	@FXML
	private TableColumn<XIntensityListItem, Boolean> xIntensityListRefinedTableColumn;

	@FXML
	private Button xIntensityListAddButton;
	@FXML
	private Button xIntensityListRemoveButton;

	@Override
	public void init() {
		PowderPatternBackgroundInterpolated model = getModelInstance();

		powderPatternComponentName.textProperty().bindBidirectional(model.getNameProperty());
		powderPatternComponentTypeLabel.textProperty().set(model.getType().getTypeName());

		doWhenFocuseLost(powderPatternComponentName, () -> getParentController().powderPatternComponentNameChanged());

		bindAndBuildRadioButtonsOption(interpolationModelOptionContainer, model.interpolationModelOption);

		powderPatternComponentScaleTextField.textProperty().bindBidirectional(model.powderPatternComponent.scaleProperty);

		xIntensityListTableView.getItems().clear();

		xIntensityListTableView.setEditable(true);
		xIntensityListTableView.setItems(model.xIntensityList);
		autoHeight(xIntensityListTableView);
		Comparator<XIntensityListItem> comparator = Comparator.nullsFirst(Comparator.comparingDouble(ri -> ri.x.get()));
		xIntensityListTableView.setSortPolicy(tw -> {
			FXCollections.sort(tw.getItems(), comparator);
			return true;
		});

		xIntensityListXTableColumn.setCellValueFactory((cdf) -> cdf.getValue().x);
		xIntensityListXTableColumn.setCellFactory(DoubleTextFieldTableCell.forNumberTableColumn());
		xIntensityListXTableColumn.addEventHandler(TableColumn.editCommitEvent(), t -> {
			Platform.runLater(() -> xIntensityListTableView.sort());
		});

		xIntensityListYTableColumn.setCellValueFactory((cdf) -> cdf.getValue().y);
		xIntensityListYTableColumn.setCellFactory(DoubleTextFieldTableCell.forNumberTableColumn());

		xIntensityListRefinedTableColumn.setCellValueFactory(cdf -> cdf.getValue().refinedProperty);
		xIntensityListRefinedTableColumn.setCellFactory(CheckBoxTableCell.forTableColumn(xIntensityListRefinedTableColumn));

		xIntensityListRemoveButton.disableProperty()
				.bind(xIntensityListTableView.getSelectionModel().selectedItemProperty().isNull());
	}

	@FXML
	public void addXIntensityListItem() {
		XIntensityListItem newItem = new XIntensityListItem();
		xIntensityListTableView.getItems().add(newItem);
		xIntensityListTableView.getSelectionModel().select(newItem);
	}

	@FXML
	public void removeXIntensityListItem() {
		XIntensityListItem selectedItem = xIntensityListTableView.getSelectionModel().getSelectedItem();
		if (selectedItem != null) {
			xIntensityListTableView.getItems().remove(selectedItem);
		}
	}

}
