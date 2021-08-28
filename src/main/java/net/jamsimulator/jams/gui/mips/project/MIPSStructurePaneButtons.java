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

package net.jamsimulator.jams.gui.mips.project;

import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import net.jamsimulator.jams.event.Listener;
import net.jamsimulator.jams.gui.action.defaults.general.GeneralActionAssemble;
import net.jamsimulator.jams.gui.image.icon.Icons;
import net.jamsimulator.jams.gui.image.quality.QualityImageView;
import net.jamsimulator.jams.gui.mips.configuration.MIPSConfigurationWindow;
import net.jamsimulator.jams.project.mips.MIPSProject;
import net.jamsimulator.jams.project.mips.configuration.MIPSSimulationConfiguration;
import net.jamsimulator.jams.project.mips.event.MIPSSimulationConfigurationAddEvent;
import net.jamsimulator.jams.project.mips.event.MIPSSimulationConfigurationRefreshEvent;
import net.jamsimulator.jams.project.mips.event.MIPSSimulationConfigurationRemoveEvent;
import net.jamsimulator.jams.project.mips.event.SelectedMIPSSimulationConfigurationChangeEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MIPSStructurePaneButtons {

    private final List<Node> nodes;
    private final MIPSProject project;
    private ComboBox<String> configBox;

    public MIPSStructurePaneButtons(MIPSStructurePane structurePane) {
        nodes = new ArrayList<>();
        project = structurePane.getProject();

        loadAssembleButton(structurePane);
        loadConfigurationComboBox(structurePane);
        loadConfigurationSettingsButton(structurePane);

        structurePane.getProject().getData().registerListeners(this, true);
    }

    public List<Node> getNodes() {
        return nodes;
    }

    private void loadConfigurationComboBox(MIPSStructurePane structurePane) {
        MIPSProject project = structurePane.project;
        configBox = new ComboBox<>();
        configBox.setMaxWidth(200);

        Set<MIPSSimulationConfiguration> configurations = project.getData().getConfigurations();
        configurations.forEach(config -> configBox.getItems().add(config.getName()));
        if (project.getData().getSelectedConfiguration().isPresent()) {
            configBox.getSelectionModel().select(project.getData().getSelectedConfiguration().get().getName());
        }

        configBox.setOnAction(target -> Platform.runLater(() ->
                project.getData().setSelectedConfiguration(configBox.getSelectionModel().getSelectedItem())));

        nodes.add(configBox);
    }

    private void loadAssembleButton(MIPSStructurePane structurePane) {
        var assemble = new Button("", new QualityImageView(Icons.PROJECT_ASSEMBLE, 20, 20));
        assemble.getStyleClass().add("buttons-hbox-button");
        assemble.setOnAction(event -> GeneralActionAssemble.compileAndShow(structurePane.project));
        nodes.add(assemble);
    }

    private void loadConfigurationSettingsButton(MIPSStructurePane structurePane) {
        var configButton = new Button("", new QualityImageView(Icons.PROJECT_SETTINGS, 20, 20));
        configButton.getStyleClass().add("buttons-hbox-button");
        configButton.setOnAction(event -> MIPSConfigurationWindow.open(structurePane.getProject().getData()));
        nodes.add(configButton);
    }

    @Listener
    private void onConfigurationAdd(MIPSSimulationConfigurationAddEvent.After event) {
        configBox.getItems().add(event.getMipsSimulationConfiguration().getName());
    }

    @Listener
    private void onConfigurationRemove(MIPSSimulationConfigurationRemoveEvent.After event) {
        configBox.getItems().remove(event.getMipsSimulationConfiguration().getName());
    }

    @Listener
    private void onConfigurationChange(SelectedMIPSSimulationConfigurationChangeEvent.After event) {
        if (event.getNewConfig() == null) {
            return;
        }
        configBox.getSelectionModel().select(event.getNewConfig().getName());
    }

    @Listener
    private void onRefresh(MIPSSimulationConfigurationRefreshEvent event) {
        configBox.getItems().clear();
        Set<MIPSSimulationConfiguration> configurations = project.getData().getConfigurations();
        configurations.forEach(config -> configBox.getItems().add(config.getName()));
        if (project.getData().getSelectedConfiguration().isPresent()) {
            configBox.getSelectionModel().select(project.getData().getSelectedConfiguration().get().getName());
        }
    }
}
