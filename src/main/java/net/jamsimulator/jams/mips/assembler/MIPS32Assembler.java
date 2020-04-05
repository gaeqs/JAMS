package net.jamsimulator.jams.mips.assembler;

import javafx.stage.Popup;
import net.jamsimulator.jams.mips.assembler.directive.Directive;
import net.jamsimulator.jams.mips.assembler.directive.set.DirectiveSet;
import net.jamsimulator.jams.mips.assembler.exception.AssemblerException;
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
 * Represents a MIPS32 assembler.
 */
public class MIPS32Assembler implements Assembler {

	private DirectiveSet directiveSet;
	private InstructionSet instructionSet;
	private RegisterSet registerSet;
	private Memory memory;

	private AssemblerData assemblerData;
	private List<AssemblingFile> files;

	private List<String> convertToGlobalLabel;
	private Map<String, Integer> globalLabels;

	private AssemblingFile currentAssemblingFile;
	private boolean compiled;

	public MIPS32Assembler(DirectiveSet directiveSet, InstructionSet instructionSet, RegisterSet registerSet, Memory memory) {
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

		this.assemblerData = new AssemblerData(memory);

		this.compiled = false;
	}

	@Override
	public void setData(List<List<String>> data) {
		Validate.notNull(data, "The raw code cannot be null!");
		files.clear();
		data.forEach(target -> this.files.add(new AssemblingFile(target)));
	}

	@Override
	public void compile() {
		if (compiled) throw new IllegalStateException("A assembler can only compile once!");
		if (files == null) throw new IllegalStateException("Data not found.");
		if (assemblerData == null) throw new AssemblerException("assembler not initialized.");
		for (AssemblingFile file : files) {
			currentAssemblingFile = file;
			compileFile(file);
		}

		//Compile instructions
		for (AssemblingFile file : files) {
			for (DirectiveSnapshot snapshot : file.directiveSnapshots) {
				snapshot.compile(this, file);
			}
			for (InstructionSnapshot snapshot : file.instructionSnapshots) {
				snapshot.compile(this, file);
			}
		}
		compiled = true;
	}

	@Override
	public Simulation createSimulation() {
		if (!compiled) throw new IllegalStateException("The program is still not compiled!");
		Simulation simulation = new Simulation(instructionSet, registerSet.copy(), memory.copy());
		simulation.getRegisterSet().getProgramCounter().setValue(assemblerData.getFirstText());
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
	public AssemblerData getAssemblerData() {
		return assemblerData;
	}

	@Override
	public AssemblingFile getCurrentAssemblingFile() {
		return currentAssemblingFile;
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
	public InstructionSet getInstructionSet() {
		return instructionSet;
	}

	@Override
	public void setAsGlobalLabel(int executingLine, String label) {
		if (convertToGlobalLabel.contains(label)) return;
		convertToGlobalLabel.add(label);
		boolean found = false;
		for (AssemblingFile file : files) {
			if (file.labels.containsKey(label)) {
				if (found)
					throw new AssemblerException(executingLine, "The label " + label +
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
			throw new AssemblerException("The global label " + label + "  already exists.");
		for (AssemblingFile file : files) {
			if (file.labels.containsKey(label)) {
				throw new AssemblerException(executingLine, "The label " + label + " already exist as a local label.");
			}
		}

		globalLabels.put(label, value);
	}

	private void compileFile(AssemblingFile file) {
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

				int index = first.indexOf(':');
				if (index != first.length() - 1) {
					parts.add(0, first.substring(index + 1));
				}
				label = first.substring(0, index);

			} else label = null;

			//If empty, parse the label or move it to the next line, and continue
			if (parts.isEmpty()) {
				if (file.rawCode.size() == lineNumber + 1) {
					checkLabel(lineNumber, file, label, assemblerData.getCurrent());
				} else {
					file.rawCode.set(lineNumber + 1, label + ":" + file.rawCode.get(lineNumber + 1));
				}
				continue;
			}

			first = parts.get(0);
			parts.remove(0);

			//If starts with ".", then compile directive
			if (first.startsWith(".")) {
				labelAddress = executeDirective(lineNumber, file, line, first.substring(1), parts);
			}
			//Else compile instruction
			else {
				//Align data and label
				assemblerData.align(2);
				labelAddress = assemblerData.getCurrent();
				compileInstruction(lineNumber, file, first, parts);
			}

			if (label != null) {
				checkLabel(lineNumber, file, label, labelAddress);
			}
		}
	}

	private int executeDirective(int lineNumber, AssemblingFile file, String line, String name, List<String> parameters) {
		Optional<Directive> optional = directiveSet.getDirective(name);
		if (!optional.isPresent()) throw new AssemblerException(lineNumber, "Directive " + name + " not found!");
		String[] pArray = parameters.toArray(new String[0]);
		int start = optional.get().execute(lineNumber, line, pArray, this);

		DirectiveSnapshot snapshot = new DirectiveSnapshot(optional.get(), pArray, lineNumber, start);
		file.directiveSnapshots.add(snapshot);

		return start == -1 ? assemblerData.getCurrent() : start;
	}

	private void compileInstruction(int line, AssemblingFile file, String mnemonic, List<String> parameters) {
		List<ParameterType>[] types = new List[parameters.size()];

		int parameterIndex = 0;
		for (String parameter : parameters) {
			List<ParameterType> list = ParameterType.getCompatibleParameterTypes(parameter, registerSet);
			if (list.isEmpty()) throw new AssemblerException(line, "Bad parameter " + parameter);
			types[parameterIndex++] = list;
		}
		Optional<Instruction> optional = instructionSet.getBestCompatibleInstruction(mnemonic, types);
		if (!optional.isPresent())
			throw new AssemblerException(line, "Instruction " + mnemonic + " with the given parameters not found.\n"
					+ Arrays.toString(types));
		Instruction instruction = optional.get();

		file.instructionSnapshots.add(new InstructionSnapshot(instruction, parameters, line, assemblerData.getCurrent()));
		assemblerData.addCurrent(instruction instanceof PseudoInstruction ?
				((PseudoInstruction) instruction).getInstructionAmount(parameters.toArray(new String[0])) << 2 : 4);
	}


	private void checkLabel(int line, AssemblingFile file, String name, int address) {
		if (!LabelUtils.isLabelLegal(name))
			throw new AssemblerException(line, "Label " + name + " contains illegal characters.");

		if (globalLabels.containsKey(name))
			throw new AssemblerException(line, "Global label " + name + " already defined.");

		if (convertToGlobalLabel.contains(name)) {
			for (AssemblingFile otherFile : files) {
				if (otherFile.labels.containsKey(name))
					throw new AssemblerException(line, "Global label " + name + " already defined as a local label.");
			}
			globalLabels.put(name, address);
		} else {
			if (file.labels.containsKey(name))
				throw new AssemblerException(line, "Label " + name + " already defined.");
			file.labels.put(name, address);
		}
	}

	private List<String> sanityLine(AssemblingFile file, String line) {
		line = StringUtils.removeComments(line).trim();
		for (Map.Entry<String, String> entry : file.equivalents.entrySet()) {
			line = line.replace(entry.getKey(), entry.getValue());
		}
		if (line.isEmpty()) return null;
		List<String> parts = StringUtils.multiSplitIgnoreInsideString(line, " ", ",", "\t");
		parts.removeIf(String::isEmpty);
		return parts;
	}

}
