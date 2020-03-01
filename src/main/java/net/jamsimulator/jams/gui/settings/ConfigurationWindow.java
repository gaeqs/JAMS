package net.jamsimulator.jams.gui.settings;

import javafx.scene.control.SplitPane;
import javafx.scene.layout.VBox;
import net.jamsimulator.jams.configuration.RootConfiguration;

import java.util.ArrayList;
import java.util.List;

public class ConfigurationWindow extends SplitPane {

	private RootConfiguration configuration;

	private VBox sectionListDisplay;
	private VBox configurationDisplay;

	private List<ConfigurationWindowSection> sections;

	public ConfigurationWindow(RootConfiguration configuration, List<ConfigurationWindowSection> sections) {
		this.configuration = configuration;

		this.sectionListDisplay = new VBox();
		this.configurationDisplay = new VBox();
		this.sections = new ArrayList<>(sections);
		init();
	}

	public RootConfiguration getConfiguration() {
		return configuration;
	}

	private void init() {
		getItems().add(sectionListDisplay);
		getItems().add(configurationDisplay);
		sectionListDisplay.getChildren().addAll(sections);
	}
}
