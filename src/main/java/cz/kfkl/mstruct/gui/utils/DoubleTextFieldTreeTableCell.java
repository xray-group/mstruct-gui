package cz.kfkl.mstruct.gui.utils;

import javafx.scene.control.TextField;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.util.Callback;
import javafx.util.StringConverter;

public class DoubleTextFieldTreeTableCell<S, T> extends TreeTableCell<S, T> {

	/***************************************************************************
	 * * Static cell factories * *
	 **************************************************************************/

	public static <S> Callback<TreeTableColumn<S, String>, TreeTableCell<S, String>> forTreeTableColumn() {
		return list -> new DoubleTextFieldTreeTableCell<S, String>();
	}

	private TextField textField;
	private DoubleTextFormatter formatter;

	/**
	 * Creates a default {@link DoubleTextFieldTreeTableCell} with
	 * {@link DoubleTextFormatter}.
	 */
	public DoubleTextFieldTreeTableCell() {
		super();
		this.getStyleClass().add("double-text-field-table-cell");
		this.formatter = new DoubleTextFormatter();
	}

	/***************************************************************************
	 * * Properties * *
	 **************************************************************************/

	/***************************************************************************
	 * * Public API * *
	 **************************************************************************/

	/** {@inheritDoc} */
	@Override
	public void startEdit() {
		if (!isEditable() || !getTreeTableView().isEditable() || !getTableColumn().isEditable()) {
			return;
		}
		super.startEdit();

		if (isEditing()) {
			if (textField == null) {
				textField = createTextField();
			}

			UseCellUtils.startEdit(this, getConverter(), null, null, textField);
		}
	}

	private TextField createTextField() {
		TextField createTextField = UseCellUtils.createTextField(this, getConverter());
		createTextField.setTextFormatter(formatter);
		return createTextField;
	}

	private StringConverter getConverter() {
		return DoubleTextFormatter.stringDoubleConverter;
	}

	/** {@inheritDoc} */
	@Override
	public void cancelEdit() {
		super.cancelEdit();
		UseCellUtils.cancelEdit(this, getConverter(), null);
	}

	/** {@inheritDoc} */
	@Override
	public void updateItem(T item, boolean empty) {
		super.updateItem(item, empty);
		UseCellUtils.updateItem(this, getConverter(), null, null, textField);
	}
}
