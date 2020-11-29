package net.jamsimulator.jams.gui.mips.editor;

import net.jamsimulator.jams.event.Listener;
import net.jamsimulator.jams.gui.editor.CodeFileEditor;
import net.jamsimulator.jams.gui.editor.popup.DocumentationPopup;
import net.jamsimulator.jams.gui.editor.popup.event.AutocompletionPopupSelectElementEvent;
import net.jamsimulator.jams.gui.util.StringStyler;
import net.jamsimulator.jams.mips.instruction.Instruction;

public class MipsDocumentationPopup extends DocumentationPopup {

	private final MIPSAutocompletionPopup autocompletionPopup;

	/**
	 * Creates the documentation popup.
	 *
	 * @param display the code display where this popup is displayed.
	 */
	public MipsDocumentationPopup(CodeFileEditor display, MIPSAutocompletionPopup autocompletionPopup) {
		super(display);
		this.autocompletionPopup = autocompletionPopup;
		autocompletionPopup.registerListeners(this, true);
		content.focusedProperty().addListener((obs, old, val) -> {
			if (val) {
				autocompletionPopup.requestFocus();
			}
		});

		scroll.focusedProperty().addListener((obs, old, val) -> {
			if (val) {
				autocompletionPopup.requestFocus();
			}
		});
	}

	@Listener
	private void onSelect(AutocompletionPopupSelectElementEvent event) {
		if (event.getSelectedElement().getElement() instanceof Instruction) {
			StringStyler.style(((Instruction) event.getSelectedElement().getElement()).getDocumentation(), content);
		}
	}

	@Override
	public void execute(int caretOffset) {
		super.execute(caretOffset);
	}
}
