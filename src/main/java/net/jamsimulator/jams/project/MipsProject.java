package net.jamsimulator.jams.project;

import net.jamsimulator.jams.mips.assembler.Assembler;
import net.jamsimulator.jams.mips.assembler.builder.AssemblerBuilder;
import net.jamsimulator.jams.mips.assembler.directive.set.DirectiveSet;
import net.jamsimulator.jams.mips.instruction.set.InstructionSet;
import net.jamsimulator.jams.mips.memory.builder.MemoryBuilder;
import net.jamsimulator.jams.mips.register.MIPS32RegisterSet;
import net.jamsimulator.jams.mips.simulation.Simulation;
import net.jamsimulator.jams.utils.Validate;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class MipsProject implements Project {

    private final String name;
    private final File folder;

    private final AssemblerBuilder assemblerBuilder;
    private final MemoryBuilder memoryBuilder;
    private final DirectiveSet directiveSet;
    private final InstructionSet instructionSet;

    private final List<File> filesToAssemble;

    public MipsProject(String name, File folder, AssemblerBuilder assemblerBuilder, MemoryBuilder memoryBuilder,
                       DirectiveSet directiveSet, InstructionSet instructionSet) {
        Validate.notNull(name, "Name cannot be null!");
        Validate.notNull(folder, "Folder cannot be null!");
        Validate.isTrue(folder.exists(), "Folder " + folder.getName() + " must exist!");
        Validate.isTrue(folder.isDirectory(), "Folder must be a directory!");
        Validate.notNull(assemblerBuilder, "Assembler builder cannot be null!");
        Validate.notNull(memoryBuilder, "Memory builder cannot be null!");
        Validate.notNull(directiveSet, "Directive set cannot be null!");
        Validate.notNull(instructionSet, "Instruction set cannot be null!");

        this.name = name;
        this.folder = folder;
        this.assemblerBuilder = assemblerBuilder;
        this.memoryBuilder = memoryBuilder;
        this.directiveSet = directiveSet;
        this.instructionSet = instructionSet;

        filesToAssemble = new ArrayList<>();
    }


    @Override
    public String getName() {
        return name;
    }

    public File getFolder() {
        return folder;
    }

    @Override
    public List<File> getFilesToAssemble() {
        return filesToAssemble;
    }

    public AssemblerBuilder getAssemblerBuilder() {
        return assemblerBuilder;
    }

    public MemoryBuilder getMemoryBuilder() {
        return memoryBuilder;
    }

    public DirectiveSet getDirectiveSet() {
        return directiveSet;
    }

    public InstructionSet getInstructionSet() {
        return instructionSet;
    }

    @Override
    public Simulation assemble() throws IOException {
        Assembler assembler = assemblerBuilder.createAssembler(directiveSet, instructionSet,
                new MIPS32RegisterSet(), memoryBuilder.createMemory());

        List<List<String>> files = new ArrayList<>();

        for (File target : filesToAssemble) {
            files.add(Files.readAllLines(target.toPath()));
        }

        assembler.setData(files);
        assembler.compile();
        return assembler.createSimulation();
    }
}
