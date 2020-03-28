package net.jamsimulator.jams.gui.configuration.explorer.node;

import net.jamsimulator.jams.Jams;
import net.jamsimulator.jams.configuration.Configuration;
import net.jamsimulator.jams.language.Language;

public class ConfigurationWindowNodeDefaultLanguage extends ConfigurationWindowNodeLanguage {

	public ConfigurationWindowNodeDefaultLanguage(Configuration configuration, String relativeNode,
												  String languageNode, Language defaultValue) {
		super(configuration, relativeNode, languageNode, defaultValue);
	}

	@Override
	protected void saveValue(Language value) {
		super.saveValue(value);
		Jams.getLanguageManager().setDefault(value.getName());
	}

	static class Builder implements ConfigurationWindowNodeBuilder<Language> {

		@Override
		public ConfigurationWindowNode<Language> create(Configuration configuration, String relativeNode, String languageNode) {
			return new ConfigurationWindowNodeDefaultLanguage(configuration, relativeNode,
					languageNode, Jams.getLanguageManager().getDefault());
		}
	}
}
