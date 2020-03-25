package net.jamsimulator.jams.gui.settings.explorer;

import net.jamsimulator.jams.gui.explorer.Explorer;
import net.jamsimulator.jams.gui.settings.ConfigurationWindow;

public class ConfigurationWindowExplorer extends Explorer {

	private ConfigurationWindow configurationWindow;

	/**
	 * Creates a settings explorer.
	 */
	public ConfigurationWindowExplorer(ConfigurationWindow configurationWindow) {
		super(false);
		this.configurationWindow = configurationWindow;
		generateMainSection();
	}

	public ConfigurationWindow getConfigurationWindow() {
		return configurationWindow;
	}

	@Override
	protected void generateMainSection() {
		mainSection = new ConfigurationWindowSection(this, null,
				"Configuration", 0, configurationWindow.getConfiguration(), configurationWindow.getTypes());
		getChildren().add(mainSection);
	}
}
