package net.jamsimulator.jams.mips.assembler;

import net.jamsimulator.jams.mips.assembler.exception.AssemblerException;
import net.jamsimulator.jams.utils.LabelUtils;
import net.jamsimulator.jams.utils.StringUtils;

import java.util.*;

/**
 * Represents a MIPS32 assembly file being assembled.
 */
public class MIPS32AssemblingFile {

	private final List<String> lines;
	private final MIPS32Assembler assembler;

	private final List<InstructionSnapshot> instructions;
	private final List<DirectiveSnapshot> directives;
	private final Map<String, String> equivalents;

	private final Map<String, Integer> labels;
	private final Set<String> convertToGlobalLabel;


	public MIPS32AssemblingFile(String rawData, MIPS32Assembler assembler) {
		this(Arrays.asList(rawData.split("([\\n\\r])+")), assembler);
	}

	public MIPS32AssemblingFile(List<String> lines, MIPS32Assembler assembler) {
		this.lines = lines;
		this.assembler = assembler;

		this.instructions = new LinkedList<>();
		this.directives = new LinkedList<>();
		this.equivalents = new HashMap<>();

		this.labels = new HashMap<>();
		this.convertToGlobalLabel = new HashSet<>();
	}

	/**
	 * Returns the {@link MIPS32Assembler} assembling this file.
	 *
	 * @return the {@link MIPS32Assembler}.
	 */
	public MIPS32Assembler getAssembler() {
		return assembler;
	}

	/**
	 * Returns the raw code of this file. This list is modifiable,
	 * so directives can modify, add or remove code if this hasn't been scanned.
	 *
	 * @return the raw code, as a modifiable list.
	 */
	public List<String> getRawCode() {
		return lines;
	}

	/**
	 * Adds an equivalent. Equivalents are replaced on the code to its value.
	 *
	 * @param key   the key to replace.
	 * @param value the replacement.
	 */
	public void addEquivalent(String key, String value) {
		equivalents.put(key, value);
	}

	/**
	 * Sets the given label as a global label.
	 *
	 * @param executingLine the line executing this command.
	 * @param label         the label.
	 */
	public void setAsGlobalLabel(int executingLine, String label) {
		convertToGlobalLabel.add(label);
		if (labels.containsKey(label)) {
			assembler.addGlobalLabel(executingLine, label, labels.remove(label));
		}
	}

	/**
	 * Returns the address that matches the given local label, if present.
	 *
	 * @param label the label.
	 * @return the address, if present.
	 */
	public OptionalInt getLocalLabelAddress(String label) {
		return labels.containsKey(label) ? OptionalInt.of(labels.get(label)) : OptionalInt.empty();
	}

	/**
	 * Executes the method {@link #getLocalLabelAddress(String)}. If the address is not found,
	 * the address is searched on the global labels.
	 *
	 * @param label the label.
	 * @return the address, if present.
	 */
	public OptionalInt getLabelAddress(String label) {
		OptionalInt optional = getLocalLabelAddress(label);
		if (!optional.isPresent()) optional = assembler.getGlobalLabelAddress(label);
		return optional;
	}

	/**
	 * Executes the first step of the assembly process: the scanning.
	 * In this step some directives are executed and labels are registered.
	 */
	public void scan() {
		int index = 0;
		for (String line : lines) {
			scanLine(index, line);
			index++;
		}
	}

	/**
	 * Executes the second step of the assembly process: the instruction assembly.
	 * In this step instructions are assembled into machine code.
	 */
	public void assembleInstructions() {
		instructions.forEach(target -> target.assemble(this));
	}

	/**
	 * Executes the third step of the assembly process: the label-required directive execution.
	 * In this step executes directives that requires labels.
	 */
	public void executeLabelRequiredDirectives() {
		directives.forEach(target -> target.executeLabelRequiredSteps(this));
	}

	private void scanLine(int index, String line) {
		MIPS32AssemblerData data = assembler.getAssemblerData();
		line = sanityLine(line);

		int labelIndex = line.indexOf(':');
		String label = null;
		int labelAddress = data.getCurrent();
		if (labelIndex != -1) {
			label = line.substring(0, labelIndex);
			line = line.substring(labelIndex + 1).trim();
		}

		if (line.isEmpty()) {
			checkLabel(index, label, labelAddress);
			return;
		}

		if (line.startsWith(".")) {
			DirectiveSnapshot snapshot = new DirectiveSnapshot(index, labelAddress, line);
			snapshot.scan(assembler);
			labelAddress = snapshot.executeNonLabelRequiredSteps(this);
			directives.add(snapshot);
		} else {
			data.align(2);
			labelAddress = data.getCurrent();
			InstructionSnapshot snapshot = new InstructionSnapshot(index, labelAddress, line);
			instructions.add(snapshot);
			data.addCurrent(snapshot.scan(assembler));
		}

		checkLabel(index, label, labelAddress);
	}

	private void checkLabel(int index, String label, int address) {
		if (label == null) return;

		if (!LabelUtils.isLabelLegal(label)) {
			throw new AssemblerException(index, "Label " + label + " contains illegal characters.");
		}

		if (convertToGlobalLabel.contains(label)) {
			assembler.addGlobalLabel(index, label, address);
		} else {
			if (assembler.getGlobalLabelAddress(label).isPresent()) {
				throw new AssemblerException(index, "Label " + label + " is already defined as a global label.");
			}
			if (labels.containsKey(label)) {
				throw new AssemblerException(index, "Label " + label + " already defined.");
			}
			labels.put(label, address);
		}
	}

	private String sanityLine(String line) {
		line = StringUtils.removeComments(line).trim();
		for (Map.Entry<String, String> entry : equivalents.entrySet()) {
			line = line.replace(entry.getKey(), entry.getValue());
		}
		return line;
	}
}
