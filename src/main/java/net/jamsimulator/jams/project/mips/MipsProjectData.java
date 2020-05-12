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

import net.jamsimulator.jams.Jams;
import net.jamsimulator.jams.mips.architecture.Architecture;
import net.jamsimulator.jams.mips.assembler.builder.AssemblerBuilder;
import net.jamsimulator.jams.mips.directive.set.DirectiveSet;
import net.jamsimulator.jams.mips.instruction.set.InstructionSet;
import net.jamsimulator.jams.mips.memory.builder.MemoryBuilder;
import net.jamsimulator.jams.mips.register.builder.RegistersBuilder;
import net.jamsimulator.jams.project.ProjectData;
import net.jamsimulator.jams.utils.Validate;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.util.Optional;

public class MipsProjectData extends ProjectData {

	public static final String MIPS_CONFIGURATION_FILE = "mips_configuration.json";

	protected Architecture architecture;
	protected AssemblerBuilder assemblerBuilder;
	protected MemoryBuilder memoryBuilder;
	protected RegistersBuilder registersBuilder;
	protected DirectiveSet directiveSet;
	protected InstructionSet instructionSet;

	protected final MipsFilesToAssemble filesToAssemble;
	protected boolean loaded;

	public MipsProjectData(MipsProject project) {
		super(project.getFolder());
		filesToAssemble = new MipsFilesToAssemble(project);
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

	public MipsFilesToAssemble getFilesToAssemble() {
		return filesToAssemble;
	}

	@Override
	public void save() {
		try {
			filesToAssemble.save(folder);
			saveMipsConfiguration();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	@Override
	public void load() {
		if (loaded) return;
		loaded = true;
		try {
			loadMipsConfiguration();
			filesToAssemble.load(folder);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void load(Architecture architecture, AssemblerBuilder assemblerBuilder, MemoryBuilder memoryBuilder,
					 RegistersBuilder registersBuilder, DirectiveSet directiveSet, InstructionSet instructionSet) {
		if (loaded) return;
		loaded = true;

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

		try {
			filesToAssemble.load(folder);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void saveMipsConfiguration() throws IOException {
		JSONObject object = new JSONObject();
		object.put("architecture", architecture.getName());
		object.put("assembler", assemblerBuilder.getName());
		object.put("memory", memoryBuilder.getName());
		object.put("registers", registersBuilder.getName());
		object.put("directives", directiveSet.getName());
		object.put("instructions", instructionSet.getName());

		File file = new File(folder, MIPS_CONFIGURATION_FILE);
		Writer writer = new FileWriter(file);
		writer.write(object.toString(1));
		writer.close();
	}


	protected void loadMipsConfiguration() throws IOException {
		File file = new File(folder, MIPS_CONFIGURATION_FILE);
		if (!file.isFile()) return;

		String value = String.join("\n", Files.readAllLines(file.toPath()));
		JSONObject object = new JSONObject(value);

		//ARCHITECTURE
		Optional<Architecture> archOptional = object.has("architecture")
				? Jams.getArchitectureManager().get(object.getString("architecture"))
				: Optional.empty();
		architecture = archOptional.orElseGet(() -> Jams.getArchitectureManager().getDefault());

		//ASSEMBLER
		Optional<AssemblerBuilder> asOptional = object.has("assembler")
				? Jams.getAssemblerBuilderManager().get(object.getString("assembler"))
				: Optional.empty();
		assemblerBuilder = asOptional.orElseGet(() -> Jams.getAssemblerBuilderManager().getDefault());

		//MEMORY
		Optional<MemoryBuilder> memOptional = object.has("memory")
				? Jams.getMemoryBuilderManager().get(object.getString("memory"))
				: Optional.empty();
		memoryBuilder = memOptional.orElseGet(() -> Jams.getMemoryBuilderManager().getDefault());

		//REGISTERS
		Optional<RegistersBuilder> regOptional = object.has("registers")
				? Jams.getRegistersBuilderManager().get(object.getString("registers"))
				: Optional.empty();
		registersBuilder = regOptional.orElseGet(() -> Jams.getRegistersBuilderManager().getDefault());

		//DIRECTIVES
		Optional<DirectiveSet> dirOptional = object.has("directives")
				? Jams.getDirectiveSetManager().get(object.getString("directives"))
				: Optional.empty();
		directiveSet = dirOptional.orElseGet(() -> Jams.getDirectiveSetManager().getDefault());

		//INSTRUCTIONS
		Optional<InstructionSet> insOptional = object.has("instructions")
				? Jams.getInstructionSetManager().get(object.getString("instructions"))
				: Optional.empty();
		instructionSet = insOptional.orElseGet(() -> Jams.getInstructionSetManager().getDefault());
	}
}
