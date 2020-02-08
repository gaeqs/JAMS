package net.jamsimulator.jams.mips.instruction.basic;

import net.jamsimulator.jams.mips.instruction.Instruction;
import net.jamsimulator.jams.mips.instruction.compiled.CompiledInstruction;
import net.jamsimulator.jams.mips.parameter.ParameterType;
import net.jamsimulator.jams.mips.parameter.parse.ParameterParseResult;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Represents a basic instruction. Basic instructions are {@link Instruction}s that have
 * a direct translation to MIPS machine code.
 */
public abstract class BasicInstruction implements Instruction {

	private String name;
	private String mnemonic;
	private int operationCode;
	private ParameterType[] parameters;

	/**
	 * Creates a basic instruction using a name, a mnemonic, a parameter types array and an operation code.
	 *
	 * @param name          the name.
	 * @param mnemonic      the mnemonic.
	 * @param parameters    the parameter types.
	 * @param operationCode the operation code.
	 */
	public BasicInstruction(String name, String mnemonic, ParameterType[] parameters, int operationCode) {
		this.name = name;
		this.mnemonic = mnemonic;
		this.parameters = parameters;
		this.operationCode = operationCode;
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


	@Override
	public CompiledInstruction[] compile(ParameterParseResult[] parameters) {
		return new CompiledInstruction[]{compileBasic(parameters)};
	}

	/**
	 * Returns the operation code of the instruction. This operation code is used to search
	 * the instruction based on an instruction code.
	 *
	 * @return the operation code.
	 */
	public int getOperationCode() {
		return operationCode;
	}

	/**
	 * Returns whether this instruction matches the given operation code.
	 * If the instruction has an instruction code, it must match the value 0.
	 * If the instruction has a fmt of a subcode, it myst match the value 0.
	 *
	 * @param operationCode the operation code.
	 * @return whether this instruction matches.
	 */
	public boolean match(int operationCode) {
		return match(operationCode, 0);
	}

	/**
	 * Returns whether this instruction matches the given operation code and the given function code.
	 * If the instruction has a fmt of a subcode, it myst match the value 0.
	 *
	 * @param operationCode the operation code.
	 * @param functionCode  the function code.
	 * @return whether this instruction matches.
	 */
	public boolean match(int operationCode, int functionCode) {
		return match(operationCode, functionCode, 0);
	}

	/**
	 * Returns whether this instruction matches the given operation code, the given function code
	 * and the given fmt or subcode.
	 *
	 * @param operationCode the operation code.
	 * @param functionCode  the function code.
	 * @param fmtSub        the fmt or the function code.
	 * @return whether this instruction matches.
	 */
	public boolean match(int operationCode, int functionCode, int fmtSub) {
		return operationCode == this.operationCode;
	}

	/**
	 * Compiles the basic instruction using the given parameters.
	 *
	 * @param parameters the parameters.
	 * @return the {@link CompiledInstruction}.
	 */
	public abstract CompiledInstruction compileBasic(ParameterParseResult[] parameters);

	/**
	 * Compiles the basic instruction using the given instruction code.
	 *
	 * @param instructionCode the instruction code.
	 * @return the {@link CompiledInstruction}.
	 */
	public abstract CompiledInstruction compileFromCode(int instructionCode);

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		BasicInstruction that = (BasicInstruction) o;
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
