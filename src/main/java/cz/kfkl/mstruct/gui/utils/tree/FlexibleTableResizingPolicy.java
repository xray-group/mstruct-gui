package cz.kfkl.mstruct.gui.utils.tree;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.scene.control.TableColumnBase;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.util.Callback;

public class FlexibleTableResizingPolicy implements Callback<TreeTableView.ResizeFeatures, Boolean> {

	private static final Logger LOG = LoggerFactory.getLogger(FlexibleTableResizingPolicy.class);

	private static final int MIN_COL_WIDTH = 5;
	private static final int MAX_COL_WIDTH = 2000;

	private boolean isFirstRun = true;

	@Override
	public String toString() {
		return "limitted-flex-resize";
	}

	@Override
	public Boolean call(TreeTableView.ResizeFeatures prop) {
		TreeTableView<?> table = prop.getTable();
		List<? extends TableColumnBase<?, ?>> visibleLeafColumns = table.getVisibleLeafColumns();

		double totalColWidth = 0;
		double totalColMinWidth = 0;
		double totalColMaxWidth = 0;
		double totalResizableColsMaxWidth = 0;

		double tableWidth = table.getWidth();
		Double delta = prop.getDelta();
		TreeTableColumn column = prop.getColumn();

		for (TableColumnBase<?, ?> col : visibleLeafColumns) {
			if (col.isVisible()) {
				totalColWidth += col.getWidth();

				if (col.isResizable()) {
					double normMin = normMin(col.getMinWidth(), tableWidth);
					totalColMinWidth += normMin;
					totalColMaxWidth += normMax(col.getMaxWidth(), normMin);
					totalResizableColsMaxWidth += col.getPrefWidth();
				} else {
					totalColMinWidth += col.getWidth();
					totalColMaxWidth += col.getWidth();
				}
			}
		}

		if (tableWidth < totalColMinWidth) {
			for (TableColumnBase<?, ?> col : visibleLeafColumns) {
				if (col.isVisible() && col.isResizable()) {
					col.setPrefWidth(normMin(col.getMinWidth(), tableWidth));
				}
			}
		} else if (tableWidth > totalColMaxWidth) {
			for (TableColumnBase<?, ?> col : visibleLeafColumns) {
				if (col.isVisible() && col.isResizable()) {
					double normMin = normMin(col.getMinWidth(), tableWidth);
					col.setPrefWidth(normMax(col.getMaxWidth(), normMin));
				}
			}
		} else {
			double distributableWidth = tableWidth - totalColMinWidth - 20 - delta; // 14 measured width of the scroll bar
			if (distributableWidth > 0.5) {
				for (TableColumnBase<?, ?> col : visibleLeafColumns) {
					if (col.isVisible() && col.isResizable()) {

						double normMin = normMin(col.getMinWidth(), tableWidth);
						double normMax = normMax(col.getMaxWidth(), normMin);
//						if (col.equals(column)) {
//							col.setPrefWidth(
//									normMin + delta + distributableWidth * (col.getPrefWidth()) / totalResizableColsMaxWidth);
//						} else 
						{
							col.setPrefWidth(normMin + distributableWidth * (col.getPrefWidth()) / totalResizableColsMaxWidth);
						}
					}
				}
			}

		}

		LOG.trace("Delta [{}], column [{}]", delta, column == null ? "null" : column.getText());
		return true;
	}

	private double normMin(double minWidth, double tableWidth) {
		minWidth = minWidth > tableWidth ? tableWidth : minWidth;
		return minWidth < 1 ? MIN_COL_WIDTH : minWidth;
	}

	private double normMax(double maxWidth, double normMin) {
		maxWidth = maxWidth < normMin ? normMin : maxWidth;
		return maxWidth > MAX_COL_WIDTH ? MAX_COL_WIDTH : maxWidth;
	}

}
