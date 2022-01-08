package cz.kfkl.mstruct.gui.utils.tree;

import java.util.Arrays;
import java.util.OptionalInt;
import java.util.stream.IntStream;

import javafx.collections.ObservableList;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumnBase;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTablePosition;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.TreeTableView.TreeTableViewFocusModel;
import javafx.scene.control.TreeTableView.TreeTableViewSelectionModel;

/**
 * Copied from:
 * https://stackoverflow.com/questions/58904792/javafx-treetableview-prevent-selection-of-certain-treeitems
 * 
 */
public class FilteredTreeTableViewSelectionModel<S> extends TreeTableViewSelectionModel<S> {

	private final TreeTableViewSelectionModel<S> selectionModel;
	private final SelectableTreeItemFilter<S> selectionFilter;

	public FilteredTreeTableViewSelectionModel(TreeTableView<S> treeTableView, TreeTableViewSelectionModel<S> selectionModel,
			SelectableTreeItemFilter<S> selectionFilter) {
		super(treeTableView);
		this.selectionModel = selectionModel;
		this.selectionFilter = selectionFilter;
		cellSelectionEnabledProperty().bindBidirectional(selectionModel.cellSelectionEnabledProperty());
		selectionModeProperty().bindBidirectional(selectionModel.selectionModeProperty());
	}

	@Override
	public ObservableList<Integer> getSelectedIndices() {
		return this.selectionModel.getSelectedIndices();
	}

	@Override
	public ObservableList<TreeItem<S>> getSelectedItems() {
		return this.selectionModel.getSelectedItems();
	}

	@Override
	public ObservableList<TreeTablePosition<S, ?>> getSelectedCells() {
		return this.selectionModel.getSelectedCells();
	}

	@Override
	public boolean isSelected(int index) {
		return this.selectionModel.isSelected(index);
	}

	@Override
	public boolean isSelected(int row, TableColumnBase<TreeItem<S>, ?> column) {
		return this.selectionModel.isSelected(row, column);
	}

	@Override
	public boolean isEmpty() {
		return this.selectionModel.isEmpty();
	}

	@Override
	public TreeItem<S> getModelItem(int index) {
		return this.selectionModel.getModelItem(index);
	}

	@Override
	public void focus(int row) {
		this.selectionModel.focus(row);
	}

	@Override
	public int getFocusedIndex() {
		return this.selectionModel.getFocusedIndex();
	}

	private TreeTablePosition<S, ?> getFocusedCell() {
		TreeTableView<S> treeTableView = getTreeTableView();
		TreeTableViewFocusModel<S> focusModel = treeTableView.getFocusModel();
		return (focusModel == null) ? new TreeTablePosition<>(treeTableView, -1, null) : focusModel.getFocusedCell();
	}

	private TreeTableColumn<S, ?> getTableColumn(int pos) {
		return getTreeTableView().getVisibleLeafColumn(pos);
	}

	// Gets a table column to the left or right of the current one, given an offset.
	private TreeTableColumn<S, ?> getTableColumn(TreeTableColumn<S, ?> column, int offset) {
		int columnIndex = getTreeTableView().getVisibleLeafIndex(column);
		int newColumnIndex = columnIndex + offset;
		TreeTableView<S> treeTableView = getTreeTableView();
		return treeTableView.getVisibleLeafColumn(newColumnIndex);
	}

	private int getRowCount() {
		TreeTableView<S> treeTableView = getTreeTableView();
		return treeTableView.getExpandedItemCount();
	}

	@Override
	public void select(int row) {
		select(row, null);
	}

	@Override
	public void select(int row, TableColumnBase<TreeItem<S>, ?> column) {
		// If the row is -1, we need to clear the selection.
		if (row == -1) {
			this.selectionModel.clearSelection();
		} else if (row >= 0 && row < getRowCount()) {
			// If the tree-item at the specified row-index is selectable, we
			// forward select call to the internal selection-model.
			TreeTableView<S> treeTableView = getTreeTableView();
			TreeItem<S> treeItem = treeTableView.getTreeItem(row);
			if (this.selectionFilter.isSelectable(treeItem)) {
				this.selectionModel.select(row, column);
			}
		}
	}

	@Override
	public void select(TreeItem<S> treeItem) {
		if (treeItem == null) {
			// If the provided tree-item is null, and we are in single-selection
			// mode we need to clear the selection.
			if (getSelectionMode() == SelectionMode.SINGLE) {
				this.selectionModel.clearSelection();
			}
			// Else, we just forward to the internal selection-model so that
			// the selected-index can be set to -1, and the selected-item
			// can be set to null.
			else {
				this.selectionModel.select(null);
			}
		} else if (this.selectionFilter.isSelectable(treeItem)) {
			this.selectionModel.select(treeItem);
		}
	}

	@Override
	public void selectIndices(int row, int... rows) {
		// If we have no trailing rows, we forward to normal row-selection.
		if (rows == null || rows.length == 0) {
			select(row);
			return;
		}

		// Filter rows so that we only end up with those rows whose corresponding
		// tree-items are selectable.
		TreeTableView<S> treeTableView = getTreeTableView();
		int[] filteredRows = IntStream.concat(IntStream.of(row), Arrays.stream(rows)).filter(rowToCheck -> {
			TreeItem<S> treeItem = treeTableView.getTreeItem(rowToCheck);
			return (treeItem != null) && selectionFilter.isSelectable(treeItem);
		}).toArray();

		// If we have rows left, we proceed to forward to internal selection-model.
		if (filteredRows.length > 0) {
			int newRow = filteredRows[0];
			int[] newRows = Arrays.copyOfRange(filteredRows, 1, filteredRows.length);
			this.selectionModel.selectIndices(newRow, newRows);
		}
	}

	@Override
	public void selectRange(int start, int end) {
		super.selectRange(start, end);
	}

	@Override
	public void selectRange(int minRow, TableColumnBase<TreeItem<S>, ?> minColumn, int maxRow,
			TableColumnBase<TreeItem<S>, ?> maxColumn) {
		super.selectRange(minRow, minColumn, maxRow, maxColumn);
	}

	@Override
	public void clearAndSelect(int row) {
		clearAndSelect(row, null);
	}

	@Override
	public void clearAndSelect(int row, TableColumnBase<TreeItem<S>, ?> column) {
		// If the row is out-of-bounds we just clear and return.
		if (row < 0 || row >= getRowCount()) {
			clearSelection();
			return;
		}

		TreeTableView<S> treeTableView = getTreeTableView();
		TreeItem<S> treeItem = treeTableView.getTreeItem(row);

		// If the tree-item at the specified row-index is selectable, we forward
		// clear-and-select call to the internal selection-model.
		if (this.selectionFilter.isSelectable(treeItem)) {
			this.selectionModel.clearAndSelect(row, column);
		}
		// Else, we just do a normal clear-selection call.
		else {
			this.selectionModel.clearSelection();
		}
	}

	@Override
	public void selectAll() {
		int rowCount = getRowCount();

		// If we are in single-selection mode, we exit prematurely as
		// we cannot select all rows.
		if (getSelectionMode() == SelectionMode.SINGLE) {
			return;
		}

		// If we only have a single row to select, we forward to the
		// row-index select-method.
		if (rowCount == 1) {
			select(0);
		}
		// Else, if we have more than one row available, we construct an array
		// of all the indices and forward to the selectIndices-method.
		else if (rowCount > 1) {
			int row = 0;
			int[] rows = IntStream.range(1, rowCount).toArray();
			selectIndices(row, rows);
		}
	}

	@Override
	public void clearSelection(int index) {
		this.selectionModel.clearSelection(index);
	}

	@Override
	public void clearSelection(int row, TableColumnBase<TreeItem<S>, ?> column) {
		this.selectionModel.clearSelection(row, column);
	}

	@Override
	public void clearSelection() {
		this.selectionModel.clearSelection();
	}

	@Override
	public void selectFirst() {
		// Find first selectable row in the tree-table by testing each tree-item
		// against our selection-filter.
		TreeTableView<S> treeTableView = getTreeTableView();
		OptionalInt firstRow = IntStream.range(0, getRowCount())
				.filter(row -> this.selectionFilter.isSelectable(treeTableView.getTreeItem(row))).findFirst();

		TreeTablePosition<S, ?> focusedCell = getFocusedCell();

		// If we managed to find a row, we forward to the appropriate internal
		// selection-model's select-method based on whether cell-seleciton is
		// enabled or not.
		firstRow.ifPresent(row -> {
			if (isCellSelectionEnabled()) {
				this.selectionModel.select(row, focusedCell.getTableColumn());
			} else {
				this.selectionModel.select(row);
			}
		});
	}

	@Override
	public void selectLast() {
		// Find first selectable row (by iterating in reverse) in the tree-table
		// by testing each tree-item against our selection-filter.
		int rowCount = getRowCount();
		TreeTableView<S> treeTableView = getTreeTableView();
		OptionalInt lastRow = IntStream.iterate(rowCount - 1, i -> i - 1).limit(rowCount)
				.filter(row -> this.selectionFilter.isSelectable(treeTableView.getTreeItem(row))).findFirst();

		TreeTablePosition<S, ?> focusedCell = getFocusedCell();

		// If we managed to find a row, we forward to the appropriate internal
		// selection-model's select-method based on whether cell-seleciton is
		// enabled or not.
		lastRow.ifPresent(row -> {
			if (isCellSelectionEnabled()) {
				this.selectionModel.select(row, focusedCell.getTableColumn());
			} else {
				this.selectionModel.select(row);
			}
		});
	}

	@Override
	public void selectPrevious() {
		TreeTableView<S> treeTableView = getTreeTableView();

		if (isCellSelectionEnabled()) {
			// In cell selection mode, we have to wrap around, going from
			// right-to-left, and then wrapping to the end of the previous line.
			TreeTablePosition<S, ?> pos = getFocusedCell();

			// If we are not at the first column, we go to the previous column.
			if (pos.getColumn() - 1 >= 0) {
				this.selectionModel.select(pos.getRow(), getTableColumn(pos.getTableColumn(), -1));
			}
			// Else, we wrap to end of previous selectable row.
			else {
				// If we have nothing selected, wrap around to the last index.
				int startIndex = (pos.getRow() == -1) ? getRowCount() : pos.getRow();

				// Find previous selectable row.
				OptionalInt previousRow = IntStream.iterate(startIndex - 1, i -> i - 1).limit(startIndex)
						.filter(row -> this.selectionFilter.isSelectable(treeTableView.getTreeItem(row))).findFirst();

				// Last column index.
				int lastColumnIndex = getTreeTableView().getVisibleLeafColumns().size() - 1;

				// If we have a previous row, forward selection to internal selection-model.
				previousRow.ifPresent(row -> this.selectionModel.select(row, getTableColumn(lastColumnIndex)));
			}
		} else {
			// If we have nothing selected, wrap around to the last index.
			int startIndex = (getFocusedIndex() == -1) ? getRowCount() : getFocusedIndex();
			if (startIndex > 0) {
				OptionalInt previousRow = IntStream.iterate(startIndex - 1, i -> i - 1).limit(startIndex)
						.filter(row -> this.selectionFilter.isSelectable(treeTableView.getTreeItem(row))).findFirst();
				previousRow.ifPresent(this.selectionModel::select);
			}
		}
	}

	@Override
	public void selectNext() {
		TreeTableView<S> treeTableView = getTreeTableView();

		if (isCellSelectionEnabled()) {
			// In cell selection mode, we have to wrap around, going from
			// left-to-right, and then wrapping to the start of the next line.
			TreeTablePosition<S, ?> pos = getFocusedCell();

			// If we are not at the last column, then go to the next column.
			if (pos.getRow() != -1 && pos.getColumn() + 1 < getTreeTableView().getVisibleLeafColumns().size()) {
				this.selectionModel.select(pos.getRow(), getTableColumn(pos.getTableColumn(), 1));
			}
			// Else, wrap to start of next selectable row.
			else if (pos.getRow() < getRowCount() - 1) {
				// If we have nothing selected, starting at -1 will work out correctly
				// because we'll search from 0 onwards.
				int startIndex = pos.getRow();

				// Find next selectable row.
				OptionalInt nextItem = IntStream.range(startIndex + 1, getRowCount())
						.filter(row -> this.selectionFilter.isSelectable(treeTableView.getTreeItem(row))).findFirst();

				// If we have a next row, forward selection to internal selection-model.
				nextItem.ifPresent(row -> this.selectionModel.select(row, getTableColumn(0)));
			}
		} else {
			// If we have nothing selected, starting at -1 will work out correctly
			// because we'll search from 0 onwards.
			int startIndex = getFocusedIndex();
			if (startIndex < getRowCount() - 1) {
				OptionalInt nextRow = IntStream.range(startIndex + 1, getRowCount())
						.filter(row -> this.selectionFilter.isSelectable(treeTableView.getTreeItem(row))).findFirst();
				nextRow.ifPresent(this.selectionModel::select);
			}
		}
	}

	@Override
	public void selectLeftCell() {
		if (!isCellSelectionEnabled()) {
			return;
		}

		TreeTablePosition<S, ?> pos = getFocusedCell();
		if (pos.getColumn() - 1 >= 0) {
			select(pos.getRow(), getTableColumn(pos.getTableColumn(), -1));
		}
	}

	@Override
	public void selectRightCell() {
		if (!isCellSelectionEnabled()) {
			return;
		}

		TreeTablePosition<S, ?> pos = getFocusedCell();
		if (pos.getColumn() + 1 < getTreeTableView().getVisibleLeafColumns().size()) {
			select(pos.getRow(), getTableColumn(pos.getTableColumn(), 1));
		}
	}

	@Override
	public void selectAboveCell() {
		TreeTablePosition<S, ?> pos = getFocusedCell();

		// If we have nothing selected, wrap around to the last row.
		if (pos.getRow() == -1) {
			selectLast();
		} else if (pos.getRow() > 0) {
			TreeTableView<S> treeTableView = getTreeTableView();

			// Find previous selectable row.
			OptionalInt previousRow = IntStream.iterate(pos.getRow() - 1, i -> i - 1).limit(pos.getRow())
					.filter(row -> this.selectionFilter.isSelectable(treeTableView.getTreeItem(row))).findFirst();

			// If we have a previous row, forward selection to internal selection-model.
			previousRow.ifPresent(row -> this.selectionModel.select(row, pos.getTableColumn()));
		}
	}

	@Override
	public void selectBelowCell() {
		TreeTablePosition<S, ?> pos = getFocusedCell();

		// If we have nothing selected, start at the first row.
		if (pos.getRow() == -1) {
			selectFirst();
		} else if (pos.getRow() < getRowCount() - 1) {
			TreeTableView<S> treeTableView = getTreeTableView();

			// Find next selectable row.
			OptionalInt nextItem = IntStream.range(pos.getRow() + 1, getRowCount())
					.filter(row -> this.selectionFilter.isSelectable(treeTableView.getTreeItem(row))).findFirst();

			// If we have a next row, forward selection to internal selection-model.
			nextItem.ifPresent(row -> this.selectionModel.select(row, pos.getTableColumn()));
		}
	}
}