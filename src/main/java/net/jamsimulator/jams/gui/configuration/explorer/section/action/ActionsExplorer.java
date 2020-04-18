package net.jamsimulator.jams.gui.configuration.explorer.section.action;

import javafx.scene.control.ScrollPane;
import net.jamsimulator.jams.event.Listener;
import net.jamsimulator.jams.gui.JamsApplication;
import net.jamsimulator.jams.gui.action.event.ActionRegisterEvent;
import net.jamsimulator.jams.gui.action.event.ActionUnregisterEvent;
import net.jamsimulator.jams.gui.explorer.Explorer;

/**
 * Represents an explorer showing all actions registered in {@link JamsApplication#getActionManager()}.
 */
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
		JamsApplication.getActionManager().registerListeners(this);
	}

	@Override
	public void refreshWidth() {
		//Not required. Makes the configuration explorer resize.
	}

	@Listener
	private void onActionRegister(ActionRegisterEvent.After event) {
		((ActionsExplorerMainSection) mainSection).addAction(event.getAction());
	}

	@Listener
	private void onActionUnregister(ActionUnregisterEvent.After event) {
		((ActionsExplorerMainSection) mainSection).removeAction(event.getAction());
	}
}
