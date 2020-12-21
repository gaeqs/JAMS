package net.jamsimulator.jams.gui.mips.simulator.memory;

import net.jamsimulator.jams.mips.memory.Memory;
import net.jamsimulator.jams.utils.NumericUtils;
import net.jamsimulator.jams.utils.StringUtils;

import java.util.function.BiFunction;

public enum MemoryRepresentation {

	HEXADECIMAL((m, a) -> "0x" + StringUtils.addZeros(Integer.toHexString(m.getWord(a, false, true)), 8)),
	DECIMAL((m, a) -> String.valueOf(m.getWord(a, false, true))),
	OCTAL((m, a) -> "0" + Integer.toOctalString(m.getWord(a, false, true))),
	BINARY((m, a) -> "0b" + StringUtils.addZeros(Integer.toBinaryString(m.getWord(a, false, true)), 32)),
	LONG((m, a) -> String.valueOf(NumericUtils.intsToLong(m.getWord(a, false, true), m.getWord(a + 4, false, true)))),
	FLOAT((m, a) -> String.valueOf(Float.intBitsToFloat(m.getWord(a, false, true)))),
	DOUBLE((m, a) -> String.valueOf(NumericUtils.intsToDouble(m.getWord(a, false, true), m.getWord(a + 4, false, true)))),
	CHAR((m, a) -> {
		char[] array = new char[4];
		int current;
		for (int i = 0; i < 4; i++) {
			current = m.getByte(a + i, false, true);
			if (current < 0) current += 256;
			array[i] = (char) current;
		}
		return new String(array);
	}),
	RGB((m, a) -> getRGBAsString(m.getWord(a, false, true))),
	RGBA((m, a) -> getRGBAAsString(m.getWord(a, false, true))),
	ENGLISH((m, a) -> NumericUtils.toEnglish(m.getWord(a, false, true))),
	ROMAN((m, a) -> NumericUtils.toRoman(m.getWord(a, false, true)));

	private final BiFunction<Memory, Integer, String> transformer;

	MemoryRepresentation(BiFunction<Memory, Integer, String> transformer) {
		this.transformer = transformer;
	}

	public String getLanguageNode() {
		return "NUMBER_FORMAT_" + name();
	}

	public boolean isColor() {
		return this == RGB || this == RGBA;
	}

	public String represent(Memory memory, int address) {
		return transformer.apply(memory, address);
	}

	private static String getRGBAsString(int value) {
		String val = StringUtils.addZeros(Integer.toHexString(value), 6);
		if (val.length() > 6) val = val.substring(val.length() - 6);
		return "#" + val;
	}

	private static String getRGBAAsString(int value) {
		return "#" + StringUtils.addZeros(Integer.toHexString(value), 8);
	}

}
