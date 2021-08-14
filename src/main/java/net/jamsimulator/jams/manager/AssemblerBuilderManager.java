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

package net.jamsimulator.jams.manager;

import net.jamsimulator.jams.mips.assembler.builder.AssemblerBuilder;
import net.jamsimulator.jams.mips.assembler.builder.MIPS32AssemblerBuilder;
import net.jamsimulator.jams.mips.assembler.builder.event.AssemblerBuilderRegisterEvent;
import net.jamsimulator.jams.mips.assembler.builder.event.AssemblerBuilderUnregisterEvent;
import net.jamsimulator.jams.mips.assembler.builder.event.DefaultAssemblerBuilderChangeEvent;

/**
 * This singleton stores all {@link AssemblerBuilder}s that projects may use.
 * <p>
 * To register an {@link AssemblerBuilder} use {@link #add(Labeled)}.
 * To unregister an {@link AssemblerBuilder} use {@link #remove(Object)}.
 * An {@link AssemblerBuilder}'s removal from the manager doesn't make projects
 * to stop using it if they're already using it.
 */
public class AssemblerBuilderManager extends DefaultValuableManager<AssemblerBuilder> {

    public static final AssemblerBuilderManager INSTANCE = new AssemblerBuilderManager();

    private AssemblerBuilderManager() {
        super(AssemblerBuilderRegisterEvent.Before::new, AssemblerBuilderRegisterEvent.After::new,
                AssemblerBuilderUnregisterEvent.Before::new, AssemblerBuilderUnregisterEvent.After::new,
                DefaultAssemblerBuilderChangeEvent.Before::new, DefaultAssemblerBuilderChangeEvent.After::new);
    }

    @Override
    protected AssemblerBuilder loadDefaultElement() {
        return MIPS32AssemblerBuilder.INSTANCE;
    }

    @Override
    protected void loadDefaultElements() {
        add(MIPS32AssemblerBuilder.INSTANCE);
    }
}
