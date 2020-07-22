package net.jamsimulator.jams.gui.util.propertyeditor;

import javafx.beans.property.Property;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.util.StringConverter;
import net.jamsimulator.jams.utils.ReflectionUtils;

public class EnumPropertyEditor extends ComboBox<Enum<?>> implements PropertyEditor<Enum<?>> {

	private final Property<? super Enum<?>> property;

	public EnumPropertyEditor(Property<? super Enum<?>> property) {
		this.property = property;
		Enum<?> value = (Enum<?>) property.getValue();
		Class<? extends Enum> clazz = value.getClass();

		try {
			Enum<?>[] values = ReflectionUtils.getEnumValues(clazz);
			for (Enum<?> val : values) {
				getItems().add(val);
			}

			getSelectionModel().select(values[0]);

			setConverter(new StringConverter<Enum<?>>() {
				@Override
				public String toString(Enum<?> object) {
					return object == null ? null : object.name();
				}

				@Override
				public Enum<?> fromString(String string) {
					return Enum.valueOf(clazz, string);
				}
			});

			setOnAction(event -> property.setValue(getSelectionModel().getSelectedItem()));

		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	@Override
	public Property<?> getProperty() {
		return property;
	}

	@Override
	public Node thisInstanceAsNode() {
		return this;
	}
}
