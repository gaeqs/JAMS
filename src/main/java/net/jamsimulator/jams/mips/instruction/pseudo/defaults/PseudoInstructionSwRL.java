package net.jamsimulator.jams.mips.instruction.pseudo.defaults;

import net.jamsimulator.jams.mips.assembler.exception.AssemblerException;
import net.jamsimulator.jams.mips.instruction.Instruction;
import net.jamsimulator.jams.mips.instruction.basic.BasicInstruction;
import net.jamsimulator.jams.mips.instruction.basic.defaults.InstructionAui;
import net.jamsimulator.jams.mips.instruction.basic.defaults.InstructionSw;
import net.jamsimulator.jams.mips.instruction.assembled.AssembledInstruction;
import net.jamsimulator.jams.mips.instruction.pseudo.PseudoInstruction;
import net.jamsimulator.jams.mips.instruction.set.InstructionSet;
import net.jamsimulator.jams.mips.parameter.ParameterType;
import net.jamsimulator.jams.mips.parameter.parse.ParameterParseResult;

public class PseudoInstructionSwRL extends PseudoInstruction {


	public static final String NAME = InstructionSw.NAME;
	public static final String MNEMONIC = InstructionSw.MNEMONIC;

	private static final ParameterType[] PARAMETER_TYPES = new ParameterType[]{ParameterType.REGISTER, ParameterType.LABEL};

	private static final ParameterType[] AUI_BASIC_PARAMETER_TYPES =
			new ParameterType[]{ParameterType.REGISTER, ParameterType.REGISTER, ParameterType.SIGNED_16_BIT};
	private static final ParameterType[] SW_BASIC_PARAMETER_TYPES =
			new ParameterType[]{ParameterType.REGISTER, ParameterType.SIGNED_16_BIT_REGISTER_SHIFT};

	private static final ParameterParseResult ZERO = ParameterParseResult.builder().register(0).build();
	private static final ParameterParseResult AT = ParameterParseResult.builder().register(1).build();

	public PseudoInstructionSwRL() {
		super(NAME, MNEMONIC, PARAMETER_TYPES);
	}

	@Override
	public int getInstructionAmount(String[] parameters) {
		return 2;
	}

	@Override
	public AssembledInstruction[] assemble(InstructionSet set, int address, ParameterParseResult[] parameters) {
		//Get instructions
		Instruction aui = set.getInstruction(InstructionAui.MNEMONIC, AUI_BASIC_PARAMETER_TYPES).orElse(null);
		if (!(aui instanceof BasicInstruction))
			throw new AssemblerException("Basic instruction '" + InstructionAui.MNEMONIC + "' not found.");
		Instruction sw = set.getInstruction(MNEMONIC, SW_BASIC_PARAMETER_TYPES).orElse(null);
		if (!(sw instanceof BasicInstruction))
			throw new AssemblerException("Basic instruction '" + MNEMONIC + "' not found.");


		int saveAddress = parameters[1].getLabelValue();
		int upper = saveAddress >> 16;
		int lower = saveAddress & 0xFFFF;

		//Get parameters
		ParameterParseResult[] auiParameters = new ParameterParseResult[]{
				AT, ZERO, ParameterParseResult.builder().immediate(upper).build()
		};


		ParameterParseResult[] swParameters = new ParameterParseResult[]{
				parameters[0], ParameterParseResult.builder().register(1).immediate(lower).build()
		};

		return new AssembledInstruction[]{((BasicInstruction) aui).assembleBasic(auiParameters, this),
				((BasicInstruction) sw).assembleBasic(swParameters, this)};
	}
}
