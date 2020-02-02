package net.jamsimulator.jams.mips.register;

/**
 * Represents a default MIPS32 {@link Register} set.
 */
public class MIPS32RegisterSet extends RegisterSet {

	/**
	 * Creates a default MIPS32 {@link RegisterSet} set.
	 */
	public MIPS32RegisterSet() {
		super(null, null, null);
		loadPrincipalRegisters();
		loadCoprocessor0Registers();
		loadCoprocessor1Registers();
		loadEssentialRegisters();
	}

	protected void loadPrincipalRegisters() {
		int id = 0;
		registers.add(new Register(0, 0, false, "zero", String.valueOf(id++)));
		registers.add(new Register(id, "at", String.valueOf(id++)));
		registers.add(new Register(id, "v0", String.valueOf(id++)));
		registers.add(new Register(id, "v1", String.valueOf(id++)));
		for (int i = 0; i < 4; i++)
			registers.add(new Register(id, "a" + i, String.valueOf(id++)));
		for (int i = 0; i < 8; i++)
			registers.add(new Register(id, "t" + i, String.valueOf(id++)));
		for (int i = 0; i < 8; i++)
			registers.add(new Register(id, "s" + i, String.valueOf(id++)));
		registers.add(new Register(id, "t8", String.valueOf(id++)));
		registers.add(new Register(id, "t8", String.valueOf(id++)));

		registers.add(new Register(id, "k0", String.valueOf(id++)));
		registers.add(new Register(id, "k1", String.valueOf(id++)));

		registers.add(new Register(id, 0x10008000, true, "gp", String.valueOf(id++)));
		registers.add(new Register(id, 0x7fffeffc, true, "sp", String.valueOf(id++)));
		registers.add(new Register(id, "ra", String.valueOf(id)));
	}

	protected void loadCoprocessor0Registers() {
		coprocessor0Registers.add(new Register(8, "8"));
		coprocessor0Registers.add(new Register(12, 0x0000ff11, true, "12"));
		coprocessor0Registers.add(new Register(13, "13"));
		coprocessor0Registers.add(new Register(14, "14"));
	}

	protected void loadCoprocessor1Registers() {
		for (int i = 0; i < 32; i++) {
			coprocessor1Registers.add(new Register(i, "f" + i, String.valueOf(i)));
		}
	}
}
