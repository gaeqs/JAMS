/*
 *  MIT License
 *
 *  Copyright (c) 2021 Gael Rial Costas
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */

package net.jamsimulator.jams.mips.memory;

import net.jamsimulator.jams.utils.Validate;

import java.util.Arrays;

/**
 * Represents a memory section. A memory section is a part of a simulation memory
 * who manages a list of {@link MemoryCell}s.
 * <p>
 * These {@link MemoryCell}s are not initialized when an instance of a MemorySection is created,
 * but when it's required to store a data in the address the {@link MemoryCell} manages.
 *
 * @see Memory
 * @see MemoryCell
 */
public class MemorySection {

    public static final int DEFAULT_CELL_SIZE = 4096;

    private final String name;
    private final int firstAddress;
    private final int length;
    private final int cellSize;

    private MemoryCell[] cells;

    /**
     * Creates a memory section using a name, the first address, the length and the cell size.
     * The length and the cell size must be bigger than 3. Length should be a multiple of cellSize
     * for optimal results.
     *
     * @param name         the name of the section.
     * @param firstAddress the first address.
     * @param length       the length.
     * @param cellSize     the cell size. Used when cells are created.
     */
    public MemorySection(String name, int firstAddress, int length, int cellSize) {
        Validate.isTrue(length >= cellSize,
                "Length must be bigger or equals to cellSize! (" + cellSize + ")");
        Validate.isTrue(cellSize > 3, "Cell size must be bigger than 3!");
        this.name = name;
        this.firstAddress = firstAddress;
        this.length = length;
        this.cellSize = cellSize;
        generateCellsArray();
    }

    /**
     * Creates a memory section using a name, the first address and the length.
     * The length must be bigger or equals to {@link #DEFAULT_CELL_SIZE} and it should be
     * a multiple of {@link #DEFAULT_CELL_SIZE} for optimal results.
     *
     * @param name         the name of the section.
     * @param firstAddress the first address.
     * @param length       the length.
     */
    public MemorySection(String name, int firstAddress, int length) {
        this(name, firstAddress, length, DEFAULT_CELL_SIZE);
    }

    /**
     * Returns the name of the memory section.
     *
     * @return the name.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the first address of the memory section.
     *
     * @return the first address.
     */
    public int getFirstAddress() {
        return firstAddress;
    }

    /**
     * Returns the last address of the memory section.
     *
     * @return the last address.
     */
    public int getLastAddress() {
        return firstAddress - 1 + length;
    }

    /**
     * Returns the length of the memory section.
     *
     * @return the length.
     */
    public int getLength() {
        return length;
    }

    /**
     * Returns the cell size of the memory section.
     *
     * @return the cell size.
     */
    public int getCellSize() {
        return cellSize;
    }

    /**
     * Returns a byte from the memory section stored in the given address.
     * The address must be absolute address from the memory.
     *
     * @param address the address.
     * @return the byte.
     */
    public byte getByte(int address) {
        if (!isInside(address))
            throw new IndexOutOfBoundsException("Address " + address + " out of bounds.");
        address -= firstAddress;
        int cellIndex = address / cellSize;
        MemoryCell cell = cells[cellIndex];
        return cell == null ? 0 : cell.getByte(address - cellIndex * cellSize);
    }

    /**
     * Stores a byte into the given address.
     * The address must be absolute address from the memory.
     *
     * @param address the address.
     * @param b       the byte.
     * @return the old byte
     */
    public byte setByte(int address, byte b) {
        if (!isInside(address))
            throw new IndexOutOfBoundsException("Address " + address + " out of bounds.");
        address -= firstAddress;
        int cellIndex = address / cellSize;
        MemoryCell cell = getOrCreateCell(cellIndex);
        return cell.setByte(address - cellIndex * cellSize, b);
    }

    /**
     * Returns a halfword from the memory section stored in the given address.
     * The address must be absolute address from the memory.
     *
     * @param address   the address.
     * @param bigEndian whether the memory is big endian.
     * @return the byte.
     */
    public short getHalfword(int address, boolean bigEndian) {
        if (!isInside(address))
            throw new IndexOutOfBoundsException("Address " + address + " out of bounds.");
        address -= firstAddress;
        int cellIndex = address / cellSize;
        MemoryCell cell = cells[cellIndex];
        return cell == null ? 0 : cell.getHalfword(address - cellIndex * cellSize, bigEndian);
    }

    /**
     * Stores a halfword into the given address.
     * The address must be absolute address from the memory.
     *
     * @param address   the address.
     * @param h         the halfword.
     * @param bigEndian whether the memory is big endian.
     * @return the old byte
     */
    public short setHalfword(int address, short h, boolean bigEndian) {
        if (!isInside(address))
            throw new IndexOutOfBoundsException("Address " + address + " out of bounds.");
        address -= firstAddress;
        int cellIndex = address / cellSize;
        MemoryCell cell = getOrCreateCell(cellIndex);
        return cell.setHalfword(address - cellIndex * cellSize, h, bigEndian);
    }

    /**
     * Returns a word from the memory section stored in the given address.
     * The address must be absolute address from the memory. The use of an unaligned
     * address may cause undefined behaviour.
     *
     * @param address   the address.
     * @param bigEndian whether the memory is big endian.
     * @return the word.
     */
    public int getWord(int address, boolean bigEndian) {
        if (!isInside(address))
            throw new IndexOutOfBoundsException("Address " + address + " out of bounds.");
        address -= firstAddress;
        int cellIndex = address / cellSize;
        MemoryCell cell = cells[cellIndex];
        return cell == null ? 0 : cell.getWord(address - cellIndex * cellSize, bigEndian);
    }

    /**
     * Stores a byte into the given address.
     * The address must be absolute address from the memory. The use of an unaligned
     * address may cause undefined behaviour.
     *
     * @param address   the address.
     * @param bigEndian whether the memory is big endian.
     * @return the old word.
     */
    public int setWord(int address, int word, boolean bigEndian) {
        if (!isInside(address))
            throw new IndexOutOfBoundsException("Address " + address + " out of bounds.");
        address -= firstAddress;
        int cellIndex = address / cellSize;
        MemoryCell cell = getOrCreateCell(cellIndex);
        return cell.setWord(address - cellIndex * cellSize, word, bigEndian);
    }

    /**
     * Return whether the given address is inside the memory section.
     *
     * @param address the address.
     * @return whether the given address is inside the memory section.
     */
    public boolean isInside(int address) {
        return Integer.compareUnsigned(address, firstAddress) >= 0 && Integer.compareUnsigned(address, firstAddress + length - 1) <= 0;
    }

    /**
     * Return whether the given address is bigger or equal to the first address of the memory section.
     *
     * @param address the address.
     * @return whether the given address is bigger or equal to the first address of the memory section.
     */
    public boolean isBigger(int address) {
        return Integer.compareUnsigned(address, firstAddress) >= 0;
    }

    /**
     * Creates a copy of the memory section.
     *
     * @return the copy.
     */
    public MemorySection copy() {
        MemorySection section = new MemorySection(name, firstAddress, length, cellSize);
        for (int i = 0; i < cells.length; i++) {
            section.cells[i] = cells[i] == null ? null : cells[i].copy();
        }
        return section;
    }

    /**
     * Wipes this memory section.
     */
    public void wipe() {
        Arrays.fill(cells, null);
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
