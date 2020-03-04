package net.jamsimulator.jams.gui.explorer;

import javafx.scene.input.KeyEvent;

import java.util.Optional;

public interface ExplorerElement {

	String getName();

	boolean isSelected();

	void select();

	void deselect();

	Optional<ExplorerElement> getNext();

	Optional<ExplorerElement> getPrevious();

	void handleKeyPressEvent(KeyEvent event);
}
