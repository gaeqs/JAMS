package net.jamsimulator.jams.gui.mips.configuration;

import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;
import net.jamsimulator.jams.gui.mips.configuration.cache.MIPSConfigurationDisplayCacheTab;
import net.jamsimulator.jams.gui.mips.configuration.syscall.MIPSConfigurationDisplaySyscallTab;
import net.jamsimulator.jams.gui.util.PixelScrollPane;
import net.jamsimulator.jams.language.Messages;
import net.jamsimulator.jams.language.wrapper.LanguageTab;
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
		loadSyscallsTab(tabPane);
		loadCacheTab(tabPane);
	}

	private void loadGeneralTab(TabPane tabPane) {
		Tab tab = new LanguageTab(Messages.SIMULATION_CONFIGURATION_GENERAL);
		tab.setClosable(false);

		ScrollPane scrollPane = new PixelScrollPane(new MIPSConfigurationDisplayGeneralTab(configuration));
		scrollPane.setFitToWidth(true);
		scrollPane.setFitToHeight(true);

		tab.setContent(scrollPane);
		tabPane.getTabs().add(tab);
	}

	private void loadSyscallsTab(TabPane tabPane) {
		Tab tab = new LanguageTab(Messages.SIMULATION_CONFIGURATION_SYSTEM_CALLS_TAB);
		tab.setClosable(false);
		tab.setContent(new MIPSConfigurationDisplaySyscallTab(configuration));
		tabPane.getTabs().add(tab);
	}

	private void loadCacheTab(TabPane tabPane) {
		Tab tab = new LanguageTab(Messages.SIMULATION_CONFIGURATION_CACHES_TAB);
		tab.setClosable(false);
		tab.setContent(new MIPSConfigurationDisplayCacheTab(configuration));
		tabPane.getTabs().add(tab);
	}
}
