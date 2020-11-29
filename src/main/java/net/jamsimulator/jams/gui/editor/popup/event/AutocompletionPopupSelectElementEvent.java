package net.jamsimulator.jams.gui.editor.popup.event;

import net.jamsimulator.jams.event.Event;
import net.jamsimulator.jams.gui.editor.popup.AutocompletionPopupElement;

public class AutocompletionPopupSelectElementEvent extends Event {

	private final AutocompletionPopupElement selectedElement;

	public AutocompletionPopupSelectElementEvent(AutocompletionPopupElement selectedElement) {
		this.selectedElement = selectedElement;
	}

	public AutocompletionPopupElement getSelectedElement() {
		return selectedElement;
	}
}
