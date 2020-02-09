package net.jamsimulator.jams.mips.compiler.directive.defaults;

import net.jamsimulator.jams.mips.compiler.Compiler;
import net.jamsimulator.jams.mips.compiler.CompilerData;
import net.jamsimulator.jams.mips.compiler.directive.Directive;
import net.jamsimulator.jams.mips.compiler.exception.CompilerException;
import net.jamsimulator.jams.utils.NumericUtils;

public class DirectiveDouble extends Directive {

	public static final String NAME = "double";

	public DirectiveDouble() {
		super(NAME);
	}

	@Override
	public int execute(int lineNumber, String line, String[] parameters, Compiler compiler) {
		if (parameters.length < 1)
			throw new CompilerException(lineNumber, "." + NAME + " must have at least one parameter.");

		for (String parameter : parameters) {
			if (!NumericUtils.isDouble(parameter))
				throw new CompilerException(lineNumber, "." + NAME + " parameter '" + parameter + "' is not a double.");
		}

		CompilerData data = compiler.getCompilerData();
		data.align(3);
		int start = data.getCurrent();
		for (String parameter : parameters) {
			long l = Double.doubleToLongBits(Long.parseLong(parameter));

			int low = (int) l;
			int high = (int) (l >> 32);

			compiler.getMemory().setWord(data.getCurrent(), low);
			compiler.getMemory().setWord(data.getCurrent() + 4, high);
			data.addCurrent(8);
		}
		return start;
	}
}
