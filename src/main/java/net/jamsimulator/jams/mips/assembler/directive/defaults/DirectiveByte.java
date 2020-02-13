package net.jamsimulator.jams.mips.assembler.directive.defaults;

import net.jamsimulator.jams.mips.assembler.Assembler;
import net.jamsimulator.jams.mips.assembler.AssemblerData;
import net.jamsimulator.jams.mips.assembler.directive.Directive;
import net.jamsimulator.jams.mips.assembler.exception.AssemblerException;
import net.jamsimulator.jams.utils.NumericUtils;

public class DirectiveByte extends Directive {

	public static final String NAME = "byte";

	public DirectiveByte() {
		super(NAME);
	}

	@Override
	public int execute(int lineNumber, String line, String[] parameters, Assembler assembler) {
		if (parameters.length < 1)
			throw new AssemblerException(lineNumber, "." + NAME + " must have at least one parameter.");

		for (String parameter : parameters) {
			if (!NumericUtils.isByte(parameter))
				throw new AssemblerException(lineNumber, "." + NAME + " parameter '" + parameter + "' is not a signed byte.");
		}

		AssemblerData data = assembler.getAssemblerData();
		data.align(0);
		int start = data.getCurrent();
		for (String parameter : parameters) {
			assembler.getMemory().setByte(data.getCurrent(), Byte.parseByte(parameter));
			data.addCurrent(1);
		}
		return start;
	}
}
