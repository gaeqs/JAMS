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

package net.jamsimulator.jams.mips.memory.cache;

import net.jamsimulator.jams.mips.memory.Memory;
import net.jamsimulator.jams.mips.memory.MemoryCell;

public class CacheBlock {

    private final int tag, start;
    private final byte[] data;

    private boolean dirty;
    private long creationTime, modificationTime;

    public CacheBlock(int tag, int start, byte[] data) {
        this.tag = tag;
        this.start = start;
        this.data = data;
        this.dirty = false;
    }

    public int getTag() {
        return tag;
    }

    public int getStart() {
        return start;
    }

    public byte[] getData() {
        return data;
    }

    public boolean isDirty() {
        return dirty;
    }

    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }

    public long getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(long creationTime) {
        this.creationTime = creationTime;
    }

    public long getModificationTime() {
        return modificationTime;
    }

    public void setModificationTime(long modificationTime) {
        this.modificationTime = modificationTime;
    }

    public byte getByte(int address) {
        return data[address];
    }

    public byte setByte(int address, byte b) {
        byte old = data[address];
        data[address] = b;
        return old;
    }


    public short getHalfword(int address, boolean bigEndian) {
        byte b0 = data[address++];
        byte b1 = data[address];
        return bigEndian ? MemoryCell.merge(b1, b0) : MemoryCell.merge(b0, b1);
    }

    public short setHalfword(int address, short word, boolean bigEndian) {
        short old = getHalfword(address, bigEndian);
        byte[] array = MemoryCell.split(word);
        if (bigEndian) {
            data[address++] = array[1];
            data[address] = array[0];
        } else {
            data[address++] = array[0];
            data[address] = array[1];
        }
        return old;
    }


    public int getWord(int address, boolean bigEndian) {
        byte b0 = data[address++];
        byte b1 = data[address++];
        byte b2 = data[address++];
        byte b3 = data[address];
        return bigEndian ? MemoryCell.merge(b3, b2, b1, b0) : MemoryCell.merge(b0, b1, b2, b3);
    }

    public int setWord(int address, int word, boolean bigEndian) {
        var old = getWord(address, bigEndian);
        byte[] array = MemoryCell.split(word);
        if (bigEndian) {
            data[address++] = array[3];
            data[address++] = array[2];
            data[address++] = array[1];
            data[address] = array[0];
        } else {
            data[address++] = array[0];
            data[address++] = array[1];
            data[address++] = array[2];
            data[address] = array[3];
        }
        return old;
    }

    public void write(Memory parent) {
        for (int i = 0; i < data.length; i++) {
            parent.setByte(start + i, data[i]);
        }
    }

    public CacheBlock copy() {
        byte[] array = new byte[data.length];
        System.arraycopy(data, 0, array, 0, data.length);
        CacheBlock copy = new CacheBlock(tag, start, array);
        copy.setCreationTime(creationTime);
        copy.setModificationTime(modificationTime);
        return copy;
    }

}
