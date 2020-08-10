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
		modifyBits(getSection(COP0RegistersBits.STATUS_EXL, 2) == 0 ? 1 : 0, COP0RegistersBits.STATUS_UM, 1);
	}

	@Override
	public void modifyBits(int value, int from, int length) {
		super.modifyBits(value, from, length);
		if (from <= COP0RegistersBits.STATUS_EXL && length + from > COP0RegistersBits.STATUS_EXL ||
				from <= COP0RegistersBits.STATUS_ERL && length + from > COP0RegistersBits.STATUS_ERL) {
			modifyBits(getSection(COP0RegistersBits.STATUS_EXL, 2) == 0 ? 1 : 0, COP0RegistersBits.STATUS_UM, 1);
		}
	}
}
