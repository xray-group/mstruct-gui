package cz.kfkl.mstruct.gui.ui;

import cz.kfkl.mstruct.gui.model.ReflectionProfileStressSimpleElement;
import cz.kfkl.mstruct.gui.model.StiffnessConstantElement;
import cz.kfkl.mstruct.gui.utils.BindingUtils;
import cz.kfkl.mstruct.gui.utils.DoubleTextFieldTableCell;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.HBox;

public class ReflectionProfileStressSimpleController extends BaseController<ReflectionProfileStressSimpleElement> {

	@FXML
	private TextField componentNameTextField;
	@FXML
	private Label componentTypeLabel;

	@FXML
	private HBox stressParContainer;
	@FXML
	private HBox rvWeightParContainer;

	@FXML
	private TableView<StiffnessConstantElement> stiffnessConstantsTableView;
	@FXML
	private TableColumn<StiffnessConstantElement, String> stiffnessConstantNameTableColumn;
	@FXML
	private TableColumn<StiffnessConstantElement, String> stiffnessConstantValueTableColumn;

	@FXML
	private Button stiffnessConstantAddButton;
	@FXML
	private Button stiffnessConstantRemoveButton;

	@Override
	public void bindToInstance() {
		ReflectionProfileStressSimpleElement model = getModelInstance();

		componentNameTextField.textProperty().bindBidirectional(model.getNameProperty());
		componentTypeLabel.textProperty().set(model.getType().toString());

		BindingUtils.bindAndBuildParFieldsNoName(stressParContainer, model.stressPar);
		BindingUtils.bindAndBuildParFieldsNoName(rvWeightParContainer, model.xECsReussVoigt.rvWeightPar);

		stiffnessConstantsTableView.setEditable(true);
		stiffnessConstantsTableView.setItems(model.xECsReussVoigt.stiffnessConstants);
		BindingUtils.autoHeight(stiffnessConstantsTableView);
		stiffnessConstantsTableView.setSortPolicy(tw -> {
			FXCollections.sort(tw.getItems());
			return true;
		});

		stiffnessConstantNameTableColumn.setCellValueFactory(c -> c.getValue().nameProperty);
		stiffnessConstantNameTableColumn.setCellFactory(TextFieldTableCell.forTableColumn());
		stiffnessConstantNameTableColumn.setEditable(true);

		stiffnessConstantNameTableColumn.addEventHandler(TableColumn.editCommitEvent(), t -> {
			Platform.runLater(() -> stiffnessConstantsTableView.sort());
		});
		// method to add a TableColumn.editCommitEvent() EventType withyour desired
		// EventHandler as the second argument.

		stiffnessConstantValueTableColumn.setCellValueFactory(c -> c.getValue().valueProperty);
		stiffnessConstantValueTableColumn.setCellFactory(DoubleTextFieldTableCell.forTableColumn());
		// When variable is renamed, re-evaluate formula
		// coefficientValueTableColumn.setOnEditCommit(event -> parseFormula());

		stiffnessConstantValueTableColumn.setEditable(true);

		stiffnessConstantRemoveButton.disableProperty()
				.bind(stiffnessConstantsTableView.getSelectionModel().selectedItemProperty().isNull());

	}

	@FXML
	public void onStiffnessConstantAddButton() {
		StiffnessConstantElement newConstant = new StiffnessConstantElement("M");
		stiffnessConstantsTableView.getItems().add(newConstant);
		stiffnessConstantsTableView.getSelectionModel().select(newConstant);
	}

	@FXML
	public void onStiffnessConstantRemoveButton() {

		StiffnessConstantElement selectedItem = stiffnessConstantsTableView.getSelectionModel().getSelectedItem();
		if (selectedItem != null) {
			stiffnessConstantsTableView.getItems().remove(selectedItem);
		}
	}

}
