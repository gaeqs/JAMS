package net.jamsimulator.jams.mips.register;

import java.util.Collection;

public class COP0Register extends Register {

	protected final String cop0Name;
	protected final int selection;

	public COP0Register(Registers registers, int identifier, int selection, String cop0Name, String... names) {
		super(registers, identifier, names);
		this.selection = selection;
		this.cop0Name = cop0Name;
	}

	public COP0Register(Registers registers, int identifier, int selection, String cop0Name, Collection<String> names) {
		super(registers, identifier, names);
		this.selection = selection;
		this.cop0Name = cop0Name;
	}

	public COP0Register(Registers registers, int identifier, int selection, int value, boolean modifiable, String cop0Name, String... names) {
		super(registers, identifier, value, modifiable, names);
		this.selection = selection;
		this.cop0Name = cop0Name;
	}

	public COP0Register(Registers registers, int identifier, int selection, int value, boolean modifiable, String cop0Name, Collection<String> names) {
		super(registers, identifier, value, modifiable, names);
		this.selection = selection;
		this.cop0Name = cop0Name;
	}

	/**
	 * Returns the sub-index of this COP0 register.
	 *
	 * @return the sub-index.
	 */
	public int getSelection() {
		return selection;
	}

	/**
	 * Returns the COP0 name of this register.
	 *
	 * @return the COP0 name.
	 */
	public String getCop0Name() {
		return cop0Name;
	}

	@Override
	public COP0Register copy(Registers registers) {
		COP0Register register = new COP0Register(registers, identifier, selection, value, modifiable, cop0Name, names);
		register.defaultValue = defaultValue;
		return register;
	}
}
