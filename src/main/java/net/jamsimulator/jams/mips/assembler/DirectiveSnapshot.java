package net.jamsimulator.jams.mips.assembler;

import net.jamsimulator.jams.mips.assembler.directive.Directive;

public class DirectiveSnapshot {

	public Directive directive;
	public String[] parameters;
	public int lineNumber;
	public int address;

	public DirectiveSnapshot(Directive directive, String[] parameters, int lineNumber, int address) {
		this.directive = directive;
		this.parameters = parameters;
		this.lineNumber = lineNumber;
		this.address = address;
	}

	public void compile(Assembler assembler, AssemblingFile file) {
		directive.postExecute(parameters, assembler, file, lineNumber, address);
	}
}
