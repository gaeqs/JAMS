package net.jamsimulator.jams.mips.assembler;

import net.jamsimulator.jams.mips.assembler.exception.AssemblerException;
import net.jamsimulator.jams.mips.directive.Directive;
import net.jamsimulator.jams.utils.StringUtils;

import java.util.Optional;

public class DirectiveSnapshot {

	public final int line;
	private final String raw;

	public int address;

	private String directiveName;

	private Directive directive;
	private String[] parameters;

	public DirectiveSnapshot(int line, int address, String raw) {
		this.raw = raw;
		this.line = line;
		this.address = address;
	}

	public void scan(MIPS32Assembler assembler) {
		decode();
		scanDirective(assembler);
	}

	public int executeNonLabelRequiredSteps(MIPS32AssemblingFile file) {
		return address = directive.execute(line, raw, parameters, file);
	}

	public void executeLabelRequiredSteps(MIPS32AssemblingFile file) {
		directive.postExecute(parameters, file, line, address);
	}

	private void decode() {
		int mnemonicIndex = raw.indexOf(' ');
		int tabIndex = raw.indexOf("\t");
		if (mnemonicIndex == -1) mnemonicIndex = tabIndex;
		else if (tabIndex != -1) mnemonicIndex = Math.min(mnemonicIndex, tabIndex);

		if (mnemonicIndex == -1) {
			directiveName = raw.substring(1);
			parameters = new String[0];
			return;
		}

		directiveName = raw.substring(1, mnemonicIndex);
		String raw = this.raw.substring(mnemonicIndex + 1);
		parameters = StringUtils.multiSplitIgnoreInsideString(raw, false, " ", ",", "\t")
				.toArray(new String[0]);
	}

	private void scanDirective(MIPS32Assembler assembler) {
		Optional<Directive> optional = assembler.getDirectiveSet().getDirective(directiveName);
		if (!optional.isPresent()) throw new AssemblerException(line, "Directive " + directiveName + " not found!");
		directive = optional.get();
	}

}
