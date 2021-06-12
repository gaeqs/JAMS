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

import net.jamsimulator.jams.mips.instruction.set.InstructionSet;
import net.jamsimulator.jams.mips.instruction.set.MIPS32r6InstructionSet;
import net.jamsimulator.jams.mips.instruction.set.event.DefaultInstructionSetChangeEvent;
import net.jamsimulator.jams.mips.instruction.set.event.InstructionSetRegisterEvent;
import net.jamsimulator.jams.mips.instruction.set.event.InstructionSetUnregisterEvent;

/**
 * This singleton stores all {@link InstructionSet}s that projects may use.
 * <p>
 * To register an {@link InstructionSet} use {@link #add(Labeled)}.
 * To unregister an {@link InstructionSet} use {@link #remove(Object)}.
 * An {@link InstructionSet}'s removal from the manager doesn't make projects
 * to stop using it if they're already using it.
 */
public class InstructionSetManager extends DefaultValuableManager<InstructionSet> {

    public static final InstructionSetManager INSTANCE = new InstructionSetManager();

    private InstructionSetManager() {
        super(InstructionSetRegisterEvent.Before::new, InstructionSetRegisterEvent.After::new,
                InstructionSetUnregisterEvent.Before::new, InstructionSetUnregisterEvent.After::new,
                DefaultInstructionSetChangeEvent.Before::new, DefaultInstructionSetChangeEvent.After::new);
    }

    @Override
    protected void loadDefaultElements() {
        add(MIPS32r6InstructionSet.INSTANCE);
    }

    @Override
    protected InstructionSet loadDefaultElement() {
        return MIPS32r6InstructionSet.INSTANCE;
    }
}
