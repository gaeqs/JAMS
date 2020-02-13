package net.jamsimulator.jams.mips.assembler.directive.defaults;

import net.jamsimulator.jams.mips.assembler.Assembler;
import net.jamsimulator.jams.mips.assembler.directive.Directive;
import net.jamsimulator.jams.mips.assembler.exception.AssemblerException;
import net.jamsimulator.jams.utils.NumericUtils;

public class DirectiveAlign extends Directive {

	public static final String NAME = "align";

	public DirectiveAlign() {
		super(NAME);
	}

	@Override
	public int execute(int lineNumber, String line, String[] parameters, Assembler assembler) {
		if (parameters.length != 1 || !NumericUtils.isInteger(parameters[0]))
			throw new AssemblerException(lineNumber, "." + NAME + " must have a numeric parameter.");
		int exp = Integer.parseInt(parameters[0]);
		if (exp < 0 || exp > 3)
			throw new AssemblerException(lineNumber, "." + NAME + " parameter must be inside the range [0, 3].");
		assembler.getAssemblerData().setNextForcedAlignment(exp);
		return -1;
	}
}
