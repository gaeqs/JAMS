package net.jamsimulator.jams.gui.project;

import javafx.scene.layout.HBox;

public interface ProjectPane {

	String getLanguageNode();

	void populateButtons(HBox buttons);

	void onClose();

}
