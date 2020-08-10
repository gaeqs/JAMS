package net.jamsimulator.jams.mips.register;

import net.jamsimulator.jams.mips.register.event.RegisterChangeValueEvent;

import java.util.Collection;

public class COP0Register extends Register {

	protected final String cop0Name;
	protected final int selection;
	protected final int softwareWriteMask;

	public COP0Register(Registers registers, int identifier, int selection, int softwareWriteMask, String cop0Name, String... names) {
		super(registers, identifier, names);
		this.selection = selection;
		this.cop0Name = cop0Name;
		this.softwareWriteMask = softwareWriteMask;
	}

	public COP0Register(Registers registers, int identifier, int selection, int softwareWriteMask, String cop0Name, Collection<String> names) {
		super(registers, identifier, names);
		this.selection = selection;
		this.cop0Name = cop0Name;
		this.softwareWriteMask = softwareWriteMask;
	}

	public COP0Register(Registers registers, int identifier, int selection, int value, int softwareWriteMask, String cop0Name, String... names) {
		super(registers, identifier, value, true, names);
		this.selection = selection;
		this.cop0Name = cop0Name;
		this.softwareWriteMask = softwareWriteMask;
	}

	public COP0Register(Registers registers, int identifier, int selection, int value, int softwareWriteMask, String cop0Name, Collection<String> names) {
		super(registers, identifier, value, true, names);
		this.selection = selection;
		this.cop0Name = cop0Name;
		this.softwareWriteMask = softwareWriteMask;
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

	/**
	 * Returns the mask used to write data using {@link #setValue(int)}.
	 *
	 * @return the mask.
	 */
	public int getSoftwareWriteMask() {
		return softwareWriteMask;
	}

	/**
	 * Modifies the bits inside the given range, avoiding any mask.
	 * <p>
	 * This method should be used only for hardware changes.
	 *
	 * @param value  the value.
	 * @param from   the first bit to modify.
	 * @param length the amount of bits to modify.
	 */
	public void modifyBits(int value, int from, int length) {
		if (!modifiable || from > 31 || length < 1) return;
		if (from + length > 32) {
			length = 32 - from;
		}
		int mask = ((1 << length) - 1) << from;
		if (!registers.eventCallsEnabled) {
			setValue0(mask(value << from, mask));
			return;
		}

		RegisterChangeValueEvent.Before before = registers.callEvent(
				new RegisterChangeValueEvent.Before(this, this.value, mask(value << from, mask)));
		if (before.isCancelled()) return;
		int old = this.value;
		setValue0(before.getNewValue());
		registers.callEvent(new RegisterChangeValueEvent.After(this, old, this.value));
	}

	@Override
	protected void setValue0(int value) {
		this.value = value;
	}

	private int mask(int value, int mask) {
		int newVal = value & mask;
		int oldVal = this.value & ~mask;
		return newVal | oldVal;
	}

	@Override
	public COP0Register copy(Registers registers) {
		COP0Register register = new COP0Register(registers, identifier, selection, value, softwareWriteMask, cop0Name, names);
		register.defaultValue = defaultValue;
		return register;
	}
}
