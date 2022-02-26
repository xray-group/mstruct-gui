package cz.kfkl.mstruct.gui.utils;

import cz.kfkl.mstruct.gui.ui.RadioButtonTableCellFactory;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.ToggleGroup;

public class RadioButtonTableCell<S, T> extends TableCell<S, T> {

	private RadioButton radioButton;
	private RadioButtonTableCellFactory<S, T> rbFactory;
	private ToggleGroup toggleGroup;

	public RadioButtonTableCell(TableColumn<S, T> column, RadioButtonTableCellFactory<S, T> rbFactory) {

		this.radioButton = new RadioButton();
		this.rbFactory = rbFactory;

		toggleGroup = rbFactory.getToggleGroup();
		this.radioButton.setToggleGroup(toggleGroup);

		if (rbFactory.getWritableValue() != null) {
			toggleGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
				if (newValue != null && newValue.getUserData() != null) {
					rbFactory.getWritableValue().setValue((T) newValue.getUserData());
				}
			});
		}
	}

	@Override
	protected void updateItem(T item, boolean empty) {
		super.updateItem(item, empty);

		setText(null);
		if (empty || item == null) {
			setGraphic(null);
		} else {
			T value = rbFactory.getSelectedValue();
			if (item.equals(value)) {
				radioButton.setSelected(true);
			}

			radioButton.setUserData(item);
			this.setGraphic(this.radioButton);
		}
	}

}