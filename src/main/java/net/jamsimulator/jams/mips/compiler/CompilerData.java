package net.jamsimulator.jams.mips.compiler;

import net.jamsimulator.jams.mips.compiler.exception.CompilerException;

public class CompilerData {

	SelectedMemorySegment selected;

	int firstText;
	int firstData;
	int firstKText;
	int firstKData;
	int currentText;
	int currentData;
	int currentKText;
	int currentKData;
	int nextForcedAlignment;

	public CompilerData(int currentText, int currentData, int currentKText, int currentKData) {
		this.firstText = currentText;
		this.firstData = currentData;
		this.firstKText = currentKText;
		this.firstKData = currentKData;
		this.currentText = currentText;
		this.currentData = currentData;
		this.currentKText = currentKText;
		this.currentKData = currentKData;
		selected = SelectedMemorySegment.TEXT;
		nextForcedAlignment = -1;
	}

	public SelectedMemorySegment getSelected() {
		return selected;
	}

	public void setSelected(SelectedMemorySegment selected) {
		this.selected = selected;
	}

	public int getFirstText() {
		return firstText;
	}

	public int getFirstData() {
		return firstData;
	}

	public int getFirstKText() {
		return firstKText;
	}

	public int getFirstKData() {
		return firstKData;
	}

	public int getCurrentText() {
		return currentText;
	}

	public int getCurrentData() {
		return currentData;
	}

	public int getCurrentKText() {
		return currentKText;
	}

	public int getCurrentKData() {
		return currentKData;
	}

	public boolean isNextAlignmentForced() {
		return nextForcedAlignment >= 0;
	}

	public int getNextForcedAlignment() {
		return nextForcedAlignment;
	}

	public void setNextForcedAlignment(int nextForcedAlignment) {
		this.nextForcedAlignment = nextForcedAlignment;
	}

	public boolean align(int unforcedAlign) {
		int align = isNextAlignmentForced() ? nextForcedAlignment : unforcedAlign;
		nextForcedAlignment = -1;
		int pow = 1;
		for (int i = 0; i < align; i++) {
			pow *= 2;
		}

		int current = getCurrent();
		int mod = current % pow;
		if (mod == 0) return false;
		addCurrent(pow - mod);
		return true;
	}


	public int getCurrent() {
		switch (selected) {
			case TEXT:
				return currentText;
			case DATA:
				return currentData;
			case KERNEL_TEXT:
				return currentKText;
			case KERNEL_DATA:
				return currentKData;
		}
		throw new CompilerException("Selected section not defined.");
	}

	public void addCurrent(int add) {
		switch (selected) {
			case TEXT:
				currentText += add;
				break;
			case DATA:
				currentData += add;
				break;
			case KERNEL_TEXT:
				currentKText += add;
				break;
			case KERNEL_DATA:
				currentKData += add;
				break;
			default:
				throw new CompilerException("Selected section not defined.");
		}
	}
}
