package net.jamsimulator.jams.gui.util.propertyeditor;

import javafx.beans.property.Property;
import net.jamsimulator.jams.utils.Validate;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public class PropertyEditors {

	private static final Map<Class<?>, Function<Property<?>, PropertyEditor<?>>> EDITORS = new HashMap<>();

	static {
		registerEditor(Boolean.class, p -> new BooleanPropertyEditor((Property<Boolean>) p));
		registerEditor(Double.class, p -> new DoublePropertyEditor((Property<Double>) p));
		registerEditor(Float.class, p -> new FloatPropertyEditor((Property<Float>) p));
		registerEditor(Integer.class, p -> new IntegerPropertyEditor((Property<Integer>) p));
		registerEditor(String.class, p -> new StringPropertyEditor((Property<String>) p));
		registerEditor(Enum.class, p -> new EnumPropertyEditor((Property<? super Enum<?>>) p));
	}

	public static void registerEditor(Class<?> clazz, Function<Property<?>, PropertyEditor<?>> editor) {
		Validate.notNull(editor, "Editor cannot be null!");
		EDITORS.put(clazz, editor);
	}

	public static <T> Optional<PropertyEditor<T>> getEditor(Property<T> property) {
		Class<?> clazz = property.getValue().getClass();
		if (clazz.isEnum()) clazz = Enum.class;
		Function<Property<?>, PropertyEditor<?>> function = EDITORS.get(clazz);
		return Optional.ofNullable((PropertyEditor<T>) function.apply(property));
	}

}
