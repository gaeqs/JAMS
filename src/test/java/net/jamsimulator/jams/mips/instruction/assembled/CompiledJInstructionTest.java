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

package net.jamsimulator.jams.mips.instruction.assembled;

import net.jamsimulator.jams.mips.instruction.basic.defaults.InstructionJ;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CompiledJInstructionTest {

    static AssembledJInstruction instruction;

    @BeforeAll
    static void initialize() {
        var instuctionJ = new InstructionJ();
        instruction = new InstructionJ.Assembled(instuctionJ, instuctionJ, 0x00100000);
        //b this
        //THERE IS NO J INSTRUCTIONS YET.
    }


    @Test
    void getOperationCode() {
        assertEquals(InstructionJ.OPERATION_CODE, instruction.getOperationCode(), "Bad OP code.");
    }

    @Test
    void getAddress() {
        assertEquals(0x00100000 >> InstructionJ.Assembled.ADDRESS_SHIFT,
                instruction.getAddress(), "Bad address.");
    }

    @Test
    void getAbsoluteAddress() {
        int pc = 0x00400000;
        assertEquals(0x00100000, instruction.getAbsoluteAddress(pc), "Bad absolute address.");
    }
}