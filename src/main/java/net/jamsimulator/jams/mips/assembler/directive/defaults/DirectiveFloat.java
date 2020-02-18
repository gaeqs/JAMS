package net.jamsimulator.jams.mips.assembler.directive.defaults;

import net.jamsimulator.jams.mips.assembler.Assembler;
import net.jamsimulator.jams.mips.assembler.AssemblerData;
import net.jamsimulator.jams.mips.assembler.directive.Directive;
import net.jamsimulator.jams.mips.assembler.exception.AssemblerException;
import net.jamsimulator.jams.utils.NumericUtils;

public class DirectiveFloat extends Directive {

	public static final String NAME = "float";

	public DirectiveFloat() {
		super(NAME);
	}

	@Override
	public int execute(int lineNumber, String line, String[] parameters, Assembler assembler) {
		if (parameters.length < 1)
			throw new AssemblerException(lineNumber, "." + NAME + " must have at least one parameter.");

		for (String parameter : parameters) {
			if (!NumericUtils.isFloat(parameter))
				throw new AssemblerException(lineNumber, "." + NAME + " parameter '" + parameter + "' is not a float.");
		}

		AssemblerData data = assembler.getAssemblerData();
		data.align(2);
		int start = data.getCurrent();
		for (String parameter : parameters) {
			assembler.getMemory().setWord(data.getCurrent(), Float.floatToIntBits(Float.parseFloat(parameter)));
			data.addCurrent(4);
		}
		return start;
	}
}