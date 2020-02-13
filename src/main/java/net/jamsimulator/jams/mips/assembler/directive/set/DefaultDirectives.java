package net.jamsimulator.jams.mips.assembler.directive.set;

import net.jamsimulator.jams.mips.assembler.directive.Directive;
import net.jamsimulator.jams.mips.assembler.directive.defaults.*;

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
		directives.add(new DirectiveFloat());
		directives.add(new DirectiveGlobl());
		directives.add(new DirectiveHalf());
		directives.add(new DirectiveInclude());
		directives.add(new DirectiveKData());
		directives.add(new DirectiveKText());
		directives.add(new DirectiveSpace());
		directives.add(new DirectiveText());
		directives.add(new DirectiveWord());
	}

}
