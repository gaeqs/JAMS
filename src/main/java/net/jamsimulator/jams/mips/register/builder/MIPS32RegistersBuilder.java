package net.jamsimulator.jams.mips.register.builder;

import net.jamsimulator.jams.mips.register.MIPS32Registers;
import net.jamsimulator.jams.mips.register.Registers;

import java.util.HashSet;
import java.util.Set;

/**
 * Represents a MIPS32 registers builder. Use this to create {@link MIPS32Registers}.
 */
public class MIPS32RegistersBuilder extends RegistersBuilder {

	public static final String NAME = "MIPS32";

	private static final Set<String> NAMES = new HashSet<>();
	private static final Set<String> GENERAL_NAMES = new HashSet<>();
	private static final Set<String> COP0_NAMES = new HashSet<>();
	private static final Set<String> COP1_NAMES = new HashSet<>();

	static {
		NAMES.add("zero");
		NAMES.add("at");
		NAMES.add("v0");
		NAMES.add("v1");
		for (int i = 0; i < 4; i++)
			NAMES.add("a" + i);
		for (int i = 0; i < 8; i++)
			NAMES.add("t" + i);
		for (int i = 0; i < 8; i++)
			NAMES.add("s" + i);
		NAMES.add("t8");
		NAMES.add("t9");
		NAMES.add("k0");
		NAMES.add("k1");
		NAMES.add("gp");
		NAMES.add("sp");
		NAMES.add("fp");
		NAMES.add("ra");
		GENERAL_NAMES.addAll(NAMES);

		for (int i = 0; i < 32; i++) {
			NAMES.add("f" + i);
			NAMES.add(String.valueOf(i));
			COP1_NAMES.add("f" + i);
			COP1_NAMES.add(String.valueOf(i));
			GENERAL_NAMES.add(String.valueOf(i));
		}

		NAMES.add("8");
		NAMES.add("12");
		NAMES.add("13");
		NAMES.add("14");
		COP0_NAMES.add("8");
		COP0_NAMES.add("12");
		COP0_NAMES.add("13");
		COP0_NAMES.add("14");
	}


	public MIPS32RegistersBuilder() {
		super(NAME, NAMES, GENERAL_NAMES, COP0_NAMES, COP1_NAMES, MIPS32Registers.VALID_REGISTERS_START);
	}

	@Override
	public Registers createRegisters() {
		return new MIPS32Registers();
	}
}
