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
import net.jamsimulator.jams.gui.util.converter.ValueConverter;
import net.jamsimulator.jams.gui.util.converter.ValueConverters;
import net.jamsimulator.jams.gui.util.value.*;
import net.jamsimulator.jams.language.wrapper.LanguageLabel;
import net.jamsimulator.jams.language.wrapper.LanguageTooltip;
import net.jamsimulator.jams.utils.Validate;

public class ConfigurationWindowNode extends HBox {

	protected Configuration configuration;
	protected String relativeNode;
	protected String languageNode;
	protected String region;

	protected ValueEditor<?> editor;
	protected ValueConverter<?> converter;


	public ConfigurationWindowNode(Configuration configuration, String relativeNode,
								   String languageNode, String region, String type) {
		getStyleClass().add("configuration-window-node");
		this.configuration = configuration;
		this.relativeNode = relativeNode;
		this.languageNode = languageNode;
		this.region = region;

		editor = ValueEditors.getByName(type).map(ValueEditor.Builder::build).orElse(null);
		Validate.notNull(editor, "Editor cannot be null! Type: " + type);
		converter = loadConverter(type);
		Validate.notNull(converter, "Converter cannot be null! Type: " + type);

		init(type);

		configuration.getString(relativeNode).flatMap(converter::fromStringSafe)
				.ifPresent(v -> editor.setCurrentValueUnsafe(v));

		editor.addListener(value -> converter.save(configuration, relativeNode, value));
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

	public String getRegion() {
		return region;
	}

	protected void init(String type) {
		setAlignment(Pos.CENTER_LEFT);
		Label label = languageNode == null ? new Label(relativeNode) : new LanguageLabel(languageNode);
		if (languageNode != null) {
			label.setTooltip(new LanguageTooltip(languageNode + "_TOOLTIP", LanguageTooltip.DEFAULT_DELAY));
		}

		Region region = new Region();
		region.setPrefWidth(10);

		if (type.equals(BooleanValueEditor.NAME)) {
			label.setOnMouseClicked(click -> editor.setCurrentValueUnsafe(!((boolean) editor.getCurrentValue())));
			getChildren().addAll(region, editor.getAsNode(), label);
		} else {
			getChildren().addAll(region, label, editor.getAsNode());
		}
	}

	protected ValueConverter<?> loadConverter(String type) {
		if (type.equals(FontValueEditor.NAME)) {
			type = StringValueEditor.NAME;
		} else if (type.equals(PositiveIntegerValueEditor.NAME)) {
			type = IntegerValueEditor.NAME;
		}

		return ValueConverters.getByName(type).orElse(null);
	}


}
