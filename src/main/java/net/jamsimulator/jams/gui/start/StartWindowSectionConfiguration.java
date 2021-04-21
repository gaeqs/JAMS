package net.jamsimulator.jams.gui.start;

import javafx.scene.Node;
import net.jamsimulator.jams.gui.configuration.ConfigurationWindow;
import net.jamsimulator.jams.language.Messages;

public class StartWindowSectionConfiguration implements StartWindowSection {

    public StartWindowSectionConfiguration() {
    }

    @Override
    public String getLanguageNode() {
        return Messages.CONFIG;
    }

    @Override
    public Node toNode() {
        return ConfigurationWindow.getInstance();
    }

    @Override
    public String getName() {
        return "projects";
    }
}
