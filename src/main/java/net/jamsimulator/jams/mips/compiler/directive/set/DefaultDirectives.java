package net.jamsimulator.jams.mips.compiler.directive.set;

import net.jamsimulator.jams.mips.compiler.directive.Directive;
import net.jamsimulator.jams.mips.compiler.directive.defaults.*;

import java.util.HashSet;
import java.util.Set;

class DefaultDirectives {

	static Set<Directive> directives = new HashSet<>();

	static {
		directives.add(new DirectiveAlign());
		directives.add(new DirectiveAscii());
		directives.add(new DirectiveAsciiz());
		directives.add(new DirectiveByte());
		directives.add(new DirectiveData());
		directives.add(new DirectiveDouble());
		directives.add(new DirectiveEqv());
		directives.add(new DirectiveExtern());
		directives.add(new DirectiveText());
	}

}
