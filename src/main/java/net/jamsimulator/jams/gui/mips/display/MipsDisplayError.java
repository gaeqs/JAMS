package net.jamsimulator.jams.gui.mips.display;

/**
 * This enum recollects all error that can be displayed by a {@link MipsFileDisplay}.
 */
public enum MipsDisplayError {

	//LABELS

	/**
	 * Used when a label has an illegal name.
	 */
	ILLEGAL_LABEL,
	/**
	 * Used when a parameter is pointing to a label that doesn't exist.
	 */
	LABEL_NOT_FOUND,
	/**
	 * Used when a label is duplicated.
	 */
	DUPLICATE_LABEL,

	//DIRECTIVES
	/**
	 * Used when a directive doesn't exist.
	 */
	DIRECTIVE_NOT_FOUND,

	//DIRECTIVE PARAMETERS

	//INSTRUCTIONS
	/**
	 * Used when an instruction doesn't exist.
	 */
	INSTRUCTION_NOT_FOUND,

	//INSTRUCTION PARAMETERS
	/**
	 * Used when an instruction's parameter has an invalid format.
	 */
	INVALID_INSTRUCTION_PARAMETER
}
