package cz.kfkl.mstruct.gui.utils.ic;

import com.google.common.base.Strings;

/**
 * Simple helper class to format tabular data in aligned rows. Row widths are
 * set in the create method. Use negative numbers for left align, positive for
 * right align where 15 is a default.
 * <p>
 * It is intended to be used only for logging.
 */
public class TableReportBuilder {
	private static final String NEW_LINE = "\n";
	private static final String INDENT = "  ";
	private static final int DEFAULT_WIDTH = 15;

	private Integer[] columnWidths;
	private StringBuilder sb;

	private TableReportBuilder(Integer[] columnWidths) {
		this.columnWidths = columnWidths;
		this.sb = new StringBuilder();
	}

	/**
	 * Creates table builder with provided column widths. Negative numbers will
	 * align column values left, positive right.
	 */
	public static TableReportBuilder create(Integer... columnWidths) {
		return new TableReportBuilder(columnWidths);
	}

	public void appendLine(Object... values) {
		sb.append(INDENT);
		for (int i = 0; i < values.length; i++) {
			int width = decideWidth(i);
			String strValue = toString(values[i]);
			String paddedValue = width >= 0 ? Strings.padStart(strValue, width, ' ') : Strings.padEnd(strValue, -width, ' ');
			sb.append(paddedValue);
		}
		sb.append(NEW_LINE);
	}

	private String toString(Object object) {
		return object == null ? "" : object.toString();
	}

	private int decideWidth(int i) {
		return i < columnWidths.length ? columnWidths[i] : DEFAULT_WIDTH;
	}

	/**
	 * Returns the formatted table as string.
	 */
	public String format() {
		return sb.toString();
	}

}
