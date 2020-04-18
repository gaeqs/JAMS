package net.jamsimulator.jams.gui.configuration.explorer;

import javafx.scene.control.ScrollPane;
import net.jamsimulator.jams.gui.configuration.ConfigurationWindow;
import net.jamsimulator.jams.gui.explorer.Explorer;
import net.jamsimulator.jams.gui.explorer.ExplorerElement;
import net.jamsimulator.jams.language.Messages;

public class ConfigurationWindowExplorer extends Explorer {

	private final ConfigurationWindow configurationWindow;

	/**
	 * Creates a settings explorer.
	 */
	public ConfigurationWindowExplorer(ConfigurationWindow configurationWindow, ScrollPane scrollPane) {
		super(scrollPane, false);
		this.configurationWindow = configurationWindow;
		generateMainSection();
	}

	public ConfigurationWindow getConfigurationWindow() {
		return configurationWindow;
	}

	@Override
	protected void generateMainSection() {
		mainSection = new ConfigurationWindowSection(this, null,
				"Configuration", Messages.CONFIG,
				0, configurationWindow.getConfiguration(), configurationWindow.getMeta());
		getChildren().add(mainSection);
	}

	@Override
	public void setSelectedElement(ExplorerElement element) {
		super.setSelectedElement(element);
		if (element instanceof ConfigurationWindowSection) {
			configurationWindow.display((ConfigurationWindowSection) element);
		}
	}
}
