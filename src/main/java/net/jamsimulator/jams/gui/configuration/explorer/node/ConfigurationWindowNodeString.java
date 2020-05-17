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

import javafx.scene.control.TextField;
import net.jamsimulator.jams.configuration.Configuration;

import java.util.Optional;

public class ConfigurationWindowNodeString extends ConfigurationWindowNode<String> {

	protected TextField field;

	public ConfigurationWindowNodeString(Configuration configuration, String relativeNode,
										 String languageNode, String region, String defaultValue) {
		super(configuration, relativeNode, languageNode, region, defaultValue);
	}


	@Override
	protected void init() {
		super.init();

		field = new TextField();
		field.setText(getValue());
		getChildren().add(field);

		field.setOnAction(target -> saveValue(field.getText()));
		field.focusedProperty().addListener((obs, old, val) -> {
			if (!val) saveValue(field.getText());
		});
	}

	@Override
	public String getValue() {
		Optional<String> optional = configuration.getString(relativeNode);
		return optional.orElse(defaultValue);
	}

	@Override
	public void setValue(String value) {
		saveValue(value);
		field.setText(value);
	}

	@Override
	protected void saveValue(String value) {
		super.saveValue(value);
	}

	static class Builder implements ConfigurationWindowNodeBuilder<String> {

		@Override
		public ConfigurationWindowNode<String> create(Configuration configuration, String relativeNode, String languageNode, String region) {
			return new ConfigurationWindowNodeString(configuration, relativeNode, languageNode, region, "");
		}
	}
}
