package net.jamsimulator.jams.gui.util.value;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import net.jamsimulator.jams.Jams;
import net.jamsimulator.jams.event.Listener;
import net.jamsimulator.jams.gui.util.converter.LanguageValueConverter;
import net.jamsimulator.jams.gui.util.converter.ValueConverter;
import net.jamsimulator.jams.gui.util.converter.ValueConverters;
import net.jamsimulator.jams.language.Language;
import net.jamsimulator.jams.language.event.LanguageRegisterEvent;
import net.jamsimulator.jams.language.event.LanguageUnregisterEvent;

import java.util.function.Consumer;

public class LanguageValueEditor extends ComboBox<Language> implements ValueEditor<Language> {

	public static final String NAME = LanguageValueConverter.NAME;

	private Consumer<Language> listener = language -> {
	};

	public LanguageValueEditor() {
		setConverter(ValueConverters.getByTypeUnsafe(Language.class));
		getItems().addAll(Jams.getLanguageManager());
		getSelectionModel().select(Jams.getLanguageManager().getSelected());
		getSelectionModel().selectedItemProperty().addListener((obs, old, val) -> listener.accept(val));
		Jams.getLanguageManager().registerListeners(this, true);
	}

	@Override
	public void setCurrentValue(Language value) {
		getSelectionModel().select(value);
	}

	@Override
	public Language getCurrentValue() {
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
	public void addListener(Consumer<Language> consumer) {
		listener = listener.andThen(consumer);
	}

	@Listener
	private void onLanguageRegister(LanguageRegisterEvent.After event) {
		getItems().add(event.getLanguage());
	}

	@Listener
	private void onLanguageUnregister(LanguageUnregisterEvent.After event) {
		if (getSelectionModel().getSelectedItem().equals(event.getLanguage()))
			setValue(Jams.getLanguageManager().getDefault());
		getItems().remove(event.getLanguage());
	}

	@Override
	public ValueConverter<Language> getLinkedConverter() {
		return ValueConverters.getByTypeUnsafe(Language.class);
	}

	public static class Builder implements ValueEditor.Builder<Language> {

		@Override
		public ValueEditor<Language> build() {
			return new LanguageValueEditor();
		}

	}
}
