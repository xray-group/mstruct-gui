package cz.kfkl.mstruct.gui.utils.tree;

import java.util.HashSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumnBase;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTablePosition;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.TreeTableView.TreeTableViewSelectionModel;

/**
 * Copied from:
 * https://stackoverflow.com/questions/58904792/javafx-treetableview-prevent-selection-of-certain-treeitems
 * 
 */
public class DirectTreeTableViewSelectionModel<S> extends TreeTableViewSelectionModel<S> {

	private static final Logger LOG = LoggerFactory.getLogger(DirectTreeTableViewSelectionModel.class);

	private final SelectableTreeItemFilter<S> selectionFilter;
	private HashSet<TreeItem<S>> selectedItems;
	private ObservableList<TreeTablePosition<S, ?>> selectedCells;
	private ObservableList<Integer> selectedIndices;

	public DirectTreeTableViewSelectionModel(TreeTableView<S> treeTableView, SelectableTreeItemFilter<S> selectionFilter) {
		super(treeTableView);
		this.selectionFilter = selectionFilter;
		this.selectedItems = new HashSet<>();
		this.selectedCells = FXCollections.observableArrayList();
		this.selectedIndices = FXCollections.observableArrayList();

	}

	@Override
	public ObservableList<TreeTablePosition<S, ?>> getSelectedCells() {
		println("getSelectedCells");
		return selectedCells;
	}

	private void println(String format, Object... args) {
		if (LOG.isTraceEnabled()) {
			LOG.trace(" ** {}", String.format(format, args));
		}
	}

	@Override
	public boolean isSelected(int row, TableColumnBase<TreeItem<S>, ?> column) {
		println("isSelected row [%s], column [%s], treeItem [%s]", row, column, getTreeItem(row));
		return isSelectedImpl(row);
	}

	private boolean isSelectedImpl(int row) {
		return selectedItems.contains(getTreeItem(row));
	}

	private TreeItem<S> getTreeItem(int row) {
		return getTreeTableView().getTreeItem(row);
	}

	@Override
	public void select(int row, TableColumnBase<TreeItem<S>, ?> column) {
		println("select row [%s], column [%s], treeItem [%s]", row, column, getTreeItem(row));
		selectImpl(row);
	}

	private void selectImpl(int row) {

		TreeItem<S> treeItem = getTreeItem(row);

		if (treeItem != null && selectionFilter.isSelectable(treeItem)) {
			selectedItems.add(treeItem);
			TreeTablePosition<S, ?> ttp = new TreeTablePosition<>(getTreeTableView(), row, null);
			selectedCells.add(ttp);
			selectedIndices.add(row);
		}
	}

	private void unselectImpl(int row) {
		selectedItems.remove(getTreeItem(row));
		TreeTablePosition<S, ?> ttp = new TreeTablePosition<>(getTreeTableView(), row, null);
		selectedCells.remove(ttp); // TODO won't work
		selectedIndices.remove(Integer.valueOf(row));
	}

	@Override
	public void clearAndSelect(int row, TableColumnBase<TreeItem<S>, ?> column) {
		println("clearAndSelect row [%s], column [%s], treeItem [%s]", row, column, getTreeItem(row));
		clearAllImpl();
		selectImpl(row);
	}

	private void clearAllImpl() {
		selectedItems.clear();
		selectedCells.clear();
		selectedIndices.clear();
	}

	@Override
	public void clearSelection(int row, TableColumnBase<TreeItem<S>, ?> column) {
		println("clearSelection row [%s], column [%s], treeItem [%s]", row, column, getTreeItem(row));
		unselectImpl(row);
	}

	@Override
	public void selectLeftCell() {
		println("selectLeftCell");
	}

	@Override
	public void selectRightCell() {
		println("selectRightCell");
	}

	@Override
	public void selectAboveCell() {
		println("selectAboveCell");
	}

	@Override
	public void selectBelowCell() {
		println("selectBelowCell");
	}

	/***
	 * TreeTableViewSelectionModel methods
	 */

	@Override
	public TreeTableView<S> getTreeTableView() {
//		println("TreeTableViewSelectionModel.getTreeTableView");
		return super.getTreeTableView();
	}

	/** {@inheritDoc} */
	@Override
	public TreeItem<S> getModelItem(int index) {
		return super.getModelItem(index);
	}

	/** {@inheritDoc} */
	@Override
	protected int getItemCount() {
		int itemCount = super.getItemCount();
		println("TreeTableViewSelectionModel.getItemCount, returns [%s]", itemCount);

		return itemCount;
	}

	/** {@inheritDoc} */
	@Override
	public void focus(int row) {
		println("TreeTableViewSelectionModel.focus, row [%s]", row);
		super.focus(row);
	}

	/** {@inheritDoc} */
	@Override
	public int getFocusedIndex() {
		int focusedIndex = super.getFocusedIndex();
		println("TreeTableViewSelectionModel.focus focusedIndex [%s]", focusedIndex);

		return focusedIndex;
	}

	/** {@inheritDoc} */
	@Override
	public void selectRange(int startRow, TableColumnBase<TreeItem<S>, ?> minColumn, int endRow,
			TableColumnBase<TreeItem<S>, ?> maxColumn) {
		println("TreeTableViewSelectionModel.selectRange minRow [%s], maxRow [%s], minColumn [%s], maxColumn [%s]", startRow,
				endRow, minColumn, maxColumn);
//		super.selectRange(minRow, minColumn, maxRow, maxColumn);

		selectRowsImpl(startRow, endRow);
	}

	private void selectRowsImpl(int startRow, int endRow) {
		final int minRow = Math.min(startRow, endRow);
		final int maxRow = Math.max(startRow, endRow);

		for (int row = minRow; row <= maxRow; row++) {
			selectImpl(row);
		}
	}

	/**
	 * MultipleSelectionModelBase
	 */

	@Override
	public ObservableList<Integer> getSelectedIndices() {
		println("getSelectedIndices");

		return selectedIndices;
	}

	@Override
	public ObservableList<TreeItem<S>> getSelectedItems() {
		println("getSelectedItems");
		return super.getSelectedItems();
	}

	// package only
//	void shiftSelection(int position, int shift, final Callback<ShiftParams, Void> callback) {
//		shiftSelection(Arrays.asList(new Pair<>(position, shift)), callback);
//	}

	@Override
	public void select(int row) {
		println("MultipleSelectionModelBase.select, row [%s]", row);
		selectImpl(row);
	}

	@Override
	public void select(TreeItem<S> obj) {
		println("MultipleSelectionModelBase.select, obj [%s]", obj);
		int row = getTreeTableView().getRow(obj);
		selectImpl(row); // TODO maybe not the most effective
//		super.select(obj);
	}

	@Override
	public void selectIndices(int row, int... rows) {
		println("MultipleSelectionModelBase.selectIndices, row [%s], rows [%s]", row, rows);
		super.selectIndices(row, rows);
	}

	@Override
	public void selectAll() {
		println("MultipleSelectionModelBase.selectAll");
//		super.selectAll();

		selectRowsImpl(0, getItemCount());
	}

	@Override
	public void selectFirst() {
		println("MultipleSelectionModelBase.selectFirst");
		super.selectFirst();
	}

	@Override
	public void selectLast() {
		println("MultipleSelectionModelBase.selectLast");
		super.selectLast();
	}

	@Override
	public void clearSelection(int index) {
		println("MultipleSelectionModelBase.clearSelection, index [%s]", index);
//		super.clearSelection(index);
		unselectImpl(index);

	}

	@Override
	public void clearSelection() {
		println("MultipleSelectionModelBase.clearSelection");
		clearAllImpl();
	}

	@Override
	public boolean isSelected(int index) {

//		boolean selected = filterableTreeItem.isTreeItemSelectable();
		boolean selected = isSelectedImpl(index);

		println("MultipleSelectionModelBase.isSelected, index[%s], returned [%s]", index, selected);
		return selected;

	}

	@Override
	public boolean isEmpty() {
//		boolean empty = super.isEmpty();
		boolean empty = selectedItems.isEmpty();
		println("MultipleSelectionModelBase.isEmpty, returned [%s]", empty);
		return empty;
	}

	@Override
	public void selectPrevious() {
		println("MultipleSelectionModelBase.selectPrevious");
		super.selectPrevious();
	}

	@Override
	public void selectNext() {
		println("MultipleSelectionModelBase.selectNext");
		super.selectNext();
	}

	/**
	 * MultipleSelectionModel
	 */

	@Override
	public void selectRange(final int start, final int end) {
		println("MultipleSelectionModel.selectRange minRow [%s], maxRow [%s]", start, end);

		selectRowsImpl(start, end);
	}

}