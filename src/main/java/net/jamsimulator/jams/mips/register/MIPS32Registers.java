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

package net.jamsimulator.jams.mips.register;

import java.util.Collections;
import java.util.Set;

/**
 * Represents a default MIPS32 {@link Register} set.
 */
public class MIPS32Registers extends Registers {

	public static final Set<Character> VALID_REGISTERS_START = Collections.singleton('$');

	/**
	 * Creates a default MIPS32 {@link Registers} set.
	 */
	public MIPS32Registers() {
		super(VALID_REGISTERS_START, null, null, null);
		loadPrincipalRegisters();
		loadCoprocessor0Registers();
		loadCoprocessor1Registers();
		loadEssentialRegisters();
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
		registers[id] = new Register(this, id, 0x7fffeffc, true, "sp", String.valueOf(id++));
		registers[id] = new Register(this, id, 0, true, "fp", String.valueOf(id++));
		registers[id] = new Register(this, id, "ra", String.valueOf(id));
	}

	protected void loadCoprocessor0Registers() {
		coprocessor0Registers[0] = new Register[4];
		coprocessor0Registers[0][0] = new COP0Register(this, 0, 0, "Index", "0");
		coprocessor0Registers[0][1] = new COP0Register(this, 0, 1, "MVPControl", "0.1");
		coprocessor0Registers[0][2] = new COP0Register(this, 0, 2, "MVPConf0", "0.2");
		coprocessor0Registers[0][3] = new COP0Register(this, 0, 3, "MVPConf1", "0.3");

		coprocessor0Registers[1] = new Register[8];
		coprocessor0Registers[1][0] = new COP0Register(this, 1, 0, "Random", "1");
		coprocessor0Registers[1][1] = new COP0Register(this, 1, 1, "VPEControl", "1.1");
		coprocessor0Registers[1][2] = new COP0Register(this, 1, 2, "VPEConf0", "1.2");
		coprocessor0Registers[1][3] = new COP0Register(this, 1, 3, "VPEConf1", "1.3");
		coprocessor0Registers[1][4] = new COP0Register(this, 1, 4, "YQMask", "1.4");
		coprocessor0Registers[1][5] = new COP0Register(this, 1, 5, "VPESchedule", "1.5");
		coprocessor0Registers[1][6] = new COP0Register(this, 1, 6, "VPEScheFBack", "1.6");
		coprocessor0Registers[1][7] = new COP0Register(this, 1, 7, "VPEOpt", "1.7");

		coprocessor0Registers[2] = new Register[8];
		coprocessor0Registers[2][0] = new COP0Register(this, 2, 0, "EntryLo0", "2");
		coprocessor0Registers[2][1] = new COP0Register(this, 2, 1, "TCStatus", "2.1");
		coprocessor0Registers[2][2] = new COP0Register(this, 2, 2, "TCBind", "2.2");
		coprocessor0Registers[2][3] = new COP0Register(this, 2, 3, "TCRestart", "2.3");
		coprocessor0Registers[2][4] = new COP0Register(this, 2, 4, "TCHalt", "2.4");
		coprocessor0Registers[2][5] = new COP0Register(this, 2, 5, "TCContext", "2.5");
		coprocessor0Registers[2][6] = new COP0Register(this, 2, 6, "TCSchedule", "2.6");
		coprocessor0Registers[2][7] = new COP0Register(this, 2, 7, "TCScheFBack", "2.7");

		coprocessor0Registers[3] = new Register[8];
		coprocessor0Registers[3][0] = new COP0Register(this, 3, 0, "EntryLo1", "3");
		coprocessor0Registers[3][7] = new COP0Register(this, 3, 7, "TCOpt", "3.7");


		coprocessor0Registers[4] = new Register[3];
		coprocessor0Registers[4][0] = new COP0Register(this, 4, 0, "Context", "4");
		coprocessor0Registers[4][1] = new COP0Register(this, 4, 1, "ContextConfig", "4.1");
		coprocessor0Registers[4][2] = new COP0Register(this, 4, 2, "UserLocal", "4.2");

		coprocessor0Registers[5] = new Register[8];
		coprocessor0Registers[5][0] = new COP0Register(this, 5, 0, "PageMask", "5");
		coprocessor0Registers[5][1] = new COP0Register(this, 5, 1, "PageGrain", "5.1");
		coprocessor0Registers[5][2] = new COP0Register(this, 5, 2, "SegCtl0", "5.2");
		coprocessor0Registers[5][3] = new COP0Register(this, 5, 3, "SegCtl1", "5.3");
		coprocessor0Registers[5][4] = new COP0Register(this, 5, 4, "SegCtl2", "5.4");
		coprocessor0Registers[5][5] = new COP0Register(this, 5, 5, "PWBase", "5.5");
		coprocessor0Registers[5][6] = new COP0Register(this, 5, 6, "PWField", "5.6");
		coprocessor0Registers[5][7] = new COP0Register(this, 5, 7, "PWSize", "5.7");

		coprocessor0Registers[6] = new Register[7];
		coprocessor0Registers[6][0] = new COP0Register(this, 6, 0, "Wired", "6");
		coprocessor0Registers[6][1] = new COP0Register(this, 6, 1, "SRSConf0", "6.1");
		coprocessor0Registers[6][2] = new COP0Register(this, 6, 2, "SRSConf1", "6.2");
		coprocessor0Registers[6][3] = new COP0Register(this, 6, 3, "SRSConf2", "6.3");
		coprocessor0Registers[6][4] = new COP0Register(this, 6, 4, "SRSConf3", "6.4");
		coprocessor0Registers[6][5] = new COP0Register(this, 6, 5, "SRSConf4", "6.5");
		coprocessor0Registers[6][6] = new COP0Register(this, 6, 6, "PWCtl", "6.6");

		coprocessor0Registers[7] = new Register[1];
		coprocessor0Registers[7][0] = new COP0Register(this, 7, 0, "HWREna", "7");

		coprocessor0Registers[8] = new Register[3];
		coprocessor0Registers[8][0] = new COP0Register(this, 8, 0, "BadVAddr", "8");
		coprocessor0Registers[8][1] = new COP0Register(this, 8, 1, "BadInstr", "8.1");
		coprocessor0Registers[8][2] = new COP0Register(this, 8, 2, "BadInstrP", "8.2");

		coprocessor0Registers[9] = new Register[1];
		coprocessor0Registers[9][0] = new COP0Register(this, 9, 0, "Count", "9");

		coprocessor0Registers[10] = new Register[7];
		coprocessor0Registers[10][0] = new COP0Register(this, 10, 0, "EntryHi", "10");
		coprocessor0Registers[10][4] = new COP0Register(this, 10, 4, "GuestCtl1", "10.4");
		coprocessor0Registers[10][5] = new COP0Register(this, 10, 5, "GuestCtl2", "10.5");
		coprocessor0Registers[10][6] = new COP0Register(this, 10, 6, "GuestCtl3", "10.6");

		coprocessor0Registers[11] = new Register[5];
		coprocessor0Registers[11][0] = new COP0Register(this, 11, 0, "Compare", "11");
		coprocessor0Registers[11][4] = new COP0Register(this, 11, 4, "GuestCtl0Ext", "11.4");


		coprocessor0Registers[12] = new Register[8];
		coprocessor0Registers[12][0] = new COP0Register(this, 12, 0, "Status", "12");
		coprocessor0Registers[12][1] = new COP0Register(this, 12, 1, "IntCtl", "12.1");
		coprocessor0Registers[12][2] = new COP0Register(this, 12, 2, "SRSCtl", "12.2");
		coprocessor0Registers[12][3] = new COP0Register(this, 12, 3, "SRSMap", "12.3");
		coprocessor0Registers[12][4] = new COP0Register(this, 12, 4, "View_IPL", "12.4");
		coprocessor0Registers[12][5] = new COP0Register(this, 12, 5, "SRSMap2", "12.5");
		coprocessor0Registers[12][6] = new COP0Register(this, 12, 6, "GuestCtl0", "12.6");
		coprocessor0Registers[12][7] = new COP0Register(this, 12, 7, "GTOffset", "12.7");

		coprocessor0Registers[13] = new Register[6];
		coprocessor0Registers[13][0] = new COP0Register(this, 13, 0, "Cause", "13");
		coprocessor0Registers[13][4] = new COP0Register(this, 13, 4, "View_RIPL", "13.4");
		coprocessor0Registers[13][5] = new COP0Register(this, 13, 5, "NestedExc", "13.5");

		coprocessor0Registers[14] = new Register[3];
		coprocessor0Registers[14][0] = new COP0Register(this, 14, 0, "EPC", "14");
		coprocessor0Registers[14][2] = new COP0Register(this, 14, 2, "NestedEPC", "14.2");

		coprocessor0Registers[15] = new Register[4];
		coprocessor0Registers[15][0] = new COP0Register(this, 15, 0, "PRId", "15");
		coprocessor0Registers[15][1] = new COP0Register(this, 15, 1, "EBase", "15.1");
		coprocessor0Registers[15][2] = new COP0Register(this, 15, 2, "CDMMBase", "15.2");
		coprocessor0Registers[15][3] = new COP0Register(this, 15, 3, "CMGCRBase", "15.3");


		coprocessor0Registers[16] = new Register[6];
		coprocessor0Registers[16][0] = new COP0Register(this, 16, 0, "Config", "16");
		coprocessor0Registers[16][1] = new COP0Register(this, 16, 1, "Config1", "16.1");
		coprocessor0Registers[16][2] = new COP0Register(this, 16, 2, "Config2", "16.2");
		coprocessor0Registers[16][3] = new COP0Register(this, 16, 3, "Config3", "16.3");
		coprocessor0Registers[16][4] = new COP0Register(this, 16, 4, "Config4", "16.4");
		coprocessor0Registers[16][5] = new COP0Register(this, 16, 5, "Config5", "16.5");

		coprocessor0Registers[17] = new Register[1];
		coprocessor0Registers[17][0] = new COP0Register(this, 17, 0, "LLAddr", "17");

		coprocessor0Registers[23] = new Register[7];
		coprocessor0Registers[23][0] = new COP0Register(this, 23, 0, "Debug", "23");
		coprocessor0Registers[23][1] = new COP0Register(this, 23, 1, "TraceControl", "23.1");
		coprocessor0Registers[23][2] = new COP0Register(this, 23, 2, "TraceControl2", "23.2");
		coprocessor0Registers[23][3] = new COP0Register(this, 23, 3, "UserTraceData1", "23.3");
		coprocessor0Registers[23][4] = new COP0Register(this, 23, 4, "TraceIBPC", "23.4");
		coprocessor0Registers[23][5] = new COP0Register(this, 23, 5, "TraceDBPC", "23.5");
		coprocessor0Registers[23][6] = new COP0Register(this, 23, 6, "Debug2", "23.6");

		coprocessor0Registers[24] = new Register[4];
		coprocessor0Registers[23][0] = new COP0Register(this, 23, 0, "DEPC", "23");
		coprocessor0Registers[23][2] = new COP0Register(this, 23, 2, "TraceContol3", "23.2");
		coprocessor0Registers[23][3] = new COP0Register(this, 23, 3, "UserTraceData2", "23.3");

		coprocessor0Registers[26] = new Register[1];
		coprocessor0Registers[26][0] = new COP0Register(this, 26, 0, "ErrCtl", "26");

		coprocessor0Registers[30] = new Register[1];
		coprocessor0Registers[30][0] = new COP0Register(this, 30, 0, "ErrorEPC", "30");

		coprocessor0Registers[31] = new Register[8];
		coprocessor0Registers[31][0] = new COP0Register(this, 31, 0, "DESAVE", "31");
		coprocessor0Registers[31][2] = new COP0Register(this, 31, 2, "KScratch1", "31.2");
		coprocessor0Registers[31][3] = new COP0Register(this, 31, 3, "KScratch2", "31.3");
		coprocessor0Registers[31][4] = new COP0Register(this, 31, 4, "KScratch3", "31.4");
		coprocessor0Registers[31][5] = new COP0Register(this, 31, 5, "KScratch4", "31.5");
		coprocessor0Registers[31][6] = new COP0Register(this, 31, 6, "KScratch5", "31.6");
		coprocessor0Registers[31][7] = new COP0Register(this, 31, 7, "KScratch6", "31.7");

		//coprocessor0Registers[8] = new Register(this, 8, "8");
		//coprocessor0Registers[12] = new Register(this, 12, 0x0000ff11, true, "12");
		//coprocessor0Registers[13] = new Register(this, 13, "13");
		//coprocessor0Registers[14] = new Register(this, 14, "14");
	}

	protected void loadCoprocessor1Registers() {
		for (int i = 0; i < 32; i++) {
			coprocessor1Registers[i] = new Register(this, i, "f" + i, String.valueOf(i));
		}
	}
}
