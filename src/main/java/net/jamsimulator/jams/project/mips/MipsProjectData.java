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
import net.jamsimulator.jams.configuration.Configuration;
import net.jamsimulator.jams.mips.assembler.builder.AssemblerBuilder;
import net.jamsimulator.jams.mips.directive.set.DirectiveSet;
import net.jamsimulator.jams.mips.instruction.set.InstructionSet;
import net.jamsimulator.jams.mips.register.builder.RegistersBuilder;
import net.jamsimulator.jams.project.ProjectData;
import net.jamsimulator.jams.project.mips.event.MipsSimulationConfigurationAddEvent;
import net.jamsimulator.jams.project.mips.event.MipsSimulationConfigurationRemoveEvent;
import net.jamsimulator.jams.project.mips.event.SelectedMipsSimulationConfigurationChangeEvent;
import net.jamsimulator.jams.utils.Validate;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class MipsProjectData extends ProjectData {

	protected Set<MipsSimulationConfiguration> configurations;
	protected MipsSimulationConfiguration selectedConfiguration;

	protected AssemblerBuilder assemblerBuilder;
	protected RegistersBuilder registersBuilder;
	protected DirectiveSet directiveSet;
	protected InstructionSet instructionSet;

	protected final MIPSFilesToAssemble filesToAssemble;

	public MipsProjectData(MipsProject project) {
		super(project.getFolder());
		filesToAssemble = new MIPSFilesToAssemble(project);
	}

	public Set<MipsSimulationConfiguration> getConfigurations() {
		return Collections.unmodifiableSet(configurations);
	}

	public boolean addConfiguration(MipsSimulationConfiguration configuration) {
		Validate.notNull(configuration, "Configuration is null!");
		if (configurations.stream().anyMatch(target -> target.getName().equals(configuration.getName())))
			return false;

		MipsSimulationConfigurationAddEvent.Before before = callEvent(new MipsSimulationConfigurationAddEvent.Before(this, configuration));
		if (before.isCancelled()) return false;

		boolean result = configurations.add(configuration);
		if (result) {
			callEvent(new MipsSimulationConfigurationAddEvent.After(this, configuration));
		}

		return result;
	}

	public boolean removeConfiguration(String name) {
		Validate.notNull(name, "Name cannot be null!");
		MipsSimulationConfiguration configuration = configurations.stream()
				.filter(target -> target.getName().equals(name)).findAny().orElse(null);
		if (configuration == null) return false;
		MipsSimulationConfigurationRemoveEvent.Before before = callEvent(new MipsSimulationConfigurationRemoveEvent.Before(this, configuration));
		if (before.isCancelled()) return false;
		boolean result = configurations.remove(configuration);
		if (result) {
			callEvent(new MipsSimulationConfigurationRemoveEvent.After(this, configuration));
		}
		if (configuration == selectedConfiguration) {
			setSelectedConfiguration(null);
		}
		return result;
	}

	public Optional<MipsSimulationConfiguration> getSelectedConfiguration() {
		return Optional.ofNullable(selectedConfiguration);
	}

	public boolean setSelectedConfiguration(String name) {
		MipsSimulationConfiguration configuration = name == null ? null : configurations.stream()
				.filter(target -> target.getName().equals(name)).findAny().orElse(null);
		if (configuration == null && name != null) return false;
		if (configuration == selectedConfiguration) return false;

		MipsSimulationConfiguration old = selectedConfiguration;
		SelectedMipsSimulationConfigurationChangeEvent.Before before =
				callEvent(new SelectedMipsSimulationConfigurationChangeEvent.Before(this, old, configuration));
		if (before.isCancelled()) return false;
		selectedConfiguration = configuration;

		callEvent(new SelectedMipsSimulationConfigurationChangeEvent.After(this, old, configuration));

		return true;
	}

	public AssemblerBuilder getAssemblerBuilder() {
		return assemblerBuilder;
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

	public MIPSFilesToAssemble getFilesToAssemble() {
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
		super.save();
	}

	@Override
	public void load() {
		if (loaded) return;
		super.load();
		try {
			loadMipsConfiguration();
			filesToAssemble.load(folder);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void load(Set<MipsSimulationConfiguration> configurations, String selected,
					 AssemblerBuilder assemblerBuilder, RegistersBuilder registersBuilder,
					 DirectiveSet directiveSet, InstructionSet instructionSet) {
		super.load();
		if (loaded) return;
		loaded = true;

		Validate.notNull(assemblerBuilder, "Assembler builder cannot be null!");
		Validate.notNull(configurations, "Memory builder cannot be null!");
		Validate.notNull(registersBuilder, "Registers builder cannot be null!");
		Validate.notNull(directiveSet, "Directive set cannot be null!");
		Validate.notNull(instructionSet, "Instruction set cannot be null!");

		this.configurations = new HashSet<>(configurations);
		this.assemblerBuilder = assemblerBuilder;
		this.registersBuilder = registersBuilder;
		this.directiveSet = directiveSet;
		this.instructionSet = instructionSet;

		this.selectedConfiguration = selected == null ? null : configurations.stream()
				.filter(target -> target.getName().equals(selected)).findAny().orElse(null);

		try {
			filesToAssemble.load(folder);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void saveMipsConfiguration() {
		data.set("mips.assembler", assemblerBuilder.getName());
		data.set("mips.registers", registersBuilder.getName());
		data.set("mips.directives", directiveSet.getName());
		data.set("mips.instructions", instructionSet.getName());
		data.remove("mips.configurations");

		configurations.forEach(config -> config.save(data, "mips.configurations"));
		data.set("mips.selectedConfiguration", selectedConfiguration == null ? null : selectedConfiguration.getName());
	}


	protected void loadMipsConfiguration() {
		//ASSEMBLER
		Optional<AssemblerBuilder> asOptional = data.getString("mips.assembler").flatMap(Jams.getAssemblerBuilderManager()::get);
		assemblerBuilder = asOptional.orElseGet(() -> Jams.getAssemblerBuilderManager().getDefault());

		//REGISTERS
		Optional<RegistersBuilder> regOptional = data.getString("mips.registers").flatMap(Jams.getRegistersBuilderManager()::get);
		registersBuilder = regOptional.orElseGet(() -> Jams.getRegistersBuilderManager().getDefault());

		//DIRECTIVES
		Optional<DirectiveSet> dirOptional = data.getString("mips.directives").flatMap(Jams.getDirectiveSetManager()::get);
		directiveSet = dirOptional.orElseGet(() -> Jams.getDirectiveSetManager().getDefault());

		//INSTRUCTIONS
		Optional<InstructionSet> insOptional = data.getString("mips.instructions").flatMap(Jams.getInstructionSetManager()::get);
		instructionSet = insOptional.orElseGet(() -> Jams.getInstructionSetManager().getDefault());

		configurations = new HashSet<>();

		Optional<Configuration> configOptional = data.get("mips.configurations");
		if (configOptional.isPresent()) {
			Configuration config = configOptional.get();

			config.getAll(false).forEach((name, data) -> {
				if (!(data instanceof Configuration)) return;
				configurations.add(new MipsSimulationConfiguration(name, (Configuration) data));
			});
		}

		String selectedConfig = data.getString("mips.selectedConfiguration").orElse(null);
		selectedConfiguration = configurations.stream().filter(target -> target.getName().equals(selectedConfig)).findAny().orElse(null);

	}
}
