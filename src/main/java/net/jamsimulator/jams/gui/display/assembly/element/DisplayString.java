package net.jamsimulator.jams.gui.display.assembly.element;

public class DisplayString extends AssemblyCodeElement {

	public DisplayString(int startIndex, int endIndex, String text) {
		super(startIndex, endIndex, text);
	}

	@Override
	public String getStyle() {
		return "string";
	}

}
