/*
 * MIT License
 *
 * Copyright (c) 2020 Gael Rial Costas
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

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
