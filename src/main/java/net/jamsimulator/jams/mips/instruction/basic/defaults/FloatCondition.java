package net.jamsimulator.jams.mips.instruction.basic.defaults;

public enum FloatCondition {

	AF(0b00000, "Always false"),
	UN(0b00001, "Unordered"),
	EQ(0b00010, "Equal"),
	UEQ(0b00011, "Unordered or equal"),
	LT(0b00100, "Ordered less than"),
	ULT(0b00101, "Unordered or less than"),
	LE(0b00110, "Ordered less than or equal"),
	ULE(0b00111, "Unordered or less than or equal"),

	SAF( 0b01000, "Signaling always false"),
	SUN( 0b01001, "Signalling unordered"),
	SEQ( 0b01010, "Ordered signalling equal"),
	SUEQ(0b01011, "Signalling unordered or equal"),
	SLT( 0b01100, "Ordered signalling less than"),
	SULT(0b01101, "Signalling unordered or less than"),
	SLE( 0b01110, "Ordered signalling less than or equal"),
	SULE(0b01111, "Signalling unordered or less than or equal"),

	OR(0b10001, "Ordered"),
	UNE(0b10010, "Not equal"),
	NE(0b10011, "Ordered not equal"),


	SOR(0b10001, "Signalling ordered"),
	SUNE(0b10010, "Signalling unordered or not equal"),
	SNE(0b10011, "Signalling ordered not equal");


	private final int code;
	private final String name;

	FloatCondition(int code, String name) {
		this.name = name;
		this.code = code;
	}

	public String getMnemonic() {
		return name().toLowerCase();
	}

	public int getCode() {
		return code;
	}

	public String getName() {
		return name;
	}
}
