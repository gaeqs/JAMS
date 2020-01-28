package net.jamsimulator.jams.mips.instruction.compiled;

import net.jamsimulator.jams.mips.instruction.BasicInstruction;
import net.jamsimulator.jams.mips.instruction.Instruction;

/**
 * Represents a compiled instruction.
 */
public abstract class CompiledInstruction {

	public static final int OPERATION_CODE_SHIFT = 26;

	protected int value;
	protected Instruction origin;
	private BasicInstruction basicOrigin;

	CompiledInstruction(int code, Instruction origin, BasicInstruction basicOrigin) {
		this.value = code;
		this.origin = origin;
		this.basicOrigin = basicOrigin;
	}

	/**
	 * Returns the numeric representation of the compiled instruction.
	 *
	 * @return the numeric representation.
	 */
	public int getCode() {
		return value;
	}

	/**
	 * Returns the original {@link Instruction} of the compiled instruction.
	 * <p>
	 * Several compiled instructions may have the same {@link Instruction}, is this was
	 * a {@link net.jamsimulator.jams.mips.instruction.PseudoInstruction}.
	 *
	 * @return the original {@link Instruction}.
	 */
	public Instruction getOrigin() {
		return origin;
	}

	/**
	 * Returns the original {@link BasicInstruction} of the compiled instruction.
	 * If the origin instruction was a {@link BasicInstruction} this method will return the same
	 * result as the method {@link #getOrigin()}.
	 *
	 * @return the original {@link BasicInstruction}.
	 */
	public BasicInstruction getBasicOrigin() {
		return basicOrigin;
	}

	/**
	 * Returns the operation code of the instruction.
	 *
	 * @return the operation code.
	 */
	public int getOperationCode() {
		return value >>> OPERATION_CODE_SHIFT;
	}

	/**
	 * Executes the instruction.
	 */
	public abstract void execute();
}
