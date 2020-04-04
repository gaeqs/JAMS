package net.jamsimulator.jams.gui.display.assembly.element;

import java.util.Objects;

public abstract class AssemblyCodeElement {

	protected final int startIndex;
	protected final int endIndex;
	protected final String text;

	public AssemblyCodeElement(int startIndex, int endIndex, String text) {
		if (startIndex > endIndex) {
			throw new IllegalArgumentException("Start index (" + startIndex + ") is bigger than the end index (" + endIndex + ").");
		}
		this.startIndex = startIndex;
		this.endIndex = endIndex;
		this.text = text;
	}

	public int getStartIndex() {
		return startIndex;
	}

	public int getEndIndex() {
		return endIndex;
	}

	public String getText() {
		return text;
	}

	public abstract String getStyle();

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		AssemblyCodeElement that = (AssemblyCodeElement) o;
		return startIndex == that.startIndex &&
				endIndex == that.endIndex;
	}

	@Override
	public int hashCode() {
		return Objects.hash(startIndex, endIndex);
	}
}
