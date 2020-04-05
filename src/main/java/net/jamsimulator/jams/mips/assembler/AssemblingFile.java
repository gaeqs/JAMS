package net.jamsimulator.jams.mips.assembler;

import net.jamsimulator.jams.mips.assembler.exception.AssemblerException;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Represents a list of instructions that are currently being compiled.
 * <p>
 * Local labels and equivalents are stored here.
 */
public class AssemblingFile {

	List<String> rawCode;
	Map<String, Integer> labels;
	List<InstructionSnapshot> instructionSnapshots;
	List<DirectiveSnapshot> directiveSnapshots;
	Map<String, String> equivalents;

	AssemblingFile(List<String> rawCode) {
		this.rawCode = rawCode;
		this.labels = new HashMap<>();
		this.instructionSnapshots = new LinkedList<>();
		this.directiveSnapshots = new LinkedList<>();
		this.equivalents = new HashMap<>();
	}

	public List<String> getRawCode() {
		return rawCode;
	}

	public Map<String, Integer> getLabels() {
		return labels;
	}

	public List<InstructionSnapshot> getInstructionSnapshots() {
		return instructionSnapshots;
	}

	public List<DirectiveSnapshot> getDirectiveSnapshots() {
		return directiveSnapshots;
	}

	public Map<String, String> getEquivalents() {
		return equivalents;
	}

	public int getLabelValue(Assembler assembler, String label, int lineNumber) {
		if (!labels.containsKey(label)) {
			if (!assembler.getGlobalLabels().containsKey(label))
				throw new AssemblerException(lineNumber, "Label " + label + " not found.");
			return assembler.getGlobalLabels().get(label);
		} else {
			return labels.get(label);
		}
	}
}
