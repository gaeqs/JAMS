package net.jamsimulator.jams.gui.settings;

import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import net.jamsimulator.jams.configuration.Configuration;
import net.jamsimulator.jams.gui.settings.parameter.ConfigurationParameterBoolean;
import net.jamsimulator.jams.language.wrapper.LanguageLabel;

import java.util.ArrayList;
import java.util.List;

public class ConfigurationWindowSection extends VBox {

	private ConfigurationWindow configurationWindow;
	private ConfigurationWindowSection parent;

	private String node;
	private String languageNode;

	private List<ConfigurationWindowSection> childSections;

	private VBox content;

	public ConfigurationWindowSection(ConfigurationWindow configurationWindow, ConfigurationWindowSection parent,
									  Configuration configuration, String node, String languageNode) {
		this.configurationWindow = configurationWindow;
		this.parent = parent;
		this.node = node;
		this.languageNode = languageNode;

		this.childSections = new ArrayList<>();

		init(configuration);
	}

	public String getNode() {
		return node;
	}

	public String getFullNode() {
		return parent == null ? node : parent.getFullNode() + "." + node;
	}

	public String getLanguageNode() {
		return languageNode;
	}

	public ConfigurationWindowSection getParentSection() {
		return parent;
	}

	public ConfigurationWindow getConfigurationWindow() {
		return configurationWindow;
	}

	private void init(Configuration configuration) {
		Label label = languageNode == null ? new Label(node) : new LanguageLabel(languageNode);
		getChildren().addAll(label);

		content = new VBox();

		configuration.getAll().forEach((key, value) -> {
			if (value instanceof Configuration) {
				content.getChildren().add(new ConfigurationWindowSection(configurationWindow, this,
						(Configuration) value, getRelativeNodeName(key), null));
			} else if (value instanceof Boolean) {
				content.getChildren().add(new ConfigurationParameterBoolean(this, getRelativeNodeName(key),
						null, false));
			}
		});

		getChildren().add(content);
	}


	private static String getRelativeNodeName(String string) {
		int index = string.lastIndexOf(".");
		if (index < 1 || index == string.length() - 1) return string;
		return string.substring(index + 1);
	}
}
