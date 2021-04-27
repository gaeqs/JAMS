package net.jamsimulator.jams.gui.start;

import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import net.jamsimulator.jams.Jams;
import net.jamsimulator.jams.gui.about.AboutWindow;
import net.jamsimulator.jams.language.Messages;

public class StartWindowSectionAbout extends AnchorPane implements StartWindowSection {

    public StartWindowSectionAbout() {
        Jams.getRecentProjects().registerListeners(this, true);
    }

    @Override
    public String getLanguageNode() {
        return Messages.START_ABOUT;
    }

    @Override
    public Node toNode() {
        return new AboutWindow();
    }

    @Override
    public String getName() {
        return "projects";
    }
}
