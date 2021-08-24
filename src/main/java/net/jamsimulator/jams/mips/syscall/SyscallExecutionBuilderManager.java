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

package net.jamsimulator.jams.mips.syscall;

import net.jamsimulator.jams.manager.Manager;
import net.jamsimulator.jams.manager.ResourceProvider;
import net.jamsimulator.jams.mips.syscall.defaults.*;

/**
 * This singleton stores all {@link SyscallExecutionBuilder}s that projects may use.
 * <p>
 * To register an {@link SyscallExecutionBuilder} use {@link Manager#add(net.jamsimulator.jams.manager.ManagerResource)}}.
 * To unregister an {@link SyscallExecutionBuilder} use {@link #remove(Object)}.
 * An {@link SyscallExecutionBuilder}'s removal from the manager doesn't make projects
 * to stop using it if they're already using it.
 */
public final class SyscallExecutionBuilderManager extends Manager<SyscallExecutionBuilder> {

    public static final String NAME = "syscall_execution_builder";
    public static final SyscallExecutionBuilderManager INSTANCE = new SyscallExecutionBuilderManager(ResourceProvider.JAMS, NAME);

    public SyscallExecutionBuilderManager(ResourceProvider provider, String name) {
        super(provider, name, SyscallExecutionBuilder.class, false);
    }

    @Override
    protected void loadDefaultElements() {
        add(new SyscallExecutionRunExceptionHandler.Builder(ResourceProvider.JAMS));
        add(new SyscallExecutionPrintInteger.Builder(ResourceProvider.JAMS));
        add(new SyscallExecutionPrintFloat.Builder(ResourceProvider.JAMS));
        add(new SyscallExecutionPrintDouble.Builder(ResourceProvider.JAMS));
        add(new SyscallExecutionPrintString.Builder(ResourceProvider.JAMS));
        add(new SyscallExecutionReadInteger.Builder(ResourceProvider.JAMS));
        add(new SyscallExecutionReadFloat.Builder(ResourceProvider.JAMS));
        add(new SyscallExecutionReadDouble.Builder(ResourceProvider.JAMS));
        add(new SyscallExecutionReadString.Builder(ResourceProvider.JAMS));
        add(new SyscallExecutionAllocateMemory.Builder(ResourceProvider.JAMS));
        add(new SyscallExecutionExit.Builder(ResourceProvider.JAMS));
        add(new SyscallExecutionPrintCharacter.Builder(ResourceProvider.JAMS));
        add(new SyscallExecutionReadCharacter.Builder(ResourceProvider.JAMS));
        add(new SyscallExecutionOpenFile.Builder(ResourceProvider.JAMS));
        add(new SyscallExecutionReadFile.Builder(ResourceProvider.JAMS));
        add(new SyscallExecutionWriteFile.Builder(ResourceProvider.JAMS));
        add(new SyscallExecutionCloseFile.Builder(ResourceProvider.JAMS));
        add(new SyscallExecutionExitWithValue.Builder(ResourceProvider.JAMS));

        add(new SyscallExecutionSystemTime.Builder(ResourceProvider.JAMS));
        add(new SyscallExecutionSleep.Builder(ResourceProvider.JAMS));

        add(new SyscallExecutionPrintHexadecimalInteger.Builder(ResourceProvider.JAMS));
        add(new SyscallExecutionPrintBinaryInteger.Builder(ResourceProvider.JAMS));
        add(new SyscallExecutionPrintUnsignedInteger.Builder(ResourceProvider.JAMS));
        add(new SyscallExecutionSetSeed.Builder(ResourceProvider.JAMS));
        add(new SyscallExecutionRandomInteger.Builder(ResourceProvider.JAMS));
        add(new SyscallExecutionRandomRangedInteger.Builder(ResourceProvider.JAMS));
        add(new SyscallExecutionRandomFloat.Builder(ResourceProvider.JAMS));
        add(new SyscallExecutionRandomDouble.Builder(ResourceProvider.JAMS));
    }

}
