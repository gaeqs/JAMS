package net.jamsimulator.jams.gui.display.assembly.element;

public class DisplayLabel extends AssemblyCodeElement {

	public DisplayLabel(int startIndex, int endIndex, String text) {
		super(startIndex, endIndex, text);
	}

	@Override
	public String getStyle() {
		return "label";
	}

}
