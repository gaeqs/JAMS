package net.jamsimulator.jams.utils;

public class Validate {

	/**
	 * Throws a {@link NullPointerException} if the given object is null.
	 *
	 * @param object the object to check.
	 * @throws NullPointerException whether the object is null.
	 */
	public static void notNull(Object object) {
		notNull(object, "null");
	}

	/**
	 * Throws a {@link NullPointerException} with the given message if the given object is null.
	 *
	 * @param object  the object to check.
	 * @param message the message.
	 * @throws NullPointerException whether the object is null.
	 */
	public static void notNull(Object object, String message) {
		if (object == null) throw new NullPointerException(message);
	}

	/**
	 * Throws an {@link IllegalArgumentException} if the given boolean is false.
	 *
	 * @param b the  boolean.
	 * @throws IllegalArgumentException whether the boolean is false.
	 */
	public static void isTrue(boolean b) {
		isTrue(b, "boolean is false");
	}

	/**
	 * Throws an {@link IllegalArgumentException}  with the given message if the given boolean is false.
	 *
	 * @param b       the  boolean.
	 * @param message the message.
	 * @throws IllegalArgumentException whether the boolean is false.
	 */
	public static void isTrue(boolean b, String message) {
		if (!b) throw new IllegalArgumentException(message);
	}

	/**
	 * Throws a {@link NullPointerException} if the given array has any null value inside it.
	 *
	 * @param array the given array.
	 * @param <T>   the array type.
	 * @throws NullPointerException whether the given array has any null value.
	 */
	public static <T> void hasNoNulls(T[] array) {
		hasNoNulls(array, "array has nulls");
	}

	/**
	 * Throws a {@link NullPointerException} with the given message if the given array has any null value inside it.
	 *
	 * @param array   the given array.
	 * @param <T>     the array type.
	 * @param message the message.
	 * @throws NullPointerException whether the given array has any null value.
	 */
	public static <T> void hasNoNulls(T[] array, String message) {
		for (T t : array) {
			if (t == null) throw new NullPointerException(message);
		}
	}

}
