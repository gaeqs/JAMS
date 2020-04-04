package net.jamsimulator.jams.gui.display.assembly.element;

public class DisplayDirectiveParameter extends AssemblyCodeElement {

	private boolean string;

	public DisplayDirectiveParameter(int startIndex, int endIndex, String text, boolean string) {
		super(startIndex, endIndex, text);
		this.string = string;
	}

	@Override
	public String getStyle() {
		return string ? "directive-parameter-string" : "directive-parameter";
	}

}
