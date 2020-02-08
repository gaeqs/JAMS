package net.jamsimulator.jams.mips.compiler.directive.defaults;

import net.jamsimulator.jams.mips.compiler.Compiler;
import net.jamsimulator.jams.mips.compiler.CompilerData;
import net.jamsimulator.jams.mips.compiler.directive.Directive;
import net.jamsimulator.jams.mips.compiler.exception.CompilerException;
import net.jamsimulator.jams.utils.NumericUtils;

public class DirectiveByte extends Directive {

	public static final String NAME = "byte";

	public DirectiveByte() {
		super(NAME);
	}

	@Override
	public int execute(int line, String[] parameters, Compiler compiler) {
		if (parameters.length < 1)
			throw new CompilerException(line, "." + NAME + " must have at least one parameter.");

		for (String parameter : parameters) {
			if (!NumericUtils.isByte(parameter))
				throw new CompilerException(line, "." + NAME + " parameter '" + parameter + "' is not a signed byte.");
		}

		CompilerData data = compiler.getCompilerData();
		data.align(0);
		int start = data.getCurrent();
		for (String parameter : parameters) {
			compiler.getMemory().setByte(data.getCurrent(), Byte.parseByte(parameter));
			data.addCurrent(1);
		}
		return start;
	}
}
