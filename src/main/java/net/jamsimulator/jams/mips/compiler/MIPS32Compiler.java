package net.jamsimulator.jams.mips.compiler;

import net.jamsimulator.jams.mips.compiler.directive.Directive;
import net.jamsimulator.jams.mips.compiler.directive.set.DirectiveSet;
import net.jamsimulator.jams.mips.compiler.exception.CompilerException;
import net.jamsimulator.jams.mips.instruction.Instruction;
import net.jamsimulator.jams.mips.instruction.pseudo.PseudoInstruction;
import net.jamsimulator.jams.mips.instruction.set.InstructionSet;
import net.jamsimulator.jams.mips.memory.Memory;
import net.jamsimulator.jams.mips.parameter.ParameterType;
import net.jamsimulator.jams.mips.register.RegisterSet;
import net.jamsimulator.jams.mips.simulation.Simulation;
import net.jamsimulator.jams.utils.LabelUtils;
import net.jamsimulator.jams.utils.StringUtils;
import net.jamsimulator.jams.utils.Validate;

import java.util.*;

/**
 * Represents a MIPS32 compiler.
 */
public class MIPS32Compiler implements Compiler {

	private DirectiveSet directiveSet;
	private InstructionSet instructionSet;
	private RegisterSet registerSet;
	private Memory memory;

	private CompilerData compilerData;
	private List<CompilingFile> files;

	private List<String> convertToGlobalLabel;
	private Map<String, Integer> globalLabels;

	private CompilingFile currentCompilingFile;
	private boolean compiled;

	public MIPS32Compiler(DirectiveSet directiveSet, InstructionSet instructionSet, RegisterSet registerSet, Memory memory,
						  int firstTextAddress, int firstDataAddress, int firstKernelTextAddress, int firstKernelDataAddress,
						  int firstExternAddress) {
		Validate.notNull(directiveSet, "The directive set cannot be null!");
		Validate.notNull(instructionSet, "The instruction set cannot be null!");

		Validate.notNull(registerSet, "The register set cannot be null!");
		Validate.notNull(memory, "The memory cannot be null!");
		this.directiveSet = directiveSet;
		this.instructionSet = instructionSet;
		this.registerSet = registerSet;
		this.memory = memory;

		this.convertToGlobalLabel = new ArrayList<>();
		this.globalLabels = new HashMap<>();
		this.files = new ArrayList<>();

		this.compilerData = new CompilerData(firstTextAddress, firstDataAddress, firstKernelTextAddress, firstKernelDataAddress, firstExternAddress);

		this.compiled = false;
	}

	@Override
	public void setData(List<List<String>> data) {
		Validate.notNull(data, "The raw code cannot be null!");
		files.clear();
		data.forEach(target -> this.files.add(new CompilingFile(target)));
	}

	@Override
	public void compile() {
		if (compiled) throw new IllegalStateException("A compiler can only compile once!");
		if (files == null) throw new IllegalStateException("Data not found.");
		if (compilerData == null) throw new CompilerException("Compiler not initialized.");
		for (CompilingFile file : files) {
			currentCompilingFile = file;
			compileFile(file);
		}

		//Compile instructions
		for (CompilingFile file : files) {
			for (InstructionSnapshot snapshot : file.snapshots) {
				snapshot.compile(this, file);
			}
		}
		compiled = true;
	}

	@Override
	public Simulation createSimulation() {
		if (!compiled) throw new IllegalStateException("The program is still not compiled!");
		Simulation simulation = new Simulation(instructionSet, registerSet.copy(), memory.copy());
		simulation.getRegisterSet().getProgramCounter().setValue(compilerData.getFirstText());
		return simulation;
	}

	@Override
	public RegisterSet getRegisterSet() {
		return registerSet;
	}

	@Override
	public Memory getMemory() {
		return memory;
	}

	@Override
	public CompilerData getCompilerData() {
		return compilerData;
	}

	@Override
	public CompilingFile getCurrentCompilingFile() {
		return currentCompilingFile;
	}

	@Override
	public Map<String, Integer> getGlobalLabels() {
		return globalLabels;
	}

	@Override
	public boolean isCompiled() {
		return compiled;
	}

	@Override
	public void setAsGlobalLabel(int executingLine, String label) {
		if (convertToGlobalLabel.contains(label)) return;
		convertToGlobalLabel.add(label);
		boolean found = false;
		for (CompilingFile file : files) {
			if (file.labels.containsKey(label)) {
				if (found)
					throw new CompilerException(executingLine, "The label " + label +
							" cannot be converted to a global label because there are two or more files with the same label.");
				found = true;
				globalLabels.put(label, file.labels.get(label));
				file.labels.remove(label);
			}
		}
	}

	@Override
	public void addGlobalLabel(int executingLine, String label, int value) {
		if (!convertToGlobalLabel.contains(label))
			convertToGlobalLabel.add(label);

		if (globalLabels.containsKey(label))
			throw new CompilerException("The global label " + label + "  already exists.");
		for (CompilingFile file : files) {
			if (file.labels.containsKey(label)) {
				throw new CompilerException(executingLine, "The label " + label + " already exist as a local label.");
			}
		}

		globalLabels.put(label, value);
	}

	private void compileFile(CompilingFile file) {
		String line;
		List<String> parts;
		String first;
		String label;
		int labelAddress;
		for (int lineNumber = 0; lineNumber < file.rawCode.size(); lineNumber++) {
			line = file.rawCode.get(lineNumber);
			parts = sanityLine(file, line);
			if (parts == null) continue;

			//Check label
			first = parts.get(0);
			if (first.contains(":")) {
				parts.remove(0);
				label = first;
			} else label = null;

			//If empty, continue
			if (parts.isEmpty()) continue;

			first = parts.get(0);
			parts.remove(0);

			//If starts with ".", then compile directive
			if (first.startsWith(".")) {
				labelAddress = executeDirective(lineNumber, line, first.substring(1), parts);
			}
			//Else compile instruction
			else {
				//Align data and label
				compilerData.align(2);
				labelAddress = compilerData.getCurrent();
				compileInstruction(lineNumber, file, first, parts);
			}

			if (label != null) {
				int index = first.indexOf(':');
				if (index != first.length() - 1) {
					parts.add(0, first.substring(index + 1));
				}
				checkLabel(lineNumber, file, first.substring(0, index), labelAddress);
			}
		}
	}

	private int executeDirective(int lineNumber, String line, String name, List<String> parameters) {
		Optional<Directive> optional = directiveSet.getDirective(name);
		if (!optional.isPresent()) throw new CompilerException(lineNumber, "Directive " + name + " not found!");
		int start = optional.get().execute(lineNumber, line, parameters.toArray(new String[0]), this);
		return start == -1 ? compilerData.getCurrent() : start;
	}

	private void compileInstruction(int line, CompilingFile file, String mnemonic, List<String> parameters) {
		List<ParameterType>[] types = new List[parameters.size()];

		int parameterIndex = 0;
		for (String parameter : parameters) {
			List<ParameterType> list = ParameterType.getCompatibleParameterTypes(parameter, registerSet);
			if (list.isEmpty()) throw new CompilerException(line, "Bad parameter " + parameter);
			types[parameterIndex++] = list;
		}
		Optional<Instruction> optional = instructionSet.getBestCompatibleInstruction(mnemonic, types);
		if (!optional.isPresent())
			throw new CompilerException(line, "Instruction " + mnemonic + " with the given parameters not found.");
		Instruction instruction = optional.get();

		file.snapshots.add(new InstructionSnapshot(instruction, parameters, line, compilerData.getCurrent()));
		compilerData.addCurrent(instruction instanceof PseudoInstruction ?
				((PseudoInstruction) instruction).getInstructionAmount(parameters.toArray(new String[0])) << 2 : 4);
	}


	private void checkLabel(int line, CompilingFile file, String name, int address) {
		if (!LabelUtils.isLabelLegal(name))
			throw new CompilerException(line, "Label " + name + " contains illegal characters.");

		if (globalLabels.containsKey(name))
			throw new CompilerException(line, "Global label " + name + " already defined.");

		if (convertToGlobalLabel.contains(name)) {
			for (CompilingFile otherFile : files) {
				if (otherFile.labels.containsKey(name))
					throw new CompilerException(line, "Global label " + name + " already defined as a local label.");
			}
			globalLabels.put(name, address);
		} else {
			if (file.labels.containsKey(name))
				throw new CompilerException(line, "Label " + name + " already defined.");
			file.labels.put(name, address);
		}
	}

	private List<String> sanityLine(CompilingFile file, String line) {
		line = StringUtils.removeComments(line).trim();
		for (Map.Entry<String, String> entry : file.equivalents.entrySet()) {
			line = line.replace(entry.getKey(), entry.getValue());
		}
		if (line.isBlank()) return null;
		List<String> parts = StringUtils.multiSplitIgnoreInsideString(line, " ", ",");
		parts.removeIf(String::isBlank);
		return parts;
	}

}