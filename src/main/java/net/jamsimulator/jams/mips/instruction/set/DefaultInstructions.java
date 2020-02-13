package net.jamsimulator.jams.mips.instruction.set;

import net.jamsimulator.jams.mips.instruction.basic.BasicInstruction;
import net.jamsimulator.jams.mips.instruction.basic.defaults.*;
import net.jamsimulator.jams.mips.instruction.pseudo.PseudoInstruction;
import net.jamsimulator.jams.mips.instruction.pseudo.defaults.*;

import java.util.HashSet;
import java.util.Set;

class DefaultInstructions {

	static Set<BasicInstruction> basicInstructions = new HashSet<>();
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

		pseudoInstructions.add(new PseudoInstructionSwRL());
	}

}
