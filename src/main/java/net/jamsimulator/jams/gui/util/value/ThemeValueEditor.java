package net.jamsimulator.jams.gui.util.value;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import net.jamsimulator.jams.event.Listener;
import net.jamsimulator.jams.gui.JamsApplication;
import net.jamsimulator.jams.gui.theme.Theme;
import net.jamsimulator.jams.gui.theme.event.ThemeRegisterEvent;
import net.jamsimulator.jams.gui.theme.event.ThemeUnregisterEvent;
import net.jamsimulator.jams.gui.util.converter.ThemeValueConverter;
import net.jamsimulator.jams.gui.util.converter.ValueConverters;

import java.util.function.Consumer;

public class ThemeValueEditor extends ComboBox<Theme> implements ValueEditor<Theme> {

	public static final String NAME = ThemeValueConverter.NAME;

	private Consumer<Theme> listener = theme -> {
	};

	public ThemeValueEditor() {
		setConverter(ValueConverters.getByTypeUnsafe(Theme.class));
		getItems().addAll(JamsApplication.getThemeManager());
		getSelectionModel().select(JamsApplication.getThemeManager().getSelected());
		getSelectionModel().selectedItemProperty().addListener((obs, old, val) -> listener.accept(val));
		JamsApplication.getThemeManager().registerListeners(this, true);
	}

	@Override
	public void setCurrentValue(Theme value) {
		getSelectionModel().select(value);
	}

	@Override
	public Theme getCurrentValue() {
		return getValue();
	}

	@Override
	public Node getAsNode() {
		return this;
	}

	@Override
	public Node buildConfigNode(Label label) {
		var box =  new HBox(label, this);
		box.setSpacing(5);
		box.setAlignment(Pos.CENTER_LEFT);
		return box;
	}

	@Override
	public void addListener(Consumer<Theme> consumer) {
		listener = listener.andThen(consumer);
	}

	@Listener
	private void onThemeRegister(ThemeRegisterEvent.After event) {
		getItems().add(event.getTheme());
	}

	@Listener
	private void onThemeUnregister(ThemeUnregisterEvent.After event) {
		if (getSelectionModel().getSelectedItem().equals(event.getTheme()))
			setValue(JamsApplication.getThemeManager().stream().findAny().orElse(null));
		getItems().remove(event.getTheme());
	}

	public static class Builder implements ValueEditor.Builder<Theme> {

		@Override
		public ValueEditor<Theme> build() {
			return new ThemeValueEditor();
		}

	}
}
