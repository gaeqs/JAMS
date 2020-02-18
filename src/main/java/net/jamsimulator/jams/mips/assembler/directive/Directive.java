package net.jamsimulator.jams.mips.assembler.directive;

import net.jamsimulator.jams.mips.assembler.Assembler;

/**
 * Represents a directive. Directive are the direct equivalent to the preprocessor code in C.
 * They are used to give orders to the assembler.
 */
public abstract class Directive {

	private String name;


	public Directive(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public abstract int execute(int lineNumber, String line, String[] parameters, Assembler assembler);

}