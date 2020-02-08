package net.jamsimulator.jams.mips.compiler.directive.set;

import net.jamsimulator.jams.mips.compiler.directive.Directive;
import net.jamsimulator.jams.mips.compiler.directive.defaults.DirectiveData;
import net.jamsimulator.jams.mips.compiler.directive.defaults.DirectiveText;

import java.util.HashSet;
import java.util.Set;

class DefaultDirectives {

	static Set<Directive> directives = new HashSet<>();

	static {
		directives.add(new DirectiveData());
		directives.add(new DirectiveText());
	}

}
