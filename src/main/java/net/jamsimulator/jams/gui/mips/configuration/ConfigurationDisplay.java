package net.jamsimulator.jams.gui.mips.configuration;

import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import net.jamsimulator.jams.language.Messages;
import net.jamsimulator.jams.language.wrapper.LanguageTab;
import net.jamsimulator.jams.project.mips.MipsProjectData;
import net.jamsimulator.jams.project.mips.MIPSSimulationConfiguration;

public class ConfigurationDisplay extends TabPane {

	private final ConfigurationsWindow window;
	private final MipsProjectData data;
	private MIPSSimulationConfiguration selected;

	public ConfigurationDisplay(ConfigurationsWindow window, MipsProjectData data) {
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
	}

	private void generateGeneralTab() {
		Tab tab = new LanguageTab(Messages.SIMULATION_CONFIGURATION_GENERAL);
		tab.setClosable(false);
		getTabs().add(tab);

		tab.setContent(new ConfigurationGeneralTab(window, data, selected));
	}

	private void generateSyscallsTab() {
		Tab tab = new LanguageTab(Messages.SIMULATION_SYSTEM_CALLS_TAB);
		tab.setClosable(false);
		getTabs().add(tab);

		ScrollPane scrollPane = new ScrollPane(new ConfigurationSyscallTab(selected));
		scrollPane.setFitToHeight(true);
		scrollPane.setFitToWidth(true);

		tab.setContent(scrollPane);
	}


}
