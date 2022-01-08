package cz.kfkl.mstruct.gui.ui;

import java.util.ArrayList;
import java.util.List;

import org.jfree.data.xy.XYSeries;

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
			return new SimpleDoubleProperty(table.getRows().get(rowIndex)[colIndex]);
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

	public XYSeries getXYSeries(String name, int xColIndex, int yColIndex) {
		XYSeries xySeries = new XYSeries(name);
		for (double[] row : rows) {
			xySeries.add(row[xColIndex], row[yColIndex]);
		}
		return xySeries;
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
			double[] rowVals = new double[colIndexes.length];
			for (int cI = 0; cI < colIndexes.length; cI++) {
				rowVals[cI] = rows.get(i)[cI];
			}
			values[i] = JvStringUtils.join(delim, rowVals);
		}

		return values;
	}

}
