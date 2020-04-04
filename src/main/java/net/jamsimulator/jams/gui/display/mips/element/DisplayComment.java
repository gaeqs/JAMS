package net.jamsimulator.jams.gui.display.mips.element;

import net.jamsimulator.jams.gui.main.WorkingPane;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class DisplayComment extends MipsCodeElement {

	public DisplayComment(int startIndex, int endIndex, String text) {
		super(startIndex, endIndex, text);
	}

	@Override
	public List<String> getStyles() {
		return Collections.singletonList("assembly-comment");
	}

	@Override
	public void searchErrors(WorkingPane pane, MipsFileElements elements) {
	}

}
