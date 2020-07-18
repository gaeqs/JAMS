package net.jamsimulator.jams.gui.editor;

/**
 * Represents a line inside a {@link CodeFileEditor}.
 */
public class CodeFileLine {

	private final String text;
	private final int index, start;


	public CodeFileLine(int index, String text, int start) {
		this.index = index;
		this.text = text;
		this.start = start;
	}

	public int getIndex() {
		return index;
	}

	public String getText() {
		return text;
	}

	public int getStart() {
		return start;
	}

	@Override
	public String toString() {
		return "CodeFileLine{" +
				"text='" + text + '\'' +
				", index=" + index +
				", start=" + start +
				'}';
	}
}
