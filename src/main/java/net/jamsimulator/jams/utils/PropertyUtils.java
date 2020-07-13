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

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.BooleanPropertyBase;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.DoublePropertyBase;
import javafx.beans.value.ObservableBooleanValue;
import javafx.beans.value.ObservableDoubleValue;

import java.lang.reflect.Field;
import java.util.Optional;

public class PropertyUtils {

	public static Optional<ObservableDoubleValue> getBoundValue(DoubleProperty property) {
		if (property instanceof DoublePropertyBase) {
			try {
				Field field = DoublePropertyBase.class.getDeclaredField("observable");
				field.setAccessible(true);
				return Optional.ofNullable((ObservableDoubleValue) field.get(property));
			} catch (Exception e) {
				e.printStackTrace();
				return Optional.empty();
			}
		}
		return Optional.empty();
	}

	public static Optional<ObservableBooleanValue> getBoundValue(BooleanProperty property) {
		if (property instanceof BooleanPropertyBase) {
			try {
				Field field = DoublePropertyBase.class.getDeclaredField("observable");
				field.setAccessible(true);
				return Optional.ofNullable((ObservableBooleanValue) field.get(property));
			} catch (Exception e) {
				e.printStackTrace();
				return Optional.empty();
			}
		}
		return Optional.empty();
	}

}
