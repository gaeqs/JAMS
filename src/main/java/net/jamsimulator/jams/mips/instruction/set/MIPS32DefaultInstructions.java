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
		basicInstructions.add(new InstructionBgec());
		basicInstructions.add(new InstructionBgtzc());
		basicInstructions.add(new InstructionBltzc());
		basicInstructions.add(new InstructionBltc());
		basicInstructions.add(new InstructionBgeuc());
		basicInstructions.add(new InstructionBltuc());
		basicInstructions.add(new InstructionBeqc());
		basicInstructions.add(new InstructionBnec());
		basicInstructions.add(new InstructionBeqzc());
		basicInstructions.add(new InstructionBnezc());
		basicInstructions.add(new InstructionBgtz());
		basicInstructions.add(new InstructionBitswap());
		basicInstructions.add(new InstructionBlez());
		basicInstructions.add(new InstructionBltz());
		basicInstructions.add(new InstructionBne());
		basicInstructions.add(new InstructionBovc());
		basicInstructions.add(new InstructionBnvc());
		basicInstructions.add(new InstructionBreak());
		basicInstructions.add(new InstructionCeilLDouble());
		basicInstructions.add(new InstructionCeilLSingle());
		basicInstructions.add(new InstructionCeilWDouble());
		basicInstructions.add(new InstructionCeilWSingle());
		basicInstructions.add(new InstructionClo());
		basicInstructions.add(new InstructionClz());
		for (FloatCondition condition : FloatCondition.values()) {
			basicInstructions.add(new InstructionCmpCondnSingle(condition));
			basicInstructions.add(new InstructionCmpCondnDouble(condition));
		}

		for (FmtNumbers to : FmtNumbers.values()) {
			for (FmtNumbers from : FmtNumbers.values()) {
				if (to == from) continue;
				basicInstructions.add(new InstructionCvtNN(to, from));
			}
		}

		basicInstructions.add(new InstructionDiv());
		basicInstructions.add(new InstructionMod());
		basicInstructions.add(new InstructionDivu());
		basicInstructions.add(new InstructionModu());
		basicInstructions.add(new InstructionDivSingle());
		basicInstructions.add(new InstructionDivDouble());

		basicInstructions.add(new InstructionEret());

		basicInstructions.add(new InstructionJ());
		basicInstructions.add(new InstructionJal());
		basicInstructions.add(new InstructionJalr());

		basicInstructions.add(new InstructionLb());
		basicInstructions.add(new InstructionLw());

		basicInstructions.add(new InstructionMfc0());
		basicInstructions.add(new InstructionMfc1());

		basicInstructions.add(new InstructionMtc0());
		basicInstructions.add(new InstructionMtc1());

		basicInstructions.add(new InstructionMuh());
		basicInstructions.add(new InstructionMuhu());
		basicInstructions.add(new InstructionMul());
		basicInstructions.add(new InstructionMulu());
		basicInstructions.add(new InstructionMulSingle());
		basicInstructions.add(new InstructionMulDouble());

		basicInstructions.add(new InstructionOr());
		basicInstructions.add(new InstructionOri());

		basicInstructions.add(new InstructionSll());
		basicInstructions.add(new InstructionSllv());

		basicInstructions.add(new InstructionSlt());
		basicInstructions.add(new InstructionSlti());
		basicInstructions.add(new InstructionSltiu());
		basicInstructions.add(new InstructionSltu());

		basicInstructions.add(new InstructionSrl());
		basicInstructions.add(new InstructionSrlv());

		basicInstructions.add(new InstructionSub());
		basicInstructions.add(new InstructionSubSingle());
		basicInstructions.add(new InstructionSubDouble());
		basicInstructions.add(new InstructionSubu());

		basicInstructions.add(new InstructionSb());
		basicInstructions.add(new InstructionSw());

		basicInstructions.add(new InstructionSyscall());

		//PSEUDO
		pseudoInstructions.add(new PseudoInstructionBI());
		pseudoInstructions.add(new PseudoInstructionBL());
		pseudoInstructions.add(new PseudoInstructionBc1eqzL());
		pseudoInstructions.add(new PseudoInstructionBc1nezL());

		//BRANCHES
		pseudoInstructions.add(new PseudoInstructionBranchL(InstructionBal.MNEMONIC));
		pseudoInstructions.add(new PseudoInstructionBranchL(InstructionBalc.MNEMONIC));
		pseudoInstructions.add(new PseudoInstructionBranchL(InstructionBc.MNEMONIC));

		pseudoInstructions.add(new PseudoInstructionBranchRRL(InstructionBeq.MNEMONIC));
		pseudoInstructions.add(new PseudoInstructionBranchRRL(InstructionBgez.MNEMONIC));
		pseudoInstructions.add(new PseudoInstructionBranchRRL(InstructionBeqzalc.MNEMONIC));
		pseudoInstructions.add(new PseudoInstructionBranchRRL(InstructionBgezalc.MNEMONIC));
		pseudoInstructions.add(new PseudoInstructionBranchRRL(InstructionBgtzalc.MNEMONIC));
		pseudoInstructions.add(new PseudoInstructionBranchRRL(InstructionBlezalc.MNEMONIC));
		pseudoInstructions.add(new PseudoInstructionBranchRRL(InstructionBltzalc.MNEMONIC));
		pseudoInstructions.add(new PseudoInstructionBranchRRL(InstructionBnezalc.MNEMONIC));
		pseudoInstructions.add(new PseudoInstructionBranchRRL(InstructionBgec.MNEMONIC));
		pseudoInstructions.add(new PseudoInstructionBranchRRL(InstructionBltc.MNEMONIC));
		pseudoInstructions.add(new PseudoInstructionBranchRRL(InstructionBgeuc.MNEMONIC));
		pseudoInstructions.add(new PseudoInstructionBranchRRL(InstructionBltuc.MNEMONIC));
		pseudoInstructions.add(new PseudoInstructionBranchRRL(InstructionBeqc.MNEMONIC));
		pseudoInstructions.add(new PseudoInstructionBranchRRL(InstructionBnec.MNEMONIC));
		pseudoInstructions.add(new PseudoInstructionBranchRRL(InstructionBne.MNEMONIC));
		pseudoInstructions.add(new PseudoInstructionBranchRRL(InstructionBovc.MNEMONIC));
		pseudoInstructions.add(new PseudoInstructionBranchRRL(InstructionBnvc.MNEMONIC));
		pseudoInstructions.add(new PseudoInstructionBranchRL(InstructionBlezc.MNEMONIC));
		pseudoInstructions.add(new PseudoInstructionBranchRL(InstructionBgezc.MNEMONIC));
		pseudoInstructions.add(new PseudoInstructionBranchRL(InstructionBgtzc.MNEMONIC));
		pseudoInstructions.add(new PseudoInstructionBranchRL(InstructionBltzc.MNEMONIC));
		pseudoInstructions.add(new PseudoInstructionBranchRL(InstructionBgtz.MNEMONIC));
		pseudoInstructions.add(new PseudoInstructionBranchRL21(InstructionBeqzc.MNEMONIC));
		pseudoInstructions.add(new PseudoInstructionBranchRL21(InstructionBnezc.MNEMONIC));

		pseudoInstructions.add(new PseudoInstructionDivRRI16());
		pseudoInstructions.add(new PseudoInstructionDivRRI32());

		pseudoInstructions.add(new PseudoInstructionJalL());
		pseudoInstructions.add(new PseudoInstructionJL());
		pseudoInstructions.add(new PseudoInstructionJrR());

		pseudoInstructions.add(new PseudoInstructionLaRL());
		pseudoInstructions.add(new PseudoInstructionLiRI16());
		pseudoInstructions.add(new PseudoInstructionLiRI32());
		pseudoInstructions.add(new PseudoInstructionLuiRI());
		pseudoInstructions.add(new PseudoInstructionLifRF());

		pseudoInstructions.add(new PseudoInstructionLbRL());
		pseudoInstructions.add(new PseudoInstructionLbRLr());
		pseudoInstructions.add(new PseudoInstructionLbRLs());
		pseudoInstructions.add(new PseudoInstructionLbRLsr());

		pseudoInstructions.add(new PseudoInstructionLwRL());
		pseudoInstructions.add(new PseudoInstructionLwRLr());
		pseudoInstructions.add(new PseudoInstructionLwRLs());
		pseudoInstructions.add(new PseudoInstructionLwRLsr());

		pseudoInstructions.add(new PseudoInstructionMoveRR());

		pseudoInstructions.add(new PseudoInstructionMulRRI16());
		pseudoInstructions.add(new PseudoInstructionMulRRI32());

		pseudoInstructions.add(new PseudoInstructionNop());

		pseudoInstructions.add(new PseudoInstructionSbRL());
		pseudoInstructions.add(new PseudoInstructionSbRLr());
		pseudoInstructions.add(new PseudoInstructionSbRLs());
		pseudoInstructions.add(new PseudoInstructionSbRLsr());

		pseudoInstructions.add(new PseudoInstructionSgeiRRI());
		pseudoInstructions.add(new PseudoInstructionSgeiuRRI());
		pseudoInstructions.add(new PseudoInstructionSgeRRR());
		pseudoInstructions.add(new PseudoInstructionSgeuRRR());

		pseudoInstructions.add(new PseudoInstructionSgtiRRI());
		pseudoInstructions.add(new PseudoInstructionSgtiuRRI());
		pseudoInstructions.add(new PseudoInstructionSgtRRR());
		pseudoInstructions.add(new PseudoInstructionSgtuRRR());

		pseudoInstructions.add(new PseudoInstructionSleiRRI());
		pseudoInstructions.add(new PseudoInstructionSleiuRRI());
		pseudoInstructions.add(new PseudoInstructionSleRRR());
		pseudoInstructions.add(new PseudoInstructionSleuRRR());

		pseudoInstructions.add(new PseudoInstructionSwRL());
		pseudoInstructions.add(new PseudoInstructionSwRLr());
		pseudoInstructions.add(new PseudoInstructionSwRLs());
		pseudoInstructions.add(new PseudoInstructionSwRLsr());
	}

}
