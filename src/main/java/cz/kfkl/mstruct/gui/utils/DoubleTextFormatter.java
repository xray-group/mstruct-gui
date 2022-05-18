package cz.kfkl.mstruct.gui.utils;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Arrays;
import java.util.Locale;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;

import cz.kfkl.mstruct.gui.utils.ic.TableReportBuilder;
import javafx.scene.control.TextFormatter;
import javafx.util.StringConverter;

public class DoubleTextFormatter extends TextFormatter<Double> {

	private static final String Digits = "(\\p{Digit}+)";
	private static final String HexDigits = "(\\p{XDigit}+)";
	// an exponent is 'e' or 'E' followed by an optionally
	// signed decimal integer.
	private static final String Exp = "[eE][+-]?" + Digits + "?";
	private static final String fpRegex = (// "[\\x00-\\x20]*"+ // Optional leading "whitespace"
	"[+-]?(" + // Optional sign character
			"NaN|" + // "NaN" string
			"Infinity|" + // "Infinity" string

			// A decimal floating-point string representing a finite positive
			// number without a leading sign has at most five basic pieces:
			// Digits . Digits ExponentPart FloatTypeSuffix
			//
			// Since this method allows integer-only strings as input
			// in addition to strings of floating-point literals, the
			// two sub-patterns below are simplifications of the grammar
			// productions from section 3.10.2 of
			// The Java Language Specification.

			// Digits ._opt Digits_opt ExponentPart_opt FloatTypeSuffix_opt
//			"(" +
			"((" + Digits + "(\\.)?(" + Digits + "?)(" + Exp + ")?)|" +

			// . Digits ExponentPart_opt FloatTypeSuffix_opt
			"(\\.(" + Digits + ")(" + Exp + ")?)"
			// + "|"

//			// Hexadecimal strings
//			+"((" +
//			// 0[xX] HexDigits ._opt BinaryExponent FloatTypeSuffix_opt
//			"(0[xX]" + HexDigits + "(\\.)?)|" +
//
//			// 0[xX] HexDigits_opt . HexDigits BinaryExponent FloatTypeSuffix_opt
//			"(0[xX]" + HexDigits + "?(\\.)" + HexDigits + ")" +
//
//			")[pP][+-]?" + Digits + "))" + "[fFdD]?"
			+ "))"
	// + "[\\x00-\\x20]*" // Optional trailing "whitespace"
	);

	private static Pattern validEditingState = Pattern.compile(fpRegex);

	// note that singleton didn't work, when used it throws
	// "java.lang.IllegalStateException: Formatter is already used in other control"
	public DoubleTextFormatter() {
		super(valueConverter, null, filter);
	}

	private static UnaryOperator<TextFormatter.Change> filter = c -> {
		String text = c.getControlNewText();

		if (text == null || text.isEmpty() || validEditingState.matcher(text).matches()) {
			return c;
		} else {
			return null;
		}
	};

	public static StringConverter<Double> valueConverter = new StringConverter<Double>() {
		@Override
		public Double fromString(String s) {
			if (s == null || s.isEmpty()) {
				return null;
			} else if ("-".equals(s) || ".".equals(s) || "-.".equals(s)) {
				return 0.0;
			} else {
				return Double.valueOf(s);
			}
		}

		@Override
		public String toString(Double d) {
			return JvStringUtils.toStringNoDotZero(d);
		}
	};

	public static StringConverter<Number> numbreValueConverter = new StringConverter<Number>() {
		@Override
		public Number fromString(String s) {
			if (s == null || s.isEmpty()) {
				return null;
			} else if ("-".equals(s) || ".".equals(s) || "-.".equals(s)) {
				return 0.0;
			} else {
				return Double.valueOf(s);
			}
		}

		@Override
		public String toString(Number number) {
			return JvStringUtils.toStringNoDotZero(number);
		}
	};

	private static String toStringNoDotZero(Double d) {
		if (d == null) {
			return null;
		} else {
			int i = (int) d.doubleValue();
			return d == i ? String.valueOf(i) : String.valueOf(d);
		}
	}

	public static StringConverter<String> stringDoubleConverter = new StringConverter<String>() {
		@Override
		public String fromString(String s) {
			Double d = valueConverter.fromString(s);
			return valueConverter.toString(d);
		}

		@Override
		public String toString(String d) {
			return d;
		}
	};

	public static void main(String[] args) {
		System.out.println(validEditingState.matcher("1.5e8").matches());
		System.out.println(validEditingState.matcher("1.5e-8").matches());
		System.out.println(validEditingState.matcher("1.5e").matches());
		System.out.println(validEditingState.matcher("1.5e-").matches());
		System.out.println(validEditingState.matcher("1.e").matches());
		System.out.println(validEditingState.matcher("-1.e").matches());
		System.out.println(validEditingState.matcher("").matches());
//		DecimalFormat df = new DecimalFormat("#.#");

		DecimalFormat df20 = new DecimalFormat("0", DecimalFormatSymbols.getInstance(Locale.ENGLISH));
		df20.setMaximumFractionDigits(20); // 340 = DecimalFormat.DOUBLE_FRACTION_DIGITS

		DecimalFormat df4 = new DecimalFormat("0", DecimalFormatSymbols.getInstance(Locale.ENGLISH));
		df4.setMaximumFractionDigits(4); // 340 = DecimalFormat.DOUBLE_FRACTION_DIGITS

		TableReportBuilder trb = TableReportBuilder.create(25, 25, 25, 25, 25, 32, 32, 32, 32, 32, 32, 32, 32);
		trb.appendLine("Double.toString(d)", "String.valueOf(d)", "String.format(\"%s\", d)", "String.format(\"%g\", d)",
				"String.format(\"%e\", d)", "String.format(\"%f\", d)", "df20.format(d)", "df4.format(d)", "toStringNoDotZero");

		for (Double d : Arrays.asList(5.1, 7654321.0, 87654321.0, 87600000.0, 8765432.1, 8765432.12345678, 7000000.0, 80000000.0,
				1234567890.123456789, 1600000000000000.0, 17000000000000000.0, 8.0, 7E-77, 2E-12, 2E22, 12E12)) {
			trb.appendLine(Double.toString(d), String.valueOf(d), String.format("%s", d), String.format("%g", d),
					String.format("%e", d), String.format("%f", d), df20.format(d), df4.format(d), toStringNoDotZero(d));
		}

		System.out.println(trb.format());
	}

}
