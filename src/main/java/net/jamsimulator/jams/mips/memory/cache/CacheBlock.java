package net.jamsimulator.jams.mips.memory.cache;

import net.jamsimulator.jams.mips.memory.MemoryCell;

public class CacheBlock {

	private final int tag;
	private final byte[] data;

	private long creationTime, modificationTime;

	public CacheBlock(int tag, byte[] data) {
		this.tag = tag;
		this.data = data;
	}

	public int getTag() {
		return tag;
	}

	public byte[] getData() {
		return data;
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

	public void setByte(int address, byte b) {
		data[address] = b;
	}

	public int getWord(int address, boolean bigEndian) {
		byte b0 = data[address++];
		byte b1 = data[address++];
		byte b2 = data[address++];
		byte b3 = data[address];
		return bigEndian ? MemoryCell.merge(b3, b2, b1, b0) : MemoryCell.merge(b0, b1, b2, b3);
	}

	public void setWord(int address, int word, boolean bigEndian) {
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
	}

	public CacheBlock copy() {
		byte[] array = new byte[data.length];
		System.arraycopy(data, 0, array, 0, data.length);
		CacheBlock copy =  new CacheBlock(tag, array);
		copy.setCreationTime(creationTime);
		copy.setModificationTime(modificationTime);
		return copy;
	}

}
