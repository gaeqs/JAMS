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

package net.jamsimulator.jams.mips.instruction.set;

import net.jamsimulator.jams.mips.instruction.basic.BasicInstruction;
import net.jamsimulator.jams.mips.instruction.basic.defaults.*;
import net.jamsimulator.jams.mips.instruction.pseudo.PseudoInstruction;
import net.jamsimulator.jams.mips.instruction.pseudo.defaults.*;

import java.util.HashSet;
import java.util.Set;

class MIPS32DefaultInstructions {

	static Set<BasicInstruction<?>> basicInstructions = new HashSet<>();
	static Set<PseudoInstruction> pseudoInstructions = new HashSet<>();

	static {
		basicInstructions.add(new InstructionAbsDouble());
		basicInstructions.add(new InstructionAbsSingle());
		basicInstructions.add(new InstructionAdd());
		basicInstructions.add(new InstructionAddDouble());
		basicInstructions.add(new InstructionAddSingle());
		basicInstructions.add(new InstructionAddiu());
		basicInstructions.add(new InstructionAddiupc());
		basicInstructions.add(new InstructionAddu());
		basicInstructions.add(new InstructionAlign());
		basicInstructions.add(new InstructionAluipc());
		basicInstructions.add(new InstructionAnd());
		basicInstructions.add(new InstructionAndi());
		basicInstructions.add(new InstructionAui());
		basicInstructions.add(new InstructionAuipc());
		basicInstructions.add(new InstructionBal());
		basicInstructions.add(new InstructionBalc());
		basicInstructions.add(new InstructionBc());
		basicInstructions.add(new InstructionBc1eqz());
		basicInstructions.add(new InstructionBc1nez());
		basicInstructions.add(new InstructionBeq());
		basicInstructions.add(new InstructionBgez());
		basicInstructions.add(new InstructionBlezalc());
		basicInstructions.add(new InstructionBgezalc());
		basicInstructions.add(new InstructionBgtzalc());
		basicInstructions.add(new InstructionBltzalc());
		basicInstructions.add(new InstructionBeqzalc());
		basicInstructions.add(new InstructionBnezalc());

		basicInstructions.add(new InstructionBlezc());
		basicInstructions.add(new InstructionBgezc());

		basicInstructions.add(new InstructionDiv());
		basicInstructions.add(new InstructionMod());
		basicInstructions.add(new InstructionDivu());
		basicInstructions.add(new InstructionModu());
		basicInstructions.add(new InstructionDivSingle());
		basicInstructions.add(new InstructionDivDouble());

		basicInstructions.add(new InstructionLw());

		basicInstructions.add(new InstructionMuh());
		basicInstructions.add(new InstructionMuhu());
		basicInstructions.add(new InstructionMul());
		basicInstructions.add(new InstructionMulu());
		basicInstructions.add(new InstructionMulSingle());
		basicInstructions.add(new InstructionMulDouble());

		basicInstructions.add(new InstructionSub());
		basicInstructions.add(new InstructionSubSingle());
		basicInstructions.add(new InstructionSubDouble());

		basicInstructions.add(new InstructionSw());

		basicInstructions.add(new InstructionSyscall());

		//PSEUDO
		pseudoInstructions.add(new PseudoInstructionBI());
		pseudoInstructions.add(new PseudoInstructionBL());
		pseudoInstructions.add(new PseudoInstructionBalL());
		pseudoInstructions.add(new PseudoInstructionBalcL());
		pseudoInstructions.add(new PseudoInstructionBcL());
		pseudoInstructions.add(new PseudoInstructionBc1eqzL());
		pseudoInstructions.add(new PseudoInstructionBc1nezL());
		pseudoInstructions.add(new PseudoInstructionBeqRRL());
		pseudoInstructions.add(new PseudoInstructionBgezRL());
		pseudoInstructions.add(new PseudoInstructionBeqzalcRL());
		pseudoInstructions.add(new PseudoInstructionBgezalcRL());
		pseudoInstructions.add(new PseudoInstructionBgtzalcRL());
		pseudoInstructions.add(new PseudoInstructionBlezalcRL());
		pseudoInstructions.add(new PseudoInstructionBltzalcRL());
		pseudoInstructions.add(new PseudoInstructionBnezalcRL());

		pseudoInstructions.add(new PseudoInstructionLuiRI());
		pseudoInstructions.add(new PseudoInstructionLwRL());
		pseudoInstructions.add(new PseudoInstructionLwRLr());
		pseudoInstructions.add(new PseudoInstructionLwRLs());
		pseudoInstructions.add(new PseudoInstructionLwRLsr());

		pseudoInstructions.add(new PseudoInstructionSwRL());
		pseudoInstructions.add(new PseudoInstructionSwRLr());
		pseudoInstructions.add(new PseudoInstructionSwRLs());
		pseudoInstructions.add(new PseudoInstructionSwRLsr());
	}

}
