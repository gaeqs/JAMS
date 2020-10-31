/*
 * MIT License
 *
 * Copyright (c) 2020 Gael Rial Costas
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package net.jamsimulator.jams.mips.directive.set;

import net.jamsimulator.jams.manager.Labeled;
import net.jamsimulator.jams.mips.directive.Directive;
import net.jamsimulator.jams.utils.Validate;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * Represents the collection of {@link Directive}s a MIPS program may use.
 */
public class DirectiveSet implements Labeled {

	protected final String name;
	protected final Set<Directive> directives;

	/**
	 * Creates the directive set.
	 */
	public DirectiveSet(String name) {
		this.name = name;
		directives = new HashSet<>();
	}

	/**
	 * Returns the name of this directive set.
	 *
	 * @return the name.
	 */
	@Override
	public String getName() {
		return name;
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
