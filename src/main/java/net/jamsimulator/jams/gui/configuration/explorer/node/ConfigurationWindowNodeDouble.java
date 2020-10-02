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

public class ConfigurationWindowNodeDouble extends ConfigurationWindowNode<Double> {

	protected TextField field;
	private String oldText;

	public ConfigurationWindowNodeDouble(Configuration configuration, String relativeNode,
										 String languageNode, String region, Double defaultValue) {
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
				double number = Double.parseDouble(field.getText());
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
	public Double getValue() {
		return configuration.getNumber(relativeNode).orElse(defaultValue).doubleValue();
	}

	@Override
	public void setValue(Double value) {
		saveValue(value);
		field.setText(String.valueOf(value));
	}

	@Override
	protected void saveValue(Double value) {
		super.saveValue(value);
	}

	static class Builder implements ConfigurationWindowNodeBuilder<Double> {

		@Override
		public ConfigurationWindowNode<Double> create(Configuration configuration, String relativeNode, String languageNode, String region) {
			return new ConfigurationWindowNodeDouble(configuration, relativeNode, languageNode, region, 0.0);
		}
	}
}
