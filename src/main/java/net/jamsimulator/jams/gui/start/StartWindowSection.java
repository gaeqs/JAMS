package net.jamsimulator.jams.gui.start;

import javafx.scene.Node;
import net.jamsimulator.jams.manager.Labeled;

public interface StartWindowSection extends Labeled {

    String getLanguageNode();

    Node toNode();

}
