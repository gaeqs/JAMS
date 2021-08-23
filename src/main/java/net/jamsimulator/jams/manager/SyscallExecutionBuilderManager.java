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

import net.jamsimulator.jams.mips.syscall.SyscallExecutionBuilder;
import net.jamsimulator.jams.mips.syscall.defaults.*;

/**
 * This singleton stores all {@link SyscallExecutionBuilder}s that projects may use.
 * <p>
 * To register an {@link SyscallExecutionBuilder} use {@link Manager#add(Labeled)}}.
 * To unregister an {@link SyscallExecutionBuilder} use {@link #remove(Object)}.
 * An {@link SyscallExecutionBuilder}'s removal from the manager doesn't make projects
 * to stop using it if they're already using it.
 */
public final class SyscallExecutionBuilderManager extends Manager<SyscallExecutionBuilder> {

    public static final SyscallExecutionBuilderManager INSTANCE = new SyscallExecutionBuilderManager();

    private SyscallExecutionBuilderManager() {
        super(SyscallExecutionBuilder.class);
    }

    @Override
    protected void loadDefaultElements() {
        add(new SyscallExecutionRunExceptionHandler.Builder());
        add(new SyscallExecutionPrintInteger.Builder());
        add(new SyscallExecutionPrintFloat.Builder());
        add(new SyscallExecutionPrintDouble.Builder());
        add(new SyscallExecutionPrintString.Builder());
        add(new SyscallExecutionReadInteger.Builder());
        add(new SyscallExecutionReadFloat.Builder());
        add(new SyscallExecutionReadDouble.Builder());
        add(new SyscallExecutionReadString.Builder());
        add(new SyscallExecutionAllocateMemory.Builder());
        add(new SyscallExecutionExit.Builder());
        add(new SyscallExecutionPrintCharacter.Builder());
        add(new SyscallExecutionReadCharacter.Builder());
        add(new SyscallExecutionOpenFile.Builder());
        add(new SyscallExecutionReadFile.Builder());
        add(new SyscallExecutionWriteFile.Builder());
        add(new SyscallExecutionCloseFile.Builder());
        add(new SyscallExecutionExitWithValue.Builder());

        add(new SyscallExecutionSystemTime.Builder());
        add(new SyscallExecutionSleep.Builder());

        add(new SyscallExecutionPrintHexadecimalInteger.Builder());
        add(new SyscallExecutionPrintBinaryInteger.Builder());
        add(new SyscallExecutionPrintUnsignedInteger.Builder());
        add(new SyscallExecutionSetSeed.Builder());
        add(new SyscallExecutionRandomInteger.Builder());
        add(new SyscallExecutionRandomRangedInteger.Builder());
        add(new SyscallExecutionRandomFloat.Builder());
        add(new SyscallExecutionRandomDouble.Builder());
    }

}
