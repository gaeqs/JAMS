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

package net.jamsimulator.jams.gui.mips.configuration;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import net.jamsimulator.jams.gui.configuration.ConfigurationRegionDisplay;
import net.jamsimulator.jams.gui.util.value.ValueEditor;
import net.jamsimulator.jams.gui.util.value.ValueEditors;
import net.jamsimulator.jams.language.Messages;
import net.jamsimulator.jams.language.wrapper.LanguageLabel;
import net.jamsimulator.jams.language.wrapper.LanguageTooltip;
import net.jamsimulator.jams.mips.architecture.Architecture;
import net.jamsimulator.jams.project.mips.configuration.MIPSSimulationConfiguration;
import net.jamsimulator.jams.project.mips.configuration.MIPSSimulationConfigurationNodePreset;
import net.jamsimulator.jams.project.mips.configuration.MIPSSimulationConfigurationPresets;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class MIPSConfigurationDisplayGeneralTab extends VBox {

    private final MIPSSimulationConfiguration configuration;
    private final List<Representation> representations;

    public MIPSConfigurationDisplayGeneralTab(MIPSSimulationConfiguration configuration) {
        setPadding(new Insets(5));
        setSpacing(5);

        this.configuration = configuration;

        Architecture architecture = configuration.getNodeValue(MIPSSimulationConfigurationPresets.ARCHITECTURE);

        representations = new ArrayList<>();


        getChildren().add(new ConfigurationRegionDisplay(Messages.SIMULATION_CONFIGURATION_GENERAL_REGION));
        configuration.getNodes().forEach((preset, value) -> {
            var representation = new Representation(preset, value);
            representation.refreshView(architecture);
            representations.add(representation);
        });
        representations.sort((o1, o2) -> o2.getPreset().getPriority() - o1.getPreset().getPriority());
        representations.stream().filter(Node::isVisible).forEach(getChildren()::add);
        representations.forEach(target -> target.refreshEnabled(representations));
    }

    private void update(MIPSSimulationConfigurationNodePreset preset, Object value) {
        configuration.setNodeValue(preset.getName(), value);
        if (preset.getType() == Architecture.class) {
            getChildren().clear();
            getChildren().add(new ConfigurationRegionDisplay(Messages.SIMULATION_CONFIGURATION_GENERAL_REGION));
            for (Representation representation : representations) {
                representation.refreshView((Architecture) value);
                if (representation.isVisible()) {
                    getChildren().add(representation);
                }
                representation.refreshEnabled(representations);
            }
        } else {
            for (Representation representation : representations) {
                representation.refreshEnabled(representations);
            }
        }
    }

    private class Representation extends HBox {

        private final MIPSSimulationConfigurationNodePreset preset;
        private final ValueEditor<?> editor;

        public Representation(MIPSSimulationConfigurationNodePreset preset, Object value) {
            setSpacing(5);
            setAlignment(Pos.CENTER_LEFT);

            this.preset = preset;
            var label = new LanguageLabel(preset.getLanguageNode());
            label.setTooltip(new LanguageTooltip(preset.getLanguageNode() + "_TOOLTIP"));
            editor = ValueEditors.getByTypeUnsafe(preset.getType()).build();

            if (value instanceof Boolean) {
                label.setOnMouseClicked(event -> editor.setCurrentValueUnsafe(!(boolean) editor.getCurrentValue()));
                getChildren().addAll(editor.getAsNode(), label);
            } else {
                getChildren().addAll(label, editor.getAsNode());
            }

            editor.setCurrentValueUnsafe(value);
            editor.addListener(v -> update(preset, v));
        }

        public MIPSSimulationConfigurationNodePreset getPreset() {
            return preset;
        }

        public void refreshView(Architecture architecture) {
            setVisible(preset.supportArchitecture(architecture));
        }

        public void refreshEnabled(Collection<Representation> representations) {
            setDisabled(representations.stream().anyMatch(target ->
                    !preset.supportsNode(target.preset.getName(), target.editor.getCurrentValue())));
        }

    }
}
