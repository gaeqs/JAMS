package net.jamsimulator.jams.mips.assembler;

import net.jamsimulator.jams.mips.assembler.exception.AssemblerException;
import net.jamsimulator.jams.mips.instruction.Instruction;
import net.jamsimulator.jams.mips.instruction.compiled.CompiledInstruction;
import net.jamsimulator.jams.mips.parameter.ParameterType;
import net.jamsimulator.jams.mips.parameter.parse.ParameterParseResult;

import java.util.List;

/**
 * Represents a instruction snapshot. Because labels may be below instructions
 * they cannot be compiled on the first scan. They must be stored in a snapshot.
 */
public class InstructionSnapshot {

	public Instruction instruction;
	public List<String> parameters;
	public int line;
	public int address;

	public InstructionSnapshot(Instruction instruction, List<String> parameters, int line, int address) {
		this.instruction = instruction;
		this.parameters = parameters;
		this.line = line;
		this.address = address;
	}

	public void compile(Assembler assembler, AssemblingFile file) {
		ParameterParseResult[] compiledParameters = new ParameterParseResult[parameters.size()];

		int index = 0;
		ParameterParseResult result;
		for (ParameterType parameter : instruction.getParameters()) {
			result = parameter.parse(parameters.get(index), assembler.getRegisterSet());

			//Parse label
			if (result.isHasLabel()) {
				result.setLabelValue(file.getLabelValue(assembler, result.getLabel(), line));
			}

			compiledParameters[index++] = result;
		}

		try {
			CompiledInstruction[] compiledInstructions = instruction.assemble(assembler.getInstructionSet(), address, compiledParameters);

			//Add instructions to memory
			int relativeAddress = address;
			for (CompiledInstruction compiledInstruction : compiledInstructions) {
				assembler.getMemory().setWord(relativeAddress, compiledInstruction.getCode());
				relativeAddress += 4;
			}
		} catch (AssemblerException ex) {
			throw new AssemblerException(line, "Error while compiling instruction.", ex);
		}

	}
}
