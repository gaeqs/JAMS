package net.jamsimulator.jams.gui.mips.editor;

import java.util.Arrays;
import java.util.Optional;

public enum MIPSSpaces {

	SPACE(" ", "\"\t\""),
	COMMA_AND_SPACE(", ", "\",\t\"");

	private final String value, displayValue;

	MIPSSpaces(String value, String displayValue) {
		this.value = value;
		this.displayValue = displayValue;
	}

	public String getValue() {
		return value;
	}

	public String getDisplayValue() {
		return displayValue;
	}

	public static Optional<MIPSSpaces> getByValue(String value) {
		return Arrays.stream(values()).filter(target -> target.getValue().equals(value)).findAny();
	}

	public static Optional<MIPSSpaces> getByDisplayValue(String displayValue) {
		return Arrays.stream(values()).filter(target -> target.getDisplayValue().equals(displayValue)).findAny();
	}
}
