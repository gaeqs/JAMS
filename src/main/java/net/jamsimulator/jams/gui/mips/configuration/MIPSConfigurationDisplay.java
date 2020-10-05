package net.jamsimulator.jams.gui.mips.configuration;

import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import net.jamsimulator.jams.project.mips.configuration.MIPSSimulationConfiguration;
import net.jamsimulator.jams.utils.AnchorUtils;

public class MIPSConfigurationDisplay extends AnchorPane {

	private final MIPSConfigurationWindow window;

	private final MIPSSimulationConfiguration configuration;

	public MIPSConfigurationDisplay(MIPSConfigurationWindow window, MIPSSimulationConfiguration configuration) {
		this.window = window;
		this.configuration = configuration;
		AnchorUtils.setAnchor(this, 5, 5, 5, 5);

		populate();
	}

	public MIPSConfigurationWindow getWindow() {
		return window;
	}

	public MIPSSimulationConfiguration getConfiguration() {
		return configuration;
	}

	private void populate() {
		loadNameField();
		loadTabs();
	}

	private void loadNameField() {
		var nameField = new MIPSConfigurationDisplayNameField(window, configuration);
		AnchorUtils.setAnchor(nameField, 0, -1, 0, 0);
		getChildren().add(nameField);
	}

	private void loadTabs() {
		var tabPane = new TabPane();
		AnchorUtils.setAnchor(tabPane, 40, 0, 0, 0);
		getChildren().add(tabPane);

		loadGeneralTab(tabPane);
	}

	private void loadGeneralTab(TabPane tabPane) {
		Tab tab = new Tab("GENERAL");
		VBox contents = new VBox();
		tab.setContent(contents);
		tabPane.getTabs().add(tab);
	}
}
