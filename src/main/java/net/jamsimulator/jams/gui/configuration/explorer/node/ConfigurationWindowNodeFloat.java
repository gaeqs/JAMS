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

public class ConfigurationWindowNodeFloat extends ConfigurationWindowNode<Float> {

	protected TextField field;
	private String oldText;

	public ConfigurationWindowNodeFloat(Configuration configuration, String relativeNode,
										String languageNode, String region, Float defaultValue) {
		super(configuration, relativeNode, languageNode, region, defaultValue);
	}


	@Override
	protected void init() {
		super.init();

		field = new TextField();
		field.setText(getValue().toString());
		getChildren().add(field);
		oldText = field.getText();

		Runnable run = () -> {
			if (oldText.equals(field.getText())) return;
			try {
				float number = Float.parseFloat(field.getText());
				saveValue(number);
				oldText = field.getText();
			} catch (NumberFormatException ex) {
				field.setText(oldText);
			}
		};

		field.setOnAction(event -> run.run());
		field.focusedProperty().addListener((obs, old, val) -> {
			if (val) return;
			run.run();
		});
		setPrefWidth(45);
	}

	@Override
	public Float getValue() {
		return configuration.getNumber(relativeNode).orElse(defaultValue).floatValue();
	}

	@Override
	public void setValue(Float value) {
		saveValue(value);
		field.setText(String.valueOf(value));
	}

	@Override
	protected void saveValue(Float value) {
		super.saveValue(value);
	}

	static class Builder implements ConfigurationWindowNodeBuilder<Float> {

		@Override
		public ConfigurationWindowNode<Float> create(Configuration configuration, String relativeNode, String languageNode, String region) {
			return new ConfigurationWindowNodeFloat(configuration, relativeNode, languageNode, region, 0.0f);
		}
	}
}
