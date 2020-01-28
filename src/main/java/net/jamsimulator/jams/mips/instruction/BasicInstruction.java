package net.jamsimulator.jams.mips.instruction;

import net.jamsimulator.jams.mips.parameter.ParameterType;

import java.util.Arrays;

/**
 * Represents a basic instruction. Basic instructions are {@link Instruction}s that have
 * a direct translation to MIPS machine code.
 */
public class BasicInstruction implements Instruction {

	private String name;
	private String mnemonic;
	private ParameterType[] parameterTypes;

	public BasicInstruction(String name, String mnemonic, ParameterType[] parameterTypes) {
		this.name = name;
		this.mnemonic = mnemonic;
		this.parameterTypes = parameterTypes;
	}


	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getMnemonic() {
		return mnemonic;
	}

	@Override
	public ParameterType[] getParameters() {
		return Arrays.copyOf(parameterTypes, parameterTypes.length);
	}
}
