/*
 *  MIT License
 *
 *  Copyright (c) 2024 Gael Rial Costas
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

package net.jamsimulator.jams.mips.instruction.set

import net.jamsimulator.jams.manager.ResourceProvider
import net.jamsimulator.jams.mips.instruction.basic.defaults.*
import net.jamsimulator.jams.mips.instruction.pseudo.defaults.PseudoInstructionBI
import net.jamsimulator.jams.mips.instruction.pseudo.defaults.PseudoInstructionBL

class MIPS32r5InstructionSet(provider: ResourceProvider) : InstructionSet(provider, NAME) {

    init {
        basicInstructions.forEach { registerInstruction(it) }
        pseudoInstructions.forEach { registerInstruction(it) }
    }

    companion object {
        const val NAME = "MIPS32r5"

        val basicInstructions by lazy {
            val set = hashSetOf(
                InstructionAbsDouble(),
                InstructionAbsSingle(),
                InstructionAdd(),
                InstructionAddDouble(),
                InstructionAddSingle(),
                R5InstructionAddi(),
                InstructionAddiu(),
                InstructionAddu(),
                InstructionAnd(),
                InstructionAndi(),

                R5InstructionBC1F(),
                R5InstructionBC1T(),
                InstructionBeq(),
                InstructionBgez(),
                R5InstructionBgezal(),
                InstructionBgtz(),
                InstructionBlez(),
                InstructionBltz(),
                R5InstructionBltzal(),
                InstructionBne(),
                InstructionBreak()
            )

            R5CCondCondition.entries.forEach { set.add(R5InstructionCCondD(it)) }
            R5CCondCondition.entries.forEach { set.add(R5InstructionCCondS(it)) }


            set
        }

        val pseudoInstructions by lazy {
            hashSetOf(
                PseudoInstructionBL(),
                PseudoInstructionBI()
            )
        }
    }

}