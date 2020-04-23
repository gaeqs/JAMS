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
		configuration.set(relativeNode, value.getName());
	}

	@Listener
	private void onLanguageRegister(LanguageRegisterEvent.After event) {
		box.getItems().add(event.getLanguage());
	}

	@Listener
	private void onLanguageUnregister(LanguageUnregisterEvent.After event) {
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
