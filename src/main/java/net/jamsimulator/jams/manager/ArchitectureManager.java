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

import net.jamsimulator.jams.mips.architecture.Architecture;
import net.jamsimulator.jams.mips.architecture.MultiCycleArchitecture;
import net.jamsimulator.jams.mips.architecture.PipelinedArchitecture;
import net.jamsimulator.jams.mips.architecture.SingleCycleArchitecture;
import net.jamsimulator.jams.mips.architecture.event.ArchitectureRegisterEvent;
import net.jamsimulator.jams.mips.architecture.event.ArchitectureUnregisterEvent;
import net.jamsimulator.jams.mips.architecture.event.DefaultArchitectureChangeEvent;

/**
 * This singleton stores all {@link Architecture}s that projects may use.
 * <p>
 * To register an {@link Architecture} use {@link #add(Labeled)}.
 * To unregister an {@link Architecture} use {@link #remove(Object)}.
 * An {@link Architecture}'s removal from the manager doesn't make projects
 * to stop using it if they're already using it.
 */
public class ArchitectureManager extends DefaultValuableManager<Architecture> {

    public static final ArchitectureManager INSTANCE = new ArchitectureManager();

    public ArchitectureManager() {
        super(ArchitectureRegisterEvent.Before::new, ArchitectureRegisterEvent.After::new,
                ArchitectureUnregisterEvent.Before::new, ArchitectureUnregisterEvent.After::new,
                DefaultArchitectureChangeEvent.Before::new, DefaultArchitectureChangeEvent.After::new);
    }

    @Override
    protected Architecture loadDefaultElement() {
        return SingleCycleArchitecture.INSTANCE;
    }

    @Override
    protected void loadDefaultElements() {
        add(SingleCycleArchitecture.INSTANCE);
        add(MultiCycleArchitecture.INSTANCE);
        add(PipelinedArchitecture.INSTANCE);
    }
}
