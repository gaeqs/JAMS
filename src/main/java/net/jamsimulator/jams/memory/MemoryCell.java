package net.jamsimulator.jams.memory;

public class MemoryCell {

	private byte[] data;

	public MemoryCell(int length) {
		data = new byte[length];
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
		return bigEndian ? merge(b3, b2, b1, b0) : merge(b0, b1, b2, b3);
	}

	public void setWord(int address, int word, boolean bigEndian) {
		byte[] array = split(word);
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


	private static int merge(byte b0, byte b1, byte b2, byte b3) {
		int i = Byte.toUnsignedInt(b3);
		i <<= 8;
		i += Byte.toUnsignedInt(b2);
		i <<= 8;
		i += Byte.toUnsignedInt(b1);
		i <<= 8;
		return i + Byte.toUnsignedInt(b0);
	}

	private static byte[] split(int i) {
		byte[] array = new byte[4];
		array[0] = (byte) (i & 0xFF);
		i >>>= 8;
		array[1] = (byte) (i & 0xFF);
		i >>>= 8;
		array[2] = (byte) (i & 0xFF);
		i >>>= 8;
		array[3] = (byte) (i & 0xFF);
		return array;
	}

}
