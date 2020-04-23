/*
 * MIT License
 *
 * Copyright (c) 2020 Gael Rial Costas
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

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
