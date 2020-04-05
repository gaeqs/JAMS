package net.jamsimulator.jams.mips.assembler.directive.defaults;

import net.jamsimulator.jams.mips.assembler.Assembler;
import net.jamsimulator.jams.mips.assembler.AssemblerData;
import net.jamsimulator.jams.mips.assembler.AssemblingFile;
import net.jamsimulator.jams.mips.assembler.directive.Directive;
import net.jamsimulator.jams.mips.assembler.exception.AssemblerException;
import net.jamsimulator.jams.utils.NumericUtils;

public class DirectiveDouble extends Directive {

	public static final String NAME = "double";

	public DirectiveDouble() {
		super(NAME);
	}

	@Override
	public int execute(int lineNumber, String line, String[] parameters, Assembler assembler) {
		if (parameters.length < 1)
			throw new AssemblerException(lineNumber, "." + NAME + " must have at least one parameter.");

		for (String parameter : parameters) {
			if (!NumericUtils.isDouble(parameter))
				throw new AssemblerException(lineNumber, "." + NAME + " parameter '" + parameter + "' is not a double.");
		}

		AssemblerData data = assembler.getAssemblerData();
		data.align(3);
		int start = data.getCurrent();
		for (String parameter : parameters) {
			long l = Double.doubleToLongBits(Long.parseLong(parameter));

			int low = (int) l;
			int high = (int) (l >> 32);

			assembler.getMemory().setWord(data.getCurrent(), low);
			assembler.getMemory().setWord(data.getCurrent() + 4, high);
			data.addCurrent(8);
		}
		return start;
	}

	@Override
	public void postExecute(String[] parameters, Assembler assembler, AssemblingFile file, int lineNumber, int address) {

	}
}
