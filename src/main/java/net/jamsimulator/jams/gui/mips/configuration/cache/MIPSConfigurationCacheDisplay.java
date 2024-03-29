/*
 *  MIT License
 *
 *  Copyright (c) 2021 Gael Rial Costas
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */

package net.jamsimulator.jams.gui.mips.configuration.cache;

import javafx.beans.property.Property;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import net.jamsimulator.jams.gui.configuration.RegionDisplay;
import net.jamsimulator.jams.gui.util.propertyeditor.BooleanPropertyEditor;
import net.jamsimulator.jams.gui.util.propertyeditor.Pow2PropertyEditor;
import net.jamsimulator.jams.gui.util.propertyeditor.PropertyEditor;
import net.jamsimulator.jams.gui.util.propertyeditor.PropertyEditors;
import net.jamsimulator.jams.language.Messages;
import net.jamsimulator.jams.language.wrapper.LanguageLabel;
import net.jamsimulator.jams.mips.memory.cache.CacheBuilder;

public class MIPSConfigurationCacheDisplay extends VBox {

    public static final String STYLE_CLASS = "display";
    public static final String REPRESENTATION_STYLE_CLASS = "representation";
    public static final String SIZE_STYLE_CLASS = "size";

    private final CacheBuilder<?> builder;
    private Label sizeLabel;

    public MIPSConfigurationCacheDisplay(CacheBuilder<?> builder) {
        this.builder = builder;
        getStyleClass().add(STYLE_CLASS);
        populate();
    }

    @SuppressWarnings("unchecked")
    private void populate() {
        getChildren().add(new RegionDisplay(Messages.SIMULATION_CONFIGURATION_CACHES_TAB_PROPERTIES));
        getChildren().add(new Region());

        for (Property<?> property : builder.getProperties()) {

            var hBox = new HBox();
            hBox.getStyleClass().add(REPRESENTATION_STYLE_CLASS);

            var languageNode = builder.getLanguageNode() + "_PROPERTY_" + property.getName();
            var label = new LanguageLabel(languageNode);

            PropertyEditor<?> editor;

            if (property.getValue() instanceof Integer) {
                editor = new Pow2PropertyEditor((Property<Integer>) property, 15);
            } else {
                editor = PropertyEditors.getEditor(property).orElse(null);
                if (editor == null) continue;
            }

            if (editor instanceof BooleanPropertyEditor bEditor) {
                hBox.getChildren().addAll(editor.thisInstanceAsNode(), label);
                label.setOnMouseClicked(event -> bEditor.setSelected(!bEditor.isSelected()));
            } else {
                hBox.getChildren().addAll(label, editor.thisInstanceAsNode());
            }

            editor.addListener(v -> refreshSize());
            getChildren().add(hBox);
        }

        getChildren().addAll(new Group(), new RegionDisplay(Messages.SIMULATION_CONFIGURATION_CACHES_TAB_INFO));

        var sizeBox = new HBox();
        sizeBox.getStyleClass().add(SIZE_STYLE_CLASS);
        sizeBox.getChildren().add(new LanguageLabel(Messages.SIMULATION_CONFIGURATION_CACHES_TAB_SIZE));
        sizeBox.getChildren().add(sizeLabel = new Label(builder.getSizeInBytes() + " B"));
        getChildren().add(sizeBox);
    }

    private void refreshSize() {
        sizeLabel.setText(builder.getSizeInBytes() + " B");
    }

}
