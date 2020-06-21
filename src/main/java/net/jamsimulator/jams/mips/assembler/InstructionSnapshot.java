package net.jamsimulator.jams.mips.assembler;

import net.jamsimulator.jams.mips.assembler.exception.AssemblerException;
import net.jamsimulator.jams.mips.instruction.Instruction;
import net.jamsimulator.jams.mips.instruction.assembled.AssembledInstruction;
import net.jamsimulator.jams.mips.instruction.pseudo.PseudoInstruction;
import net.jamsimulator.jams.mips.instruction.set.InstructionSet;
import net.jamsimulator.jams.mips.parameter.ParameterType;
import net.jamsimulator.jams.mips.parameter.parse.ParameterParseResult;
import net.jamsimulator.jams.mips.register.Registers;
import net.jamsimulator.jams.utils.StringUtils;

import java.util.*;

public class InstructionSnapshot {

	private final int line, address;
	private final String raw;


	private String mnemonic;
	private List<String> parameters;
	private Instruction instruction;

	public InstructionSnapshot(int line, int address, String raw) {
		this.address = address;
		this.line = line;
		this.raw = raw;
	}

	public int scan(MIPS32Assembler assembler) {
		assembler.addOriginalInstruction(address, raw);
		decode();
		return scanInstruction(assembler.getRegisters(), assembler.getInstructionSet());
	}

	public void assemble(MIPS32AssemblingFile file) {
		ParameterParseResult[] parameters = assembleParameters(file);

		try {
			Assembler assembler = file.getAssembler();
			AssembledInstruction[] assembledInstructions =
					instruction.assemble(assembler.getInstructionSet(), address, parameters);

			//Add instructions to memory
			int relativeAddress = address;
			for (AssembledInstruction assembledInstruction : assembledInstructions) {
				assembler.getMemory().setWord(relativeAddress, assembledInstruction.getCode());
				relativeAddress += 4;
			}
		} catch (AssemblerException ex) {
			throw new AssemblerException(line, "Error while assembling instruction.", ex);
		}
	}

	private void decode() {
		int mnemonicIndex = raw.indexOf(' ');
		int tabIndex = raw.indexOf("\t");
		if (mnemonicIndex == -1) mnemonicIndex = tabIndex;
		else if (tabIndex != -1) mnemonicIndex = Math.min(mnemonicIndex, tabIndex);

		if (mnemonicIndex == -1) {
			mnemonic = raw;
			parameters = Collections.emptyList();
			return;
		}

		mnemonic = raw.substring(0, mnemonicIndex);
		String raw = this.raw.substring(mnemonicIndex + 1);
		parameters = StringUtils.multiSplitIgnoreInsideString(raw, false, " ", ",", "\t");
	}

	private int scanInstruction(Registers registers, InstructionSet instructionSet) {
		List<ParameterType>[] types = new List[parameters.size()];
		int parameterIndex = 0;
		for (String parameter : parameters) {
			List<ParameterType> list = ParameterType.getCompatibleParameterTypes(parameter, registers);
			if (list.isEmpty()) throw new AssemblerException(line, "Bad parameter " + parameter);
			types[parameterIndex++] = list;
		}

		Optional<Instruction> optional = instructionSet.getBestCompatibleInstruction(mnemonic, types);
		if (!optional.isPresent())
			throw new AssemblerException(line, "Instruction " + mnemonic + " with the given parameters not found.\n"
					+ Arrays.toString(types));
		instruction = optional.get();

		return instruction instanceof PseudoInstruction
				? ((PseudoInstruction) instruction).getInstructionAmount(parameters) << 2
				: 4;
	}

	private ParameterParseResult[] assembleParameters(MIPS32AssemblingFile file) {
		ParameterParseResult[] assembledParameters = new ParameterParseResult[parameters.size()];

		int index = 0;
		ParameterParseResult result;
		for (ParameterType parameter : instruction.getParameters()) {
			result = parameter.parse(parameters.get(index), file.getAssembler().getRegisters());

			//Parse label
			if (result.isHasLabel()) {

				OptionalInt optional = file.getLabelAddress(result.getLabel());
				if (!optional.isPresent()) {
					throw new AssemblerException(line, "Label " + result.getLabel() + " not found.");
				}

				result.setLabelValue(optional.getAsInt());
			}
			assembledParameters[index++] = result;
		}

		return assembledParameters;
	}

}
