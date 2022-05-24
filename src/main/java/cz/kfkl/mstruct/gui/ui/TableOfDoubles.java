package cz.kfkl.mstruct.gui.ui;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.ToDoubleFunction;

import cz.kfkl.mstruct.gui.utils.JvStringUtils;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ObservableValue;

public class TableOfDoubles {
	public class RowIndex {
		private TableOfDoubles table;
		private int rowIndex;

		public RowIndex(TableOfDoubles table, int index) {
			this.table = table;
			this.rowIndex = index;
		}

		public ObservableValue<Number> getObservableValue(Integer colIndex) {
			return new SimpleDoubleProperty(getValue(colIndex));
		}

		public double getValue(Integer colIndex) {
			double[] ds = table.getRows().get(rowIndex);
			return ds.length > colIndex ? ds[colIndex] : 0;
		}
	}

	private List<double[]> rows;

	public TableOfDoubles() {
		this.rows = new ArrayList<>();
	}

	public List<double[]> getRows() {
		return rows;
	}

	public void addRow(double[] row) {
		rows.add(row);
	}

	public List<RowIndex> getRowIndexes() {
		List<RowIndex> list = new ArrayList<>(rows.size());
		for (int i = 0; i < rows.size(); i++) {
			list.add(new RowIndex(this, i));
		}
		return list;
	}

	public double[] getColumnData(int colIndex) {
		double[] values = new double[rows.size()];

		for (int i = 0; i < rows.size(); i++) {
			values[i] = rows.get(i)[colIndex];
		}

		return values;
	}

	public String[] getColumnsDataJoined(String delim, int... colIndexes) {
		String[] values = new String[rows.size()];

		for (int i = 0; i < rows.size(); i++) {
			Object[] rowVals = new String[colIndexes.length];
			for (int cI = 0; cI < colIndexes.length; cI++) {
				rowVals[cI] = JvStringUtils.toStringNoDotZero(rows.get(i)[cI]);
			}
			values[i] = JvStringUtils.join(delim, rowVals);
		}

		return values;
	}

	public static Comparator<RowIndex> createComparator(int colIndex) {
		return Comparator.nullsFirst(Comparator.comparingDouble((ToDoubleFunction<RowIndex>) ri -> ri.getValue(colIndex)));
	}

}
