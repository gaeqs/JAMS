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
