package net.jamsimulator.jams.mips.compiler;

import net.jamsimulator.jams.mips.compiler.exception.CompilerException;
import net.jamsimulator.jams.mips.instruction.Instruction;
import net.jamsimulator.jams.mips.instruction.compiled.CompiledInstruction;
import net.jamsimulator.jams.mips.parameter.ParameterType;
import net.jamsimulator.jams.mips.parameter.parse.ParameterParseResult;

import java.util.List;

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

	public void compile(Compiler compiler, CompilingFile file) {
		ParameterParseResult[] compiledParameters = new ParameterParseResult[parameters.size()];

		int index = 0;
		ParameterParseResult result;
		for (ParameterType parameter : instruction.getParameters()) {
			result = parameter.parse(parameters.get(index), compiler.getRegisterSet());

			//Parse label
			if (result.isHasLabel()) {
				result.setLabelValue(getLabelValue(compiler, file, result.getLabel()));
			}

			compiledParameters[index++] = result;
		}

		CompiledInstruction[] compiledInstructions = instruction.compile(compiledParameters);

		//Add instructions to memory
		int relativeAddress = address;
		for (CompiledInstruction compiledInstruction : compiledInstructions) {
			compiler.getMemory().setWord(relativeAddress, compiledInstruction.getCode());
			relativeAddress += 4;
		}

	}

	private int getLabelValue(Compiler compiler, CompilingFile file, String label) {
		if (!file.labels.containsKey(label)) {
			if (!compiler.getGlobalLabels().containsKey(label))
				throw new CompilerException(line, "Label " + label + " not found.");
			return compiler.getGlobalLabels().get(label);
		} else {
			return file.labels.get(label);
		}
	}
}