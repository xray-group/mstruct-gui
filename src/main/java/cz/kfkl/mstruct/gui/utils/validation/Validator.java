package cz.kfkl.mstruct.gui.utils.validation;

import java.util.Collection;
import java.util.Map;

import com.google.common.base.Strings;

/**
 * Simplifies in-code validations. If the provided condition of assert*
 * functions is not met the {@link UnexpectedException} is thrown. The exception
 * error message is formatted using {@link String#format(String, Object...)}
 * method.
 * 
 * The validate* methods works the same but throws {@link PopupErrorException}.
 */
public class Validator {
	private static final String RAWTYPES = "rawtypes";

	Validator() {
	}

	public static void assertEquals(Object toCheck, Object toCheck2, String message, Object... params) {
		error(!(toCheck != null && toCheck.equals(toCheck2)), message, params);
	}

	public static void assertTrue(boolean toCheck, String message, Object... params) {
		error(!toCheck, message, params);
	}

	public static void assertFalse(boolean toCheck, String message, Object... params) {
		error(toCheck, message, params);
	}

	public static void assertType(Object toCheck, Class<?> type, String message, Object... params) {
		error(!(type.isInstance(toCheck)), message, params);
	}

	public static void assertNotNull(Object toCheck, String message) {
		assertNotNull(toCheck, message, (Object[]) null);
	}

	public static void assertNotNull(Object toCheck, String message, Object... params) {
		error(toCheck == null, message, params);
	}

	/**
	 * Throws an exception if a is not null.
	 */
	public static void assertIsNull(Object a, String message) {
		assertIsNull(a, message, (Object[]) null);
	}

	public static void assertIsNull(Object a, String message, Object... params) {
		error(a != null, message, params);
	}

	public static void assertSize(Collection<?> fields, int size, String message, Object... params) {
		error(fields == null || fields.size() != size, message, params);
	}

	public static void assertNotEmpty(Collection<?> fields, String message, Object... params) {
		error(fields == null || fields.size() == 0, message, params);
	}

	public static void assertNotBlank(String toCheck, String message) {
		assertNotBlank(toCheck, message, (Object[]) null);
	}

	public static void assertNotBlank(String toCheck, String message, Object... params) {
		error(Strings.isNullOrEmpty(toCheck), message, params);
	}

	public static void assertIsBlank(String toCheck, String message, Object... params) {
		error(isBlank(toCheck), message, params);
	}

	public static void assertSame(Object a, Object b, String message, Object... params) {
		boolean same = (a == null && b == null) || (a != null && a.equals(b));

		error(!same, message, params);
	}

	public static void assertLength(Object[] array, int length, String message, Object... params) {
		error(array == null || array.length != length, message, params);
	}

	public static void assertNotIn(@SuppressWarnings(RAWTYPES) Map map, Object key, String message, Object... params) {
		error(map.containsKey(key), message, params);
	}

	public static void assertKeyInMap(@SuppressWarnings(RAWTYPES) Map map, Object key, String message, Object... params) {
		error(!map.containsKey(key), message, params);
	}

	public static void assertKeyNotInMap(@SuppressWarnings(RAWTYPES) Map map, Object key, String message, Object... params) {
		error(map.containsKey(key), message, params);
	}

	public static void assertNotInCollection(@SuppressWarnings(RAWTYPES) Collection c, Object toCheck, String message,
			Object... params) {
		error(c.contains(toCheck), message, params);
	}

	public static void assertInCollection(@SuppressWarnings(RAWTYPES) Collection c, Object toCheck, String message,
			Object... params) {
		error(!c.contains(toCheck), message, params);
	}

	public static void assertMoreThanZero(int toCheck, String message, Object... params) {
		assertMoreThanX(0, toCheck, message, params);
	}

	/**
	 *
	 * Assert that the value toCheck is greater than X
	 *
	 * @param x       - the value X
	 * @param toCheck - The value that should be greater than X
	 * @param message - Error message
	 * @param params  - Error message parameters
	 */
	public static void assertMoreThanX(int x, int toCheck, String message, Object... params) {
		error(!(toCheck > x), message, params);
	}

	private static void error(boolean throwError, String message, Object... params) {
		if (throwError) {
			error(message, params);
		}
	}

	/**
	 * Throws a UnexpectedException;
	 *
	 * @param message
	 * @param params
	 */
	public static void error(String message, Object... params) throws PopupErrorException {
		String errorMessage = getErrorMessage(message, params);
		throw new UnexpectedException(errorMessage);
	}

	private static String getErrorMessage(String message, Object... params) {
		String errorMessage = null;

		if (null == params) {
			errorMessage = message;
		} else {
			errorMessage = String.format(message, params);
		}

		return errorMessage;
	}

	private static void userException(boolean throwUserException, String message, Object... params) {
		if (throwUserException) {
			String errorMessage = getErrorMessage(message, params);
			throw new PopupErrorException(errorMessage, true);
		}
	}

	/**
	 * Throws PdmException with message for user if toCheck is not equal toCheck2.
	 */
	public static void validateEquals(Object toCheck, Object toCheck2, String message, Object... params) {
		userException(!(toCheck != null && toCheck.equals(toCheck2)), message, params);
	}

	/**
	 * Throws PdmException with message for user if toCheck is not true.
	 */
	public static void validateTrue(boolean toCheck, String message, Object... params) {
		userException(!toCheck, message, params);
	}

	/**
	 * Throws {@link PdmException} with message for user if toCheck is not false.
	 */
	public static void validateFalse(boolean toCheck, String message) {
		validateFalse(toCheck, message, (Object[]) null);
	}

	public static void validateFalse(boolean toCheck, String message, Object... params) {
		userException(toCheck, message, params);
	}

	/**
	 * Throws {@link PdmException} with message for user if toCheck is null.
	 */
	public static void validateNotNull(Object toCheck, String message) {
		userException(toCheck == null, message, (Object[]) null);
	}

	/**
	 * Throws {@link PdmException} with message for user if toCheck is null.
	 */
	public static void validateNotNull(Object toCheck, String message, Object... params) {
		userException(toCheck == null, message, params);
	}

	/**
	 * Throws {@link PdmException} with message for user if toCheck is not null.
	 */
	public static void validateIsNull(Object toCheck, String message) {
		userException(toCheck != null, message, (Object[]) null);
	}

	/**
	 * Throws {@link PdmException} with message for user if toCheck is not null.
	 */
	public static void validateIsNull(Object toCheck, String message, Object... params) {
		userException(toCheck != null, message, params);
	}

	/**
	 * Throws {@link PdmException} with message for user if toCheck is blank.
	 */
	public static void validateNotBlank(String toCheck, String message) {
		userException(isBlank(toCheck), message, (Object[]) null);
	}

	private static boolean isBlank(String string) {
		return string == null || string.isBlank();
	}

	/**
	 * Throws {@link PdmException} with message for user if toCheck is blank.
	 */
	public static void validateNotBlank(String toCheck, String message, Object... params) {
		userException(isBlank(toCheck), message, params);
	}

	/**
	 * Throws {@link PdmException} with message for user if toCheck is not blank.
	 */
	public static void validateIsBlank(String toCheck, String message, Object... params) {
		userException(!isBlank(toCheck), message, params);
	}

}
