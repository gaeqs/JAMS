package net.jamsimulator.jams.mips.assembler.directive.set;

import net.jamsimulator.jams.mips.assembler.directive.Directive;
import net.jamsimulator.jams.utils.Validate;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * Represents the collection of {@link Directive}s a MIPS program may use.
 */
public class DirectiveSet {

	private final Set<Directive> directives;

	/**
	 * Creates the directive set.
	 *
	 * @param loadDefaultDirectives whether default directives should be added automatically.
	 * @param loadPluginDirectives  whether plugin directives should be added automatically.
	 */
	public DirectiveSet(boolean loadDefaultDirectives, boolean loadPluginDirectives) {
		directives = new HashSet<>();
		if (loadDefaultDirectives) {
			directives.addAll(DefaultDirectives.directives);
		}
	}

	/**
	 * Returns a unmodifiable {@link Set} with all {@link Directive}s
	 * registered in this directive set.
	 * <p>
	 * Any attempt to modify this {@link Set} results in an {@link UnsupportedOperationException}.
	 *
	 * @return the unmodifiable {@link Set}.
	 * @see Collections#unmodifiableSet(Set)
	 */
	public Set<Directive> getDirectives() {
		return Collections.unmodifiableSet(directives);
	}

	/**
	 * Returns the {@link Directive} that matches the given name.
	 *
	 * @param name the name.
	 * @return the directive, if present.
	 */
	public Optional<Directive> getDirective(String name) {
		Validate.notNull(name, "Name cannot be null!");
		return directives.stream().filter(target -> target.getName().equals(name)).findFirst();
	}

	/**
	 * Registers a {@link Directive}. This method will fail if a directive with the same name is already registered.
	 *
	 * @param directive the {@link Directive} to register.
	 * @return whether the {@link Directive} has been registered.
	 */
	public boolean registerDirective(Directive directive) {
		Validate.notNull(directive, "Directive cannot be null!");
		return directives.add(directive);
	}

}
