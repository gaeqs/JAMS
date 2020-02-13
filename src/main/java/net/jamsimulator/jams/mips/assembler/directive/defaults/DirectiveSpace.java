package net.jamsimulator.jams.mips.assembler.directive.defaults;

import net.jamsimulator.jams.mips.assembler.Assembler;
import net.jamsimulator.jams.mips.assembler.AssemblerData;
import net.jamsimulator.jams.mips.assembler.directive.Directive;
import net.jamsimulator.jams.mips.assembler.exception.AssemblerException;
import net.jamsimulator.jams.utils.NumericUtils;

public class DirectiveSpace extends Directive {

	public static final String NAME = "space";

	public DirectiveSpace() {
		super(NAME);
	}

	@Override
	public int execute(int lineNumber, String line, String[] parameters, Assembler assembler) {
		if (parameters.length != 1)
			throw new AssemblerException(lineNumber, "." + NAME + " must have one parameter.");

		if (!NumericUtils.isInteger(parameters[0]))
			throw new AssemblerException(parameters[0] + " is not a number.");
		int i = Integer.parseInt(parameters[0]);
		if (i < 0) throw new AssemblerException(i + " cannot be negative.");

		AssemblerData data = assembler.getAssemblerData();
		data.align(0);
		int start = data.getCurrent();
		data.addCurrent(i);
		return start;
	}
}
