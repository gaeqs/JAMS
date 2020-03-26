package net.jamsimulator.jams.gui.settings.explorer.node;

import javafx.scene.control.ComboBox;
import javafx.util.StringConverter;
import net.jamsimulator.jams.Jams;
import net.jamsimulator.jams.configuration.Configuration;
import net.jamsimulator.jams.event.Listener;
import net.jamsimulator.jams.language.Language;
import net.jamsimulator.jams.language.event.LanguageRegisterEvent;
import net.jamsimulator.jams.language.event.LanguageUnregisterEvent;

import java.util.Optional;

public class ConfigurationWindowNodeLanguage extends ConfigurationWindowNode<Language> {

	private ComboBox<Language> box;

	public ConfigurationWindowNodeLanguage(Configuration configuration, String relativeNode,
										   String languageNode, Language defaultValue) {
		super(configuration, relativeNode, languageNode, defaultValue);
		Jams.getLanguageManager().registerListeners(this);
	}

	@Override
	protected void init() {
		super.init();
		box = new ComboBox<>();

		box.setConverter(new StringConverter<Language>() {
			@Override
			public String toString(Language object) {
				return object.getName();
			}

			@Override
			public Language fromString(String string) {
				return Jams.getLanguageManager().get(string).orElse(Jams.getLanguageManager().getDefault());
			}
		});

		box.getItems().addAll(Jams.getLanguageManager().getAll());
		box.getSelectionModel().select(getValue());
		getChildren().add(box);

		box.getSelectionModel().selectedItemProperty().addListener((obs, old, val) -> saveValue(val));
	}

	@Override
	public Language getValue() {
		Optional<String> optional = configuration.get(relativeNode);
		if (!optional.isPresent()) return defaultValue;
		return Jams.getLanguageManager().get(optional.get()).orElse(defaultValue);
	}

	@Override
	public void setValue(Language value) {
		box.getSelectionModel().select(value);
		saveValue(value);
	}

	@Override
	protected void saveValue(Language value) {
		System.out.println("SAVING!");
		configuration.set(relativeNode, value.getName());
	}

	@Listener
	private void onLanguageRegister(LanguageRegisterEvent event) {
		box.getItems().add(event.getLanguage());
	}

	@Listener
	private void onLanguageUnregister(LanguageUnregisterEvent event) {
		if (box.getSelectionModel().getSelectedItem().equals(event.getLanguage()))
			setValue(defaultValue);
		box.getItems().remove(event.getLanguage());
	}

	static class Builder implements ConfigurationWindowNodeBuilder<Language> {

		@Override
		public ConfigurationWindowNode<Language> create(Configuration configuration, String relativeNode, String languageNode) {
			return new ConfigurationWindowNodeLanguage(configuration, relativeNode,
					languageNode, Jams.getLanguageManager().getDefault());
		}
	}
}
