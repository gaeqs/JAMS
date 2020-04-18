package net.jamsimulator.jams.gui.configuration.explorer.section.action;

import javafx.scene.control.ScrollPane;
import net.jamsimulator.jams.gui.explorer.Explorer;

public class ActionsExplorer extends Explorer {

	/**
	 * Creates an explorer.
	 *
	 * @param scrollPane the {@link ScrollPane} holding this explorer, if present.
	 */
	public ActionsExplorer(ScrollPane scrollPane) {
		super(scrollPane, true);
	}

	@Override
	protected void generateMainSection() {
		mainSection = new ActionsExplorerMainSection(this);
		getChildren().add(mainSection);
		mainSection.expand();
	}

	@Override
	public void refreshWidth() {
		//Not required. Makes the configuration explorer resize.
	}
}
