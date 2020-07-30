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
	private final boolean requiresEvenRegister;

	FmtNumbers(int cvt, int fmt, int fmt3, String name, String mnemonic, boolean requiresEvenRegister) {
		this.cvt = cvt;
		this.fmt = fmt;
		this.fmt3 = fmt3;
		this.name = name;
		this.mnemonic = mnemonic;
		this.requiresEvenRegister = requiresEvenRegister;
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

	public boolean requiresEvenRegister() {
		return requiresEvenRegister;
	}

	public Number from(Register register, Register aux) {
		return from(register.getValue(), aux == null ? 0 : aux.getValue());
	}

	public Number from(int val, int aux) {
		switch (this) {
			case WORD:
				return val;
			case LONG:
				long high = aux;
				high <<= 32;
				return high + val;
			case SINGLE:
				return Float.intBitsToFloat(val);
			case DOUBLE:
				return NumericUtils.intsToDouble(val, aux);
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

	public int[] to(Number number) {
		switch (this) {
			case WORD:
				return new int[]{number.intValue()};
			case LONG:
				return new int[]{number.intValue(), (int) (number.longValue() >> 32)};
			case SINGLE:
				return new int[]{Float.floatToIntBits(number.floatValue())};
			case DOUBLE:
				return NumericUtils.doubleToInts(number.doubleValue());
		}
		return new int[0];
	}
}
