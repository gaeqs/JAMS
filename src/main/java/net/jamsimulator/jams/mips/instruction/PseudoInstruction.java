package net.jamsimulator.jams.mips.instruction;

import net.jamsimulator.jams.mips.instruction.basic.BasicInstruction;
import net.jamsimulator.jams.mips.parameter.ParameterType;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Represents a pseudo-instruction. Pseudo-instructions are MIPS instructions that are separated into several
 * {@link BasicInstruction} in compile time. Pseudo-instructions don't have a direct translation into machine code.
 */
public class PseudoInstruction implements Instruction {

	private String name;
	private String mnemonic;
	private ParameterType[] parameters;
	private BasicInstruction[] basicInstructions;


	public PseudoInstruction(String name, String mnemonic, ParameterType[] parameters, BasicInstruction[] basicInstructions) {
		this.name = name;
		this.mnemonic = mnemonic;
		this.parameters = parameters;
		this.basicInstructions = basicInstructions;
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
		return Arrays.copyOf(parameters, parameters.length);
	}

	@Override
	public boolean match(String mnemonic, ParameterType[] parameters) {
		return this.mnemonic.equals(mnemonic) && Arrays.equals(this.parameters, parameters);
	}

	@Override
	public boolean match(String mnemonic, List<ParameterType>[] parameters) {
		if (parameters.length != this.parameters.length) return false;
		int i = 0;
		for (List<ParameterType> possibilities : parameters) {
			if (!possibilities.contains(this.parameters[i])) return false;
			i++;
		}
		return true;
	}

	/**
	 * Returns a list with the basic instructions of this pseudo-instructions.
	 *
	 * @return the basic instructions.
	 */
	public BasicInstruction[] getBasicInstructions() {
		return Arrays.copyOf(basicInstructions, basicInstructions.length);
	}


	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		PseudoInstruction that = (PseudoInstruction) o;
		return mnemonic.equals(that.mnemonic) &&
				Arrays.equals(parameters, that.parameters);
	}

	@Override
	public int hashCode() {
		int result = Objects.hash(mnemonic);
		result = 31 * result + Arrays.hashCode(parameters);
		return result;
	}
}
