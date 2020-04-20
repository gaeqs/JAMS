package net.jamsimulator.jams.gui.mips.display.element;

import javafx.scene.layout.VBox;
import net.jamsimulator.jams.gui.main.WorkingPane;

import java.util.Collections;
import java.util.List;

public class DisplayComment extends MipsCodeElement {

	public DisplayComment(int startIndex, int endIndex, String text) {
		super(startIndex, endIndex, text);
	}

	@Override
	public List<String> getStyles() {
		return Collections.singletonList("mips-comment");
	}

	@Override
	public void searchErrors(WorkingPane pane, MipsFileElements elements) {
	}

	@Override
	public boolean searchLabelErrors(List<String> labels) {
		return false;
	}

	@Override
	public void populatePopup(VBox popup) {
		populatePopupWithErrors(popup);
	}

}
