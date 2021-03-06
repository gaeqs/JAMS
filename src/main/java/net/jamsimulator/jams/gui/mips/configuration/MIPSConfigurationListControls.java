package net.jamsimulator.jams.gui.mips.configuration;

import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import net.jamsimulator.jams.gui.JamsApplication;
import net.jamsimulator.jams.gui.explorer.ExplorerElement;
import net.jamsimulator.jams.gui.image.NearestImageView;
import net.jamsimulator.jams.gui.image.icon.Icons;
import net.jamsimulator.jams.language.Messages;
import net.jamsimulator.jams.language.wrapper.LanguageTooltip;
import net.jamsimulator.jams.project.mips.configuration.MIPSSimulationConfiguration;

import java.util.Set;


public class MIPSConfigurationListControls extends HBox {

	public MIPSConfigurationsList list;

	public MIPSConfigurationListControls(MIPSConfigurationsList list) {
		this.list = list;
		populate();
	}


	private void populate() {
		generateAddButton();
		generateRemoveButton();
		generateCopyButton();
	}

	private void generateAddButton() {
		var icon = JamsApplication.getIconManager().getOrLoadSafe(Icons.CONTROL_ADD
		).orElse(null);

		var button = new Button(null, new NearestImageView(icon, 16, 16));
		button.setTooltip(new LanguageTooltip(Messages.GENERAL_ADD));
		button.getStyleClass().add("bold-button");

		button.setOnAction(event -> {
			var baseName = "New Configuration";
			var configs = list.getWindow().getProjectData().getConfigurations();

			var name = baseName;

			var amount = 1;
			while (isNameCaught(configs, name)) {
				name = baseName + " (" + amount++ + ")";
			}

			list.getWindow().getProjectData().addConfiguration(new MIPSSimulationConfiguration(name));
		});

		getChildren().add(button);
	}

	private boolean isNameCaught(Set<MIPSSimulationConfiguration> configurations, String name) {
		return configurations.stream().anyMatch(target -> target.getName().equals(name));
	}

	private void generateRemoveButton() {
		var icon = JamsApplication.getIconManager().getOrLoadSafe(Icons.CONTROL_REMOVE
		).orElse(null);

		var button = new Button(null, new NearestImageView(icon, 16, 16));
		button.setTooltip(new LanguageTooltip(Messages.GENERAL_REMOVE));
		button.getStyleClass().add("bold-button");

		button.setOnAction(event -> {
			var selected = list.getContents().getSelectedElements();
			var data = list.getWindow().getProjectData();
			if (selected.isEmpty()) return;
			for (ExplorerElement element : selected) {
				data.removeConfiguration(element.getName());
			}

			list.getContents().getMainSection().getElementByIndex(0).ifPresentOrElse(
					list.getContents()::selectElementAlone,
					() -> list.getWindow().display(null));
		});

		getChildren().add(button);
	}

	private void generateCopyButton() {
		var icon = JamsApplication.getIconManager().getOrLoadSafe(Icons.CONTROL_COPY
		).orElse(null);

		var button = new Button(null, new NearestImageView(icon, 16, 16));
		button.setTooltip(new LanguageTooltip(Messages.GENERAL_COPY));
		button.getStyleClass().add("bold-button");

		button.setOnAction(event -> {
			var selected = list.getContents().getSelectedElements();

			for (ExplorerElement element : selected) {
				if (element instanceof MIPSConfigurationsListContents.Representation) {
					copyConfiguration(((MIPSConfigurationsListContents.Representation) element).getConfiguration());
				}
			}

		});

		getChildren().add(button);
	}

	private void copyConfiguration(MIPSSimulationConfiguration configuration) {
		var baseName = configuration.getName() + " - Copy";
		var configs = list.getWindow().getProjectData().getConfigurations();

		var name = baseName;

		var amount = 1;
		while (isNameCaught(configs, name)) {
			name = baseName + " (" + amount++ + ")";
		}

		list.getWindow().getProjectData().addConfiguration(new MIPSSimulationConfiguration(name, configuration));
	}

}
