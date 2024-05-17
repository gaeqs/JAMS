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

package net.jamsimulator.jams.mips.instruction.pseudo.defaults

import net.jamsimulator.jams.mips.instruction.assembled.AssembledInstruction
import net.jamsimulator.jams.mips.instruction.basic.defaults.InstructionOri
import net.jamsimulator.jams.mips.instruction.basic.defaults.R5InstructionLui
import net.jamsimulator.jams.mips.instruction.pseudo.PseudoInstruction
import net.jamsimulator.jams.mips.instruction.set.InstructionSet
import net.jamsimulator.jams.mips.parameter.InstructionParameterTypes
import net.jamsimulator.jams.mips.parameter.ParameterType
import net.jamsimulator.jams.mips.parameter.parse.ParameterParseResult

class R5PseudoInstructionLiRI32 : PseudoInstruction(MNEMONIC, PARAMETER_TYPES) {

    companion object {
        const val MNEMONIC = "li"

        @JvmField
        val PARAMETER_TYPES = InstructionParameterTypes(ParameterType.REGISTER, ParameterType.SIGNED_32_BIT)
    }

    override fun getInstructionAmount(parameters: Array<String>) = 2

    override fun assemble(
        set: InstructionSet,
        address: Int,
        parameters: Array<ParameterParseResult>
    ): Array<AssembledInstruction> {
        val ins = instructions(
            set,
            R5InstructionLui::class.java,
            InstructionOri::class.java
        )

        val load = parameters[1].immediate
        val lui = parameters(AT, immediate(upper(load)))
        val ori = parameters(parameters[0], AT, immediate(lower(load)))

        return assemble(ins, lui, ori)
    }
}