package cz.kfkl.mstruct.gui.ui;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import com.google.common.base.CharMatcher;
import com.google.common.base.Splitter;

import cz.kfkl.mstruct.gui.utils.validation.UnexpectedException;

public class TabularDataParser {

	public static final double DEFAULT_DOUBLE = 0;

	private static final String COMMENT_PREFIX = "#";

	private Integer expectedRows;

	private Splitter splitter;

	public TabularDataParser() {
		splitter = Splitter.on(CharMatcher.whitespace()).omitEmptyStrings().trimResults();
	}

	public TableOfDoubles parse(File datFile) {
		try (Scanner sc = new Scanner(datFile)) {
			return doParse(sc);
		} catch (FileNotFoundException e) {
			throw new UnexpectedException(e, "Cannot parse file [%s]", datFile);
		}
	}

	public TableOfDoubles parse(String data) {
		Scanner sc = new Scanner(data);
		return doParse(sc);
	}

	private TableOfDoubles doParse(Scanner sc) {
		TableOfDoubles table = new TableOfDoubles();
		while (sc.hasNextLine()) {
			String line = sc.nextLine().trim();
			if (isNotComment(line)) {
				ArrayList<Double> rowDoubles = new ArrayList<>();
				for (String item : splitter.split(line)) {
					double d = Double.parseDouble(item);
					rowDoubles.add(d);
				}

				double[] rowD = fitToRow(rowDoubles);
				table.addRow(rowD);
			}
		}

		return table;
	}

	private boolean isNotComment(String line) {
		return !line.startsWith(COMMENT_PREFIX);
	}

	private double[] fitToRow(List<Double> rowDoubles) {
		double[] arrayOfDoubles;
		if (expectedRows == null) {
			arrayOfDoubles = new double[rowDoubles.size()];
			int i = 0;
			for (Double dbl : rowDoubles) {
				arrayOfDoubles[i++] = defaultDouble(dbl);
			}
		} else {
			// TODO report mismatches
			arrayOfDoubles = new double[expectedRows];
			for (int i = 0; i < expectedRows; i++) {
				if (i < rowDoubles.size()) {
					arrayOfDoubles[i] = defaultDouble(rowDoubles.get(i));
				} else {
					arrayOfDoubles[i] = DEFAULT_DOUBLE;
				}
			}
		}

		return arrayOfDoubles;
	}

	public void setExpectedRows(Integer expectedRows) {
		this.expectedRows = expectedRows;
	}

	private double defaultDouble(Double dbl) {
		return dbl == null ? DEFAULT_DOUBLE : dbl.doubleValue();
	}

}
