/*
 *  MIT License
 *
 *  Copyright (c) 2021 Gael Rial Costas
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */

package net.jamsimulator.jams.mips.register.builder;

import net.jamsimulator.jams.manager.DefaultValuableManager;
import net.jamsimulator.jams.manager.ResourceProvider;

/**
 * This singleton stores all {@link RegistersBuilder}s that projects may use.
 * <p>
 * To register an {@link RegistersBuilder} use {@link #add(net.jamsimulator.jams.manager.ManagerResource)}.
 * To unregister an {@link RegistersBuilder} use {@link #remove(Object)}.
 * An {@link RegistersBuilder}'s removal from the manager doesn't make projects
 * to stop using it if they're already using it.
 */
public final class RegistersBuilderManager extends DefaultValuableManager<RegistersBuilder> {

    public static final String NAME = "register_builder";
    public static final RegistersBuilderManager INSTANCE = new RegistersBuilderManager(ResourceProvider.JAMS, NAME);

    public RegistersBuilderManager(ResourceProvider provider, String name) {
        super(provider, name, RegistersBuilder.class, false);
    }

    @Override
    protected void loadDefaultElements() {
        add(new MIPS32RegistersBuilder(ResourceProvider.JAMS));
    }

    @Override
    protected RegistersBuilder loadDefaultElement() {
        return get(MIPS32RegistersBuilder.NAME).orElseThrow();
    }

}
