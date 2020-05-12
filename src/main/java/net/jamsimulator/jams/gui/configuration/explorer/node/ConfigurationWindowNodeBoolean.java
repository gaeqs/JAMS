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

import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import net.jamsimulator.jams.configuration.Configuration;
import net.jamsimulator.jams.language.wrapper.LanguageLabel;

public class ConfigurationWindowNodeBoolean extends ConfigurationWindowNode<Boolean> {

	protected CheckBox box;

	public ConfigurationWindowNodeBoolean(Configuration configuration, String relativeNode,
										  String languageNode, Boolean defaultValue) {
		super(configuration, relativeNode, languageNode, defaultValue);
	}


	@Override
	protected void init() {
		box = new CheckBox();
		box.setSelected(getValue());
		box.selectedProperty().addListener((obs, old, val) -> saveValue(val));

		setAlignment(Pos.CENTER_LEFT);
		Label label = languageNode == null ? new Label(relativeNode) : new LanguageLabel(languageNode);

		Region region = new Region();
		region.setPrefWidth(10);

		getChildren().addAll(region, box, label);
	}

	@Override
	public void setValue(Boolean value) {
		saveValue(value);
		box.setSelected(value);
	}

	@Override
	protected void saveValue(Boolean value) {
		super.saveValue(value);
	}

	static class Builder implements ConfigurationWindowNodeBuilder<Boolean> {

		@Override
		public ConfigurationWindowNode<Boolean> create(Configuration configuration, String relativeNode, String languageNode) {
			return new ConfigurationWindowNodeBoolean(configuration, relativeNode, languageNode, false);
		}
	}
}
