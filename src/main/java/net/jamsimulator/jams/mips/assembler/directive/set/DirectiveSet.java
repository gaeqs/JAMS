package net.jamsimulator.jams.mips.assembler.directive.set;

import net.jamsimulator.jams.mips.assembler.directive.Directive;
import net.jamsimulator.jams.utils.Validate;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class DirectiveSet {

	private Set<Directive> directives;

	public DirectiveSet(boolean loadDefaultDirectives, boolean loadPluginDirectives) {
		directives = new HashSet<>();
		if (loadDefaultDirectives) {
			directives.addAll(DefaultDirectives.directives);
		}
	}


	public Optional<Directive> getDirective(String name) {
		Validate.notNull(name, "Name cannot be null!");
		return directives.stream().filter(target -> target.getName().equals(name)).findFirst();
	}

	public boolean registerDirective(Directive directive) {
		Validate.notNull(directive, "Directive cannot be null!");
		return directives.add(directive);
	}

}
