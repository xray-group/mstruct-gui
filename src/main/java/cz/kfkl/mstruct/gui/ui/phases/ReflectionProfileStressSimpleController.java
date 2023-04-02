package cz.kfkl.mstruct.gui.ui.phases;

import static cz.kfkl.mstruct.gui.utils.BindingUtils.autoHeight;
import static cz.kfkl.mstruct.gui.utils.BindingUtils.bindAndBuildParFieldsNoName;
import static cz.kfkl.mstruct.gui.utils.BindingUtils.bindDoubleTableColumn;
import static cz.kfkl.mstruct.gui.utils.BindingUtils.bindStringTableColumn;
import static cz.kfkl.mstruct.gui.utils.BindingUtils.doWhenFocuseLost;

import cz.kfkl.mstruct.gui.model.phases.ReflectionProfileStressSimpleElement;
import cz.kfkl.mstruct.gui.model.phases.StiffnessConstantElement;
import cz.kfkl.mstruct.gui.ui.BaseController;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

public class ReflectionProfileStressSimpleController
		extends BaseController<ReflectionProfileStressSimpleElement, PowderPatternCrystalController> {

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
	public void init() {
		ReflectionProfileStressSimpleElement model = getModelInstance();

		componentNameTextField.textProperty().bindBidirectional(model.getNameProperty());
		componentTypeLabel.textProperty().set(model.getType().toString());
		doWhenFocuseLost(componentNameTextField, () -> getParentController().componentNameChanged());

		bindAndBuildParFieldsNoName(stressParContainer, model.stressPar);
		bindAndBuildParFieldsNoName(rvWeightParContainer, model.xECsReussVoigt.rvWeightPar);

		stiffnessConstantsTableView.setEditable(true);
		stiffnessConstantsTableView.setItems(model.xECsReussVoigt.stiffnessConstants);
		autoHeight(stiffnessConstantsTableView);
		stiffnessConstantsTableView.setSortPolicy(tw -> {
			FXCollections.sort(tw.getItems());
			return true;
		});

		bindStringTableColumn(stiffnessConstantNameTableColumn, v -> v.nameProperty);
		stiffnessConstantNameTableColumn.addEventHandler(TableColumn.editCommitEvent(), t -> {
			Platform.runLater(() -> stiffnessConstantsTableView.sort());
		});
		// method to add a TableColumn.editCommitEvent() EventType withyour desired
		// EventHandler as the second argument.

		bindDoubleTableColumn(stiffnessConstantValueTableColumn, v -> v.valueProperty);
		// When variable is renamed, re-evaluate formula
		// coefficientValueTableColumn.setOnEditCommit(event -> parseFormula());

		stiffnessConstantRemoveButton.disableProperty()
				.bind(stiffnessConstantsTableView.getSelectionModel().selectedItemProperty().isNull());

	}

	@FXML
	public void onStiffnessConstantAddButton() {
		StiffnessConstantElement newConstant = new StiffnessConstantElement();
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
