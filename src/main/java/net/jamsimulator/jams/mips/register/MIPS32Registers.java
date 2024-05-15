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

package net.jamsimulator.jams.mips.register;

import net.jamsimulator.jams.mips.instruction.set.InstructionSet;
import net.jamsimulator.jams.mips.instruction.set.MIPS32r5InstructionSet;
import net.jamsimulator.jams.mips.memory.MIPS32Memory;

import java.util.Collections;
import java.util.Set;

/**
 * Represents a default MIPS32 {@link Register} set.
 */
public class MIPS32Registers extends Registers {

    public static final Set<Character> VALID_REGISTERS_START = Collections.singleton('$');

    public static final int HI = 32;
    public static final int LO = 33;

    /**
     * Creates a default MIPS32 {@link Registers} set.
     */
    public MIPS32Registers(InstructionSet set) {
        super(
                VALID_REGISTERS_START,
                new Register[set instanceof MIPS32r5InstructionSet ? 34 : 32],
                null,
                new Register[set instanceof MIPS32r5InstructionSet ? 40 : 32]
        );
        loadPrincipalRegisters();
        loadCoprocessor0Registers();
        loadCoprocessor1Registers();
        loadEssentialRegisters();

        if (set instanceof MIPS32r5InstructionSet) {
            loadR5Registers();
            loadR5Coprocessor1Registers();
        }
    }

    protected void loadPrincipalRegisters() {
        int id = 0;
        registers[id] = new Register(this, 0, 0, false, "zero", String.valueOf(id++));
        registers[id] = new Register(this, id, "at", String.valueOf(id++));
        registers[id] = new Register(this, id, "v0", String.valueOf(id++));
        registers[id] = new Register(this, id, "v1", String.valueOf(id++));
        for (int i = 0; i < 4; i++)
            registers[id] = new Register(this, id, "a" + i, String.valueOf(id++));
        for (int i = 0; i < 8; i++)
            registers[id] = new Register(this, id, "t" + i, String.valueOf(id++));
        for (int i = 0; i < 8; i++)
            registers[id] = new Register(this, id, "s" + i, String.valueOf(id++));
        registers[id] = new Register(this, id, "t8", String.valueOf(id++));
        registers[id] = new Register(this, id, "t9", String.valueOf(id++));

        registers[id] = new Register(this, id, "k0", String.valueOf(id++));
        registers[id] = new Register(this, id, "k1", String.valueOf(id++));

        registers[id] = new Register(this, id, 0x10008000, true, "gp", String.valueOf(id++));
        registers[id] = new Register(this, id, MIPS32Memory.STACK, true, "sp", String.valueOf(id++));
        registers[id] = new Register(this, id, 0, true, "fp", String.valueOf(id++));
        registers[id] = new Register(this, id, "ra", String.valueOf(id));
    }

    protected void loadCoprocessor0Registers() {
        coprocessor0Registers[0] = new Register[4];
        coprocessor0Registers[0][0] = new COP0Register(this, 0, 0, 0xFFFFFFFF, "Index", "0");
        coprocessor0Registers[0][1] = new COP0Register(this, 0, 1, 0xFFFFFFFF, "MVPControl", "0.1");
        coprocessor0Registers[0][2] = new COP0Register(this, 0, 2, 0xFFFFFFFF, "MVPConf0", "0.2");
        coprocessor0Registers[0][3] = new COP0Register(this, 0, 3, 0xFFFFFFFF, "MVPConf1", "0.3");

        coprocessor0Registers[1] = new Register[8];
        coprocessor0Registers[1][0] = new COP0Register(this, 1, 0, 0xFFFFFFFF, "Random", "1");
        coprocessor0Registers[1][1] = new COP0Register(this, 1, 1, 0xFFFFFFFF, "VPEControl", "1.1");
        coprocessor0Registers[1][2] = new COP0Register(this, 1, 2, 0xFFFFFFFF, "VPEConf0", "1.2");
        coprocessor0Registers[1][3] = new COP0Register(this, 1, 3, 0xFFFFFFFF, "VPEConf1", "1.3");
        coprocessor0Registers[1][4] = new COP0Register(this, 1, 4, 0xFFFFFFFF, "YQMask", "1.4");
        coprocessor0Registers[1][5] = new COP0Register(this, 1, 5, 0xFFFFFFFF, "VPESchedule", "1.5");
        coprocessor0Registers[1][6] = new COP0Register(this, 1, 6, 0xFFFFFFFF, "VPEScheFBack", "1.6");
        coprocessor0Registers[1][7] = new COP0Register(this, 1, 7, 0xFFFFFFFF, "VPEOpt", "1.7");

        coprocessor0Registers[2] = new Register[8];
        coprocessor0Registers[2][0] = new COP0Register(this, 2, 0, 0xFFFFFFFF, "EntryLo0", "2");
        coprocessor0Registers[2][1] = new COP0Register(this, 2, 1, 0xFFFFFFFF, "TCStatus", "2.1");
        coprocessor0Registers[2][2] = new COP0Register(this, 2, 2, 0xFFFFFFFF, "TCBind", "2.2");
        coprocessor0Registers[2][3] = new COP0Register(this, 2, 3, 0xFFFFFFFF, "TCRestart", "2.3");
        coprocessor0Registers[2][4] = new COP0Register(this, 2, 4, 0xFFFFFFFF, "TCHalt", "2.4");
        coprocessor0Registers[2][5] = new COP0Register(this, 2, 5, 0xFFFFFFFF, "TCContext", "2.5");
        coprocessor0Registers[2][6] = new COP0Register(this, 2, 6, 0xFFFFFFFF, "TCSchedule", "2.6");
        coprocessor0Registers[2][7] = new COP0Register(this, 2, 7, 0xFFFFFFFF, "TCScheFBack", "2.7");

        coprocessor0Registers[3] = new Register[8];
        coprocessor0Registers[3][0] = new COP0Register(this, 3, 0, 0xFFFFFFFF, "EntryLo1", "3");
        coprocessor0Registers[3][7] = new COP0Register(this, 3, 7, 0xFFFFFFFF, "TCOpt", "3.7");


        coprocessor0Registers[4] = new Register[3];
        coprocessor0Registers[4][0] = new COP0Register(this, 4, 0, 0xFFFFFFFF, "Context", "4");
        coprocessor0Registers[4][1] = new COP0Register(this, 4, 1, 0xFFFFFFFF, "ContextConfig", "4.1");
        coprocessor0Registers[4][2] = new COP0Register(this, 4, 2, 0xFFFFFFFF, "UserLocal", "4.2");

        coprocessor0Registers[5] = new Register[8];
        coprocessor0Registers[5][0] = new COP0Register(this, 5, 0, 0xFFFFFFFF, "PageMask", "5");
        coprocessor0Registers[5][1] = new COP0Register(this, 5, 1, 0xFFFFFFFF, "PageGrain", "5.1");
        coprocessor0Registers[5][2] = new COP0Register(this, 5, 2, 0xFFFFFFFF, "SegCtl0", "5.2");
        coprocessor0Registers[5][3] = new COP0Register(this, 5, 3, 0xFFFFFFFF, "SegCtl1", "5.3");
        coprocessor0Registers[5][4] = new COP0Register(this, 5, 4, 0xFFFFFFFF, "SegCtl2", "5.4");
        coprocessor0Registers[5][5] = new COP0Register(this, 5, 5, 0xFFFFFFFF, "PWBase", "5.5");
        coprocessor0Registers[5][6] = new COP0Register(this, 5, 6, 0xFFFFFFFF, "PWField", "5.6");
        coprocessor0Registers[5][7] = new COP0Register(this, 5, 7, 0xFFFFFFFF, "PWSize", "5.7");

        coprocessor0Registers[6] = new Register[7];
        coprocessor0Registers[6][0] = new COP0Register(this, 6, 0, 0xFFFFFFFF, "Wired", "6");
        coprocessor0Registers[6][1] = new COP0Register(this, 6, 1, 0xFFFFFFFF, "SRSConf0", "6.1");
        coprocessor0Registers[6][2] = new COP0Register(this, 6, 2, 0xFFFFFFFF, "SRSConf1", "6.2");
        coprocessor0Registers[6][3] = new COP0Register(this, 6, 3, 0xFFFFFFFF, "SRSConf2", "6.3");
        coprocessor0Registers[6][4] = new COP0Register(this, 6, 4, 0xFFFFFFFF, "SRSConf3", "6.4");
        coprocessor0Registers[6][5] = new COP0Register(this, 6, 5, 0xFFFFFFFF, "SRSConf4", "6.5");
        coprocessor0Registers[6][6] = new COP0Register(this, 6, 6, 0xFFFFFFFF, "PWCtl", "6.6");

        coprocessor0Registers[7] = new Register[1];
        coprocessor0Registers[7][0] = new COP0Register(this, 7, 0, 0xFFFFFFFF, "HWREna", "7");

        coprocessor0Registers[8] = new Register[3];
        coprocessor0Registers[8][0] = new COP0Register(this, 8, 0, 0xFFFFFFFF, "BadVAddr", "8");
        coprocessor0Registers[8][1] = new COP0Register(this, 8, 1, 0xFFFFFFFF, "BadInstr", "8.1");
        coprocessor0Registers[8][2] = new COP0Register(this, 8, 2, 0xFFFFFFFF, "BadInstrP", "8.2");

        coprocessor0Registers[9] = new Register[1];
        coprocessor0Registers[9][0] = new COP0Register(this, 9, 0, 0xFFFFFFFF, "Count", "9");

        coprocessor0Registers[10] = new Register[7];
        coprocessor0Registers[10][0] = new COP0Register(this, 10, 0, 0xFFFFFFFF, "EntryHi", "10");
        coprocessor0Registers[10][4] = new COP0Register(this, 10, 4, 0xFFFFFFFF, "GuestCtl1", "10.4");
        coprocessor0Registers[10][5] = new COP0Register(this, 10, 5, 0xFFFFFFFF, "GuestCtl2", "10.5");
        coprocessor0Registers[10][6] = new COP0Register(this, 10, 6, 0xFFFFFFFF, "GuestCtl3", "10.6");

        coprocessor0Registers[11] = new Register[5];
        coprocessor0Registers[11][0] = new COP0Register(this, 11, 0, 0xFFFFFFFF, "Compare", "11");
        coprocessor0Registers[11][4] = new COP0Register(this, 11, 4, 0xFFFFFFFF, "GuestCtl0Ext", "11.4");


        coprocessor0Registers[12] = new Register[8];
        coprocessor0Registers[12][0] = new COP0StatusRegister(this, 12, 0, 0x00000311, 0xf85bff17, "Status", "12");
        coprocessor0Registers[12][1] = new COP0Register(this, 12, 1, 0x00000020, 0xFFFFFFFF, "IntCtl", "12.1");
        coprocessor0Registers[12][2] = new COP0Register(this, 12, 2, 0xFFFFFFFF, "SRSCtl", "12.2");
        coprocessor0Registers[12][3] = new COP0Register(this, 12, 3, 0xFFFFFFFF, "SRSMap", "12.3");
        coprocessor0Registers[12][4] = new COP0Register(this, 12, 4, 0xFFFFFFFF, "View_IPL", "12.4");
        coprocessor0Registers[12][5] = new COP0Register(this, 12, 5, 0xFFFFFFFF, "SRSMap2", "12.5");
        coprocessor0Registers[12][6] = new COP0Register(this, 12, 6, 0xFFFFFFFF, "GuestCtl0", "12.6");
        coprocessor0Registers[12][7] = new COP0Register(this, 12, 7, 0xFFFFFFFF, "GTOffset", "12.7");

        coprocessor0Registers[13] = new Register[6];
        coprocessor0Registers[13][0] = new COP0Register(this, 13, 0, 0x00800000, 0xFFFFFFFF, "Cause", "13");
        coprocessor0Registers[13][4] = new COP0Register(this, 13, 4, 0xFFFFFFFF, "View_RIPL", "13.4");
        coprocessor0Registers[13][5] = new COP0Register(this, 13, 5, 0xFFFFFFFF, "NestedExc", "13.5");

        coprocessor0Registers[14] = new Register[3];
        coprocessor0Registers[14][0] = new COP0Register(this, 14, 0, 0xFFFFFFFF, "EPC", "14");
        coprocessor0Registers[14][2] = new COP0Register(this, 14, 2, 0xFFFFFFFF, "NestedEPC", "14.2");

        coprocessor0Registers[15] = new Register[4];
        coprocessor0Registers[15][0] = new COP0Register(this, 15, 0, 0xFFFFFFFF, "PRId", "15");
        coprocessor0Registers[15][1] = new COP0Register(this, 15, 1, 0x80000000, 0x3ffff000, "EBase", "15.1");
        coprocessor0Registers[15][2] = new COP0Register(this, 15, 2, 0xFFFFFFFF, "CDMMBase", "15.2");
        coprocessor0Registers[15][3] = new COP0Register(this, 15, 3, 0xFFFFFFFF, "CMGCRBase", "15.3");


        coprocessor0Registers[16] = new Register[6];
        coprocessor0Registers[16][0] = new COP0Register(this, 16, 0, 0x80000000, 0x7FFFFFFF, "Config", "16");
        coprocessor0Registers[16][1] = new COP0Register(this, 16, 1, 0x80000000, 0x7FFFFFFF, "Config1", "16.1");
        coprocessor0Registers[16][2] = new COP0Register(this, 16, 2, 0x80000000, 0x7FFFFFFF, "Config2", "16.2");
        coprocessor0Registers[16][3] = new COP0Register(this, 16, 3, 0x80000020, 0x00000000, "Config3", "16.3");
        coprocessor0Registers[16][4] = new COP0Register(this, 16, 4, 0x80000000, 0x7FFFFFFF, "Config4", "16.4");
        coprocessor0Registers[16][5] = new COP0Register(this, 16, 5, 0x00000000, 0x68000320, "Config5", "16.5");

        coprocessor0Registers[17] = new Register[1];
        coprocessor0Registers[17][0] = new COP0Register(this, 17, 0, 0xFFFFFFFF, "LLAddr", "17");

        coprocessor0Registers[23] = new Register[7];
        coprocessor0Registers[23][0] = new COP0Register(this, 23, 0, 0xFFFFFFFF, "Debug", "23");
        coprocessor0Registers[23][1] = new COP0Register(this, 23, 1, 0xFFFFFFFF, "TraceControl", "23.1");
        coprocessor0Registers[23][2] = new COP0Register(this, 23, 2, 0xFFFFFFFF, "TraceControl2", "23.2");
        coprocessor0Registers[23][3] = new COP0Register(this, 23, 3, 0xFFFFFFFF, "UserTraceData1", "23.3");
        coprocessor0Registers[23][4] = new COP0Register(this, 23, 4, 0xFFFFFFFF, "TraceIBPC", "23.4");
        coprocessor0Registers[23][5] = new COP0Register(this, 23, 5, 0xFFFFFFFF, "TraceDBPC", "23.5");
        coprocessor0Registers[23][6] = new COP0Register(this, 23, 6, 0xFFFFFFFF, "Debug2", "23.6");

        coprocessor0Registers[24] = new Register[4];
        coprocessor0Registers[24][0] = new COP0Register(this, 23, 0, 0xFFFFFFFF, "DEPC", "23");
        coprocessor0Registers[24][2] = new COP0Register(this, 23, 2, 0xFFFFFFFF, "TraceContol3", "23.2");
        coprocessor0Registers[24][3] = new COP0Register(this, 23, 3, 0xFFFFFFFF, "UserTraceData2", "23.3");

        coprocessor0Registers[26] = new Register[1];
        coprocessor0Registers[26][0] = new COP0Register(this, 26, 0, 0xFFFFFFFF, "ErrCtl", "26");

        coprocessor0Registers[30] = new Register[1];
        coprocessor0Registers[30][0] = new COP0Register(this, 30, 0, 0xFFFFFFFF, "ErrorEPC", "30");

        coprocessor0Registers[31] = new Register[8];
        coprocessor0Registers[31][0] = new COP0Register(this, 31, 0, 0xFFFFFFFF, "DESAVE", "31");
        coprocessor0Registers[31][2] = new COP0Register(this, 31, 2, 0xFFFFFFFF, "KScratch1", "31.2");
        coprocessor0Registers[31][3] = new COP0Register(this, 31, 3, 0xFFFFFFFF, "KScratch2", "31.3");
        coprocessor0Registers[31][4] = new COP0Register(this, 31, 4, 0xFFFFFFFF, "KScratch3", "31.4");
        coprocessor0Registers[31][5] = new COP0Register(this, 31, 5, 0xFFFFFFFF, "KScratch4", "31.5");
        coprocessor0Registers[31][6] = new COP0Register(this, 31, 6, 0xFFFFFFFF, "KScratch5", "31.6");
        coprocessor0Registers[31][7] = new COP0Register(this, 31, 7, 0xFFFFFFFF, "KScratch6", "31.7");
    }

    protected void loadCoprocessor1Registers() {
        for (int i = 0; i < 32; i++) {
            coprocessor1Registers[i] = new Register(this, i, "f" + i, String.valueOf(i));
        }
    }

    protected void loadR5Registers() {
        registers[HI] = new Register(this, HI, "hi", String.valueOf(HI));
        registers[LO] = new Register(this, LO, "lo", String.valueOf(LO));
    }

    protected void loadR5Coprocessor1Registers() {
        for (int i = 0; i < 8; i++) {
            coprocessor1Registers[i + 32] = new Register(this, 32 + i, "fc" + i, String.valueOf(i));
        }
    }
}
