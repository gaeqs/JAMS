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

import net.jamsimulator.jams.mips.register.builder.MIPS32RegistersBuilder;
import net.jamsimulator.jams.mips.register.builder.RegistersBuilder;
import net.jamsimulator.jams.mips.register.builder.event.DefaultRegistersBuilderChangeEvent;
import net.jamsimulator.jams.mips.register.builder.event.RegistersBuilderRegisterEvent;
import net.jamsimulator.jams.mips.register.builder.event.RegistersBuilderUnregisterEvent;

/**
 * This singleton stores all {@link RegistersBuilder}s that projects may use.
 * <p>
 * To register an {@link RegistersBuilder} use {@link #add(Labeled)}.
 * To unregister an {@link RegistersBuilder} use {@link #remove(Object)}.
 * An {@link RegistersBuilder}'s removal from the manager doesn't make projects
 * to stop using it if they're already using it.
 */
public class RegistersBuilderManager extends DefaultValuableManager<RegistersBuilder> {

	public static final RegistersBuilderManager INSTANCE = new RegistersBuilderManager();


	private RegistersBuilderManager() {
		super(RegistersBuilderRegisterEvent.Before::new, RegistersBuilderRegisterEvent.After::new,
				RegistersBuilderUnregisterEvent.Before::new, RegistersBuilderUnregisterEvent.After::new,
				DefaultRegistersBuilderChangeEvent.Before::new, DefaultRegistersBuilderChangeEvent.After::new);
	}

	@Override
	protected void loadDefaultElements() {
		add(MIPS32RegistersBuilder.INSTANCE);
	}

	@Override
	protected RegistersBuilder loadDefaultElement() {
		return MIPS32RegistersBuilder.INSTANCE;
	}

}
