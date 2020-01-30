package net.jamsimulator.jams.memory;

import net.jamsimulator.jams.utils.Validate;

public class MemorySection {

	public static final int DEFAULT_CELL_SIZE = 4096;

	private String name;
	private int firstAddress;
	private int length;
	private int cellSize;

	private MemoryCell[] cells;

	public MemorySection(String name, int firstAddress, int length, int cellSize) {
		Validate.isTrue(length > 3, "Length must be bigger than 3!");
		Validate.isTrue(cellSize > 3, "Cell size must be bigger than 3!");
		this.name = name;
		this.firstAddress = firstAddress;
		this.length = length;
		this.cellSize = cellSize;
		generateCellsArray();
	}

	public MemorySection(String name, int firstAddress, int length) {
		this(name, firstAddress, length, DEFAULT_CELL_SIZE);
	}

	public String getName() {
		return name;
	}

	public int getFirstAddress() {
		return firstAddress;
	}

	public int getLastAddress() {
		return firstAddress - 1 + length;
	}

	public int getLength() {
		return length;
	}

	public int getCellSize() {
		return cellSize;
	}

	public byte getByte(int address) {
		if (!isInside(address))
			throw new IndexOutOfBoundsException("Address " + address + " out of bounds.");
		address -= firstAddress;
		int cellIndex = address / cellSize;
		MemoryCell cell = getOrCreateCell(cellIndex);
		return cell.getByte(address - cellIndex * cellSize);
	}

	public void setByte(int address, byte b) {
		if (!isInside(address))
			throw new IndexOutOfBoundsException("Address " + address + " out of bounds.");
		address -= firstAddress;
		int cellIndex = address / cellSize;
		MemoryCell cell = getOrCreateCell(cellIndex);
		cell.setByte(address - cellIndex * cellSize, b);
	}

	public int getWord(int address, boolean bigEndian) {
		if (!isInside(address))
			throw new IndexOutOfBoundsException("Address " + address + " out of bounds.");
		address -= firstAddress;
		int cellIndex = address / cellSize;
		MemoryCell cell = getOrCreateCell(cellIndex);
		return cell.getWord(address - cellIndex * cellSize, bigEndian);
	}

	public void setWord(int address, int word, boolean bigEndian) {
		if (!isInside(address))
			throw new IndexOutOfBoundsException("Address " + address + " out of bounds.");
		address -= firstAddress;
		int cellIndex = address / cellSize;
		MemoryCell cell = getOrCreateCell(cellIndex);
		cell.setWord(address - cellIndex * cellSize, word, bigEndian);
	}

	public boolean isInside(int address) {
		address -= firstAddress;
		return address >= 0 && address - length < 0;
	}


	private void generateCellsArray() {
		int cellsAmount = Math.floorDiv(length, cellSize);
		if (length % cellsAmount != 0) cellsAmount++;
		cells = new MemoryCell[cellsAmount];
	}

	private MemoryCell getOrCreateCell(int index) {
		MemoryCell cell = cells[index];
		if (cell == null) {
			return cells[index] = new MemoryCell(cellSize);
		}
		return cell;
	}
}
