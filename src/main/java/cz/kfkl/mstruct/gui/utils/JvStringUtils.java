package cz.kfkl.mstruct.gui.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.beans.property.StringProperty;

public class JvStringUtils {

	private static final Logger LOG = LoggerFactory.getLogger(JvStringUtils.class);

	private static final char APOSTROPHE = '\'';
	private static final String COMMA_SEPARATOR = ",";

	/**
	 * A simulation of Apache's method...
	 * <p>
	 * Gets the substring before the first occurrence of a separator. The separator
	 * is not returned.
	 * </p>
	 *
	 * <p>
	 * A <code>null</code> string input will return <code>null</code>. An empty ("")
	 * string input will return the empty string. A <code>null</code> separator will
	 * return the input string.
	 * </p>
	 *
	 * <p>
	 * If nothing is found, the string input is returned.
	 * </p>
	 *
	 * <pre>
	 * StringUtils.substringBefore(null, *)      = null
	 * StringUtils.substringBefore("", *)        = ""
	 * StringUtils.substringBefore("abc", "a")   = ""
	 * StringUtils.substringBefore("abcba", "b") = "a"
	 * StringUtils.substringBefore("abc", "c")   = "ab"
	 * StringUtils.substringBefore("abc", "d")   = "abc"
	 * StringUtils.substringBefore("abc", "")    = ""
	 * StringUtils.substringBefore("abc", null)  = "abc"
	 * </pre>
	 *
	 * @param str       the String to get a substring from, may be null
	 * @param separator the String to search for, may be null
	 * @return the substring before the first occurrence of the separator,
	 *         <code>null</code> if null String input
	 * @since 2.0
	 */
	public static String substringBefore(String str, String separator) {
		if (isEmpty(str) || separator == null) {
			return str;
		}
		if (separator.length() == 0) {
			return "";
		}
		int pos = str.indexOf(separator);
		if (pos < 0) {
			return str;
		}
		return str.substring(0, pos);
	}

	/**
	 * A simulation of Apache's method...
	 * 
	 * <p>
	 * Gets the substring after the first occurrence of a separator. The separator
	 * is not returned.
	 * </p>
	 *
	 * <p>
	 * A <code>null</code> string input will return <code>null</code>. An empty ("")
	 * string input will return the empty string. A <code>null</code> separator will
	 * return the empty string if the input string is not <code>null</code>.
	 * </p>
	 *
	 * <p>
	 * If nothing is found, the empty string is returned.
	 * </p>
	 *
	 * <pre>
	 * StringUtils.substringAfter(null, *)      = null
	 * StringUtils.substringAfter("", *)        = ""
	 * StringUtils.substringAfter(*, null)      = ""
	 * StringUtils.substringAfter("abc", "a")   = "bc"
	 * StringUtils.substringAfter("abcba", "b") = "cba"
	 * StringUtils.substringAfter("abc", "c")   = ""
	 * StringUtils.substringAfter("abc", "d")   = ""
	 * StringUtils.substringAfter("abc", "")    = "abc"
	 * </pre>
	 *
	 * @param str       the String to get a substring from, may be null
	 * @param separator the String to search for, may be null
	 * @return the substring after the first occurrence of the separator,
	 *         <code>null</code> if null String input
	 * @since 2.0
	 */
	public static String substringAfter(String str, String separator) {
		if (isEmpty(str)) {
			return str;
		}
		if (separator == null) {
			return "";
		}
		int pos = str.indexOf(separator);
		if (pos < 0) {
			return "";
		}
		return str.substring(pos + separator.length());
	}

	/**
	 * A simulation of Apache's method...
	 * <p>
	 * Checks if a String is empty ("") or null.
	 * </p>
	 *
	 * <pre>
	 * StringUtils.isEmpty(null)      = true
	 * StringUtils.isEmpty("")        = true
	 * StringUtils.isEmpty(" ")       = false
	 * StringUtils.isEmpty("bob")     = false
	 * StringUtils.isEmpty("  bob  ") = false
	 * </pre>
	 *
	 * <p>
	 * NOTE: This method changed in Lang version 2.0. It no longer trims the String.
	 * That functionality is available in isBlank().
	 * </p>
	 *
	 * @param str the String to check, may be null
	 * @return <code>true</code> if the String is empty or null
	 */
	public static boolean isEmpty(String str) {
		return str == null || str.length() == 0;
	}

	/**
	 * A clone of the Apache's method.
	 * <p>
	 * Checks if a String is whitespace, empty ("") or null.
	 * </p>
	 *
	 * <pre>
	 * StringUtils.isBlank(null)      = true
	 * StringUtils.isBlank("")        = true
	 * StringUtils.isBlank(" ")       = true
	 * StringUtils.isBlank("bob")     = false
	 * StringUtils.isBlank("  bob  ") = false
	 * </pre>
	 *
	 * @param str the String to check, may be null
	 * @return <code>true</code> if the String is null, empty or whitespace
	 * @since 2.0
	 */
	public static boolean isBlank(String str) {
		int strLen;
		if (str == null || (strLen = str.length()) == 0) {
			return true;
		}
		for (int i = 0; i < strLen; i++) {
			if ((Character.isWhitespace(str.charAt(i)) == false)) {
				return false;
			}
		}
		return true;
	}

	public static boolean isNotBlank(String str) {
		return !isBlank(str);
	}

	public static String joinCsv(double[] values) {
		return join(COMMA_SEPARATOR, values);
	}

	public static String join(String delim, double[] values) {
		StringBuilder sb = new StringBuilder();
		boolean isFirst = true;
		for (double d : values) {
			if (isFirst) {
				isFirst = false;
			} else {
				sb.append(delim);
			}

			sb.append(d);
		}

		return sb.toString();
	}

	public static String join(String delim, List<?> values) {
		StringBuilder sb = new StringBuilder();
		boolean isFirst = true;
		for (Object d : values) {
			if (isFirst) {
				isFirst = false;
			} else {
				sb.append(delim);
			}

			sb.append(d);
		}

		return sb.toString();
	}

	public static String join(String delim, Object... values) {
		StringBuilder sb = new StringBuilder();
		boolean isFirst = true;
		for (Object d : values) {
			if (isFirst) {
				isFirst = false;
			} else {
				sb.append(delim);
			}

			sb.append(d);
		}

		return sb.toString();
	}

	public static String joinSkipNullsAndBlanks(String separator, Object... toBeJoined) {
		List<String> parsed = new ArrayList<>();
		for (Object element : toBeJoined) {
			String elementAsString = toString(element);
			if (!isBlank(elementAsString)) {
				parsed.add(elementAsString);
			}
		}
		return join(separator, parsed);
	}

	/**
	 * @return a new list containing
	 *         {@link org.apache.commons.lang.StringUtils#isNotBlank(String)} items
	 *         from the list passed.
	 */
	public static List<String> removeBlanks(List<String> list) {
		List<String> values = new ArrayList<>();

		if (list != null) {
			ListIterator<String> lit = list.listIterator();
			while (lit.hasNext()) {
				String s = lit.next();
				if (isNotBlank(s)) {
					values.add(s);
				}
			}
		}

		return values;
	}

	/**
	 * Wraps each string with apostrophes and join them with comma. For null return
	 * null. For empty iterable an empty string.
	 * 
	 * Warning: for SQL parameters the {@link #joinApostrophedSql(Iterable)} should
	 * be used instead
	 */
	public static String joinApostrophed(Iterable<String> strings) {
		if (strings == null) {
			return null;
		}

		StringBuilder builder = new StringBuilder();
		Iterator<String> it = strings.iterator();
		if (it.hasNext()) {
			builder.append(APOSTROPHE).append(it.next()).append(APOSTROPHE);
		}

		while (it.hasNext()) {
			builder.append(COMMA_SEPARATOR);
			builder.append(APOSTROPHE).append(it.next()).append(APOSTROPHE);
		}

		return builder.toString();
	}

	/**
	 * Joins the map to a string. Every map entry is formated using
	 * {@link String#format(String, Object...)} and the provided pattern (key first,
	 * value second). The formated entries are delimited with the delim string.
	 * 
	 * @param map     a map to format to string
	 * @param pattern {@link String#format(String, Object...)} like pattern, should
	 *                contain '%s' (or other type) twice. The first is used for the
	 *                map entry key, second for the value. For example "%s=%s".
	 * @param delim   the delimiter to use
	 * @return the map formatted to a string, never null
	 */
	public static <K, V> String joinMap(Map<K, V> map, String pattern, String delim) {
		boolean isFirst = true;
		StringBuilder sb = new StringBuilder();
		if (map != null) {
			for (Map.Entry<K, V> entry : map.entrySet()) {
				if (!isFirst) {
					sb.append(delim);
				}

				sb.append(String.format(pattern, entry.getKey(), entry.getValue()));
				isFirst = false;
			}
		}

		return sb.toString();
	}

	/**
	 * Join items of the provided collection to a string. The items are delimited by
	 * the delim. If there is more then maxCount items in the collection, only first
	 * maxCount is formatted and the abbrevSufix is appended to the end of the
	 * string.
	 * 
	 * @param collection  a collection to format to a string
	 * @param delim       delimiter
	 * @param maxCount    maximum number of items formatted
	 * @param abbrevSufix a text added to the end if there was more items then
	 *                    defined by the maxCount
	 * @return the collection formatted to a string, never null, empty string if the
	 *         collection == null or empty, the abbrevSufix only if maxCount <= 0
	 */
	public static <T> String joinAbreviate(Collection<T> collection, String delim, int maxCount, String abbrevSufix) {
		boolean isFirst = true;
		StringBuilder sb = new StringBuilder();
		if (collection != null) {
			for (T item : collection) {
				if (maxCount <= 0) {
					sb.append(abbrevSufix);
					break;
				}

				if (!isFirst) {
					sb.append(delim);
				}

				sb.append(item);
				maxCount--;
				isFirst = false;
			}
		}

		return sb.toString();
	}

	/**
	 * @return JvStringUtils.joinAbreviate(usedCrystalsToRemove, ", ", 5, "...")
	 */
	public static <T> String joinAbreviate(Collection<T> collection, int maxCount) {
		return JvStringUtils.joinAbreviate(collection, ", ", 5, "...");
	}

	/**
	 * Transforms array of Object to array of String using {@link #toString(Object)}
	 * method (i.e. result of {@link Object#toString()} method or empty string for
	 * nulls).
	 * 
	 * @return object array transformed to strings, null if the array was null
	 */
	public static String[] toString(Object[] objValues) {
		String[] strValues = null;
		if (objValues != null) {
			strValues = new String[objValues.length];
			for (int i = 0; i < objValues.length; i++) {
				strValues[i] = toString(objValues[i]);
			}
		}

		return strValues;
	}

	public static String toString(Object object) {
		return object == null ? null : object.toString();
	}

	/**
	 * A bit nicer pretty print of doubles. For d == null return null. Otherwise if
	 * d is integer (d == (int) d ) then return Integer.valueOf((int) d) otherwise
	 * uses the d.toString().
	 */
	public static String toStringNoDotZero(Double d) {
		if (d == null) {
			return null;
		} else {
			int i = (int) d.doubleValue();
			return d == i ? Integer.toString(i) : d.toString();
		}
	}

	/**
	 * Parse provided StringProperty value into Double. Null is return if the string
	 * value is empty or cannot be parsed to Double.
	 */
	public static Double parseDouble(StringProperty stringProperty) {
		Double valueDouble = null;
		String valueStr = stringProperty.get();
		if (JvStringUtils.isNotBlank(valueStr)) {
			try {
				valueDouble = Double.valueOf(valueStr);
			} catch (Exception e) {
				LOG.debug("Failed to parse [{}] into a double.", valueStr);
				// TODO report as validation error ?
			}
		}
		return valueDouble;
	}

}
