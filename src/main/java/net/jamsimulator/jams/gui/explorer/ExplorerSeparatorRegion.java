package net.jamsimulator.jams.gui.explorer;

import javafx.scene.layout.Region;

/**
 * Small class used to add padding to an {@link Explorer}.
 */
public class ExplorerSeparatorRegion extends Region {

	public static final int HIERARCHY_SEPARATOR_SIZE = 20;

	public ExplorerSeparatorRegion() {
		setPrefWidth(HIERARCHY_SEPARATOR_SIZE);
	}

}
