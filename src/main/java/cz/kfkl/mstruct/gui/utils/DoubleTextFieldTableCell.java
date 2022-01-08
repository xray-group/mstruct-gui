package cz.kfkl.mstruct.gui.utils;

import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextField;
import javafx.util.Callback;
import javafx.util.StringConverter;

public class DoubleTextFieldTableCell<S, T> extends TableCell<S, T> {

	/***************************************************************************
	 * * Static cell factories * *
	 **************************************************************************/

	public static <S> Callback<TableColumn<S, String>, TableCell<S, String>> forTableColumn() {
		return list -> new DoubleTextFieldTableCell<S, String>();
	}

	private TextField textField;
	private DoubleTextFormatter formatter;

	/**
	 * Creates a default {@link DoubleTextFieldTableCell} with
	 * {@link DoubleTextFormatter}.
	 */
	public DoubleTextFieldTableCell() {
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
		if (!isEditable() || !getTableView().isEditable() || !getTableColumn().isEditable()) {
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
