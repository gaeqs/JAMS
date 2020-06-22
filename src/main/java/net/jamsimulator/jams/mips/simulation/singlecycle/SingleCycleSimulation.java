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

package net.jamsimulator.jams.mips.simulation.singlecycle;

import net.jamsimulator.jams.gui.util.Log;
import net.jamsimulator.jams.mips.architecture.SingleCycleArchitecture;
import net.jamsimulator.jams.mips.instruction.assembled.AssembledInstruction;
import net.jamsimulator.jams.mips.instruction.exception.InstructionNotFoundException;
import net.jamsimulator.jams.mips.instruction.execution.SingleCycleExecution;
import net.jamsimulator.jams.mips.instruction.set.InstructionSet;
import net.jamsimulator.jams.mips.memory.Memory;
import net.jamsimulator.jams.mips.register.Registers;
import net.jamsimulator.jams.mips.simulation.Simulation;
import net.jamsimulator.jams.mips.simulation.singlecycle.event.SingleCycleInstructionExecutionEvent;
import net.jamsimulator.jams.utils.StringUtils;

public class SingleCycleSimulation extends Simulation<SingleCycleArchitecture> {

	private Log log;

	public SingleCycleSimulation(SingleCycleArchitecture architecture, InstructionSet instructionSet, Registers registerSet, Memory memory, int instructionStackBottom) {
		super(architecture, instructionSet, registerSet, memory, instructionStackBottom);
	}

	public void setLog(Log log) {
		this.log = log;
	}

	@Override
	public void nextStep() {
		int pc = registerSet.getProgramCounter().getValue();

		if (pc > instructionStackBottom) {
			throw new InstructionNotFoundException("Dropped off bottom.");
		}

		//Fetch and Decode
		registerSet.getProgramCounter().setValue(pc + 4);
		AssembledInstruction instruction = fetch(pc);

		if (instruction == null) {
			int code = memory.getWord(pc);
			throw new InstructionNotFoundException("Couldn't decode instruction 0x" +
					StringUtils.addZeros(Integer.toHexString(code), 8) + ". (" + StringUtils.addZeros(Integer.toBinaryString(code), 32) + ")");
		}

		sendInstructionLog(instruction, pc);

		//Execute, Memory and Write

		SingleCycleExecution<?> execution = (SingleCycleExecution<?>)
				instruction.getBasicOrigin().generateExecution(this, instruction).orElse(null);

		//Send before event
		SingleCycleInstructionExecutionEvent.Before before = callEvent(
				new SingleCycleInstructionExecutionEvent.Before(this, pc, instruction, execution));
		if (before.isCancelled()) return;

		//Gets the modifies execution. This may be null.
		execution = before.getExecution().orElse(null);

		if (execution == null) {
			throw new InstructionNotFoundException("Couldn't decode instruction " +
					StringUtils.addZeros(Integer.toHexString(instruction.getCode()), 8) + ".");
		}

		execution.execute();


		callEvent(new SingleCycleInstructionExecutionEvent.After(this, pc, instruction, execution));
	}

	@Override
	public void executeAll() {
		while (registerSet.getProgramCounter().getValue() <= instructionStackBottom) {
			nextStep();
		}
	}


	private void sendInstructionLog(AssembledInstruction instruction, int pc) {
		String address = "0x" + StringUtils.addZeros(Integer.toHexString(pc), 8);
		String opCode = StringUtils.addZeros(Integer.toBinaryString(instruction.getOperationCode()), 6);
		String mnemonic = instruction.getBasicOrigin().getMnemonic();
		String code = "0x" + StringUtils.addZeros(Integer.toHexString(instruction.getCode()), 8);

		if (log == null) {
			System.out.println(address + "\t" + opCode + "\t" + mnemonic + " \t" + code);
		} else {
			log.println(address + "\t" + opCode + "\t" + mnemonic + " \t" + code);
		}
	}
}
