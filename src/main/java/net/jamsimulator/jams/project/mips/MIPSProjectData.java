/*
 *  MIT License
 *
 *  Copyright (c) 2021 Gael Rial Costas
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

package net.jamsimulator.jams.project.mips;

import net.jamsimulator.jams.configuration.Configuration;
import net.jamsimulator.jams.gui.editor.code.indexing.EditorIndex;
import net.jamsimulator.jams.gui.editor.code.indexing.global.ProjectGlobalIndex;
import net.jamsimulator.jams.gui.mips.editor.index.MIPSEditorIndex;
import net.jamsimulator.jams.manager.Manager;
import net.jamsimulator.jams.mips.assembler.builder.AssemblerBuilder;
import net.jamsimulator.jams.mips.directive.set.DirectiveSet;
import net.jamsimulator.jams.mips.instruction.set.InstructionSet;
import net.jamsimulator.jams.mips.register.builder.RegistersBuilder;
import net.jamsimulator.jams.project.GlobalIndexHolder;
import net.jamsimulator.jams.project.ProjectData;
import net.jamsimulator.jams.project.mips.configuration.MIPSSimulationConfiguration;
import net.jamsimulator.jams.project.mips.event.MIPSSimulationConfigurationAddEvent;
import net.jamsimulator.jams.project.mips.event.MIPSSimulationConfigurationRemoveEvent;
import net.jamsimulator.jams.project.mips.event.SelectedMIPSSimulationConfigurationChangeEvent;
import net.jamsimulator.jams.utils.Validate;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class MIPSProjectData extends ProjectData implements GlobalIndexHolder {

    public static final String GLOBAL_INDEX_FILE_NAME = "files_to_assemble.json";

    public static final String NODE_ASSEMBLER = "mips.assembler";
    public static final String NODE_REGISTERS = "mips.registers";
    public static final String NODE_DIRECTIVES = "mips.directives";
    public static final String NODE_INSTRUCTIONS = "mips.instructions";
    public static final String NODE_CONFIGURATIONS = "mips.configurations";
    public static final String NODE_SELECTED_CONFIGURATION = "mips.selected_configuration";
    protected final ProjectGlobalIndex globalIndex;
    protected Set<MIPSSimulationConfiguration> configurations;
    protected MIPSSimulationConfiguration selectedConfiguration;
    protected AssemblerBuilder assemblerBuilder;
    protected RegistersBuilder registersBuilder;
    protected DirectiveSet directiveSet;
    protected InstructionSet instructionSet;

    public MIPSProjectData(MIPSProject project) {
        super(MIPSProjectType.INSTANCE, project.getFolder());
        globalIndex = new ProjectGlobalIndex(project) {
            @Override
            protected EditorIndex generateIndexForFile(File file) {
                return new MIPSEditorIndex(getProject());
            }
        };
    }

    public Set<MIPSSimulationConfiguration> getConfigurations() {
        return Collections.unmodifiableSet(configurations);
    }

    public boolean addConfiguration(MIPSSimulationConfiguration configuration) {
        Validate.notNull(configuration, "Configuration is null!");
        if (configurations.stream().anyMatch(target -> target.getName().equals(configuration.getName())))
            return false;

        var before = callEvent(new MIPSSimulationConfigurationAddEvent.Before(this, configuration));
        if (before.isCancelled()) return false;

        boolean result = configurations.add(configuration);
        if (result) {
            callEvent(new MIPSSimulationConfigurationAddEvent.After(this, configuration));
            if (selectedConfiguration == null) {
                setSelectedConfiguration(configuration.getName());
            }
        }

        return result;
    }

    public boolean removeConfiguration(String name) {
        Validate.notNull(name, "Name cannot be null!");
        MIPSSimulationConfiguration configuration = configurations.stream()
                .filter(target -> target.getName().equals(name)).findAny().orElse(null);
        if (configuration == null) return false;
        var before = callEvent(new MIPSSimulationConfigurationRemoveEvent.Before(this, configuration));
        if (before.isCancelled()) return false;
        boolean result = configurations.remove(configuration);
        if (result) {
            callEvent(new MIPSSimulationConfigurationRemoveEvent.After(this, configuration));
        }
        if (configuration == selectedConfiguration) {
            setSelectedConfiguration(configurations.stream().findAny().map(MIPSSimulationConfiguration::getName).orElse(null));
        }
        return result;
    }

    public Optional<MIPSSimulationConfiguration> getSelectedConfiguration() {
        return Optional.ofNullable(selectedConfiguration);
    }

    public boolean setSelectedConfiguration(String name) {
        MIPSSimulationConfiguration configuration = name == null ? null : configurations.stream()
                .filter(target -> target.getName().equals(name)).findAny().orElse(null);
        if (configuration == null && name != null) return false;
        if (configuration == selectedConfiguration) return false;

        MIPSSimulationConfiguration old = selectedConfiguration;
        SelectedMIPSSimulationConfigurationChangeEvent.Before before =
                callEvent(new SelectedMIPSSimulationConfigurationChangeEvent.Before(this, old, configuration));
        if (before.isCancelled()) return false;
        selectedConfiguration = configuration;

        callEvent(new SelectedMIPSSimulationConfigurationChangeEvent.After(this, old, configuration));

        return true;
    }

    public AssemblerBuilder getAssemblerBuilder() {
        return assemblerBuilder;
    }

    public void setAssemblerBuilder(AssemblerBuilder assemblerBuilder) {
        this.assemblerBuilder = assemblerBuilder;
    }

    public RegistersBuilder getRegistersBuilder() {
        return registersBuilder;
    }

    public void setRegistersBuilder(RegistersBuilder registersBuilder) {
        this.registersBuilder = registersBuilder;
    }

    public DirectiveSet getDirectiveSet() {
        return directiveSet;
    }

    public void setDirectiveSet(DirectiveSet directiveSet) {
        this.directiveSet = directiveSet;
    }

    public InstructionSet getInstructionSet() {
        return instructionSet;
    }

    public void setInstructionSet(InstructionSet instructionSet) {
        this.instructionSet = instructionSet;
    }

    @Override
    public ProjectGlobalIndex getGlobalIndex() {
        return globalIndex;
    }

    @Override
    public void save() {
        try {
            var file = new File(metadataFolder, GLOBAL_INDEX_FILE_NAME);
            globalIndex.saveFiles(file);
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
            var file = new File(metadataFolder, GLOBAL_INDEX_FILE_NAME);
            globalIndex.loadFiles(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void load(Set<MIPSSimulationConfiguration> configurations, String selected,
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
            var file = new File(metadataFolder, GLOBAL_INDEX_FILE_NAME);
            globalIndex.loadFiles(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveMipsConfiguration() {
        data.convertAndSet(NODE_ASSEMBLER, assemblerBuilder, AssemblerBuilder.class);
        data.convertAndSet(NODE_REGISTERS, registersBuilder, RegistersBuilder.class);
        data.convertAndSet(NODE_DIRECTIVES, directiveSet, DirectiveSet.class);
        data.convertAndSet(NODE_INSTRUCTIONS, instructionSet, InstructionSet.class);
        data.remove(NODE_CONFIGURATIONS);

        configurations.forEach(config -> config.save(data, NODE_CONFIGURATIONS));
        data.set(NODE_SELECTED_CONFIGURATION, selectedConfiguration == null ? null : selectedConfiguration.getName());
    }


    protected void loadMipsConfiguration() {
        assemblerBuilder = data.getAndConvertOrElse(NODE_ASSEMBLER, AssemblerBuilder.class, Manager.ofD(AssemblerBuilder.class).getDefault());
        registersBuilder = data.getAndConvertOrElse(NODE_REGISTERS, RegistersBuilder.class, Manager.ofD(RegistersBuilder.class).getDefault());
        directiveSet = data.getAndConvertOrElse(NODE_DIRECTIVES, DirectiveSet.class, Manager.ofD(DirectiveSet.class).getDefault());
        instructionSet = data.getAndConvertOrElse(NODE_INSTRUCTIONS, InstructionSet.class, Manager.ofD(InstructionSet.class).getDefault());

        configurations = new HashSet<>();
        Optional<Configuration> configOptional = data.get(NODE_CONFIGURATIONS);
        if (configOptional.isPresent()) {
            Configuration config = configOptional.get();

            config.getAll(false).forEach((name, data) -> {
                if (!(data instanceof Configuration)) return;
                configurations.add(new MIPSSimulationConfiguration(name, (Configuration) data));
            });
        }

        String selectedConfig = data.getString(NODE_SELECTED_CONFIGURATION).orElse(null);
        selectedConfiguration = configurations.stream().filter(target -> target.getName().equals(selectedConfig)).findAny().orElse(null);

        // Let's try to get a default configuration
        if (selectedConfiguration == null) {
            selectedConfiguration = configurations.stream().findAny().orElse(null);
        }
    }
}
