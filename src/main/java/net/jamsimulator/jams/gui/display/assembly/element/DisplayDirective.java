package net.jamsimulator.jams.gui.display.assembly.element;

public class DisplayDirective extends AssemblyCodeElement {

	public DisplayDirective(int startIndex, int endIndex, String text) {
		super(startIndex, endIndex, text);
	}

	@Override
	public String getStyle() {
		return "directive";
	}

}
