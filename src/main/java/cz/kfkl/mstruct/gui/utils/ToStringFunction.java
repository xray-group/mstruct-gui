package cz.kfkl.mstruct.gui.utils;

/**
 * Represents a function that produces an String from any object.
 */
@FunctionalInterface
public interface ToStringFunction<T> {

	/**
	 * Applies this function to the given argument.
	 *
	 * @param value the function argument
	 * @return the function result
	 */
	String toString(T value);
}