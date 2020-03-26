package net.jamsimulator.jams.gui.settings.explorer;

import net.jamsimulator.jams.configuration.Configuration;
import net.jamsimulator.jams.utils.Validate;

public class ConfigurationMetadata {

	public static final String TYPE_KEY = "type";
	public static final String LANGUAGE_NODE_KEY = "language_node";

	private final String type;
	private final String languageNode;

	public ConfigurationMetadata(Configuration configuration) {
		Validate.notNull(configuration, "Configuration cannot be null!");
		this.type = configuration.getString(TYPE_KEY).orElse(null);
		this.languageNode = configuration.getString(LANGUAGE_NODE_KEY).orElse(null);
	}

	public String getType() {
		return type;
	}

	public String getLanguageNode() {
		return languageNode;
	}
}
