package net.jamsimulator.jams.gui.configuration.explorer.node;

import net.jamsimulator.jams.configuration.Configuration;
import net.jamsimulator.jams.gui.JamsApplication;
import net.jamsimulator.jams.gui.theme.Theme;

public class ConfigurationWindowNodeSelectedTheme extends ConfigurationWindowNodeTheme {

	public ConfigurationWindowNodeSelectedTheme(Configuration configuration, String relativeNode,
												String languageNode, Theme defaultValue) {
		super(configuration, relativeNode, languageNode, defaultValue);
	}

	@Override
	protected void saveValue(Theme value) {
		super.saveValue(value);
		JamsApplication.getThemeManager().setSelected(value.getName());
	}

	static class Builder implements ConfigurationWindowNodeBuilder<Theme> {

		@Override
		public ConfigurationWindowNode<Theme> create(Configuration configuration, String relativeNode, String languageNode) {
			return new ConfigurationWindowNodeSelectedTheme(configuration, relativeNode,
					languageNode, JamsApplication.getThemeManager().getSelected());
		}
	}
}
