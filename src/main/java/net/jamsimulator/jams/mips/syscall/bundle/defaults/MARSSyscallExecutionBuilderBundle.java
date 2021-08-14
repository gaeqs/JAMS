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

package net.jamsimulator.jams.mips.syscall.bundle.defaults;

import net.jamsimulator.jams.mips.syscall.bundle.SyscallExecutionBuilderBundle;
import net.jamsimulator.jams.mips.syscall.defaults.*;

public class MARSSyscallExecutionBuilderBundle extends SyscallExecutionBuilderBundle {

    public static final String NAME = "MARS";

    public MARSSyscallExecutionBuilderBundle() {
        super(NAME);
        addBuilder(1, SyscallExecutionPrintInteger.NAME);
        addBuilder(2, SyscallExecutionPrintFloat.NAME);
        addBuilder(3, SyscallExecutionPrintDouble.NAME);
        addBuilder(4, SyscallExecutionPrintString.NAME);
        addBuilder(5, SyscallExecutionReadInteger.NAME);
        addBuilder(6, SyscallExecutionReadFloat.NAME);
        addBuilder(7, SyscallExecutionReadDouble.NAME);
        addBuilder(8, SyscallExecutionReadString.NAME);
        addBuilder(9, SyscallExecutionAllocateMemory.NAME);
        addBuilder(10, SyscallExecutionExit.NAME);
        addBuilder(11, SyscallExecutionPrintCharacter.NAME);
        addBuilder(12, SyscallExecutionReadCharacter.NAME);
        addBuilder(13, SyscallExecutionOpenFile.NAME);
        addBuilder(14, SyscallExecutionReadFile.NAME);
        addBuilder(15, SyscallExecutionWriteFile.NAME);
        addBuilder(16, SyscallExecutionCloseFile.NAME);
        addBuilder(17, SyscallExecutionExitWithValue.NAME);

        addBuilder(30, SyscallExecutionSystemTime.NAME);
        addBuilder(32, SyscallExecutionSleep.NAME);
        addBuilder(34, SyscallExecutionPrintHexadecimalInteger.NAME);
        addBuilder(35, SyscallExecutionPrintBinaryInteger.NAME);
        addBuilder(36, SyscallExecutionPrintUnsignedInteger.NAME);
        addBuilder(40, SyscallExecutionSetSeed.NAME);
        addBuilder(41, SyscallExecutionRandomInteger.NAME);
        addBuilder(42, SyscallExecutionRandomRangedInteger.NAME);
        addBuilder(43, SyscallExecutionRandomFloat.NAME);
        addBuilder(44, SyscallExecutionRandomDouble.NAME);
    }
}
