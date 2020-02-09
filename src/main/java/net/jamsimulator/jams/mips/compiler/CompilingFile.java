package net.jamsimulator.jams.mips.compiler;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Represents a list of instructions that are currently being compiled.
 * <p>
 * Local labels and equivalents are stored here.
 */
public class CompilingFile {

	List<String> rawCode;
	Map<String, Integer> labels;
	List<InstructionSnapshot> snapshots;
	Map<String, String> equivalents;

	CompilingFile(List<String> rawCode) {
		this.rawCode = rawCode;
		this.labels = new HashMap<>();
		this.snapshots = new LinkedList<>();
		this.equivalents = new HashMap<>();
	}

	public List<String> getRawCode() {
		return rawCode;
	}

	public Map<String, Integer> getLabels() {
		return labels;
	}

	public List<InstructionSnapshot> getSnapshots() {
		return snapshots;
	}

	public Map<String, String> getEquivalents() {
		return equivalents;
	}
}
