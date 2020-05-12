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
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import net.jamsimulator.jams.configuration.Configuration;
import net.jamsimulator.jams.language.wrapper.LanguageLabel;

import java.util.Optional;

public class ConfigurationWindowNode<E> extends HBox {

	protected Configuration configuration;
	protected String relativeNode;
	protected String languageNode;
	protected E defaultValue;


	public ConfigurationWindowNode(Configuration configuration, String relativeNode,
								   String languageNode, E defaultValue) {
		getStyleClass().add("configuration-window-node");
		this.configuration = configuration;
		this.relativeNode = relativeNode;
		this.languageNode = languageNode;
		this.defaultValue = defaultValue;
		init();
	}

	public Configuration getConfiguration() {
		return configuration;
	}

	public String getRelativeNode() {
		return relativeNode;
	}

	public String getLanguageNode() {
		return languageNode;
	}

	public E getDefaultValue() {
		return defaultValue;
	}

	public E getValue() {
		Optional<E> optional = configuration.get(relativeNode);
		return optional.orElse(defaultValue);
	}

	public void setValue(E value) {
		saveValue(value);
	}

	protected void saveValue(E value) {
		configuration.set(relativeNode, value);
	}

	protected void init() {
		setAlignment(Pos.CENTER_LEFT);
		Label label = languageNode == null ? new Label(relativeNode) : new LanguageLabel(languageNode);

		Region region = new Region();
		region.setPrefWidth(10);

		getChildren().addAll(region, label);
	}
}
