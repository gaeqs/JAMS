package net.jamsimulator.jams.gui.settings.explorer.node;

import net.jamsimulator.jams.configuration.Configuration;

public interface ConfigurationWindowNodeBuilder<E> {

	ConfigurationWindowNode<E> create(Configuration configuration, String relativeNode, String languageNode);

}
