package net.jamsimulator.jams.mips.assembler.directive.defaults;

import net.jamsimulator.jams.mips.assembler.Assembler;
import net.jamsimulator.jams.mips.assembler.SelectedMemorySegment;
import net.jamsimulator.jams.mips.assembler.directive.Directive;
import net.jamsimulator.jams.mips.assembler.exception.AssemblerException;

public class DirectiveKData extends Directive {

	public static final String NAME = "kdata";

	public DirectiveKData() {
		super(NAME);
	}

	@Override
	public int execute(int lineNumber, String line, String[] parameters, Assembler assembler) {
		if (parameters.length != 0)
			throw new AssemblerException(lineNumber, "." + NAME + " directive cannot have parameters.");
		assembler.getAssemblerData().setSelected(SelectedMemorySegment.KERNEL_DATA);
		return  -1;
	}
}
