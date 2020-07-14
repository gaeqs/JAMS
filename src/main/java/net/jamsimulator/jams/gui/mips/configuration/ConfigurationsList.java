package net.jamsimulator.jams.gui.mips.configuration;

import javafx.geometry.Orientation;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import net.jamsimulator.jams.Jams;
import net.jamsimulator.jams.event.Listener;
import net.jamsimulator.jams.project.mips.MipsProjectData;
import net.jamsimulator.jams.project.mips.MipsSimulationConfiguration;
import net.jamsimulator.jams.project.mips.event.MipsSimulationConfigurationAddEvent;
import net.jamsimulator.jams.project.mips.event.MipsSimulationConfigurationRemoveEvent;

import java.util.HashMap;
import java.util.Set;

public class ConfigurationsList extends VBox {

	private final MipsProjectData data;
	private final ConfigurationsWindow window;
	private final VBox contents;

	private ConfigurationRepresentation selected;
	protected Button add, remove;


	public ConfigurationsList(ConfigurationsWindow window, MipsProjectData data) {
		getStyleClass().add("mips-configurations-list");
		this.data = data;
		this.window = window;

		loadButtons();

		contents = new VBox();
		contents.getStyleClass().add("mips-configurations-list");

		ScrollPane contentsScroll = new ScrollPane(contents);
		contentsScroll.setFitToHeight(true);
		contentsScroll.setFitToWidth(true);

		getChildren().add(contentsScroll);

		setMinWidth(210);

		data.getConfigurations().forEach(this::addConfiguration);

		data.registerListeners(this, true);
	}

	public void refreshName(MipsSimulationConfiguration configuration) {
		contents.getChildren().stream().filter(target -> target instanceof ConfigurationRepresentation &&
				((ConfigurationRepresentation) target).getConfiguration().equals(configuration))
				.forEach(target -> ((ConfigurationRepresentation) target).refreshName());
	}

	private void loadButtons() {
		add = new Button("+");
		add.getStyleClass().add("bold-button");
		add.setOnAction(event -> {
			Set<MipsSimulationConfiguration> configs = data.getConfigurations();
			String name = "New Configuration";
			if (configs.stream().anyMatch(target -> target.getName().equals(name))) {
				String newName = null;
				int i = 2;
				boolean repeat;
				do {
					String current = name + " (" + i + ")";
					repeat = configs.stream().anyMatch(target -> target.getName().equals(current));
					if (!repeat) newName = current;
					i++;
				} while (repeat);
				data.addConfiguration(new MipsSimulationConfiguration(newName, Jams.getArchitectureManager().getDefault(),
						Jams.getMemoryBuilderManager().getDefault(), new HashMap<>()));
			} else {
				data.addConfiguration(new MipsSimulationConfiguration(name, Jams.getArchitectureManager().getDefault(),
						Jams.getMemoryBuilderManager().getDefault(), new HashMap<>()));
			}
		});

		remove = new Button("-");
		remove.getStyleClass().add("bold-button");
		remove.setOnAction(event -> {
			if (selected == null) return;
			data.removeConfiguration(selected.getConfiguration().getName());
		});
		remove.setDisable(true);

		HBox hBox = new HBox(add, remove);
		hBox.getStyleClass().add("mips-configurations-list-buttons");
		getChildren().add(hBox);
		getChildren().add(new Separator(Orientation.HORIZONTAL));
	}

	private void select(ConfigurationRepresentation representation) {
		if (selected != null) {
			selected.getStyleClass().remove("mips-configurations-list-entry-selected");
		}
		selected = representation;
		if (selected != null) {
			selected.getStyleClass().add("mips-configurations-list-entry-selected");
			window.getDisplay().select(selected.getConfiguration());
		}
		remove.setDisable(selected == null);
	}

	private void addConfiguration(MipsSimulationConfiguration configuration) {
		contents.getChildren().add(new ConfigurationRepresentation(configuration));
	}

	@Listener
	private void onConfigurationAdded(MipsSimulationConfigurationAddEvent.After event) {
		addConfiguration(event.getMipsSimulationConfiguration());
	}

	@Listener
	private void onConfigurationRemoved(MipsSimulationConfigurationRemoveEvent.After event) {
		contents.getChildren().removeIf(target -> target instanceof ConfigurationRepresentation
				&& ((ConfigurationRepresentation) target)
				.getConfiguration().equals(event.getMipsSimulationConfiguration()));
		if (selected != null && selected.getConfiguration() == event.getMipsSimulationConfiguration()) {
			select(null);
		}
	}


	private class ConfigurationRepresentation extends HBox {

		private final MipsSimulationConfiguration configuration;
		private final Label label;

		public ConfigurationRepresentation(MipsSimulationConfiguration configuration) {
			super();
			getStyleClass().add("mips-configurations-list-entry");
			this.configuration = configuration;

			label = new Label(configuration.getName());
			getChildren().add(label);

			setOnMouseClicked(event -> select(this));
		}

		public MipsSimulationConfiguration getConfiguration() {
			return configuration;
		}

		public void refreshName() {
			label.setText(configuration.getName());
		}
	}
}
