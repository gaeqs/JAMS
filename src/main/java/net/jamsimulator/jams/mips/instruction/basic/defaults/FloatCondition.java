package net.jamsimulator.jams.mips.instruction.basic.defaults;

import net.jamsimulator.jams.Jams;

public enum FloatCondition {

	AF(0b00000),
	UN(0b00001),
	EQ(0b00010),
	UEQ(0b00011),
	LT(0b00100),
	ULT(0b00101),
	LE(0b00110),
	ULE(0b00111),

	SAF(0b01000),
	SUN(0b01001),
	SEQ(0b01010),
	SUEQ(0b01011),
	SLT(0b01100),
	SULT(0b01101),
	SLE(0b01110),
	SULE(0b01111),

	OR(0b10001),
	UNE(0b10010),
	NE(0b10011),


	SOR(0b10001),
	SUNE(0b10010),
	SNE(0b10011);


	private final int code;

	FloatCondition(int code) {
		this.code = code;
	}

	public String getMnemonic() {
		return name().toLowerCase();
	}

	public int getCode() {
		return code;
	}

	public String getName() {
		return Jams.getLanguageManager().getSelected().getOrDefault("FLOAT_CONDITION_" + name());
	}
}
