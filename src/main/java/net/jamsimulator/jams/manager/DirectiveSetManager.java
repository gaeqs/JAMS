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

package net.jamsimulator.jams.manager;

import net.jamsimulator.jams.mips.directive.set.DirectiveSet;
import net.jamsimulator.jams.mips.directive.set.MIPS32DirectiveSet;
import net.jamsimulator.jams.mips.directive.set.event.DefaultDirectiveSetChangeEvent;
import net.jamsimulator.jams.mips.directive.set.event.DirectiveSetRegisterEvent;
import net.jamsimulator.jams.mips.directive.set.event.DirectiveSetUnregisterEvent;

/**
 * This singleton stores all {@link DirectiveSet}s that projects may use.
 * <p>
 * To register an {@link DirectiveSet} use {@link #add(Labeled)}.
 * To unregister an {@link DirectiveSet} use {@link #remove(Object)}.
 * An {@link DirectiveSet}'s removal from the manager doesn't make projects
 * to stop using it if they're already using it.
 */
public class DirectiveSetManager extends DefaultValuableManager<DirectiveSet> {

	public static final DirectiveSetManager INSTANCE = new DirectiveSetManager();

	private DirectiveSetManager() {
		super(DirectiveSetRegisterEvent.Before::new, DirectiveSetRegisterEvent.After::new,
				DirectiveSetUnregisterEvent.Before::new, DirectiveSetUnregisterEvent.After::new,
				DefaultDirectiveSetChangeEvent.Before::new, DefaultDirectiveSetChangeEvent.After::new);
	}

	@Override
	protected void loadDefaultElements() {
		add(MIPS32DirectiveSet.INSTANCE);
	}

	@Override
	protected DirectiveSet loadDefaultElement() {
		return MIPS32DirectiveSet.INSTANCE;
	}
}
