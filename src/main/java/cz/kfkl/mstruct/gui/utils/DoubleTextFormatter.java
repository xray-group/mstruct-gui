package cz.kfkl.mstruct.gui.utils;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Arrays;
import java.util.Locale;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;

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
	private static DoubleTextFormatter INSTANCE;

	private static Pattern validEditingState = Pattern.compile(fpRegex);

	public synchronized static DoubleTextFormatter getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new DoubleTextFormatter();
		}
		return INSTANCE;
	}

	private static UnaryOperator<TextFormatter.Change> filter = c -> {
		String text = c.getControlNewText();

		if (text == null || text.isEmpty() || validEditingState.matcher(text).matches()) {
			return c;
		} else {
			return null;
		}
	};

	private static StringConverter<Double> converter = new StringConverter<Double>() {
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
			return d == null ? null : toStringConverter(d);
		}

	};

	private static String toStringConverter(Double d) {
		int i = (int) d.doubleValue();
		return d == i ? String.valueOf(i) : String.valueOf(d);

		// return d.toString();
	}

	public static StringConverter<String> stringDoubleConverter = new StringConverter<String>() {
		@Override
		public String fromString(String s) {
			Double d = converter.fromString(s);
			return d == null ? null : d.toString();
		}

		@Override
		public String toString(String d) {
			return d;
		}
	};

	TextFormatter<Double> textFormatter = new TextFormatter<>(converter, null, filter);

	public DoubleTextFormatter() {
		super(converter, null, filter);
	}

	public static void main(String[] args) {
		System.out.println(validEditingState.matcher("1.5e8").matches());
		System.out.println(validEditingState.matcher("1.5e-8").matches());
		System.out.println(validEditingState.matcher("1.5e").matches());
		System.out.println(validEditingState.matcher("1.5e-").matches());
		System.out.println(validEditingState.matcher("1.e").matches());
		System.out.println(validEditingState.matcher("-1.e").matches());
		System.out.println(validEditingState.matcher("").matches());
//		DecimalFormat df = new DecimalFormat("#.#");

		DecimalFormat df = new DecimalFormat("0", DecimalFormatSymbols.getInstance(Locale.ENGLISH));
		df.setMaximumFractionDigits(30); // 340 = DecimalFormat.DOUBLE_FRACTION_DIGITS

		for (Double d : Arrays.asList(5.1, 1.5, 1234567890.123456, 8.0, 7E-57, 2E-20)) {
			System.out.println(Double.toString(d));
			System.out.println(d.toString());
//			System.out.println(df.format(d));
			System.out.println(String.format("%g", d));
			System.out.println(String.format("%s", d));
			System.out.println(String.format("%e", d));
			System.out.println(String.format("%f", d));
			System.out.println(df.format(d));
		}
	}

}
