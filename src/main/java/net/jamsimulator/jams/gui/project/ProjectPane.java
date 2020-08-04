package net.jamsimulator.jams.gui.project;

import javafx.scene.layout.HBox;

public interface ProjectPane {

	String getLanguageNode();

	/**
	 * Populates the {@link ProjectTab}'s buttons h-box.
	 *
	 * @param buttonsHBox the buttons h-box.
	 */
	void populateHBox(HBox buttonsHBox);

	void onClose();

}
