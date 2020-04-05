package net.jamsimulator.jams.mips.assembler.directive.defaults;

import net.jamsimulator.jams.mips.assembler.Assembler;
import net.jamsimulator.jams.mips.assembler.AssemblerData;
import net.jamsimulator.jams.mips.assembler.AssemblingFile;
import net.jamsimulator.jams.mips.assembler.directive.Directive;
import net.jamsimulator.jams.mips.assembler.exception.AssemblerException;
import net.jamsimulator.jams.mips.memory.Memory;
import net.jamsimulator.jams.utils.NumericUtils;

public class DirectiveWord extends Directive {

	public static final String NAME = "word";

	public DirectiveWord() {
		super(NAME);
	}

	@Override
	public int execute(int lineNumber, String line, String[] parameters, Assembler assembler) {
		if (parameters.length < 1)
			throw new AssemblerException(lineNumber, "." + NAME + " must have at least one parameter.");

		AssemblerData data = assembler.getAssemblerData();
		data.align(2);
		int start = data.getCurrent();
		data.addCurrent(4 * parameters.length);
		return start;
	}

	@Override
	public void postExecute(String[] parameters, Assembler assembler, AssemblingFile file, int lineNumber, int address) {
		Memory memory = assembler.getMemory();
		for (String parameter : parameters) {
			int value = NumericUtils.decodeIntegerSafe(parameter).orElseGet(() -> file.getLabelValue(assembler, parameter, lineNumber));
			memory.setWord(address, value);
			address += 4;
		}
	}
}
