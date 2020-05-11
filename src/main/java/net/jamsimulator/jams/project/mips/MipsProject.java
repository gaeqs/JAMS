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

package net.jamsimulator.jams.project.mips;

import net.jamsimulator.jams.mips.architecture.Architecture;
import net.jamsimulator.jams.mips.assembler.Assembler;
import net.jamsimulator.jams.mips.assembler.builder.AssemblerBuilder;
import net.jamsimulator.jams.mips.assembler.directive.set.DirectiveSet;
import net.jamsimulator.jams.mips.instruction.set.InstructionSet;
import net.jamsimulator.jams.mips.memory.builder.MemoryBuilder;
import net.jamsimulator.jams.mips.register.MIPS32Registers;
import net.jamsimulator.jams.mips.register.builder.RegistersBuilder;
import net.jamsimulator.jams.mips.simulation.Simulation;
import net.jamsimulator.jams.project.BasicProject;
import net.jamsimulator.jams.utils.Validate;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class MipsProject extends BasicProject {

	private final Architecture architecture;
	private final AssemblerBuilder assemblerBuilder;
	private final MemoryBuilder memoryBuilder;
	private final RegistersBuilder registersBuilder;
	private final DirectiveSet directiveSet;
	private final InstructionSet instructionSet;

	public MipsProject(String name, File folder, Architecture architecture, AssemblerBuilder assemblerBuilder, MemoryBuilder memoryBuilder,
					   RegistersBuilder registersBuilder, DirectiveSet directiveSet, InstructionSet instructionSet) {
		super(name, folder, false);
		Validate.notNull(name, "Name cannot be null!");
		Validate.notNull(folder, "Folder cannot be null!");
		Validate.isTrue(folder.exists(), "Folder " + folder.getName() + " must exist!");
		Validate.isTrue(folder.isDirectory(), "Folder must be a directory!");
		Validate.notNull(assemblerBuilder, "Assembler builder cannot be null!");
		Validate.notNull(memoryBuilder, "Memory builder cannot be null!");
		Validate.notNull(registersBuilder, "Registers builder cannot be null!");
		Validate.notNull(directiveSet, "Directive set cannot be null!");
		Validate.notNull(instructionSet, "Instruction set cannot be null!");

		this.architecture = architecture;
		this.assemblerBuilder = assemblerBuilder;
		this.memoryBuilder = memoryBuilder;
		this.registersBuilder = registersBuilder;
		this.directiveSet = directiveSet;
		this.instructionSet = instructionSet;

		loadData();
	}

	@Override
	public MipsProjectData getData() {
		return (MipsProjectData) super.getData();
	}

	public Architecture getArchitecture() {
		return architecture;
	}

	public AssemblerBuilder getAssemblerBuilder() {
		return assemblerBuilder;
	}

	public MemoryBuilder getMemoryBuilder() {
		return memoryBuilder;
	}

	public RegistersBuilder getRegistersBuilder() {
		return registersBuilder;
	}

	public DirectiveSet getDirectiveSet() {
		return directiveSet;
	}

	public InstructionSet getInstructionSet() {
		return instructionSet;
	}

	@Override
	public Simulation<?> assemble() throws IOException {
		Assembler assembler = assemblerBuilder.createAssembler(directiveSet, instructionSet,
				new MIPS32Registers(), memoryBuilder.createMemory());

		List<List<String>> files = new ArrayList<>();

		for (File target : getData().getFilesToAssemble().getFiles()) {
			files.add(Files.readAllLines(target.toPath()));
		}

		assembler.setData(files);
		assembler.compile();
		return assembler.createSimulation(architecture);
	}

	@Override
	public void onClose() {
		data.save();
	}


	@Override
	protected void loadData() {
		data = new MipsProjectData(this);
		data.load();
	}
}
