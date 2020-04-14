package net.jamsimulator.jams.gui.explorer;

import javafx.scene.layout.Region;

/**
 * Small class used to add padding to an {@link Explorer}.
 */
public class ExplorerSeparatorRegion extends Region {

	public static final int HIERARCHY_SEPARATOR_SIZE_FILE = 38;
	public static final int HIERARCHY_SEPARATOR_SIZE_FOLDER = 20;

	public ExplorerSeparatorRegion(boolean folder, int hierarchyLevel) {
		double width;
		if (folder)
			width = HIERARCHY_SEPARATOR_SIZE_FOLDER * hierarchyLevel;
		else
			width = HIERARCHY_SEPARATOR_SIZE_FOLDER * (hierarchyLevel - 1) + HIERARCHY_SEPARATOR_SIZE_FILE;
		setPrefWidth(width);
		setMinWidth(width);
	}

	public ExplorerSeparatorRegion(double width) {
		setPrefWidth(width);
	}

}
