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

import net.jamsimulator.jams.mips.memory.cache.CacheBuilder;
import net.jamsimulator.jams.mips.memory.cache.builder.AssociativeCacheBuilder;
import net.jamsimulator.jams.mips.memory.cache.builder.DirectCacheBuilder;
import net.jamsimulator.jams.mips.memory.cache.builder.SetAssociativeCacheBuilder;
import net.jamsimulator.jams.mips.memory.cache.event.CacheBuilderRegisterEvent;
import net.jamsimulator.jams.mips.memory.cache.event.CacheBuilderUnregisterEvent;

/**
 * This singleton stores all {@link CacheBuilder}s that projects may use.
 * <p>
 * To register an {@link CacheBuilder} use {@link #add(CacheBuilder)}.
 * To unregister an {@link CacheBuilder} use {@link #remove(Object)}.
 * An {@link CacheBuilder}'s removal from the manager doesn't make projects
 * to stop using it if they're already using it.
 */
public class CacheBuilderManager extends Manager<CacheBuilder<?>> {

	public static final CacheBuilderManager INSTANCE = new CacheBuilderManager();

	private CacheBuilderManager() {
		super(CacheBuilderRegisterEvent.Before::new, CacheBuilderRegisterEvent.After::new,
				CacheBuilderUnregisterEvent.Before::new, CacheBuilderUnregisterEvent.After::new);
	}

	@Override
	protected void loadDefaultElements() {
		add(new AssociativeCacheBuilder());
		add(new DirectCacheBuilder());
		add(new SetAssociativeCacheBuilder());
	}

}
