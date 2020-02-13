package net.jamsimulator.jams.mips.instruction.basic;

import net.jamsimulator.jams.mips.instruction.compiled.CompiledRSOPInstruction;
import net.jamsimulator.jams.mips.parameter.ParameterType;

/**
 * Represents a R-Type SOP basic instruction. This subclass adds the function code
 * of the instruction, allowing the simulator to find this instruction based on
 * an instruction code.
 */
public abstract class BasicRSOPInstruction extends BasicInstruction {

	private int functionCode;
	private int sopCode;

	/**
	 * Creates a basic instruction using a name, a mnemonic, a parameter types array and an operation code.
	 *
	 * @param name          the name.
	 * @param mnemonic      the mnemonic.
	 * @param parameters    the parameter types.
	 * @param operationCode the operation code.
	 * @param functionCode  the function code.
	 */
	public BasicRSOPInstruction(String name, String mnemonic, ParameterType[] parameters, int operationCode,
								int functionCode, int sopCode) {
		super(name, mnemonic, parameters, operationCode);
		this.functionCode = functionCode;
		this.sopCode = sopCode;
	}

	@Override
	public boolean match(int instructionCode) {
		int functionCode = instructionCode & CompiledRSOPInstruction.FUNCTION_CODE_MASK;
		int sopCode = instructionCode >> CompiledRSOPInstruction.SOP_SHIFT & CompiledRSOPInstruction.SOP_MASK;
		return super.match(instructionCode) && functionCode == this.functionCode && sopCode == this.sopCode;
	}

	/**
	 * Returns the function code of the instruction.
	 *
	 * @return the function code.
	 */
	public int getFunctionCode() {
		return functionCode;
	}

	/**
	 * Returns the sop code of this instruction.
	 *
	 * @return the sop code.
	 */
	public int getSopCode() {
		return sopCode;
	}
}
