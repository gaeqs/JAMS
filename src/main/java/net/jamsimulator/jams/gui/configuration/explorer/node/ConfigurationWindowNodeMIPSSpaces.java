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
import net.jamsimulator.jams.gui.mips.editor.MIPSSpaces;

import java.util.Optional;

public class ConfigurationWindowNodeMIPSSpaces extends ConfigurationWindowNode<MIPSSpaces> {

    private ComboBox<MIPSSpaces> box;

    public ConfigurationWindowNodeMIPSSpaces(Configuration configuration, String relativeNode,
									String languageNode, String region, MIPSSpaces defaultValue) {
	   super(configuration, relativeNode, languageNode, region, defaultValue);
    }

    @Override
    protected void init() {
	   super.init();
	   box = new ComboBox<>();

	   box.getItems().addAll(MIPSSpaces.values());
	   box.getSelectionModel().select(getValue());
	   box.setConverter(new StringConverter<MIPSSpaces>() {
		  @Override
		  public String toString(MIPSSpaces object) {
			 return object.getDisplayValue();
		  }

		  @Override
		  public MIPSSpaces fromString(String string) {
			 return MIPSSpaces.getByDisplayValue(string).orElse(MIPSSpaces.SPACE);
		  }
	   });

	   getChildren().add(box);

	   box.getSelectionModel().selectedItemProperty().addListener((obs, old, val) -> saveValue(val));
    }

    @Override
    public MIPSSpaces getValue() {
	   Optional<String> optional = configuration.get(relativeNode);
	   try {
		  return optional.map(MIPSSpaces::valueOf).orElse(defaultValue);
	   } catch (IllegalArgumentException ignore) {
		  return defaultValue;
	   }
    }

    @Override
    public void setValue(MIPSSpaces value) {
	   box.getSelectionModel().select(value);
	   saveValue(value);
    }

    @Override
    protected void saveValue(MIPSSpaces value) {
	   configuration.set(relativeNode, value.name());
    }

    static class Builder implements ConfigurationWindowNodeBuilder<MIPSSpaces> {

	   @Override
	   public ConfigurationWindowNode<MIPSSpaces> create(Configuration configuration, String relativeNode, String languageNode, String region) {
		  return new ConfigurationWindowNodeMIPSSpaces(configuration, relativeNode,
				languageNode, region, MIPSSpaces.SPACE);
	   }
    }
}
