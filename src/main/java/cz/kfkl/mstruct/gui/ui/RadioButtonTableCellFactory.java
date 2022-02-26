package cz.kfkl.mstruct.gui.ui;

import cz.kfkl.mstruct.gui.utils.RadioButtonTableCell;
import javafx.beans.value.ObservableValue;
import javafx.beans.value.WritableValue;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.ToggleGroup;
import javafx.util.Callback;

public class RadioButtonTableCellFactory<S, T> implements Callback<TableColumn<S, T>, TableCell<S, T>> {

	private ToggleGroup toggleGroup;
	private ObservableValue<T> observableValue;
	private WritableValue<T> writableValue;

	public RadioButtonTableCellFactory(WritableValue<T> selectedValue) {
		this(new ToggleGroup(), selectedValue, null);
	}

	public RadioButtonTableCellFactory(ObservableValue<T> observableValue) {
		this(new ToggleGroup(), null, observableValue);
	}

	public RadioButtonTableCellFactory(ToggleGroup toggleGroup, WritableValue<T> selectedValue,
			ObservableValue<T> observableValue) {
		super();
		this.toggleGroup = toggleGroup;
		this.observableValue = observableValue;
		this.writableValue = selectedValue;
	}

	@Override
	public TableCell<S, T> call(TableColumn<S, T> column) {
		return new RadioButtonTableCell<>(column, this);
	}

	public T getSelectedValue() {
		return writableValue == null ? observableValue.getValue() : writableValue.getValue();
	}

	public WritableValue<T> getWritableValue() {
		return writableValue;
	}

	public ToggleGroup getToggleGroup() {
		return toggleGroup;
	}

}
