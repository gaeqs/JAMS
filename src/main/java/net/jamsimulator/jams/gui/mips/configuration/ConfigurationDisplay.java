package net.jamsimulator.jams.gui.mips.configuration;

import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import net.jamsimulator.jams.gui.util.PixelScrollPane;
import net.jamsimulator.jams.language.Messages;
import net.jamsimulator.jams.language.wrapper.LanguageTab;
import net.jamsimulator.jams.project.mips.MIPSProjectData;
import net.jamsimulator.jams.project.mips.MIPSSimulationConfiguration;

public class ConfigurationDisplay extends TabPane {

	private final ConfigurationsWindow window;
	private final MIPSProjectData data;
	private MIPSSimulationConfiguration selected;

	public ConfigurationDisplay(ConfigurationsWindow window, MIPSProjectData data) {
		this.window = window;
		this.data = data;
	}

	public void select(MIPSSimulationConfiguration configuration) {
		selected = configuration;
		getTabs().clear();
		populate();
	}


	private void populate() {
		generateGeneralTab();
		generateSyscallsTab();
		generateCachesTab();
	}

	private void generateGeneralTab() {
		Tab tab = new LanguageTab(Messages.SIMULATION_CONFIGURATION_GENERAL);
		tab.setClosable(false);
		getTabs().add(tab);

		tab.setContent(new ConfigurationGeneralTab(window, data, selected));
	}

	private void generateSyscallsTab() {
		Tab tab = new LanguageTab(Messages.SIMULATION_CONFIGURATION_SYSTEM_CALLS_TAB);
		tab.setClosable(false);
		getTabs().add(tab);

		var scrollPane = new PixelScrollPane(new ConfigurationSyscallTab(selected));

		scrollPane.setFitToHeight(true);
		scrollPane.setFitToWidth(true);

		tab.setContent(scrollPane);
	}

	private void generateCachesTab() {
		Tab tab = new LanguageTab(Messages.SIMULATION_CONFIGURATION_CACHES_TAB);
		tab.setClosable(false);
		getTabs().add(tab);

		ScrollPane scrollPane = new PixelScrollPane(new ConfigurationCacheTab(selected));
		scrollPane.setFitToHeight(true);
		scrollPane.setFitToWidth(true);

		tab.setContent(scrollPane);
	}


}
