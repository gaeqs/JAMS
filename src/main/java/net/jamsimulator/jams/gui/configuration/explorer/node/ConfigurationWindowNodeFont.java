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
import javafx.scene.text.Font;
import javafx.util.StringConverter;
import net.jamsimulator.jams.configuration.Configuration;
import net.jamsimulator.jams.event.Listener;
import net.jamsimulator.jams.gui.JamsApplication;
import net.jamsimulator.jams.gui.theme.Theme;
import net.jamsimulator.jams.gui.theme.event.ThemeRegisterEvent;
import net.jamsimulator.jams.gui.theme.event.ThemeUnregisterEvent;

import java.util.Optional;
import java.util.stream.Collectors;

public class ConfigurationWindowNodeFont extends ConfigurationWindowNode<Font> {

	private ComboBox<Font> box;

	public ConfigurationWindowNodeFont(Configuration configuration, String relativeNode,
									   String languageNode, String region, Font defaultValue) {
		super(configuration, relativeNode, languageNode, region, defaultValue);
	}

	@Override
	protected void init() {
		super.init();
		box = new ComboBox<>();

		box.setConverter(new StringConverter<Font>() {
			@Override
			public String toString(Font object) {
				return object.getName();
			}

			@Override
			public Font fromString(String string) {
				return Font.font(string);
			}
		});

		box.getItems().addAll(Font.getFamilies().stream().map(Font::font).collect(Collectors.toList()));
		box.getSelectionModel().select(getValue());
		getChildren().add(box);

		box.getSelectionModel().selectedItemProperty().addListener((obs, old, val) -> saveValue(val));
	}

	@Override
	public Font getValue() {
		Optional<String> optional = configuration.get(relativeNode);
		if (!optional.isPresent()) return defaultValue;
		return Font.font(optional.get());
	}

	@Override
	public void setValue(Font value) {
		box.getSelectionModel().select(value);
		saveValue(value);
	}

	@Override
	protected void saveValue(Font value) {
		configuration.set(relativeNode, value.getName());
	}

	static class Builder implements ConfigurationWindowNodeBuilder<Font> {

		@Override
		public ConfigurationWindowNode<Font> create(Configuration configuration, String relativeNode, String languageNode, String region) {
			return new ConfigurationWindowNodeFont(configuration, relativeNode,
					languageNode, region, JamsApplication.getThemeManager().getGeneralFont());
		}
	}
}
