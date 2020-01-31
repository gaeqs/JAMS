package net.jamsimulator.jams.mips.register;

/**
 * Represents a default MIPS32 {@link Register} set.
 */
public class MIPS32Registers extends Registers {

	/**
	 * Creates a default MIPS32 {@link Registers} set.
	 */
	public MIPS32Registers() {
		super(null, null, null);
		loadPrincipalRegisters();
		loadCoprocessor0Registers();
		loadCoprocessor1Registers();
		loadEssentialRegisters();
	}

	protected void loadPrincipalRegisters() {
		int value = 0;
		registers.add(new Register(0, false, "zero", String.valueOf(value++)));
		registers.add(new Register("at", String.valueOf(value++)));
		registers.add(new Register("v0", String.valueOf(value++)));
		registers.add(new Register("v1", String.valueOf(value++)));
		for (int i = 0; i < 4; i++)
			registers.add(new Register("a" + i, String.valueOf(value++)));
		for (int i = 0; i < 8; i++)
			registers.add(new Register("t" + i, String.valueOf(value++)));
		for (int i = 0; i < 8; i++)
			registers.add(new Register("s" + i, String.valueOf(value++)));
		registers.add(new Register("t8", String.valueOf(value++)));
		registers.add(new Register("t8", String.valueOf(value++)));

		registers.add(new Register("k0", String.valueOf(value++)));
		registers.add(new Register("k1", String.valueOf(value++)));

		registers.add(new Register(0x10008000, true, "gp", String.valueOf(value++)));
		registers.add(new Register(0x7fffeffc, true, "sp", String.valueOf(value++)));
		registers.add(new Register("ra", String.valueOf(value)));
	}

	protected void loadCoprocessor0Registers() {
		coprocessor0Registers.add(new Register("8"));
		coprocessor0Registers.add(new Register(0x0000ff11, true, "12"));
		coprocessor0Registers.add(new Register("13"));
		coprocessor0Registers.add(new Register("14"));
	}

	protected void loadCoprocessor1Registers() {
		for (int i = 0; i < 32; i++) {
			coprocessor1Registers.add(new Register("f" + i, String.valueOf(i)));
		}
	}
}
