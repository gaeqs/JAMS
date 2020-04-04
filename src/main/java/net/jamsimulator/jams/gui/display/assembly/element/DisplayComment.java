package net.jamsimulator.jams.gui.display.assembly.element;

public class DisplayComment extends AssemblyCodeElement {

	public DisplayComment(int startIndex, int endIndex, String text) {
		super(startIndex, endIndex, text);
	}

	@Override
	public String getStyle() {
		return "comment";
	}

}
