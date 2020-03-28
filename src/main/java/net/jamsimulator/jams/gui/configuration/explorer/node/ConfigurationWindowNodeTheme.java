package net.jamsimulator.jams.gui.configuration.explorer.node;

import javafx.scene.control.ComboBox;
import javafx.util.StringConverter;
import net.jamsimulator.jams.configuration.Configuration;
import net.jamsimulator.jams.event.Listener;
import net.jamsimulator.jams.gui.JamsApplication;
import net.jamsimulator.jams.gui.theme.Theme;
import net.jamsimulator.jams.gui.theme.event.ThemeRegisterEvent;
import net.jamsimulator.jams.gui.theme.event.ThemeUnregisterEvent;

import java.util.Optional;

public class ConfigurationWindowNodeTheme extends ConfigurationWindowNode<Theme> {

	private ComboBox<Theme> box;

	public ConfigurationWindowNodeTheme(Configuration configuration, String relativeNode,
										String languageNode, Theme defaultValue) {
		super(configuration, relativeNode, languageNode, defaultValue);
		JamsApplication.getThemeManager().registerListeners(this);
	}

	@Override
	protected void init() {
		super.init();
		box = new ComboBox<>();

		box.setConverter(new StringConverter<Theme>() {
			@Override
			public String toString(Theme object) {
				return object.getName();
			}

			@Override
			public Theme fromString(String string) {
				return JamsApplication.getThemeManager().get(string).orElse(JamsApplication.getThemeManager().getSelected());
			}
		});

		box.getItems().addAll(JamsApplication.getThemeManager().getAll());
		box.getSelectionModel().select(getValue());
		getChildren().add(box);

		box.getSelectionModel().selectedItemProperty().addListener((obs, old, val) -> saveValue(val));
	}

	@Override
	public Theme getValue() {
		Optional<String> optional = configuration.get(relativeNode);
		if (!optional.isPresent()) return defaultValue;
		return JamsApplication.getThemeManager().get(optional.get()).orElse(defaultValue);
	}

	@Override
	public void setValue(Theme value) {
		box.getSelectionModel().select(value);
		saveValue(value);
	}

	@Override
	protected void saveValue(Theme value) {
		configuration.set(relativeNode, value.getName());
	}

	@Listener
	private void onThemeRegister(ThemeRegisterEvent event) {
		box.getItems().add(event.getTheme());
	}

	@Listener
	private void onThemeUnregister(ThemeUnregisterEvent event) {
		if (box.getSelectionModel().getSelectedItem().equals(event.getTheme()))
			setValue(defaultValue);
		box.getItems().remove(event.getTheme());
	}

	static class Builder implements ConfigurationWindowNodeBuilder<Theme> {

		@Override
		public ConfigurationWindowNode<Theme> create(Configuration configuration, String relativeNode, String languageNode) {
			return new ConfigurationWindowNodeTheme(configuration, relativeNode,
					languageNode, JamsApplication.getThemeManager().getSelected());
		}
	}
}
