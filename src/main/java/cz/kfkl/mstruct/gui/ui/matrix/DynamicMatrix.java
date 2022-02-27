package cz.kfkl.mstruct.gui.ui.matrix;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.kfkl.mstruct.gui.utils.BindingUtils;
import cz.kfkl.mstruct.gui.utils.DoubleTextFieldTableCell;
import cz.kfkl.mstruct.gui.utils.JvStringUtils;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class DynamicMatrix<T extends Tuple> {

	private static final Logger LOG = LoggerFactory.getLogger(DynamicMatrix.class);

	private static final int COLUMN_WIDTH = 60;
	private TableView<DynamicMatrixRow<T>> tableView;

	private ObservableList<T> tuples;
	private Supplier<T> tupleSupplier;

	private Map<TupleKey, T> tuplesIndexMap = new LinkedHashMap<>();
	private Function<T, ObservableValue<String>> valuesFunction;

	private boolean isSymetric = true;

	/**
	 * Construct square matrix with both columns and rows having same keys.
	 */
	public DynamicMatrix(TableView<DynamicMatrixRow<T>> tableView, List<String> keys,
			Function<T, ObservableValue<String>> valuesFunction) {
		this(tableView, keys, valuesFunction, true);
	}

	public DynamicMatrix(TableView<DynamicMatrixRow<T>> tableView, List<String> keys,
			Function<T, ObservableValue<String>> valuesFunction, boolean symetric) {
		this.tableView = tableView;
		this.valuesFunction = valuesFunction;
		this.isSymetric = symetric;
		this.tableView.getSelectionModel().setCellSelectionEnabled(true);

		createColumns(keys);
		createRows(tableView, keys);
	}

	private void createColumns(List<String> keys) {
		ObservableList<TableColumn<DynamicMatrixRow<T>, ?>> columns = this.tableView.getColumns();
		columns.add(createTitleColumn());
		for (String key : keys) {
			columns.add(createValueColumn(key));
		}
		this.tableView.setPrefWidth(COLUMN_WIDTH * columns.size() + 15);
	}

	private TableColumn<DynamicMatrixRow<T>, Object> createTitleColumn() {
		TableColumn<DynamicMatrixRow<T>, Object> rowTitleColumn = new TableColumn<>();
		rowTitleColumn.setCellValueFactory(new PropertyValueFactory<>("rowKey"));
		rowTitleColumn.setPrefWidth(COLUMN_WIDTH);

		rowTitleColumn.setCellFactory(tc -> {
			TableCell<DynamicMatrixRow<T>, Object> cell = new TableCell<DynamicMatrixRow<T>, Object>() {
				@Override
				protected void updateItem(Object item, boolean empty) {
					super.updateItem(item, empty);
					setText(empty || item == null ? "" : item.toString());
				}
			};
			cell.getStyleClass().add("row-title-column");
			return cell;
		});
		rowTitleColumn.setSortable(false);
		return rowTitleColumn;
	}

	private TableColumn<DynamicMatrixRow<T>, String> createValueColumn(String key) {
		TableColumn<DynamicMatrixRow<T>, String> column = new TableColumn<>(key);
		column.setPrefWidth(COLUMN_WIDTH);
		column.setSortable(false);
		column.setEditable(true);
		column.setCellValueFactory(cdf -> {
			String colKey = cdf.getTableColumn().getText();
			T tuple = cdf.getValue().getValue(colKey);
			LOG.trace("setCellValueFactory colKey [{}] rowKey [{}], tuple [{}]", colKey, cdf.getValue().getRowKey(), tuple);
			ObservableValue<String> observableValue = tuple == null ? null : this.valuesFunction.apply(tuple);
			return observableValue;
		});
		column.setCellFactory(DoubleTextFieldTableCell.forTableColumn());
		// null); column.setOnEditCommit(
		column.addEventHandler(TableColumn.<DynamicMatrixRow<T>, String>editCommitEvent(), event -> {
			String newValue = event.getNewValue();
			String rowKey = event.getRowValue().getRowKey();
			String colKey = event.getTableColumn().getText();

			LOG.trace("OnEditCommit event [{}] value [{}] rowValue [{}] tableColumn [{}]", event, newValue, event.getRowValue(),
					event.getTableColumn());
			T existingTuple = this.getTuple(rowKey, colKey);
			if (JvStringUtils.isBlank(newValue) || "0".equals(newValue)) {

				if (existingTuple != null) {
					this.removeTuple(existingTuple);
					this.tableView.refresh();
				}
			} else {

				if (existingTuple == null) {
					this.addNewTuple(rowKey, colKey);
					this.tableView.refresh();
				}
			}

		});
		return column;
	}

	private void createRows(TableView<DynamicMatrixRow<T>> tableView, List<String> keys) {
		List<DynamicMatrixRow<T>> rows = new ArrayList<>();
		for (String key : keys) {
			rows.add(new DynamicMatrixRow<T>(key, this));
		}
		this.tableView.getItems().addAll(rows);
		BindingUtils.autoHeight(tableView);
	}

	public T getTuple(String rowKey, String columnKey) {
		return tuplesIndexMap.get(isSymetric ? TupleKey.createSymetric(rowKey, columnKey) : new TupleKey(rowKey, columnKey));
	}

	private T addNewTuple(String rowKey, String columnKey) {
		T tuple;
		tuple = tupleSupplier.get();
		tuple.setRowIndex(rowKey);
		tuple.setColumnIndex(columnKey);
		tuples.add(tuple);
		tuplesIndexMap.put(createTupleKey(tuple), tuple);
		return tuple;
	}

	private T removeTuple(T tuple) {
		tuples.remove(tuple);
		tuplesIndexMap.remove(createTupleKey(tuple));
		return tuple;
	}

	public void registerTuples(ObservableList<T> tuplesList, Supplier<T> tupleSupplier) {
		this.tuples = tuplesList;
		this.tupleSupplier = tupleSupplier;

		for (T tuple : tuples) {
			TupleKey createTupleKey = createTupleKey(tuple);
			tuplesIndexMap.put(createTupleKey, tuple);
		}
	}

	private TupleKey createTupleKey(T tuple) {
		String rowIndex = tuple.getRowIndex();
		String colIndex = tuple.getColumnIndex();

		return isSymetric ? TupleKey.createSymetric(rowIndex, colIndex) : new TupleKey(rowIndex, colIndex);
	}

}
