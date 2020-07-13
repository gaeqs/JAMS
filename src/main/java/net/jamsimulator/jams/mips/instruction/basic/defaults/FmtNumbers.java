package net.jamsimulator.jams.mips.instruction.basic.defaults;

import net.jamsimulator.jams.mips.register.Register;
import net.jamsimulator.jams.utils.NumericUtils;

public enum FmtNumbers {

	SINGLE(0b100000, 0b10000, 1, "Single", "s", false),
	DOUBLE(0b100001, 0b10001, 0, "Double", "d", true),
	WORD(0b100100, 0b10100, 4, "Word", "w", false),
	LONG(0b100101, 0b10101, 5, "Long", "l", true);

	private final int cvt, fmt, fmt3;
	private final String name, mnemonic;
	private final boolean requiresEventRegister;

	FmtNumbers(int cvt, int fmt, int fmt3, String name, String mnemonic, boolean requiresEvenRegister) {
		this.cvt = cvt;
		this.fmt = fmt;
		this.fmt3 = fmt3;
		this.name = name;
		this.mnemonic = mnemonic;
		this.requiresEventRegister = requiresEvenRegister;
	}

	public int getCvt() {
		return cvt;
	}

	public int getFmt() {
		return fmt;
	}

	public int getFmt3() {
		return fmt3;
	}

	public String getName() {
		return name;
	}

	public String getMnemonic() {
		return mnemonic;
	}

	public boolean requiresEventRegister() {
		return requiresEventRegister;
	}

	public Number from(Register register, Register aux) {
		switch (this) {
			case WORD:
				return register.getValue();
			case LONG:
				long high = aux.getValue();
				high <<= 32;
				return high + register.getValue();
			case SINGLE:
				return Float.intBitsToFloat(register.getValue());
			case DOUBLE:
				return NumericUtils.intsToDouble(register.getValue(), aux.getValue());
		}
		return 0;
	}

	public void to(Number number, Register register, Register aux) {
		switch (this) {
			case WORD:
				register.setValue(number.intValue());
				break;
			case LONG:
				register.setValue(number.intValue());
				aux.setValue((int) (number.longValue() >> 32));
				break;
			case SINGLE:
				register.setValue(Float.floatToIntBits(number.floatValue()));
				break;
			case DOUBLE:
				int[] ints = NumericUtils.doubleToInts(number.doubleValue());
				register.setValue(ints[0]);
				aux.setValue(ints[1]);
				break;
		}
	}

}
