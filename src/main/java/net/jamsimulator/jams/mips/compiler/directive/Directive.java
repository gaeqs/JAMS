package net.jamsimulator.jams.mips.compiler.directive;

import net.jamsimulator.jams.mips.compiler.Compiler;

public abstract class Directive {

	private String name;


	public Directive(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public abstract int execute(int line, String[] parameters, Compiler compiler);

}
