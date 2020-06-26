/*
 * MIT License
 *
 * Copyright (c) 2020 Gael Rial Costas
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package net.jamsimulator.jams.mips.assembler;

import net.jamsimulator.jams.mips.assembler.exception.AssemblerException;
import net.jamsimulator.jams.mips.memory.Memory;

/**
 * Contains all the data required by the assembler.
 */
public class MIPS32AssemblerData {

	private SelectedMemorySegment selected;

	private final int firstText;
	private final int firstData;
	private final int firstKText;
	private final int firstKData;
	private final int firstExtern;
	private int currentText;
	private int currentData;
	private int currentKText;
	private int currentKData;
	private int currentExtern;
	private int nextForcedAlignment;


	public MIPS32AssemblerData(Memory memory) {
		this(memory.getFirstTextAddress(), memory.getFirstDataAddress(), memory.getFirstKernelTextAddress(),
				memory.getFirstKernelDataAddress(), memory.getFirstExternalAddress());
	}

	public MIPS32AssemblerData(int currentText, int currentData, int currentKText, int currentKData, int currentExtern) {
		this.firstText = currentText;
		this.firstData = currentData;
		this.firstKText = currentKText;
		this.firstKData = currentKData;
		this.firstExtern = currentExtern;
		this.currentText = currentText;
		this.currentData = currentData;
		this.currentKText = currentKText;
		this.currentKData = currentKData;
		this.currentExtern = currentExtern;
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

	public int getFirstExtern() {
		return firstExtern;
	}

	public int getCurrentText() {
		return currentText;
	}

	public void setCurrentText(int currentText) {
		this.currentText = currentText;
	}

	public int getCurrentData() {
		return currentData;
	}


	public void setCurrentData(int currentData) {
		this.currentData = currentData;
	}

	public int getCurrentKText() {
		return currentKText;
	}

	public void setCurrentKText(int currentKText) {
		this.currentKText = currentKText;
	}

	public int getCurrentKData() {
		return currentKData;
	}

	public void setCurrentKData(int currentKData) {
		this.currentKData = currentKData;
	}

	public int getCurrentExtern() {
		return currentExtern;
	}

	public void setCurrentExtern(int currentExtern) {
		this.currentExtern = currentExtern;
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

	/**
	 * Aligns the memory to the given power. The given value won't be used if a forced alignment is scheduled:
	 * the forced alignment will be used instead.
	 * <p>
	 * Examples:
	 * If the alignment is 0 no changes will be performed.
	 * If the alignment is 1 the address will be aligned to a multiple of 2. 373621 -> 373622
	 * If the alignment is 2 the address will be aligned to a multiple of 4. 373621 -> 373624
	 *
	 * @param unforcedAlignment the alignment to use if no forced alignment is scheduled.
	 * @return whether any changes were made.
	 */
	public boolean align(int unforcedAlignment) {
		int align = isNextAlignmentForced() ? nextForcedAlignment : unforcedAlignment;
		nextForcedAlignment = -1;
		int pow = 1 << align;

		int current = getCurrent();
		int mod = current % pow;
		if (mod == 0) return false;
		addCurrent(pow - mod);
		return true;
	}

	/**
	 * Returns the next memory address to be used of the selected memory segment.
	 *
	 * @return the next memory address.
	 */
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
			case EXTERN:
				return currentExtern;
		}
		throw new AssemblerException("Selected section not defined.");
	}

	/**
	 * Adds an offset to the current memory address of the selected memory segment.
	 * This method is used to reserve memory for an instruction or a directive.
	 *
	 * @param add the offset to add.
	 */
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
			case EXTERN:
				currentExtern += add;
				break;
			default:
				throw new AssemblerException("Selected section not defined.");
		}
	}
}
