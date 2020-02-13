package net.jamsimulator.jams.mips.assembler.directive.defaults;

import net.jamsimulator.jams.mips.assembler.Assembler;
import net.jamsimulator.jams.mips.assembler.AssemblerData;
import net.jamsimulator.jams.mips.assembler.SelectedMemorySegment;
import net.jamsimulator.jams.mips.assembler.directive.Directive;
import net.jamsimulator.jams.mips.assembler.exception.AssemblerException;
import net.jamsimulator.jams.utils.LabelUtils;
import net.jamsimulator.jams.utils.NumericUtils;

public class DirectiveExtern extends Directive {

	public static final String NAME = "extern";

	public DirectiveExtern() {
		super(NAME);
	}

	@Override
	public int execute(int lineNumber, String line, String[] parameters, Assembler assembler) {
		if (parameters.length != 2)
			throw new AssemblerException(lineNumber, "." + NAME + " must have two parameter.");

		if (!LabelUtils.isLabelLegal(parameters[0]))
			throw new AssemblerException("Label " + parameters[0] + " is not legal.");
		if (!NumericUtils.isInteger(parameters[1]))
			throw new AssemblerException(parameters[1] + " is not a number.");
		int i = Integer.parseInt(parameters[1]);
		if (i < 0)
			throw new AssemblerException(i + " cannot be negative.");

		AssemblerData data = assembler.getAssemblerData();
		SelectedMemorySegment old = data.getSelected();
		data.setSelected(SelectedMemorySegment.EXTERN);
		data.align(0);
		int start = data.getCurrent();
		data.addCurrent(i);
		assembler.setAsGlobalLabel(lineNumber, parameters[0]);
		data.setSelected(old);
		return start;
	}
}
