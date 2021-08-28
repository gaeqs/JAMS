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

package net.jamsimulator.jams.gui.mips.configuration.syscall;

import javafx.beans.property.Property;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import net.jamsimulator.jams.gui.configuration.ConfigurationRegionDisplay;
import net.jamsimulator.jams.gui.util.propertyeditor.BooleanPropertyEditor;
import net.jamsimulator.jams.gui.util.propertyeditor.PropertyEditors;
import net.jamsimulator.jams.language.Messages;
import net.jamsimulator.jams.language.wrapper.LanguageLabel;
import net.jamsimulator.jams.mips.syscall.SyscallExecutionBuilder;

public class MIPSConfigurationSyscallDisplay extends VBox {

    public static final String STYLE_CLASS = "display";
    public static final String REPRESENTATION_STYLE_CLASS = "representation";

    private final SyscallExecutionBuilder<?> builder;

    public MIPSConfigurationSyscallDisplay(SyscallExecutionBuilder<?> builder) {
        this.builder = builder;
        getStyleClass().add(STYLE_CLASS);
        populate();
    }

    private void populate() {
        getChildren().add(new ConfigurationRegionDisplay(Messages.SIMULATION_CONFIGURATION_SYSTEM_CALLS_TAB_PROPERTIES));
        getChildren().add(new Region());

        for (Property<?> property : builder.getProperties()) {
            var hBox = new HBox();
            hBox.getStyleClass().add(REPRESENTATION_STYLE_CLASS);

            var languageNode = builder.getLanguageNode() + "_PROPERTY_" + property.getName();
            var label = new LanguageLabel(languageNode);

            var editor = PropertyEditors.getEditor(property).orElse(null);
            if (editor == null) continue;

            if (editor instanceof BooleanPropertyEditor bEditor) {
                hBox.getChildren().addAll(editor.thisInstanceAsNode(), label);
                label.setOnMouseClicked(event -> bEditor.setSelected(!bEditor.isSelected()));
            } else {
                hBox.getChildren().addAll(label, editor.thisInstanceAsNode());
            }
            getChildren().add(hBox);
        }
    }

}
