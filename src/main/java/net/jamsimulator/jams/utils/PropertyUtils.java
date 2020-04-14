package net.jamsimulator.jams.utils;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.DoublePropertyBase;
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

}
