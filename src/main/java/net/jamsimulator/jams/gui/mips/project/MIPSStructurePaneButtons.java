package net.jamsimulator.jams.gui.mips.project;

import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.image.Image;
import net.jamsimulator.jams.event.Listener;
import net.jamsimulator.jams.gui.JamsApplication;
import net.jamsimulator.jams.gui.action.defaults.general.GeneralActionAssemble;
import net.jamsimulator.jams.gui.image.NearestImageView;
import net.jamsimulator.jams.gui.image.icon.Icons;
import net.jamsimulator.jams.gui.mips.configuration.MIPSConfigurationWindow;
import net.jamsimulator.jams.gui.util.FixedButton;
import net.jamsimulator.jams.project.mips.MIPSProject;
import net.jamsimulator.jams.project.mips.MIPSSimulationConfiguration;
import net.jamsimulator.jams.project.mips.event.MipsSimulationConfigurationAddEvent;
import net.jamsimulator.jams.project.mips.event.MipsSimulationConfigurationRemoveEvent;
import net.jamsimulator.jams.project.mips.event.SelectedMipsSimulationConfigurationChangeEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MIPSStructurePaneButtons {

	private final List<Node> nodes;

	private ComboBox<String> configBox;

	public MIPSStructurePaneButtons(MIPSStructurePane structurePane) {
		nodes = new ArrayList<>();

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
		Image icon = JamsApplication.getIconManager().getOrLoadSafe(Icons.PROJECT_ASSEMBLE, Icons.PROJECT_ASSEMBLE_PATH,
				1024, 1024).orElse(null);

		Button assemble = new FixedButton("", new NearestImageView(icon, 18, 18), 28, 28);
		assemble.getStyleClass().add("buttons-hbox-button");
		assemble.setOnAction(event -> GeneralActionAssemble.compileAndShow(structurePane.project));
		nodes.add(assemble);
	}

	private void loadConfigurationSettingsButton(MIPSStructurePane structurePane) {
		Image icon = JamsApplication.getIconManager().getOrLoadSafe(Icons.PROJECT_SETTINGS,
				Icons.PROJECT_SETTINGS_PATH, 1024, 1024).orElse(null);

		Button configButton = new FixedButton("", new NearestImageView(icon, 18, 18), 28, 28);
		configButton.getStyleClass().add("buttons-hbox-button");
		configButton.setOnAction(event -> MIPSConfigurationWindow.open(structurePane.getProject().getData()));
		nodes.add(configButton);
	}

	@Listener
	private void onConfigurationAdd(MipsSimulationConfigurationAddEvent.After event) {
		configBox.getItems().add(event.getMipsSimulationConfiguration().getName());
	}

	@Listener
	private void onConfigurationRemove(MipsSimulationConfigurationRemoveEvent.After event) {
		configBox.getItems().remove(event.getMipsSimulationConfiguration().getName());
	}

	@Listener
	private void onConfigurationChange(SelectedMipsSimulationConfigurationChangeEvent.After event) {
		if (event.getNewConfig() == null) {
			return;
		}
		configBox.getSelectionModel().select(event.getNewConfig().getName());
	}
}
