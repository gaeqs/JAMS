package net.jamsimulator.jams.mips.register;

import java.util.Collection;

public class COP0StatusRegister extends COP0Register {

	public COP0StatusRegister(Registers registers, int identifier, int selection, int softwareWriteMask, String cop0Name, String... names) {
		super(registers, identifier, selection, softwareWriteMask, cop0Name, names);
	}

	public COP0StatusRegister(Registers registers, int identifier, int selection, int softwareWriteMask, String cop0Name, Collection<String> names) {
		super(registers, identifier, selection, softwareWriteMask, cop0Name, names);
	}

	public COP0StatusRegister(Registers registers, int identifier, int selection, int value, int softwareWriteMask, String cop0Name, String... names) {
		super(registers, identifier, selection, value, softwareWriteMask, cop0Name, names);
	}

	public COP0StatusRegister(Registers registers, int identifier, int selection, int value, int softwareWriteMask, String cop0Name, Collection<String> names) {
		super(registers, identifier, selection, value, softwareWriteMask, cop0Name, names);
	}


	@Override
	protected void setValue0(int value) {
		super.setValue0(value);

		boolean userMode = getSection(COP0RegistersBits.STATUS_EXL, 2) == 0;
		if (getBit(COP0RegistersBits.STATUS_UM) != userMode) {
			int mask = 1 << COP0RegistersBits.STATUS_UM;
			this.value &= ~mask;
			if(userMode) {
				this.value |= mask;
			}
		}
	}

	@Override
	public COP0Register copy(Registers registers) {
		COP0StatusRegister register = new COP0StatusRegister(registers, identifier, selection, value, softwareWriteMask, cop0Name, names);
		register.defaultValue = defaultValue;
		return register;
	}
}
