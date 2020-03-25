package net.jamsimulator.jams.gui.settings.explorer.node;

import net.jamsimulator.jams.Jams;
import net.jamsimulator.jams.configuration.Configuration;
import net.jamsimulator.jams.language.Language;

public class ConfigurationWindowNodeSelectedLanguage extends ConfigurationWindowNodeLanguage {

	public ConfigurationWindowNodeSelectedLanguage(Configuration configuration, String relativeNode,
												   String languageNode, Language defaultValue) {
		super(configuration, relativeNode, languageNode, defaultValue);
	}

	@Override
	protected void saveValue(Language value) {
		configuration.set(relativeNode, value.getName());
		Jams.getLanguageManager().setSelected(value.getName());
	}

	static class Builder implements ConfigurationWindowNodeBuilder<Language> {

		@Override
		public ConfigurationWindowNode<Language> create(Configuration configuration, String relativeNode, String languageNode) {
			return new ConfigurationWindowNodeSelectedLanguage(configuration, relativeNode,
					languageNode, Jams.getLanguageManager().getDefault());
		}
	}
}
